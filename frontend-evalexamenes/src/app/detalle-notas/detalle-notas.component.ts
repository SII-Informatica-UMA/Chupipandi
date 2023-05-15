import { Component, Input } from "@angular/core";
import { Examen } from "../examen";
import { ExamenService } from "../examen.service";

@Component({
  selector: 'app-detalle-notas',
  templateUrl: './detalle-notas.component.html',
  styleUrls: ['./detalle-notas.component.css']
})
export class DetalleNotasComponent {
  @Input() notas?: Examen [];

  constructor(private examenService: ExamenService) { }
}
