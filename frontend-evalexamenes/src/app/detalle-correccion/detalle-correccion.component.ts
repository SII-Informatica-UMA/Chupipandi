import { NgFor, NgIf } from "@angular/common";
import { Component, EventEmitter } from "@angular/core";
import { FormsModule, NgModel } from "@angular/forms";
import { NgbAccordionModule } from "@ng-bootstrap/ng-bootstrap";
import { Examen } from "../examen";
import { ExamenService } from "../examen.service";

@Component({
  selector: 'app-detalle-correccion',
  templateUrl: './detalle-correccion.component.html',
  styleUrls: ['./detalle-correccion.component.css'],
  imports: [NgbAccordionModule, NgFor, NgIf, FormsModule],
  standalone: true
})
export class DetalleCorreccionComponent {
  corregidos: Examen[];
  pendientes: Examen[];
  examenElegido?: Examen;
  modificandoNota: boolean = false;
  modifiedCorrecion: EventEmitter<any> = new EventEmitter<any>();

  constructor(private examenService: ExamenService) {
    this.corregidos = [];
    this.pendientes = [];
    this.getCorreccion();
  }

  getCorreccion(): void {
    this.corregidos = [];
    this.pendientes = [];
    this.examenService.getCorreccion()
      .subscribe({
        next: (correccion) => {
          for (let id of correccion.corregidos) {
            this.examenService.getExamen(id)
              .subscribe({
                next: (examen) => {
                    this.corregidos?.push(examen);
                },
                error: () => null
              });
          }
          for (let id of correccion.pendientes) {
            this.examenService.getExamen(id)
              .subscribe({
                next: (examen) => this.pendientes?.push(examen),
                error: () => null
              });
          }
        },
        error: () => {
          this.corregidos = [],
          this.pendientes = []
        }
      });
  }

  modificarNota(): void {
    if (this.modificandoNota) {
      if (this.examenElegido) {
        if (this.examenElegido.nota < 0)
          this.examenElegido.nota = 0;
        this.examenService.putExamen(this.examenElegido)
            .subscribe({
              next: (examen) => this.examenElegido = examen,
              error: () => null
            })
        if (!this.corregidos.includes(this.examenElegido))
            this.corregidos.push(this.examenElegido);
        if (this.pendientes.includes(this.examenElegido))
            this.pendientes.splice(this.pendientes.indexOf(this.examenElegido), 1);
        this.sort();
      }
    }
    this.modificandoNota = !this.modificandoNota;
    this.modifiedCorrecion.emit();
  }

  sort(): void {
    this.corregidos.sort((ex1, ex2) => ex1.id - ex2.id);
    this.pendientes.sort((ex1, ex2) => ex1.id - ex2.id);
  }
}
