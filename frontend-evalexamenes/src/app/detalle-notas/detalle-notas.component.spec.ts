import { ComponentFixture, TestBed } from '@angular/core/testing';
import { NgbAccordionModule } from '@ng-bootstrap/ng-bootstrap';
import { Examen } from '../examen';
import { DetalleNotasComponent } from './detalle-notas.component';

describe('DetalleNotasComponent', () => {
  let fixture: ComponentFixture<DetalleNotasComponent>;
  let component: DetalleNotasComponent;
  beforeEach(async () => {
    await TestBed.configureTestingModule({
      declarations: [],
      imports: [
        DetalleNotasComponent,
        NgbAccordionModule
      ]
    }).compileComponents().then(() => {
      fixture = TestBed.createComponent(DetalleNotasComponent);
      component = fixture.componentInstance;
    });
  });

  it('should create the component', () => {
    expect(component).toBeTruthy();
  });

  it('should not have div.container if notas.length == 0', (done: DoneFn) => {
    const compiled = fixture.nativeElement as HTMLElement;
    const element = compiled.querySelector('div.container')?.innerHTML;
    fixture.whenStable().then(() => {
      expect(element).toBeUndefined();
      done();
    });
  })

  it('should have div.container if notas.length > 0', (done: DoneFn) => {
    const examen: Examen = {id: 1, materia: 1, codigoAlumno: 1, nota: 8.5};
    component.notas = [examen];
    fixture.detectChanges();

    const compiled = fixture.nativeElement as HTMLElement;
    const element = compiled.querySelector('div.container')?.innerHTML;

    fixture.whenStable().then(() => {
      expect(element).toContain('div');
      expect(element).toContain('h2');
      expect(element).toContain('Lista de notas');
      done();
    });
  })

  it('should not have accordion if notas.length == 0', (done: DoneFn) => {
    const compiled = fixture.nativeElement as HTMLElement;
    const element = compiled.querySelector('#accordion')?.innerHTML;

    fixture.whenStable().then(() => {
      expect(element).toBeUndefined();
      done();
    });
  })

  it('should have accordion if notas.length > 0', (done: DoneFn) => {
    const examen: Examen = {id: 1, materia: 1, codigoAlumno: 1, nota: 8.5};
    component.notas = [examen];
    fixture.detectChanges();

    const compiled = fixture.nativeElement as HTMLElement;
    const element = compiled.querySelector('#accordion')?.innerHTML;

    fixture.whenStable().then(() => {
      expect(element).toContain('div');
      expect(element).toContain(`Examen ID ${examen.id}`);
      done();
    })
  })
});
