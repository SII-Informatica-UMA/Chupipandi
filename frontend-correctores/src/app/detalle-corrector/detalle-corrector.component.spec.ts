import { ComponentFixture, TestBed } from '@angular/core/testing';

import { DetalleCorrectorComponent } from './detalle-corrector.component';

describe('DetalleCorrectorComponent', () => {
  let component: DetalleCorrectorComponent;
  let fixture: ComponentFixture<DetalleCorrectorComponent>;

  beforeEach(async () => {
    await TestBed.configureTestingModule({
      declarations: [ DetalleCorrectorComponent ]
    })
    .compileComponents();

    fixture = TestBed.createComponent(DetalleCorrectorComponent);
    component = fixture.componentInstance;
    fixture.detectChanges();
  });

  it('should create', () => {
    expect(component).toBeTruthy();
  });
});
