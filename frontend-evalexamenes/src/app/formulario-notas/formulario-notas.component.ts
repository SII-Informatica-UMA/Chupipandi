import { Component } from "@angular/core";
import { NgbActiveModal } from "@ng-bootstrap/ng-bootstrap";
import { Estudiante } from "../estudiante"

@Component({
  selector: 'app-formulario-notas',
  templateUrl: './formulario-notas.component.html',
  styleUrls: ['./formulario-notas.component.css']
})
export class FormularioNotasComponent {
  estudiante: Estudiante = {dni: '', apellido: ''};

  constructor(public modal: NgbActiveModal) { }

  buscarNotas(): void {
    this.modal.close(this.estudiante);
  }
}
