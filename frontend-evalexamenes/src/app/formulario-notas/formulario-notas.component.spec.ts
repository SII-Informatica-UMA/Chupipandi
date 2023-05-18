import { ComponentFixture, TestBed } from '@angular/core/testing';
import { FormsModule, NgModel } from '@angular/forms';
import { By } from '@angular/platform-browser';
import { NgbAccordionModule } from '@ng-bootstrap/ng-bootstrap';
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

  it('should have a div.form-group with content', (done: DoneFn) => {
    const compiled = fixture.nativeElement as HTMLElement;
    const element = compiled.querySelector('div.form-group')?.innerHTML;

    fixture.whenStable().then(() => {
      expect(element).toContain('label');
      expect(element).toContain('DNI:');
      expect(element).toContain('Apellido:');
      expect(element).toContain('input');
      expect(element).toContain('button');
      done();
    })
  })

  it('should have inputs with no text when estudiante is empty', (done: DoneFn) => {
    fixture.detectChanges();
    const debug = fixture.debugElement;
    const inputDNI = debug.query(By.css('#dni')).nativeElement;
    const inputApellido = debug.query(By.css('#apellido')).nativeElement;

    fixture.whenStable().then(() => {
      expect(inputDNI.value).toBe('');
      expect(inputApellido.value).toBe('');
      done();
    })
  })
});
