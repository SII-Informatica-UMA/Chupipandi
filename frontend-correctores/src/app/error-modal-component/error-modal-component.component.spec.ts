import { ComponentFixture, TestBed } from '@angular/core/testing';

import { ErrorModalComponentComponent } from './error-modal-component.component';

describe('ErrorModalComponentComponent', () => {
  let component: ErrorModalComponentComponent;
  let fixture: ComponentFixture<ErrorModalComponentComponent>;

  beforeEach(async () => {
    await TestBed.configureTestingModule({
      declarations: [ ErrorModalComponentComponent ]
    })
    .compileComponents();

    fixture = TestBed.createComponent(ErrorModalComponentComponent);
    component = fixture.componentInstance;
    fixture.detectChanges();
  });

  it('should create', () => {
    expect(component).toBeTruthy();
  });
});
