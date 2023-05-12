import { ComponentFixture, TestBed } from '@angular/core/testing';

import { FormularioCorrectorComponent } from './formulario-corrector.component';

describe('FormularioCorrectorComponent', () => {
  let component: FormularioCorrectorComponent;
  let fixture: ComponentFixture<FormularioCorrectorComponent>;

  beforeEach(async () => {
    await TestBed.configureTestingModule({
      declarations: [ FormularioCorrectorComponent ]
    })
    .compileComponents();

    fixture = TestBed.createComponent(FormularioCorrectorComponent);
    component = fixture.componentInstance;
    fixture.detectChanges();
  });

  it('should create', () => {
    expect(component).toBeTruthy();
  });
});
