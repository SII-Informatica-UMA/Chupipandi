import { Component, EventEmitter, forwardRef, Injectable, Output } from "@angular/core";
import { NG_VALUE_ACCESSOR } from "@angular/forms";
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
