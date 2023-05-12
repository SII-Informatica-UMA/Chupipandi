import { Component, OnInit } from '@angular/core';
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
    });
  }

  editarCorrector(corrector: CorrectorNuevo, id: bigint): void {
    this.correctorService.editCorrector(corrector, id)
      .subscribe(() => {
        this.actualizaCorrectores(corrector.identificadorUsuario);
      });
    this.correctorElegido = this.correctores.find(corrector => corrector.id === id);
  }

  eliminarCorrector(id: bigint): void {
    this.correctorService.deleteCorrector(id)
      .subscribe(() => {
        this.actualizaCorrectores();
      });
    this.correctorElegido = undefined;
  }

  // TODO: Implementar correctamente la busqueda (de momento no hace nada) 
  busqCorrectores() {
    this.correctorService.getCorrectores(this.idConvBusq).subscribe(correctores => {
      console.log(this.correctores);
      console.log(correctores);
      this.correctores = correctores;
    });
  }

}
