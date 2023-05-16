import { Component, ViewChild } from '@angular/core';
import { NgbAlert, NgbModal } from '@ng-bootstrap/ng-bootstrap';
import { Estudiante } from './estudiante';
import { Examen } from './examen';
import { ExamenService } from './examen.service';
import { FormularioNotasComponent } from './formulario-notas/formulario-notas.component';
import { FormularioNotificacionComponent } from './formulario-notificacion/formulario-notificacion.component';
import { NotasService } from './notas.service';
import { NotificacionService } from './notificacion.service';

@Component({
  selector: 'app-root',
  templateUrl: './app.component.html',
  styleUrls: ['./app.component.css']
})
export class AppComponent {
  notas?: Examen[];
  dni?: string;
  apellido?: string;
  notFoundAlertClosed: boolean = true;
  notFoundTimer: any;
  activeNav: number = 1;


  @ViewChild('notFoundAlert', { static: false }) notFoundAlert?: NgbAlert;
  @ViewChild('formNotas', { static: false }) formularioNotas: FormularioNotasComponent;
  @ViewChild('formNoti', { static: false }) formularioNotificacion?: FormularioNotificacionComponent;

  constructor(private notificacionService: NotificacionService, private examenService: ExamenService, private notasService: NotasService, private modalService: NgbModal) {
    this.formularioNotas = new FormularioNotasComponent;
    this.notFoundTimer = setTimeout(() => null, 5000);
  }

  getNotas(): void {
    clearTimeout(this.notFoundTimer);
    let estudiante: Estudiante = this.formularioNotas.estudiante;
    console.log(estudiante)
    this.dni = estudiante.dni;
    this.apellido = estudiante.apellido;
    this.notasService.getNotas(this.dni, this.apellido)
      .subscribe({
        next: (notas) => this.notas = notas,
        error: () => this.notas = []
      });
    this.notFoundAlertClosed = !(this.notas?.length == 0);
    if (!this.notFoundAlertClosed) this.notFoundTimer = setTimeout(() => this.notFoundAlert?.close(), 5000);
    else {
      this.notFoundAlert?.close();
      this.notFoundAlertClosed = true
    }
    this.formularioNotas.estudiante = {dni: '', apellido: ''};
  }

  restartNotificacion(): void {
    if (this.formularioNotificacion)
      this.formularioNotificacion.notificacion = {plantillaAsunto: '', plantillaCuerpo: '', programacionEnvio: new Date, medios: []};
  }

  sendNotificacion(): void {
    if (this.formularioNotificacion)
      this.notificacionService.postNotificacion(this.formularioNotificacion.notificacion)
        .subscribe({
          error: (error) => console.log(error)
        })
  }

  private actualizaComponentes(): void {

  }
}
