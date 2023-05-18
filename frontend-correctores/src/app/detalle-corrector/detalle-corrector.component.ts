import { Component, EventEmitter, Input, Output } from '@angular/core';
import { Corrector, CorrectorNuevo } from '../model/interfaces';
import { CorrectorService } from '../service/corrector.service';
import { NgbModal } from '@ng-bootstrap/ng-bootstrap';
import { FormularioCorrectorComponent } from '../formulario-corrector/formulario-corrector.component';

@Component({
  selector: 'app-detalle-corrector',
  templateUrl: './detalle-corrector.component.html',
  styleUrls: ['./detalle-corrector.component.css']
})
export class DetalleCorrectorComponent {
  @Input() corrector?: Corrector;
  @Output() correctorEditado = new EventEmitter<CorrectorNuevo>();
  @Output() correctorEliminado = new EventEmitter<bigint>();

  constructor(private correctorService: CorrectorService, private modalService: NgbModal) { }

  editarCorrector(): void {
    let ref = this.modalService.open(FormularioCorrectorComponent);
    ref.componentInstance.accion = "Editar";
    // Clona el objeto corrector y se lo pasa al formulario (no es necesario ya)
    // ref.componentInstance.corrector = {...this.corrector};
    ref.componentInstance.createFormGroup();
    ref.result.then((corrector: CorrectorNuevo) => {
      this.correctorEditado.emit(corrector);
    }, () => { console.log("Edición cancelada") });
  }

  eliminarCorrector(): void {
    this.correctorEliminado.emit(this.corrector?.id);
  }
}
