import { ComponentFixture, TestBed } from '@angular/core/testing';

import { FormularioCorrectorComponent } from './formulario-corrector.component';
import { FormsModule, ReactiveFormsModule, ValidatorFn, Validators } from '@angular/forms';
import { NgbActiveModal, NgbCollapseModule } from '@ng-bootstrap/ng-bootstrap';

describe('El componente FormularioCorrectorComponent', () => {
  let component: FormularioCorrectorComponent;
  let fixture: ComponentFixture<FormularioCorrectorComponent>;
  let compiled: HTMLElement;

  beforeEach(async () => {
    await TestBed.configureTestingModule({
      declarations: [FormularioCorrectorComponent],
      imports: [FormsModule, ReactiveFormsModule, NgbCollapseModule],
      providers: [NgbActiveModal]
    }).compileComponents();

    fixture = TestBed.createComponent(FormularioCorrectorComponent);
    component = fixture.componentInstance;
    compiled = fixture.nativeElement;
  });

  it('debería añadir los valores correctamente', () => {
    component.accion = "Añadir";
    component.createFormGroup();
    component.correctForm.controls['identificadorUsuario'].setValue('11');
    component.correctForm.controls['telefono'].setValue('222333444');
    component.correctForm.controls['maximasCorrecciones'].setValue('15');
    component.correctForm.controls['identificadorConvocatoria'].setValue('1');
    component.correctForm.controls['materia'].setValue('1');

    expect(component.correctForm.value).toEqual({
        identificadorUsuario: '11',
        telefono: '222333444',
        maximasCorrecciones: '15',
        identificadorConvocatoria: '1',
        materia: '1'
    });
  });

  it('deberia deshabilitar el boton de guardar si el formulario es inválido', () => {
    component.accion = "Añadir";
    component.createFormGroup();
    
    component.correctForm.controls['identificadorUsuario'].setValue('');
    component.correctForm.controls['telefono'].setValue('');
    component.correctForm.controls['maximasCorrecciones'].setValue('');
    component.correctForm.controls['identificadorConvocatoria'].setValue('');
    component.correctForm.controls['materia'].setValue('');
    fixture.detectChanges();

    // El formulario ahora es invalido, y por tanto el boton de submit esta deshabilitado
    expect(component.correctForm.valid).toBeFalsy();
    const submitButton = compiled.querySelector('#submit-btn') as HTMLButtonElement;
    expect(submitButton.disabled).toBeTruthy();
  });

  it('deberia habilitar el boton de guardar si el formulario es válido', () => {
    component.accion = "Añadir";
    component.createFormGroup();
    component.correctForm.controls['identificadorUsuario'].setValue('11');
    component.correctForm.controls['telefono'].setValue('222333444');
    component.correctForm.controls['maximasCorrecciones'].setValue('15');
    component.correctForm.controls['identificadorConvocatoria'].setValue('1');
    component.correctForm.controls['materia'].setValue('1');
    fixture.detectChanges();

    // El formulario ahora es valido, y por tanto el boton de submit esta habilitado
    expect(component.correctForm.valid).toBeTruthy();
    const submitButton = compiled.querySelector('#submit-btn') as HTMLButtonElement;
    expect(submitButton.disabled).toBeFalsy();
  });

  it('deberian ser visibles (y obligatorios) los campos para la convocatoria', () => {
    component.accion = "Añadir";
    component.createFormGroup();

    // Collapsed es false -> campos visibles
    expect(component.collapsed).toBeFalsy();
    // Campos de la convocatoria son obligatorios
    expect(component.correctForm.get('identificadorConvocatoria')?.hasValidator(Validators.required)).toBeTruthy();
    expect(component.correctForm.get('materia')?.hasValidator(Validators.required)).toBeTruthy();
    fixture.detectChanges();

    // El formulario ahora es invalido, y por tanto el boton de submit esta deshabilitado
    expect(component.correctForm.valid).toBeFalsy();
    const submitButton = compiled.querySelector('#submit-btn') as HTMLButtonElement;
    expect(submitButton.disabled).toBeTruthy();
  });

  it('deberia alternarse la visibilidad de los campos para la convocatoria al pinchar en el switch', () => {
    component.accion = "Editar";
    component.createFormGroup();

    // Collapsed es true -> campos ocultos
    expect(component.collapsed).toBeTruthy();
    // Campos de la convocatoria no son obligatorios
    expect(component.correctForm.get('identificadorConvocatoria')?.hasValidator(Validators.required)).toBeFalsy();
    expect(component.correctForm.get('materia')?.hasValidator(Validators.required)).toBeFalsy();

    // Como puedo simular el click en el switch?
    // let switchBtn = compiled.querySelector('#flexSwitchCheckDefaul') as HTMLInputElement;

    // Simulo el click en el switch ¿?
    component.collapsed = !component.collapsed;
    component.nuevaConvoc();
    fixture.autoDetectChanges();

    // console.log("validator" + component.correctForm.get('identificadorConvocatoria')?.validator);
    expect(component.correctForm.get('identificadorConvocatoria')?.hasValidator(Validators.required)).toBeTruthy();
    expect(component.correctForm.get('materia')?.hasValidator(Validators.required)).toBeTruthy();
    
    // El formulario ahora es invalido, y por tanto el boton de submit esta deshabilitado
    expect(component.correctForm.valid).toBeFalsy();
    const submitButton = fixture.nativeElement.querySelector('#submit-btn');
    expect(submitButton.disabled).toBeTruthy();
  });
});