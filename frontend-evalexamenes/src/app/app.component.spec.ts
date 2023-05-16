import { TestBed } from '@angular/core/testing';
import { AppComponent } from './app.component';
import { HttpClientModule } from '@angular/common/http';
import { NgbAlertModule, NgbNavModule } from '@ng-bootstrap/ng-bootstrap';


describe('AppComponent', () => {
  beforeEach(async () => {
    await TestBed.configureTestingModule({
      imports: [HttpClientModule, NgbAlertModule, NgbNavModule],
      declarations: [
        AppComponent
      ],
    }).compileComponents();
  });

  it('should create the AppComponent', () => {
    const fixture = TestBed.createComponent(AppComponent);
    const app = fixture.componentInstance;
    expect(app).toBeTruthy();
  });

  // it(`should have as title 'frontend-ms_evalexamenes'`, () => {
  //   const fixture = TestBed.createComponent(AppComponent);
  //   const app = fixture.componentInstance;
  //   expect(app.title).toEqual('frontend-ms_evalexamenes');
  // });

  it('should render title', () => {
    const fixture = TestBed.createComponent(AppComponent);
    fixture.detectChanges();
    const compiled = fixture.nativeElement as HTMLElement;
    expect(compiled.querySelector('.row h2')?.textContent).toContain('Elige una opción');
  });

  it(`should call getNotas()`, () => {
    let spy: any;
    const fixture = TestBed.createComponent(AppComponent);
    const app = fixture.componentInstance;
    spy = spyOn(app, 'getNotas');
    app.getNotas();
    expect(spy).toHaveBeenCalled();
  });

  it(`should call restartNotificacion()`, () => {
    let spy: any;
    const fixture = TestBed.createComponent(AppComponent);
    const app = fixture.componentInstance;
    spy = spyOn(app, 'restartNotificacion');
    app.restartNotificacion();
    expect(spy).toHaveBeenCalled();
  });

  it(`should call sendNotificacion()`, () => {
    let spy: any;
    const fixture = TestBed.createComponent(AppComponent);
    const app = fixture.componentInstance;
    spy = spyOn(app, 'sendNotificacion');
    app.sendNotificacion();
    expect(spy).toHaveBeenCalled();
  });
});
