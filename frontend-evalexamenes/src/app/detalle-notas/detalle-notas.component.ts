import { NgFor, NgIf } from "@angular/common";
import { Component, Input } from "@angular/core";
import { NgbAccordionModule } from "@ng-bootstrap/ng-bootstrap";
import { Examen } from "../examen";

@Component({
  selector: 'app-detalle-notas',
  templateUrl: './detalle-notas.component.html',
  styleUrls: ['./detalle-notas.component.css'],
  imports: [NgbAccordionModule, NgFor, NgIf],
  standalone: true
})
export class DetalleNotasComponent {
  @Input() notas?: Examen [];
}
