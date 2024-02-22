import { Injectable } from '@angular/core';
import { HttpClient, HttpResponse } from '@angular/common/http';
import { Observable, firstValueFrom } from 'rxjs';
import { ErrorModalComponentComponent } from '../error-modal-component/error-modal-component.component';
import { NgbModal } from '@ng-bootstrap/ng-bootstrap';

@Injectable({
    providedIn: 'root'
})

export class TokenService {
    private tokenUrl = 'http://localhost:8081/token';

    constructor(private http: HttpClient, private modalService: NgbModal) { }

    async updateToken(): Promise<void> {
        try {
            const response = await firstValueFrom(this.http.get<{token: string}>(`${this.tokenUrl}/nuevo`, { observe: 'response' }));
            if (response && response.body?.token) {
                localStorage.setItem("token", response.body.token);
            } else {
                throw new Error('Token no recibido');
            }
        } catch (error : any) {
            console.error('Error al actualizar el token', error);
            const modalRef = this.modalService.open(ErrorModalComponentComponent);
            modalRef.componentInstance.message = 'Error al actualizar el token: ' + error.message;
        }
    }

    getTokenValidity(): Observable<HttpResponse<boolean>> {
        return this.http.get<boolean>(`${this.tokenUrl}/validez?token=${localStorage.getItem("token")}`, { observe: 'response' });
    }

    getToken(): string {
        return localStorage.getItem("token") ?? '';
    }

}