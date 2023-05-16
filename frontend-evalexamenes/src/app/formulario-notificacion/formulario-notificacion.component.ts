import { TranslationWidth } from "@angular/common";
import { Component, Injectable } from "@angular/core";
import { FormsModule } from "@angular/forms";
import { NgbCalendar, NgbDate, NgbDateAdapter, NgbDateParserFormatter, NgbDatepickerI18n, NgbDatepickerModule, NgbDateStruct, NgbTimepickerModule } from "@ng-bootstrap/ng-bootstrap";
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
  imports: [NgbTimepickerModule, FormsModule, NgbDatepickerModule],
  providers: [I18n, {provide: NgbDatepickerI18n, useClass: DatePickerI18n},
              DateAdapter, {provide: NgbDateAdapter, useClass: DateAdapter},
              DateParserFormatter, {provide: NgbDateParserFormatter, useClass: DateParserFormatter}]
})

@Injectable({
  providedIn: 'root'
})
export class FormularioNotificacionComponent {
  notificacion: Notificacion = {plantillaAsunto: '', plantillaCuerpo: '', programacionEnvio: new Date, medios: []};
  hora: any = { hour: 0, minute: 0, second: 0};
  meridian: boolean = true;


  model?: NgbDateStruct;
  modelString?: string;

  constructor(private ngbCalendar: NgbCalendar, private dateAdapter: NgbDateAdapter<string>) {}

  get today() {
    return this.ngbCalendar.getToday();
  }

  get todayString() {
    return this.dateAdapter.toModel(this.ngbCalendar.getToday())!
  }

  get fechaYHora() {
    let fecha = `${this.model?.year}-${this.model?.month}-${this.model?.day}T${this.hora.hour}:${this.hora.minute}:${this.hora.second}.000Z`;
    return fecha;
  }


}
