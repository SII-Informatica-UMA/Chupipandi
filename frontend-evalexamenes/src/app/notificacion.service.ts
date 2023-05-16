import { Injectable } from "@angular/core";
import { Notificacion } from "./notificacion";
import { HttpClient, HttpHeaders, HttpResponse } from "@angular/common/http";
import { Observable } from "rxjs";

@Injectable({
  providedIn: 'root'
})
export class NotificacionService {
  private baseURI: string = 'http://localhost:8080/notificaciones/notas'
  private token: string = 'eyJhbGciOiJIUzUxMiIsInR5cCI6IkpXVCJ9.eyJyb2xlcyI6WyJDT1JSRUNUT1IiLCJWSUNFUlJFQ1RPUkFETyJdLCJzdWIiOiJ1c2VyIiwiaWF0IjoxNjgxOTEwNTg4LCJleHAiOjE2ODQ1MDI1ODh9.jeqrAzyQ0sNq52W4Y-_Zc5guWYAOIjRKjQSn6-UEY2z8eBlZMswfukVSFou3YxnKvV8lO9aX6_iRsi1MAAJLTw';
  private headers = new HttpHeaders({
    'Authorization': 'Bearer ' + this.token
  });

  constructor(private http: HttpClient) { }

  postNotificacion(notificacion: Notificacion): Observable<Notificacion> {
    return this.http.post<Notificacion>(this.baseURI, notificacion, { headers: this.headers });
  }
}
