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
  introducir: boolean = false;

  public correctForm: FormGroup;

  get identificadorUsuario() { return this.correctForm.get('identificadorUsuario'); }
  get telefono() { return this.correctForm.get('telefono'); }
  get maximasCorrecciones() { return this.correctForm.get('maximasCorrecciones'); }
  get flexSwitchCheckDefault() { return this.correctForm.get('flexSwitchCheckDefault'); }
  get identificadorConvocatoria() { return this.correctForm.get('identificadorConvocatoria'); }
  get materia() { return this.correctForm.get('materia'); }
  
  constructor(public modal: NgbActiveModal) {
    this.correctForm = this.createFormGroup();
  }

  createFormGroup() {
    return new FormGroup({
      identificadorUsuario: new FormControl('', Validators.required),
      telefono: new FormControl(''),
      maximasCorrecciones: new FormControl('', Validators.required),
      flexSwitchCheckDefault: new FormControl(''),
      identificadorConvocatoria: new FormControl('', Validators.required),
      materia : new FormControl('', Validators.required)
    });
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
