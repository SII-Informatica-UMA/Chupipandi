import { Component } from '@angular/core';
import { NgbActiveModal } from '@ng-bootstrap/ng-bootstrap';
import { FormControl, FormGroup, Validators } from '@angular/forms';

@Component({
  selector: 'app-formulario-corrector',
  templateUrl: './formulario-corrector.component.html',
  styleUrls: ['./formulario-corrector.component.css']
})
export class FormularioCorrectorComponent {
  accion: "Añadir" | "Editar" | undefined;

  public correctForm: FormGroup;

  get checkActivo() { return this.correctForm.get('flexSwitchCheckDefault')?.value; }
  
  constructor(public modal: NgbActiveModal) {
    this.correctForm = new FormGroup({});
  }

  createFormGroup(): void {
    if (this.accion === "Añadir") {
      this.correctForm.addControl('identificadorUsuario', new FormControl('', Validators.required));
      this.correctForm.addControl('telefono', new FormControl(''));
      this.correctForm.addControl('maximasCorrecciones', new FormControl('', Validators.required));
      this.correctForm.addControl('flexSwitchCheckDefault', new FormControl(''));
      this.correctForm.addControl('identificadorConvocatoria', new FormControl('', Validators.required));
      this.correctForm.addControl('materia', new FormControl('', Validators.required));
    } else if (this.accion === "Editar") {
      this.correctForm.addControl('identificadorUsuario', new FormControl(''));
      this.correctForm.addControl('telefono', new FormControl(''));
      this.correctForm.addControl('maximasCorrecciones', new FormControl(''));
      this.correctForm.addControl('flexSwitchCheckDefault', new FormControl(''));
      this.correctForm.addControl('identificadorConvocatoria', new FormControl(''));
      this.correctForm.addControl('materia', new FormControl(''));
    }
  }

  // Si usamos opcion de deshabilitar el boton de guardar hasta que el formulario sea valido
  guardarCorrector(): void {
    if (!this.correctForm.invalid) {
      this.correctForm.removeControl('flexSwitchCheckDefault');
      this.modal.close(this.correctForm.value);
    }
  }

  // Si usamos opcion de mostrar error cada vez que se detecta un error en un campo
  // guardarCorrector(): void {
  //   if (!this.correctForm.invalid) {
  //     this.correctForm.removeControl('flexSwitchCheckDefault');
  //     this.modal.close(this.correctForm.value);
  //   } else {
  //     Object.keys(this.correctForm.controls).forEach(field => {
  //         const control = this.correctForm.get(field);
  //         control?.markAsTouched({ onlySelf: true });
  //       }
  //     );
  //   }
  // }
}
