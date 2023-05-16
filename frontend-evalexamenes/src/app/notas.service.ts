import { Injectable } from "@angular/core";
import { Examen } from "./examen";
import { HttpClient, HttpParams, HttpResponse } from "@angular/common/http";
import { catchError, Observable, of, retry, throwError } from "rxjs";

@Injectable({
  providedIn: 'root'
})
export class NotasService {
  private baseURI: string = 'http://localhost:8080/notas';

  constructor(private http: HttpClient) { }

  getNotas(dni: string, apellido: string): Observable<Examen []> {
    return this.http.get<Examen []>(`${this.baseURI}?dni=${dni}&apellido=${apellido}`);
  }
}
