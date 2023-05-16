import { Time } from "@angular/common";
import { Component, Injectable } from "@angular/core";
import { FormsModule } from "@angular/forms";
import { NgbCalendar, NgbDateAdapter, NgbDateParserFormatter, NgbDatepickerModule, NgbDateStruct, NgbTimepicker, NgbTimepickerModule } from "@ng-bootstrap/ng-bootstrap";
import { Notificacion } from "../notificacion";
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
  imports: [NgbTimepickerModule, FormsModule, NgbDatepickerModule]
})

@Injectable({
  providedIn: 'root'
})
export class FormularioNotificacionComponent {
  notificacion: Notificacion = {plantillaAsunto: '', plantillaCuerpo: '', programacionEnvio: new Date, medios: []};
  fecha?: NgbDateStruct;
  hora: any = { hour: 13, minute: 30 };
  meridian: boolean = true;

  model1?: string;
  model2?: string;

  constructor(private ngbCalendar: NgbCalendar, private dateAdapter: NgbDateAdapter<string>) {}

  get today() {
    return this.dateAdapter.toModel(this.ngbCalendar.getToday())!;
  }
}
