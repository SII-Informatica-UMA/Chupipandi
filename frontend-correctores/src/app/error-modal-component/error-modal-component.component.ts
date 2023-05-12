import { Component } from '@angular/core';
import { NgbActiveModal } from '@ng-bootstrap/ng-bootstrap';

@Component({
  selector: 'app-error-modal-component',
  templateUrl: './error-modal-component.component.html',
  styleUrls: ['./error-modal-component.component.css']
})
export class ErrorModalComponentComponent {
  constructor(public modal: NgbActiveModal) {}
  errorCode: any;
  errorMessage: string = '';
}
