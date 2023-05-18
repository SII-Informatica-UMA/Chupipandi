import { NgIf } from "@angular/common";
import { Component, EventEmitter, Injectable, Output } from "@angular/core";
import { FormsModule, ReactiveFormsModule } from "@angular/forms";
import { NgbCalendar, NgbDateAdapter, NgbDateParserFormatter, NgbDatepickerI18n, NgbDatepickerModule, NgbDateStruct, NgbTimepickerModule } from "@ng-bootstrap/ng-bootstrap";
import { Notificacion } from "../notificacion";

const I18N_VALUES = {
  es: {
    weekdays: ['L', 'M', 'X', 'J', 'V', 'S', 'D'],
    monthsFull: ['Enero', 'Febrero', 'Marzo', 'Abril', 'Mayo', 'Junio', 'Julio', 'Agosto', 'Septiembre', 'Octubre', 'Noviembre', 'Diciembre'],
    monthsShort:['Ene', 'Feb', 'Mar', 'Abr', 'May', 'Jun', 'Jul', 'Ago', 'Sep', 'Oct', 'Nov', 'Dic'],
    weekLabel: 'semana'
  }
};

@Injectable()
export class I18n {
  language = 'es';
}

@Injectable()
export class DatePickerI18n extends NgbDatepickerI18n {
  constructor(private _i18n: I18n) {
    super();
  }

  getWeekdayLabel(weekday: number): string {
      return I18N_VALUES[this._i18n.language as keyof typeof I18N_VALUES].weekdays[weekday - 1];
  }

  override getWeekLabel(): string {
      return I18N_VALUES[this._i18n.language as keyof typeof I18N_VALUES].weekLabel;
  }

  getMonthShortName(month: number): string {
      return I18N_VALUES[this._i18n.language as keyof typeof I18N_VALUES].monthsShort[month - 1];
  }

  getMonthFullName(month: number): string {
    return I18N_VALUES[this._i18n.language as keyof typeof I18N_VALUES].monthsFull[month - 1];
  }

  getDayAriaLabel(date: NgbDateStruct): string {
      return `${date.day}/${date.month}/${date.year}`;
  }
}
@Injectable()
export class DateAdapter extends NgbDateAdapter<string> {
  readonly DELIMITER = '-';

  fromModel(value: string | null): NgbDateStruct | null {
    if (value) {
      const date = value.split(this.DELIMITER);
      return {
        day: parseInt(date[0], 10),
        month: parseInt(date[1], 10),
        year: parseInt(date[2], 10)
      };
    }
    return null;
  }

  toModel(date: NgbDateStruct | null): string | null {
    return date ? date.day + this.DELIMITER + date.month + this.DELIMITER + date.year : null;
  }
}

@Injectable()
export class DateParserFormatter extends NgbDateParserFormatter {
  readonly DELIMITER = '/';

  parse(value: string): NgbDateStruct | null {
    if (value) {
      const date = value.split(this.DELIMITER);
      return {
        day: parseInt(date[0], 10),
        month: parseInt(date[1], 10),
        year: parseInt(date[2], 10)
      }
    };
    return null;
  }

  format(date: NgbDateStruct | null): string {
    return date ? date.day + this.DELIMITER + date.month + this.DELIMITER + date.year : '';
  }
}


@Component({
  selector: 'app-formulario-notificacion',
  templateUrl: './formulario-notificacion.component.html',
  styleUrls: ['./formulario-notificacion.component.css'],
  standalone: true,
  imports: [NgbTimepickerModule, FormsModule, NgbDatepickerModule, ReactiveFormsModule, NgIf],
  providers: [I18n, {provide: NgbDatepickerI18n, useClass: DatePickerI18n},
              DateAdapter, {provide: NgbDateAdapter, useClass: DateAdapter},
              DateParserFormatter, {provide: NgbDateParserFormatter, useClass: DateParserFormatter}]
})

@Injectable({
  providedIn: 'root'
})
export class FormularioNotificacionComponent {
  notificacion: Notificacion = {plantillaAsunto: '', plantillaCuerpo: '', programacionEnvio: '', medios: []};
  hora: any = { hour: 0, minute: 0, second: 0};
  emailChecked: boolean = false;
  smsChecked: boolean = false;
  maxLengthAsunto: number = 200;
  maxLengthCuerpo: number = 500;
  @Output() sendNotificacion: EventEmitter<any> = new EventEmitter<any>();

  fecha?: NgbDateStruct | null;
  fechaString?: string;

  constructor(private ngbCalendar: NgbCalendar, public dateAdapter: NgbDateAdapter<string>) {}

  get today() {
    return this.ngbCalendar.getToday();
  }

  get fechaYHora() {
    if (this.fechaString)
      this.fecha = this.dateAdapter.fromModel(this.fechaString);
    let addZeroDate = (toZero: number | undefined) => typeof toZero != 'undefined' && toZero < 10 ? '0' : '';
    let fechaString = `${this.fecha?.year}-${addZeroDate(this.fecha?.month) + this.fecha?.month}-${addZeroDate(this.fecha?.day) + this.fecha?.day}T${addZeroDate(this.hora.hour) + this.hora.hour}:${addZeroDate(this.hora.minute) + this.hora.minute}:${addZeroDate(this.hora.second) + this.hora.second}.000Z`;
    return fechaString;
  }

  changeMedios(checked: boolean, checkedValue: string): void {
    if (checked && !this.notificacion.medios.includes(checkedValue)) this.notificacion.medios.push(checkedValue)
    else if (!checked && this.notificacion.medios.includes(checkedValue)) this.notificacion.medios.splice(this.notificacion.medios.indexOf(checkedValue), 1);
  }

  checkHora(): void {
    let outputDate = new Date();
    let now = new Date();
    outputDate.setHours(this.hora.hour);
    outputDate.setMinutes(this.hora.minute);
    outputDate.setSeconds(this.hora.second);
    if (this.fecha?.day == this.today.day && this.fecha?.month == this.today.month && this.fecha?.year == this.today.year) {

      if (outputDate < now)
        this.hora = {hour: now.getHours(), minute: now.getMinutes(), second: now.getSeconds()};
    }
  }

  checkForm(): boolean {
    this.checkHora();
    this.notificacion.programacionEnvio = this.fechaYHora;
    return this.notificacion.plantillaAsunto != '' && this.notificacion.plantillaCuerpo != '' && this.fechaString != '' && this.notificacion.medios.length != 0;
  }

  getDate(dateToConvert: string): string {
    let regexpDate = /[\-:.TZ]/g
    let elements = dateToConvert.split(regexpDate);

    return `${elements[2]}/${elements[1]}/${elements[0]} ${elements[3]}:${elements[4]}:${elements[5]}`;
  }
}
