import { Injectable } from "@angular/core";
import { Notificacion } from "./notificacion";
import { HttpClient, HttpResponse } from "@angular/common/http";
import { Observable } from "rxjs";

@Injectable({
  providedIn: 'root'
})
export class NotificacionService {
  private baseURI: string = 'http://localhost:8080/notificaciones/notas'

  constructor(private http: HttpClient) { }

  postNotificacion(notificacion: Notificacion): Observable<Notificacion> {
    return this.http.post<Notificacion>(this.baseURI, notificacion);
  }
}
