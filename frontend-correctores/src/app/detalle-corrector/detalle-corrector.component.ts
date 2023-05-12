import { Component, EventEmitter, Input, Output } from '@angular/core';
import { Corrector, CorrectorNuevo } from '../interfaces';
import { CorrectorService } from '../corrector.service';
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
    ref.componentInstance.corrector = {...this.corrector};
    ref.result.then((corrector: CorrectorNuevo) => {
      this.correctorEditado.emit(corrector);
    });
  }

  eliminarCorrector(): void {
    this.correctorEliminado.emit(this.corrector?.id);
  }
}
