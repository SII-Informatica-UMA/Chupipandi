import { ComponentFixture, TestBed } from '@angular/core/testing';

import { ErrorModalComponentComponent } from './error-modal-component.component';
import { NgbActiveModal, NgbCollapseModule } from '@ng-bootstrap/ng-bootstrap';

describe('ErrorModalComponentComponent', () => {
  let component: ErrorModalComponentComponent;
  let fixture: ComponentFixture<ErrorModalComponentComponent>;

  beforeEach(async () => {
    await TestBed.configureTestingModule({
        declarations: [ErrorModalComponentComponent],
        imports: [NgbCollapseModule],
        providers: [NgbActiveModal]
      }).compileComponents().then(() => {
        fixture = TestBed.createComponent(ErrorModalComponentComponent);
        component = fixture.componentInstance;
        component.errorCode = '500';
        component.errorMessage = 'Internal Server Error';
        fixture.detectChanges();
      });
  });

  it('deberia mostrar el codigo de error', () => {
    let errorCodeElement = fixture.nativeElement.querySelector('.modal-title');
    expect(errorCodeElement.textContent).toContain('500');
  });

  it('deberia mostrar el mensaje de error', () => {
    let errorMessageElement = fixture.nativeElement.querySelector('.modal-body');
    expect(errorMessageElement.textContent).toContain('Internal Server Error');
  });

  it('deberia cerrar la ventana cuando se hace click en el boton de cerrar', () => {
    spyOn(component.modal, 'dismiss');
    let closeButton = fixture.nativeElement.querySelector('.btn-secondary');
    closeButton.click();
    expect(component.modal.dismiss).toHaveBeenCalled();
  });
});
