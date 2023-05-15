import { Component, ViewChild } from '@angular/core';
import { TitleStrategy } from '@angular/router';
import { NgbAlert, NgbModal } from '@ng-bootstrap/ng-bootstrap';
import { Correccion } from './correccion';
import { Estudiante } from './estudiante';
import { Examen } from './examen';
import { ExamenService } from './examen.service';
import { FormularioNotasComponent } from './formulario-notas/formulario-notas.component';
import { NotasService } from './notas.service';

@Component({
  selector: 'app-root',
  templateUrl: './app.component.html',
  styleUrls: ['./app.component.css']
})
export class AppComponent {
  notas?: Examen[];
  correccion?: Correccion;
  dni?: string;
  apellido?: string;
  alertClosed: boolean = true;

  @ViewChild('notFoundAlert', { static: false }) notFoundAlert?: NgbAlert;

  constructor(private examenService: ExamenService, private notasService: NotasService, private modalService: NgbModal) {}

  getNotas(): void {
    let ref = this.modalService.open(FormularioNotasComponent);
    ref.componentInstance.estudiante = {dni: '', apellido: ''};
    ref.result.then((estudiante: Estudiante) => {
      this.dni = estudiante.dni;
      this.apellido = estudiante.apellido;
      this.notasService.getNotas(this.dni, this.apellido)
        .subscribe({
          next: (notas) => this.notas = notas,
          error: () => this.notas = [],
          complete: () => console.log('Complete')
        });
      this.alertClosed = !(this.notas?.length == 0);
      if (!this.alertClosed) setTimeout(() => this.notFoundAlert?.close(), 5000);
      else {
        this.notFoundAlert?.close();
        this.alertClosed = true
      }
    })
  }

  getCorrecion(): void {
    this.examenService.getCorrecion()
      .subscribe(correccion => this.correccion = correccion);
  }
}
