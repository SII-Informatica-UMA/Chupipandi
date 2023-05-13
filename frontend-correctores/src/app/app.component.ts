import { Component, Input, OnInit } from '@angular/core';
import { Corrector, CorrectorNuevo } from './interfaces';
import { CorrectorService } from './corrector.service';
import { NgbModal } from '@ng-bootstrap/ng-bootstrap';
import { FormularioCorrectorComponent } from './formulario-corrector/formulario-corrector.component';

@Component({
  selector: 'app-root',
  templateUrl: './app.component.html',
  styleUrls: ['./app.component.css']
})
export class AppComponent implements OnInit {
  title = 'frontend-correctores';
  correctores: Corrector[] = [];
  correctorElegido?: Corrector;
  idConvBusq?: bigint;
  busquedaFallida: boolean = false;

  constructor(private correctorService: CorrectorService, private modalService: NgbModal) { }

  ngOnInit(): void {
    this.actualizaCorrectores();
  }

  private actualizaCorrectores(idUsuario?: bigint): void {
    this.correctorService.getCorrectores()
      .subscribe(correctores => {
        this.correctores = correctores;
        if (idUsuario) {
          this.correctorElegido = this.correctores.find(corrector => corrector.identificadorUsuario === idUsuario);
        }
      });
  }

  elegirCorrector(corrector: Corrector): void {
    this.correctorElegido = corrector;
  }

  aniadirCorrector(): void {
    let ref = this.modalService.open(FormularioCorrectorComponent);
    ref.componentInstance.accion = "Añadir";
    ref.componentInstance.corrector = { identificadorUsuario: 0, identificadorConvocatoria: undefined, telefono: '', materia: '', maximasCorrecciones: 0 };
    ref.result.then((corrector: CorrectorNuevo) => {
      this.correctorService.addCorrector(corrector)
        .subscribe(c => {
          this.actualizaCorrectores();
        })
    }, () => { console.log("Añadir cancelado") });
  }

  editarCorrector(corrector: CorrectorNuevo, id: bigint): void {
    this.correctorService.editCorrector(corrector, id)
      .subscribe(() => {
        this.actualizaCorrectores(corrector.identificadorUsuario);
      });
  }

  eliminarCorrector(id: bigint): void {
    this.correctorService.deleteCorrector(id)
      .subscribe(() => {
        this.actualizaCorrectores();
      });
    this.correctorElegido = undefined;
  }

  busqCorrectores() {
    this.correctorService.getCorrectores(this.idConvBusq).subscribe(correctores => {
      this.busquedaFallida = (correctores.length === 0);
      this.correctores = correctores;
    });
    this.correctorElegido = undefined;
  }

}