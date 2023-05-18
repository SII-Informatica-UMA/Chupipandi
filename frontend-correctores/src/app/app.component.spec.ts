import { ComponentFixture, TestBed } from '@angular/core/testing';
import { AppComponent } from './app.component';
import { CorrectorService } from './service/corrector.service';
import { NgbModal } from '@ng-bootstrap/ng-bootstrap';

import { HttpClient } from '@angular/common/http';

describe('El componente principal', () => {
  const mockCorrectores = {
    getCorrectores: () => {
      return [
        {
          id: 1n,
          identificadorUsuario: 11n,
          telefono: '+34 111-222-333',
          maximasCorrecciones: 10,
          materias: [
            {
              idMateria: 1n,
              idConvocatoria: 1n
            },
            {
              idMateria: 2n,
              idConvocatoria: 2n
            }
          ]
        },
        {
          id: 2n,
          identificadorUsuario: 22n,
          telefono: '+34 444-555-666',
          maximasCorrecciones: 15,
          materias: [
            {
              idMateria: 3n,
              idConvocatoria: 2n
            },
            {
              idMateria: 4n,
              idConvocatoria: 3n
            }
          ]
        },
        {
          id: 3n,
          identificadorUsuario: 33n,
          telefono: '+34 777-888-999',
          maximasCorrecciones: 20,
          materias: [
            {
              idMateria: 5n,
              idConvocatoria: 3n
            },
            {
              idMateria: 6n,
              idConvocatoria: 4n
            }
          ]
        }
      ]
    },
    // actualizaCorrectores: () => {
    //   return mockCorrectores.getCorrectores();
    // }
  }

  let component: AppComponent;
  let fixture: ComponentFixture<AppComponent>;
  let compiled: HTMLElement;

  beforeEach(async () => {
    await TestBed.configureTestingModule({
      declarations: [
        AppComponent
      ],
      providers: [
        { provide: CorrectorService, useValue: mockCorrectores },
        NgbModal, HttpClient
      ]
    }).compileComponents();
    fixture = TestBed.createComponent(AppComponent);
    component = fixture.componentInstance;
    compiled = fixture.nativeElement;
  });

  it('debe mostrar una lista de correctores', () => {
    component.correctores = mockCorrectores.getCorrectores();
    // fixture.detectChanges();
    let listaCorrectores = compiled.querySelectorAll('.list-group-item');
    expect(listaCorrectores.length).toEqual(component.correctores.length);
  });

  // FIX: Como sobrescribir el metodo actualizaCorrectores (como con getCorrectores)
  // da prolemas fixture.detectChanges() -> llama a ngOnInit() -> llama a actualizaCorrectores() -> llama al backend

  // test para comprobar que salsa una alerta cuando no se encuentra ningun corrector
  it('should display busquedaFallida message if busquedaFallida is true', () => {
    component.busquedaFallida = true;
    // spyOn(component, 'actualizaCorrectores').and.returnValue(mockCorrectores.getCorrectores());
    // spyOn(component, 'ngOnInit').and.callFake(() => {
    //   component.correctores = mockCorrectores.getCorrectores()
    // });
    // fixture.detectChanges();
    const message = fixture.nativeElement.querySelector('.alert.alert-info') as HTMLDivElement;
    expect(message).toBeTruthy();
    expect(message.textContent).toContain('No se han encontrado convocatorias con el id');
  });

  // TODO: Algun test de la integracion con el teclado

});
