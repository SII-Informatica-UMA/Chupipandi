import { Component, Injectable } from "@angular/core";
import { Estudiante } from "../estudiante";
@Component({
  selector: 'app-formulario-notas',
  templateUrl: './formulario-notas.component.html',
  styleUrls: ['./formulario-notas.component.css']
})

@Injectable({
  providedIn: 'root'
})
export class FormularioNotasComponent {
  estudiante: Estudiante = {dni: '', apellido: ''};

  constructor() { }
}
