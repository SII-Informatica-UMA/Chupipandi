import { Component, ViewChild } from '@angular/core';
import { NgbAlert } from '@ng-bootstrap/ng-bootstrap';
import { DetalleCorreccionComponent } from './detalle-correccion/detalle-correccion.component';
import { Estudiante } from './estudiante';
import { Examen } from './examen';
import { FormularioNotasComponent } from './formulario-notas/formulario-notas.component';
import { FormularioNotificacionComponent } from './formulario-notificacion/formulario-notificacion.component';
import { NotasService } from './notas.service';
import { Notificacion } from './notificacion';
import { NotificacionService } from './notificacion.service';

@Component({
  selector: 'app-root',
  templateUrl: './app.component.html',
  styleUrls: ['./app.component.css'],
})
export class AppComponent {
  title = 'frontend-ms_evalexamenes';
  notas?: Examen[];
  dni?: string;
  apellido?: string;
  notFoundAlertClosed: boolean = true;
  notFoundTimer: any;
  activeNav: number = 1;
  notificacion?: Notificacion;

  @ViewChild('notFoundAlert', { static: false }) notFoundAlert?: NgbAlert;
  @ViewChild('notiError', { static: false }) notiError?: NgbAlert;
  @ViewChild('formNotas', { static: false }) formularioNotas: FormularioNotasComponent;
  @ViewChild('formNoti', { static: false }) formularioNotificacion?: FormularioNotificacionComponent;
  @ViewChild('detalleCorreccion', { static: false }) detalleCorrecion?: DetalleCorreccionComponent;

  constructor(private notificacionService: NotificacionService, private notasService: NotasService) {
    this.formularioNotas = new FormularioNotasComponent;
    this.notFoundTimer = setTimeout(() => null, 5000);
  }

  getNotas(): void {
    clearTimeout(this.notFoundTimer);
    let estudiante: Estudiante = this.formularioNotas.estudiante;
    this.dni = estudiante.dni;
    this.apellido = estudiante.apellido;
    this.notasService.getNotas(this.dni, this.apellido)
      .subscribe({
        next: (notas) => {
          this.notFoundAlert?.close();
          this.notFoundAlertClosed = true;
          this.notas = notas;
        },
        error: () => {
          this.notas = [];
          this.notFoundAlertClosed = false;
          this.notFoundTimer = setTimeout(() => this.notFoundAlert?.close(), 5000);
        }
      });
    this.formularioNotas.estudiante = {dni: '', apellido: ''};
  }

  restartNotificacion(): void {
    if (this.formularioNotificacion) {
      this.formularioNotificacion.notificacion = {plantillaAsunto: '', plantillaCuerpo: '', programacionEnvio: '', medios: []};
      this.formularioNotificacion.emailChecked = false;
      this.formularioNotificacion.smsChecked = false;
      this.formularioNotificacion.fecha = undefined;
      this.formularioNotificacion.fechaString = '';
      this.formularioNotificacion.hora = {hour: 0, minute: 0, second: 0};
    }
  }

  sendNotificacion(): void {
    if (this.formularioNotificacion) {
      this.notificacion = this.formularioNotificacion.notificacion;
      this.notificacionService.postNotificacion(this.notificacion)
        .subscribe({
          error: (error) => console.log(`ERROR POST NOTIFICACIÓN: ${error}`)
        });
    }
    this.restartNotificacion();
  }

  actualizaComponentes(): void {
    if (this.detalleCorrecion) {
      this.detalleCorrecion.getCorreccion();
      this.detalleCorrecion.sort();
    }
  }
}
