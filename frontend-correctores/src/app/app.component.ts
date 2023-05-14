import { Component, Input, OnInit } from '@angular/core';
import { Corrector, CorrectorNuevo } from './model/interfaces';
import { CorrectorService } from './service/corrector.service';
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

  private actualizaCorrectores(id?: bigint): void {
    this.correctorService.getCorrectores()
      .subscribe(correctores => {
        this.correctores = correctores;
        if (id) {
          this.correctorElegido = this.correctores.find(corrector => corrector.id === id);
        }
      });
  }

  elegirCorrector(corrector: Corrector): void {
    this.correctorElegido = corrector;
  }

  aniadirCorrector(): void {
    let ref = this.modalService.open(FormularioCorrectorComponent);
    ref.componentInstance.accion = "Añadir";
    ref.componentInstance.createFormGroup();
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
        this.actualizaCorrectores(id);
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
