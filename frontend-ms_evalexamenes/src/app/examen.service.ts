import { Injectable } from "@angular/core";
import { Examen } from "./examen";
import { Correcion } from "./correcion";
import { HttpClient, HttpResponse } from "@angular/common/http"
import { Observable } from "rxjs";

@Injectable({
  providedIn: 'root'
})
export class ExamenService {
  private baseURI: string = 'http://localhost:8080/examenes';

  constructor(private http: HttpClient) { }

  putExamen(examen: Examen): Observable<Examen> {
    return this.http.put<Examen>(this.baseURI + '/' + examen.id, examen);
  }

  getCorrecion(): Observable<Correcion> {
    return this.http.get<Correcion>(this.baseURI + '/correciones');
  }
}
