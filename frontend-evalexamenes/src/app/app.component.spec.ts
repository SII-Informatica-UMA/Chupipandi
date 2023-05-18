import { HttpClientTestingModule } from '@angular/common/http/testing';
import { ComponentFixture, TestBed } from '@angular/core/testing';
import { By } from '@angular/platform-browser';
import { NgbNavModule } from '@ng-bootstrap/ng-bootstrap';
import { AppComponent } from './app.component';
import { DetalleCorreccionComponent } from './detalle-correccion/detalle-correccion.component';
import { DetalleNotasComponent } from './detalle-notas/detalle-notas.component';
import { Estudiante } from './estudiante';
import { FormularioNotasComponent } from './formulario-notas/formulario-notas.component';
import { FormularioNotificacionComponent } from './formulario-notificacion/formulario-notificacion.component';

describe('AppComponent', () => {
  let fixture: ComponentFixture<AppComponent>;
  let app: AppComponent;
  beforeEach(async () => {
    await TestBed.configureTestingModule({
      declarations: [
        AppComponent
      ],
      imports: [
        HttpClientTestingModule,
        NgbNavModule
      ]
    }).compileComponents().then(() => {
      fixture = TestBed.createComponent(AppComponent);
      app = fixture.componentInstance;
    });
  });

  it('should create the app', () => {
    expect(app).toBeTruthy();
  });

  it(`should have as title 'frontend-ms_evalexamenes'`, () => {
    expect(app.title).toEqual('frontend-ms_evalexamenes');
  });

  it(`should change to navOne when clicked`, (done: DoneFn) => {
    const compiled = fixture.nativeElement as HTMLElement;
    const navOne = compiled.querySelector('#navOne') as HTMLInputElement;

    fixture.whenStable().then(() =>{
      navOne.click();
      fixture.detectChanges();
      expect(app.activeNav).toEqual(1);
      done();
    })
  });

  it(`should change to navTwo when clicked`, (done: DoneFn) => {
    const compiled = fixture.nativeElement as HTMLElement;
    const navTwo = compiled.querySelector('#navTwo') as HTMLInputElement;

    fixture.whenStable().then(() =>{
      navTwo.click();
      fixture.detectChanges();
      expect(app.activeNav).toEqual(1);
      done();
    })
  });

  it(`should change to navThree when clicked`, (done: DoneFn) => {
    const compiled = fixture.nativeElement as HTMLElement;
    const navThree = compiled.querySelector('#navThree') as HTMLInputElement;

    fixture.whenStable().then(() =>{
      navThree.click();
      fixture.detectChanges();
      expect(app.activeNav).toEqual(1);
      done();
    })
  });

  it(`should change to navFour when clicked`, (done: DoneFn) => {
    const compiled = fixture.nativeElement as HTMLElement;
    const navFour = compiled.querySelector('#navFour') as HTMLInputElement;

    fixture.whenStable().then(() =>{
      navFour.click();
      fixture.detectChanges();
      expect(app.activeNav).toEqual(1);
      done();
    });
  });

  // it(`#notas.length should be 0 when searching a non-valid student`, (done: DoneFn) => {
  //   spyOn(app, 'getNotas');

  //   const estudiante: Estudiante = {dni: '', apellido: ''};
  //   console.log(app.formularioNotas);
  //   app.formularioNotas.getNotas.emit(estudiante);
  //   fixture.detectChanges();

  //   fixture.whenStable().then(() => {
  //     fixture.detectChanges();
  //     expect(app.getNotas).toHaveBeenCalled();
  //     expect(app.notas?.length).toEqual(0);
  //     done();
  //   })
  // })
});
