import { HttpClient, HttpHeaders } from "@angular/common/http";
import { Injectable } from "@angular/core";
import { Observable } from "rxjs";
import { Correccion } from "./correccion";
import { Examen } from "./examen";

@Injectable({
  providedIn: 'root'
})
export class ExamenService {
  private baseURI: string = 'http://localhost:8080/examenes';
  private token: string = 'eyJhbGciOiJIUzUxMiIsInR5cCI6IkpXVCJ9.eyJyb2xlcyI6WyJDT1JSRUNUT1IiLCJWSUNFUlJFQ1RPUkFETyJdLCJzdWIiOiJ1c2VyIiwiaWF0IjoxNjgxOTEwNTg4LCJleHAiOjE2ODQ1MDI1ODh9.jeqrAzyQ0sNq52W4Y-_Zc5guWYAOIjRKjQSn6-UEY2z8eBlZMswfukVSFou3YxnKvV8lO9aX6_iRsi1MAAJLTw';
  private headers = new HttpHeaders({
    'Authorization': 'Bearer ' + this.token
  });

  constructor(private http: HttpClient) { }

  putExamen(examen: Examen): Observable<Examen> {
    return this.http.put<Examen>(this.baseURI + '/' + examen.id, examen, { headers: this.headers });
  }

  getExamen(id: number): Observable<Examen> {
    return this.http.get<Examen>(this.baseURI + '/' + id, { headers: this.headers })
  }

  getCorreccion(): Observable<Correccion> {
    return this.http.get<Correccion>(this.baseURI + '/correcciones', { headers: this.headers });
  }
}
