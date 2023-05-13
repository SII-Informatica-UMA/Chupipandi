import { Injectable } from '@angular/core';
import { Corrector, CorrectorNuevo } from './interfaces';
import { HttpClient, HttpHeaders, HttpResponse } from '@angular/common/http';
import { Observable, catchError, throwError } from 'rxjs';
import { ErrorModalComponentComponent } from './error-modal-component/error-modal-component.component';
import { NgbModal } from '@ng-bootstrap/ng-bootstrap';

@Injectable({
    providedIn: 'root'
})

export class CorrectorService {
    private correctoresUrl = 'http://localhost:8081/correctores';
    // Token valido hasta 05-07-2023
    private token: string = 'eyJhbGciOiJIUzUxMiIsInR5cCI6IkpXVCJ9.eyJyb2xlcyI6WyJWSUNFUlJFQ1RPUkFETyJdLCJzdWIiOiJ1c2VyIiwiaWF0IjoxNjgzMzk4NzExLCJleHAiOjE2ODg1ODI3MTF9.yVMtQqvOgg9NAhngl-ZYudi18xXWEy-Z6xyP2VtFXmjGdOCaFdAP3QDPro7Ox6ky_TomzceMbZeAUKpsJFaWzg';
    private headers = new HttpHeaders({
        'Authorization': 'Bearer ' + this.token
    });

    constructor(private http: HttpClient, private modalService: NgbModal) { }

    getCorrectores(idConv?: bigint): Observable<Corrector[]> {
        let uri = idConv ? `${this.correctoresUrl}?idConvocatoria=${idConv}` : this.correctoresUrl;
        return this.http.get<Corrector[]>(uri, { headers: this.headers }).pipe(catchError(e => this.handleError(e)));
    }

    // getCorrectoresPorConvocatoria(idConv: bigint): Observable<Corrector[]> {
    //     return this.http.get<Corrector[]>(`${this.correctoresUrl}?idConvoc=${idConv}`, { headers: this.headers })
    //             .pipe(catchError(e => this.handleError(e)));
    // }

    addCorrector(corrector: CorrectorNuevo): Observable<HttpResponse<Corrector>> {
        return this.http.post<Corrector>(this.correctoresUrl, corrector, { headers: this.headers, observe: 'response' })
            .pipe(catchError(e => this.handleError(e)));
    }

    // me estan pasando un CorrectorNuevo, pero necesito el ID que solo lo tiene el Corrector
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
        modalRef.componentInstance.errorCode = `Error code: ${error.status}`;
        // TODO: Tratar de definir mejor los mensajes de error 400 (ver cómo desde el backend mandar un mensaje personalizado)
        switch (error.status) {
            case 400:
                errorMessage = "Error del cliente";
                break;
            case 403:
                errorMessage = "Error en el token o en los permisos";
                break;
            case 404:
                errorMessage = "No se encontró el recurso";
                break;
            default:
                errorMessage = `Error desconocido: ${error.error.message}`;
        }
        modalRef.componentInstance.errorMessage = errorMessage;
        return throwError(() => errorMessage);
    }

}