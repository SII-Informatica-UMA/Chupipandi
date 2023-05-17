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
  collapsed: boolean = true;

  public correctForm: FormGroup = new FormGroup({});

  constructor(public modal: NgbActiveModal) { }

  createFormGroup(): void {
    if (this.accion === "Añadir") {
      // Para que el formulario muestre abierto los campos de nueva convocatoria (en añadir es obligatorio)
      this.collapsed = false;
      this.correctForm.addControl('identificadorUsuario', new FormControl('', [Validators.required, Validators.min(1)]));
      this.correctForm.addControl('telefono', new FormControl('', Validators.pattern("^[0-9]{9}$")));
      this.correctForm.addControl('maximasCorrecciones', new FormControl('', [Validators.required, Validators.min(1)]));
      this.correctForm.addControl('flexSwitchCheckDefault', new FormControl(''));
      this.correctForm.addControl('identificadorConvocatoria', new FormControl('', [Validators.required, Validators.min(1)]));
      this.correctForm.addControl('materia', new FormControl('', Validators.required));
    } else if (this.accion === "Editar") {
      this.correctForm.addControl('identificadorUsuario', new FormControl('', Validators.min(1)));
      this.correctForm.addControl('telefono', new FormControl('', Validators.pattern("^[0-9]{9}$")));
      this.correctForm.addControl('maximasCorrecciones', new FormControl('', Validators.min(1)));
      this.correctForm.addControl('flexSwitchCheckDefault', new FormControl(''));
      this.correctForm.addControl('identificadorConvocatoria', new FormControl('', Validators.min(1)));
      this.correctForm.addControl('materia', new FormControl(''));
    }
  }

  // Si usamos opcion de deshabilitar el boton de guardar hasta que el formulario sea valido
  guardarCorrector(): void {
    if (!this.correctForm.invalid) {
      // Quitamos el campo del switch para que no se envie al backend
      this.correctForm.removeControl('flexSwitchCheckDefault');
      // Añadimos el telefono (solo si se ha editado): separadores y prefijo de España
      if (this.correctForm.get('telefono')?.value !== "") {
        let tlf = this.correctForm.get('telefono')?.value;
        this.correctForm.get('telefono')?.setValue(tlf.replace(/(\d{3})(\d{3})(\d{3})/, '+34 $1-$2-$3'));
      }
      this.modal.close(this.correctForm.value);
    }
  }
  
  nuevaConvoc(): void {
    if (!this.collapsed) {
      // Si se despliega el formulario de nueva convocatoria, se añaden los validadores
      this.correctForm.controls['materia'].addValidators(Validators.required);
      this.correctForm.controls['identificadorConvocatoria'].addValidators(Validators.required);
      // y se actualiza la validacion de los campos
      this.correctForm.controls['materia'].updateValueAndValidity();
      this.correctForm.controls['identificadorConvocatoria'].updateValueAndValidity();
    } else {
      // Si se oculta el formulario de nueva convocatoria, se eliminan los validadores
      this.correctForm.controls['materia'].removeValidators(Validators.required);
      this.correctForm.controls['identificadorConvocatoria'].removeValidators(Validators.required);
      // y se actualiza la validacion de los campos
      this.correctForm.controls['materia'].updateValueAndValidity();
      this.correctForm.controls['identificadorConvocatoria'].updateValueAndValidity();
    }
  }
}
