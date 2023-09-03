import { Injectable } from '@angular/core';
import { Corrector, CorrectorNuevo } from '../model/interfaces';
import { HttpClient, HttpHeaders, HttpResponse, HttpStatusCode } from '@angular/common/http';
import { Observable, catchError, firstValueFrom, lastValueFrom, throwError } from 'rxjs';
import { ErrorModalComponentComponent } from '../error-modal-component/error-modal-component.component';
import { NgbModal } from '@ng-bootstrap/ng-bootstrap';

@Injectable({
    providedIn: 'root'
})

export class CorrectorService {
    private correctoresUrl = 'http://localhost:8081/correctores';
    private tokenUrl = 'http://localhost:8081/token';

    // Token valido 01/01/2023 - 01/01/2033
    private token: string = 'eyJhbGciOiJIUzUxMiJ9.eyJyb2xlcyI6WyJWSUNFUlJFQ1RPUkFETyIsIkNPUlJFQ1RPUiJdLCJzdWIiOiJ1c2VyIiwiaWF0IjoxNjcyNTI3NjAwLCJleHAiOjE5ODgxNDY4MDB9.4e4pTFYwSxy81WAFpAM33UsaTA4FQMgiT43QZVsf8XMr5IsZmuRpaC68O6233BYGFaXhe9DNwQEMA8F_6wYUFg';
    private headers = new HttpHeaders({
        'Authorization': 'Bearer ' + this.token
    });

    constructor(private http: HttpClient, private modalService: NgbModal) { }

    // TODO: Actualiza el token invalido por uno nuevo consultando la url /token/nuevo
    //
    // async updateToken(): Promise<void> {
    //   try {
    //       const response: HttpResponse<string> = await firstValueFrom(this.http.get<string>(`${this.tokenUrl}/nuevo`, { observe: 'response' }));
    //       this.token = JSON.parse(JSON.stringify(response.body)).token as string
    //       console.log(this.token);
    //   } catch (error) {
    //       throw error;
    //   }
    // }
    //
    // getTokenValidity(): Observable<HttpResponse<boolean>> {
    //   return this.http.get<boolean>(`${this.tokenUrl}/validez?token=${this.token}`, { observe: 'response' });
    // }

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

    handleError(error: any) {
        let errorMessage = '';
        const modalRef = this.modalService.open(ErrorModalComponentComponent);
        modalRef.componentInstance.errorCode = `Error code ${error.status}: `;
        switch (error.status) {
            case 0:
                errorMessage = "Error de angular ¿?";
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