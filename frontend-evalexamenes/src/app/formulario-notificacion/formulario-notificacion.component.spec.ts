import { HttpClientTestingModule } from '@angular/common/http/testing';
import { ComponentFixture, TestBed } from '@angular/core/testing';
import { NgbDate } from "@ng-bootstrap/ng-bootstrap";
import { FormularioNotificacionComponent } from './formulario-notificacion.component';

describe('AppComponent', () => {
	let fixture: ComponentFixture<FormularioNotificacionComponent>;
	let app: FormularioNotificacionComponent;
	beforeEach(async () => {
		await TestBed.configureTestingModule({
		declarations: [
		],
		imports: [
			FormularioNotificacionComponent,
			HttpClientTestingModule,
		]
		}).compileComponents().then(() => {
		fixture = TestBed.createComponent(FormularioNotificacionComponent);
		app = fixture.componentInstance;
		});
	});

	it('should create the app', () => {
		expect(app).toBeTruthy();
	});

	it(`should call get today() and return todays date`, () => {
		expect(app.today).toEqual(new NgbDate(new Date().getFullYear(), new Date().getMonth() + 1, new Date().getDate()));
	});

	it(`should return a date string with format 'DD-MM-YYYYTHH:MM:SS.SSSZ'`, () => {
		app.fechaString = '2021-05-05';
		expect(app.fechaYHora).toEqual('5-05-2021T00:00:00.000Z');
	});
	
	it(`should include a new 'medio'`, () => {
		app.notificacion.medios = []
		app.changeMedios(true, 'Mail');
		expect(app.notificacion.medios).toEqual(['Mail']);
	});

	it(`should not include a new 'medio'`, () => {
		app.notificacion.medios = ['Mail']
		app.changeMedios(true, 'Mail');
		expect(app.notificacion.medios).toEqual(['Mail']);
	});
	
	it(`should not include nor delete a new 'medio'`, () => {
		app.notificacion.medios = []
		app.changeMedios(false, 'Mail');
		expect(app.notificacion.medios).toEqual([]);
	});
	
	it(`should exclude new 'medio'`, () => {
		app.notificacion.medios = ['Mail']
		app.changeMedios(false, 'Mail');
		expect(app.notificacion.medios).toEqual([]);
	});
	
	it(`should update the time if the date is today and the time is earlier than the current time`, () => {
		const now = new Date();

		app.fecha = { day: now.getDate(), month: now.getMonth() + 1, year: now.getFullYear() };
		app.hora = { hour: now.getHours() - 2, minute: now.getMinutes(), second: now.getSeconds() };

		app.checkHora();

		expect(app.hora.hour).toBe(now.getHours());
		expect(app.hora.minute).toBe(now.getMinutes());
		expect(app.hora.second).toBe(now.getSeconds());

	});
	
	it(`should not update the time if the date is not today`, () => {
		const now = new Date();

		app.fecha = { day: now.getDate() + 1, month: now.getMonth() + 1, year: now.getFullYear() };
		app.hora = { hour: now.getHours() - 2, minute: now.getMinutes(), second: now.getSeconds() };

		app.checkHora();

		expect(app.hora.hour).toBe(now.getHours() - 2);
		expect(app.hora.minute).toBe(now.getMinutes());
		expect(app.hora.second).toBe(now.getSeconds());

	});

	it('should not update the time if the date is today but the time is the same or later than the current time', () => {
		const now = new Date();
	
		app.fecha = { day: now.getDate(), month: now.getMonth() + 1, year: now.getFullYear() };
		app.hora = { hour: now.getHours() + 2, minute: now.getMinutes(), second: now.getSeconds() };
	
		app.checkHora();
		
		expect(app.hora.hour).toBe(now.getHours() + 2);
		expect(app.hora.minute).toBe(now.getMinutes());
		expect(app.hora.second).toBe(now.getSeconds());
	  });
	

	it(`should run getDate(date) and format the date`, () => {
		expect(app.getDate('2023-05-18T10:05:48.731Z')).toEqual('18/05/2023 10:05:48');	
	});

	it(`should checkForm() when all notificacion fields are filled and return true`, () => {
		app.notificacion.plantillaAsunto = 'Asunto';
		app.notificacion.plantillaCuerpo = 'Cuerpo';
		app.notificacion.medios = ['Mail'];
		app.fechaString = '2021-05-05';

		expect(app.checkForm()).toEqual(true);
	});

	it(`should checkForm() when notificacion plantillaAsunto is empty and return false`, () => {
		app.notificacion.plantillaAsunto = '';
		app.notificacion.plantillaCuerpo = 'Cuerpo';
		app.notificacion.medios = ['Mail'];
		app.fechaString = '2021-05-05';

		expect(app.checkForm()).toEqual(false);
	});

	it(`should checkForm() when notificacion plantillaCuerpo is empty and return false`, () => {
		app.notificacion.plantillaAsunto = 'Asunto';
		app.notificacion.plantillaCuerpo = '';
		app.notificacion.medios = ['Mail'];
		app.fechaString = '2021-05-05';

		expect(app.checkForm()).toEqual(false);
	});

	it(`should checkForm() when notificacion medios is empty and return false`, () => {
		app.notificacion.plantillaAsunto = 'Asunto';
		app.notificacion.plantillaCuerpo = 'Cuerpo';
		app.notificacion.medios = [];
		app.fechaString = '2021-05-05';

		expect(app.checkForm()).toEqual(false);
	});

	it(`should checkForm() when notificacion fechaString is empty and return false`, () => {
		app.notificacion.plantillaAsunto = 'Asunto';
		app.notificacion.plantillaCuerpo = 'Cuerpo';
		app.notificacion.medios = ['Mail'];
		app.fechaString = '';

		expect(app.checkForm()).toEqual(false);
	});
	
});
