import { ComponentFixture, TestBed, getTestBed } from '@angular/core/testing';
import { AppComponent } from './app.component';
import { CorrectorService } from './service/corrector.service';

import { FormsModule } from '@angular/forms';
import { DetalleCorrectorComponent } from './detalle-corrector/detalle-corrector.component';

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
      ];
    }
  }

  let component: AppComponent;
  let fixture: ComponentFixture<AppComponent>;
  let compiled: HTMLElement;

  beforeEach(async () => {
    await TestBed.configureTestingModule({
      imports: [FormsModule],
      declarations: [
        AppComponent,
        DetalleCorrectorComponent
      ],
      providers: [
        { provide: CorrectorService, useValue: mockCorrectores },
      ]
    }).compileComponents().then(() => {
      fixture = TestBed.createComponent(AppComponent);
      component = fixture.componentInstance;
      compiled = fixture.nativeElement;
    });
  });

  it('debe mostrar una lista de correctores', () => {
    spyOn(component, 'actualizaCorrectores').and.callFake(() => {
      component.correctores = mockCorrectores.getCorrectores()
    });
    fixture.detectChanges();
    let listaCorrectores = compiled.querySelectorAll('.list-group-item');
    expect(listaCorrectores.length).toEqual(component.correctores.length);
  });

  it('debe mostrar el mensaje de busquedaFallida si no se encuentran correctores', () => {
    component.busquedaFallida = true;
    spyOn(component, 'actualizaCorrectores').and.callFake(() => {
      component.correctores = mockCorrectores.getCorrectores()
    });
    fixture.detectChanges();
    const message = fixture.nativeElement.querySelector('.alert.alert-info') as HTMLDivElement;
    expect(message).toBeTruthy();
    expect(message.textContent).toContain('No se han encontrado convocatorias con el id');
  });

  it('deberia mostrar la info del siguiente corrector (hacia abajo) hacer click en "ArrowDown"', () => {
    component.correctores = mockCorrectores.getCorrectores();
    
    // Cualquier posicion es valida, el array es circular ;)
    let position = 33;
    component.correctorElegido = component.correctores[position % component.correctores.length];
    let correctorExp = component.correctores[(position + 1) % component.correctores.length];
    
    spyOn(component, 'actualizaCorrectores').and.callFake(() => {
      component.correctores = mockCorrectores.getCorrectores()
    });

    component.onKeyDown(new KeyboardEvent('keydown', { key: 'ArrowDown' }));
    
    fixture.detectChanges();

    expect(correctorExp).toEqual(component.correctorElegido);
  });

  it('deberia mostrar la info del siguiente corrector (hacia arriba) hacer click en "ArrowUp"', () => {
    component.correctores = mockCorrectores.getCorrectores();
    
    // Cualquier posicion es valida, el array es circular ;)
    let position = 33;
    component.correctorElegido = component.correctores[position % component.correctores.length];
    let correctorExp = component.correctores[(position - 1) % component.correctores.length];
    
    spyOn(component, 'actualizaCorrectores').and.callFake(() => {
      component.correctores = mockCorrectores.getCorrectores()
    });

    component.onKeyDown(new KeyboardEvent('keydown', { key: 'ArrowUp' }));
    
    fixture.detectChanges();

    expect(correctorExp).toEqual(component.correctorElegido);
  });

  it('deberia llamar al metodo para eliminar el corrector seleccionado al hacer click en "Delete"', () => {
    component.correctores = mockCorrectores.getCorrectores();
    component.correctorElegido = component.correctores[0];

    spyOn(component, 'actualizaCorrectores').and.callFake(() => {
      component.correctores = mockCorrectores.getCorrectores()
    });
    
    spyOn(component, 'eliminarCorrector');
    
    component.onKeyDown(new KeyboardEvent('keydown', { key: 'Delete' }));
    
    // Deberia llamarse la funcion de eliminar corrector al hacer click en "Delete"
    expect(component.eliminarCorrector).toHaveBeenCalled();
  });

  it('deberia deseleccionar el corrector seleccionado al hacer click en "Escape"', () => {
    component.correctores = mockCorrectores.getCorrectores();
    component.correctorElegido = component.correctores[0];

    // En este punto el corrector seleccionado deberia estar definido
    expect(component.correctorElegido).toBeDefined();
    
    spyOn(component, 'actualizaCorrectores').and.callFake(() => {
      component.correctores = mockCorrectores.getCorrectores()
    });
    
    component.onKeyDown(new KeyboardEvent('keydown', { key: 'Escape' }));
    
    // En este punto el corrector seleccionado deberia ser undefined
    expect(component.correctorElegido).toBeUndefined();
  });

});