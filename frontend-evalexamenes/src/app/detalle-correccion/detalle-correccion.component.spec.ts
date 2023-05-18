import { ComponentFixture, TestBed } from '@angular/core/testing';
import { of, throwError } from 'rxjs';
import { DetalleCorreccionComponent } from './detalle-correccion.component';
import { ExamenService } from '../examen.service';
import { Examen } from '../examen';
import { HttpClientTestingModule } from '@angular/common/http/testing';


describe('DetalleCorreccionComponent', () => {
	let fixture: ComponentFixture<DetalleCorreccionComponent>;
	let app: DetalleCorreccionComponent;
	let examenService: ExamenService;

  beforeEach(async () => {
    await TestBed.configureTestingModule({
      declarations: [],
	  imports: [HttpClientTestingModule],
      providers: [ExamenService],
    }).compileComponents();
  });

  beforeEach(() => {
    fixture = TestBed.createComponent(DetalleCorreccionComponent);
    app = fixture.componentInstance;
    examenService = TestBed.inject(ExamenService);
  });

  it('should create', () => {
    expect(app).toBeTruthy();
  });

  it('should call getCorreccion on app initialization', () => {
    spyOn(app, 'getCorreccion');
	app.getCorreccion();
    fixture.detectChanges();
    expect(app.getCorreccion).toHaveBeenCalled();
  });

  it('should populate corregidos and pendientes arrays with the response from examenService', () => {
    const correccion = {
      corregidos: [1, 2, 3],
      pendientes: [4, 5, 6]
    };
    const examen1: Examen = { id: 1, materia: 123, codigoAlumno: 456, nota: 8.5 };
    const examen2: Examen = { id: 2, materia: 789, codigoAlumno: 101, nota: 6.2 };
    const examen3: Examen = { id: 3, materia: 654, codigoAlumno: 321, nota: 9.0 };
    const examen4: Examen = { id: 4, materia: 987, codigoAlumno: 654, nota: 7.8 };
    const examen5: Examen = { id: 5, materia: 321, codigoAlumno: 987, nota: 6.9 };
    const examen6: Examen = { id: 6, materia: 654, codigoAlumno: 123, nota: 8.2 };


    spyOn(examenService, 'getCorreccion').and.returnValue(of(correccion));
    spyOn(examenService, 'getExamen').and.returnValues(
      of(examen1),
      of(examen2),
      of(examen3),
      of(examen4),
      of(examen5),
      of(examen6)
    );

    app.getCorreccion();

    expect(examenService.getCorreccion).toHaveBeenCalled();
    expect(examenService.getExamen).toHaveBeenCalledTimes(6);
    expect(app.corregidos).toEqual([examen1, examen2, examen3]);
    expect(app.pendientes).toEqual([examen4, examen5, examen6]);
  });

  it('should handle error in getCorreccion method', () => {
    spyOn(examenService, 'getCorreccion').and.returnValue(throwError('error'));

    app.getCorreccion();

    expect(examenService.getCorreccion).toHaveBeenCalled();
    expect(app.corregidos).toEqual([]);
    expect(app.pendientes).toEqual([]);
  });
});
