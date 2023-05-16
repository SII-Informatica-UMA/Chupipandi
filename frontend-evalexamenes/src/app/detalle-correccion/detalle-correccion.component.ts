import { NgFor, NgIf } from "@angular/common";
import { Component } from "@angular/core";
import { NgbAccordionModule } from "@ng-bootstrap/ng-bootstrap";
import { Examen } from "../examen";
import { ExamenService } from "../examen.service";

@Component({
  selector: 'app-detalle-correccion',
  templateUrl: './detalle-correccion.component.html',
  styleUrls: ['./detalle-correccion.component.css'],
  imports: [NgbAccordionModule, NgFor, NgIf],
  standalone: true
})
export class DetalleCorreccionComponent {
  corregidos: Examen[];
  pendientes: Examen[];

  constructor(private examenService: ExamenService) {
    this.corregidos = [];
    this.pendientes = [];
    this.getCorreccion();
  }

  getCorreccion(): void {
    this.examenService.getCorreccion()
      .subscribe({
        next: (correccion) => {
          for (let id of correccion.corregidos) {
            this.examenService.getExamen(id)
              .subscribe({
                next: (examen) => this.corregidos?.push(examen),
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
}
