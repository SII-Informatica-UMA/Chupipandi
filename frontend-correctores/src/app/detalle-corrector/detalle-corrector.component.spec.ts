import { ComponentFixture, TestBed } from '@angular/core/testing';
import { HttpClientTestingModule } from '@angular/common/http/testing';
import { By } from '@angular/platform-browser';
import { Corrector, CorrectorNuevo, Materia } from '../model/interfaces';
import { DetalleCorrectorComponent } from './detalle-corrector.component';
import { NgbModal, NgbModalRef } from '@ng-bootstrap/ng-bootstrap';
import { FormularioCorrectorComponent } from '../formulario-corrector/formulario-corrector.component';
import { CorrectorService } from '../service/corrector.service';

describe('DetalleCorrectorComponent', () => {
  let fixture: ComponentFixture<DetalleCorrectorComponent>;
  let component: DetalleCorrectorComponent;
  let ventana: NgbModalRef;
  let correctorService: CorrectorService;
  let modalService: NgbModal;

  beforeEach(async () => {
    await TestBed.configureTestingModule({
      imports: [HttpClientTestingModule],
      declarations: [DetalleCorrectorComponent],
      providers: [CorrectorService, NgbModal]
    }).compileComponents();
  });

  beforeEach(() => {
    fixture = TestBed.createComponent(DetalleCorrectorComponent);
    component = fixture.componentInstance;
    correctorService = TestBed.inject(CorrectorService);
    modalService = TestBed.inject(NgbModal);
  });

  it('should create the component', () => {
    expect(component).toBeTruthy();
  });

  // it('should emit correctorEditado event when editarCorrector is called', () => {
  //   const mockCorrector: Corrector = { id: 1n, identificadorUsuario: 1n, telefono: '+34 954-362-837',
  //     maximasCorrecciones: 21, materias: [{ idMateria: 1n, idConvocatoria: 1n }] };
  //   const mockMateria: Materia = { materia: 1n };
  //   const mockCorrectorNuevo: CorrectorNuevo = { identificadorUsuario: 1n,
  //     identificadorConvocatoria: 1n, telefono: '+34 954-362-837', materia: mockMateria, 
  //     maximasCorrecciones: 21 };
    
  //   ventana = modalService.open(FormularioCorrectorComponent);
  //   ventana.componentInstance.accion = "Editar";
  //   ventana.componentInstance.createFormGroup();
  //   // spyOn(modalService, 'open').and.returnValue({ result: Promise.resolve(mockCorrectorNuevo) });
    
  //   spyOn(ventana.componentInstance.correctorEditado, 'emit');

  //   // component.corrector = mockCorrector;
  //   // component.editarCorrector();

  //   fixture.detectChanges();

  //   const editarButton = fixture.debugElement.query(By.css('.btn-outline-primary'));
  //   editarButton.nativeElement.click();

  //   fixture.whenStable().then(() => {
  //     expect(modalService.open).toHaveBeenCalledWith(FormularioCorrectorComponent);
  //     expect(component.correctorEditado.emit).toHaveBeenCalledWith(mockCorrectorNuevo);
  //   });
  // });

  it('should emit correctorEliminado event when eliminarCorrector is called', () => {
    const mockCorrector: Corrector = { id: 1n, identificadorUsuario: 1n, telefono: '+34 954-362-837',
      maximasCorrecciones: 21, materias: [{ idMateria: 1n, idConvocatoria: 1n }] };

    spyOn(component.correctorEliminado, 'emit');

    component.corrector = mockCorrector;
    component.eliminarCorrector();

    expect(component.correctorEliminado.emit).toHaveBeenCalledWith(mockCorrector.id);
  });

  it('should display corrector details', () => {
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
    
    
    const materiaRows = fixture.debugElement.queryAll(By.css('tbody tr'));
    expect(materiaRows.length).toBe(mockCorrector.materias.length);

  });

});