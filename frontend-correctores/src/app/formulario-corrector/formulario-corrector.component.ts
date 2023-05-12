import { Component } from '@angular/core';
import { CorrectorNuevo } from '../interfaces';
import { NgbActiveModal } from '@ng-bootstrap/ng-bootstrap';

@Component({
  selector: 'app-formulario-corrector',
  templateUrl: './formulario-corrector.component.html',
  styleUrls: ['./formulario-corrector.component.css']
})
export class FormularioCorrectorComponent {
  accion: "Añadir" | "Editar" | undefined;
  corrector: CorrectorNuevo = {identificadorUsuario: BigInt(0), identificadorConvocatoria: undefined, telefono: '', materia: '', maximasCorrecciones: 0};

  constructor(public modal: NgbActiveModal) { }

  guardarCorrector(): void {
    this.modal.close(this.corrector);
  }
}
