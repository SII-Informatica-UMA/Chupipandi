import { HttpClientTestingModule } from '@angular/common/http/testing';
import { ComponentFixture, TestBed, waitForAsync } from '@angular/core/testing';
import { NgbNavModule } from '@ng-bootstrap/ng-bootstrap';
import { AppComponent } from './app.component';
import { Notificacion } from './notificacion';


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

  it(`should change to navOne when clicked`, waitForAsync(() => {
    const compiled = fixture.nativeElement as HTMLElement;
    const navOne = compiled.querySelector('#navOne') as HTMLInputElement;

    navOne.click();
    fixture.detectChanges();
    fixture.whenStable().then(() => {
      expect(app.activeNav).toEqual(1);
    })
  }));

  it(`should change to navTwo when clicked`, waitForAsync(() => {
    const compiled = fixture.nativeElement as HTMLElement;
    const navTwo = compiled.querySelector('#navTwo') as HTMLInputElement;

    navTwo.click();
    fixture.detectChanges();
    fixture.whenStable().then(() => {
      expect(app.activeNav).toEqual(2);
    })
  }));

  it(`should change to navThree when clicked`, waitForAsync(() => {
    const compiled = fixture.nativeElement as HTMLElement;
    const navThree = compiled.querySelector('#navThree') as HTMLInputElement;

    navThree.click();
    fixture.detectChanges();
    fixture.whenStable().then(() => {
      expect(app.activeNav).toEqual(3);
    })
  }));

  it('should correctly restart a notification', waitForAsync(() => {
    let notificacion: Notificacion = {plantillaAsunto: '', plantillaCuerpo: '', programacionEnvio: '', medios: []};
    let emailChecked: boolean = false;
    let smsChecked: boolean = false;
    let fecha: any = undefined;
    let fechaString: string = '';
    let hora: any = {hour: 0, minute: 0, second: 0};
    spyOn(app, 'restartNotificacion');
    app.restartNotificacion();
    fixture.detectChanges();

    fixture.whenStable().then(() => {
      expect(app.formularioNotificacion?.notificacion).toEqual(notificacion);
      expect(app.formularioNotificacion?.emailChecked).toEqual(emailChecked);
      expect(app.formularioNotificacion?.smsChecked).toEqual(smsChecked);
      expect(app.formularioNotificacion?.fecha).toBe(fecha);
      expect(app.formularioNotificacion?.fechaString).toEqual(fechaString);
      expect(app.formularioNotificacion?.hora).toEqual(hora);
      expect(app.restartNotificacion).toHaveBeenCalled();
    })
  }))


});
