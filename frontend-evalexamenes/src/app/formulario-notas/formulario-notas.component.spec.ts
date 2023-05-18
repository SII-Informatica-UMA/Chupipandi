import { ComponentFixture, TestBed, waitForAsync } from '@angular/core/testing';
import { FormsModule, NgModel } from '@angular/forms';
import { By } from '@angular/platform-browser';
import { NgbAccordionModule } from '@ng-bootstrap/ng-bootstrap';
import { AppComponent } from '../app.component';
import { FormularioNotasComponent } from './formulario-notas.component';

describe('FormularioNotasComponent', () => {
  let fixture: ComponentFixture<FormularioNotasComponent>;
  let component: FormularioNotasComponent;
  beforeEach(async () => {
    await TestBed.configureTestingModule({
      declarations: [
        FormularioNotasComponent,
        NgModel
      ],
      imports: [
        NgbAccordionModule,
        FormsModule
      ]
    }).compileComponents().then(() => {
      fixture = TestBed.createComponent(FormularioNotasComponent);
      component = fixture.componentInstance;
    });
  });

  it('should create the component', () => {
    expect(component).toBeTruthy();
  });

  it('should have a div.form-group with content', waitForAsync(() => {
    const compiled = fixture.nativeElement as HTMLElement;
    const element = compiled.querySelector('div.form-group')?.innerHTML;

    fixture.whenStable().then(() => {
      expect(element).toContain('label');
      expect(element).toContain('DNI:');
      expect(element).toContain('Apellido:');
      expect(element).toContain('input');
      expect(element).toContain('button');
    })
  }))

  it('should have inputs with no text when estudiante is empty', waitForAsync(() => {
    const debug = fixture.debugElement;
    const inputDNI = debug.query(By.css('#dni')).nativeElement as HTMLInputElement;
    const inputApellido = debug.query(By.css('#apellido')).nativeElement as HTMLInputElement;


    inputDNI.dispatchEvent(new Event('input'));
    inputApellido.dispatchEvent(new Event('input'));
    fixture.detectChanges();

    fixture.whenStable().then(() => {
      expect(inputDNI.value).toBe('');
      expect(inputApellido.value).toBe('');
    })
  }))

  it('should have inputs with text when estudiante is not empty', waitForAsync(() => {
    component.estudiante = {dni: '05981804X', apellido: 'González'};
    const debug = fixture.debugElement;
    const inputDNI = debug.query(By.css('#dni')).nativeElement as HTMLInputElement;
    const inputApellido = debug.query(By.css('#apellido')).nativeElement as HTMLInputElement;
    fixture.detectChanges();

    fixture.whenStable().then(() => {
      expect(inputDNI.value).toBe('05981804X');
      expect(inputApellido.value).toBe('González');
    })
  }))

  it('should change estudiante when changing input values', waitForAsync(() => {
    const debug = fixture.debugElement;
    const inputDNI = debug.query(By.css('#dni')).nativeElement as HTMLInputElement;
    const inputApellido = debug.query(By.css('#apellido')).nativeElement as HTMLInputElement;

    fixture.detectChanges();
    inputDNI.value = '05981804X';
    inputApellido.value = 'González';

    inputDNI.dispatchEvent(new Event('input'));
    inputApellido.dispatchEvent(new Event('input'));

    fixture.whenStable().then(() => {
      expect(component.estudiante.dni).toEqual('05981804X');
      expect(component.estudiante.apellido).toEqual('González');
    })
  }))

  it('should emit an event', waitForAsync(() => {
    spyOn(component.getNotas, 'emit');
    const element = fixture.nativeElement.querySelector('button');
    element.dispatchEvent(new Event('click'));

    fixture.detectChanges();

    fixture.whenStable().then(() => {
      expect(component.getNotas.emit).toHaveBeenCalled();
    })
  }))
});
