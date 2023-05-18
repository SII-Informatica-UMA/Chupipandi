import { Component } from '@angular/core';
import { NgbActiveModal } from '@ng-bootstrap/ng-bootstrap';

@Component({
  selector: 'app-error-modal-component',
  template: `
    <div class="modal-header">
    <h4 class="modal-title">{{ errorCode }}</h4>
    </div>
    <div class="modal-body">
      {{ errorMessage }}
    </div>
    <div class="modal-footer">
      <button type="button" class="btn btn-secondary" (click)="modal.dismiss()">Cerrar</button>
    </div>
    `,
  styleUrls: ['./error-modal-component.component.css']
})
export class ErrorModalComponentComponent {
  constructor(public modal: NgbActiveModal) { }
  errorCode: any;
  errorMessage: string = '';
}
