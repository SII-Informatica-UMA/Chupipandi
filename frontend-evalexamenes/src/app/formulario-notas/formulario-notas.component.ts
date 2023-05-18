import { Component, EventEmitter, Injectable, Output } from "@angular/core";
import { Estudiante } from "../estudiante";
@Component({
  selector: 'app-formulario-notas',
  templateUrl: './formulario-notas.component.html',
  styleUrls: ['./formulario-notas.component.css'],
})

@Injectable({
  providedIn: 'root'
})
export class FormularioNotasComponent {
  estudiante: Estudiante = {dni: '', apellido: ''};
  @Output() getNotas: EventEmitter<Estudiante> = new EventEmitter<Estudiante>();

  constructor() { }
}
