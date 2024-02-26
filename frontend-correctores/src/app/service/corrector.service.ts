import { Injectable } from '@angular/core';
import { Corrector, CorrectorNuevo } from '../model/interfaces';
import { HttpClient, HttpHeaders, HttpResponse, HttpStatusCode } from '@angular/common/http';
import { Observable, catchError, firstValueFrom, lastValueFrom, throwError } from 'rxjs';
import { ErrorModalComponentComponent } from '../error-modal-component/error-modal-component.component';
import { NgbModal } from '@ng-bootstrap/ng-bootstrap';
import { TokenService } from './token.service';

@Injectable({
    providedIn: 'root'
})

export class CorrectorService {
    private correctoresUrl = 'http://localhost:8081/correctores';

    private headers = new HttpHeaders({
        'Authorization': 'Bearer ' + this.tokenService.getToken()
    });

    constructor(private http: HttpClient, private tokenService: TokenService, private modalService: NgbModal) { }

    getCorrectores(idConv?: bigint): Observable<Corrector[]> {
        let uri = idConv ? `${this.correctoresUrl}?idConvocatoria=${idConv}` : this.correctoresUrl;
        return this.http.get<Corrector[]>(uri, { headers: this.headers }).pipe(catchError(e => this.handleError(e)));
    }

    addCorrector(corrector: CorrectorNuevo): Observable<HttpResponse<Corrector>> {
        return this.http.post<Corrector>(this.correctoresUrl, corrector, { headers: this.headers, observe: 'response' })
            .pipe(catchError(e => this.handleError(e)));
    }

    editCorrector(corrector: CorrectorNuevo, id: bigint): Observable<HttpResponse<Corrector>> {
        return this.http.put<Corrector>(`${this.correctoresUrl}/${id}`, corrector, { headers: this.headers, observe: 'response' })
            .pipe(catchError(e => this.handleError(e)));
    }

    deleteCorrector(id: bigint): Observable<HttpResponse<Corrector>> {
        return this.http.delete<Corrector>(`${this.correctoresUrl}/${id}`, { headers: this.headers, observe: 'response' })
            .pipe(catchError(e => this.handleError(e)));
    }

    async tryConnect(): Promise<void> {
        try {
            const response = await firstValueFrom(this.http.get<{token: string}>(`${this.correctoresUrl}/healthcheck`, { observe: 'response' }));
            if (response && response.status === HttpStatusCode.Ok) {
                console.log("Conexion correcta");
            }
        } catch (error : any) {
            throw new Error('Error al conectar con el servidor');
        }
    }

    handleError(error: any) {
        let errorMessage = '';
        const modalRef = this.modalService.open(ErrorModalComponentComponent);
        modalRef.componentInstance.errorCode = `Error code ${error.status}: `;
        switch (error.status) {
            // Status code 0 is returned when the server is not available. It gets no response.
            case 0:
                errorMessage = "Comprobar si el backend está disponible";
                modalRef.componentInstance.errorCode += "Error de conexión";
                break;
            case HttpStatusCode.BadRequest:
                errorMessage = "Error del cliente";
                modalRef.componentInstance.errorCode += "Bad Request";
                break;
            case HttpStatusCode.Forbidden:
                errorMessage = "Error en el token o en los permisos";
                modalRef.componentInstance.errorCode += "Forbidden";
                break;
            case HttpStatusCode.NotFound:
                errorMessage = "No se encontró el recurso";
                modalRef.componentInstance.errorCode += "Not Found";
                break;
            case HttpStatusCode.Conflict:
                errorMessage = "Ya existe un corrector con ese identificador de usuario";
                modalRef.componentInstance.errorCode += "Conflict";
                break;
            default:
                errorMessage = `Error desconocido: ${error.error.message}`;
        }
        modalRef.componentInstance.errorMessage = errorMessage;
        return throwError(() => errorMessage);
    }

}