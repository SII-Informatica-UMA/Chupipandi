import { HttpClient } from "@angular/common/http";
import { Injectable } from "@angular/core";
import { Observable } from "rxjs";
import { Examen } from "./examen";

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
