import { Component, OnInit } from '@angular/core';
import { Examen } from './examen';
import { Notificacion } from './notificacion';
import { ExamenService } from './examen.service';
import { NotasService } from './notas.service';
import { Correcion } from './correcion';
import { NgbModal } from '@ng-bootstrap/ng-bootstrap'

@Component({
  selector: 'app-root',
  templateUrl: './app.component.html',
  styleUrls: ['./app.component.css']
})
export class AppComponent {
  title = 'frontend-ms_evalexamenes';
  notas?: Examen[];
  correcion?: Correcion;
  dni: string = "05981804X";
  apellido: string = "González";

  constructor(private examenService: ExamenService, private notasService: NotasService, private modalService: NgbModal) { }

  getNotas(): void {
    this.notasService.getNotas(this.dni, this.apellido)
      .subscribe(notas => this.notas = notas);
  }

  getCorrecion(): void {
    this.examenService.getCorrecion()
      .subscribe(correcion => this.correcion = correcion);
  }
}
