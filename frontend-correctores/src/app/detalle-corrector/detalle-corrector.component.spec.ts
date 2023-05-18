import { ComponentFixture, TestBed } from '@angular/core/testing';
import { HttpClientTestingModule } from '@angular/common/http/testing';
import { By } from '@angular/platform-browser';
import { Corrector, CorrectorNuevo, Materia } from '../model/interfaces';
import { DetalleCorrectorComponent } from './detalle-corrector.component';
import { NgbActiveModal, NgbModal } from '@ng-bootstrap/ng-bootstrap';
import { FormularioCorrectorComponent } from '../formulario-corrector/formulario-corrector.component';
import { CorrectorService } from '../service/corrector.service';

describe('DetalleCorrectorComponent', () => {
  let fixture: ComponentFixture<DetalleCorrectorComponent>;
  let component: DetalleCorrectorComponent;
  
  let fixtureForm: ComponentFixture<FormularioCorrectorComponent>;
  let formComponent: FormularioCorrectorComponent;
  
  let correctorService: CorrectorService;

  beforeEach(async () => {
    await TestBed.configureTestingModule({
      imports: [HttpClientTestingModule],
      declarations: [DetalleCorrectorComponent],
      providers: [CorrectorService, NgbModal, NgbActiveModal]
    }).compileComponents().then(() => {
      fixture = TestBed.createComponent(DetalleCorrectorComponent);
      component = fixture.componentInstance;
      
      correctorService = TestBed.inject(CorrectorService);
    });
  });

  it('hacer click en el boton "Editar" deberia llamar al metodo editarCorrector', (done: DoneFn) => {
    fixtureForm = TestBed.createComponent(FormularioCorrectorComponent);
    formComponent = fixtureForm.componentInstance;

    formComponent.accion = "Editar";

    spyOn(component, 'editarCorrector');

    const html = fixture.nativeElement as HTMLElement; //dom html
    const btn = html.querySelector('#btnEditar') as HTMLButtonElement;
    btn.click();

    fixture.whenStable().then(() => {
      expect(component.editarCorrector).toHaveBeenCalled();
      done();
    });
  });

  // it('deberia emitir el evento correctorEliminado cuando se llama al metodo eliminarCorrector', () => {
  //   const mockCorrector: Corrector = { id: 1n, identificadorUsuario: 1n, telefono: '+34 954-362-837',
  //     maximasCorrecciones: 21, materias: [{ idMateria: 1n, idConvocatoria: 1n }] };

  //   spyOn(component.correctorEliminado, 'emit');

  //   component.corrector = mockCorrector;
  //   component.eliminarCorrector();

  //   expect(component.correctorEliminado.emit).toHaveBeenCalledWith(mockCorrector.id);
  // });

  it('hacer click en el boton "Eliminar" deberia llamar al metodo eliminarCorrector', (done: DoneFn) => {
    spyOn(component, 'eliminarCorrector');

    const html = fixture.nativeElement as HTMLElement; //dom html
    const btn = html.querySelector('#btnEliminar') as HTMLButtonElement;
    btn.click();

    fixture.whenStable().then(() => {
      expect(component.eliminarCorrector).toHaveBeenCalled();
      done();
    });
  });

  it('deberia mostrar la informacion del corrector', () => {
    const mockCorrector: Corrector = {
      id: 1n,
      identificadorUsuario: 1n,
      telefono: '+34 954-362-837',
      maximasCorrecciones: 21,
      materias: [
        { 
          idMateria: 1n,
          idConvocatoria: 1n
        }
      ]
    };

    component.corrector = mockCorrector;
    fixture.detectChanges();

    const idUsuarioLabel = fixture.debugElement.query(By.css('label[for="idUsuario"]'));
    expect(idUsuarioLabel.nativeElement.textContent).toContain('Id. Usuario:');
    
    const idUsuarioSpan = fixture.debugElement.query(By.css('#idUsuario'));
    expect(idUsuarioSpan.nativeElement.textContent).toContain(mockCorrector.identificadorUsuario.toString());
    

    const telefonoLabel = fixture.debugElement.query(By.css('label[for="telefono"]'));
    expect(telefonoLabel.nativeElement.textContent).toContain('Teléfono:');
    
    const telefonoSpan = fixture.debugElement.query(By.css('#telefono'));
    expect(telefonoSpan.nativeElement.textContent).toContain(mockCorrector.telefono);
    

    const maxcorrLabel = fixture.debugElement.query(By.css('label[for="maxcorr"]'));
    expect(maxcorrLabel.nativeElement.textContent).toContain('Máx. Correcciones:');
    
    const maxcorrSpan = fixture.debugElement.query(By.css('#maxcorr'));
    expect(maxcorrSpan.nativeElement.textContent).toContain(mockCorrector.maximasCorrecciones.toString());
    
    
    // Por simplicidad comprobamos que el numero de materias que se muestran es igual
    // al numero de materias que le estamos añadiendo a traves del mockCorrector
    const materiaRows = fixture.debugElement.queryAll(By.css('tbody tr'));
    expect(materiaRows.length).toBe(mockCorrector.materias.length);

  });

});