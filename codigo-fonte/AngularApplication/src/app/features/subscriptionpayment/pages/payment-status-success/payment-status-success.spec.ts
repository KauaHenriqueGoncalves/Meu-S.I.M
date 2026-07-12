import { ComponentFixture, TestBed } from '@angular/core/testing';

import { PaymentStatusSuccess } from './payment-status-success';

describe('PaymentStatusSucess', () => {
  let component: PaymentStatusSuccess;
  let fixture: ComponentFixture<PaymentStatusSuccess>;

  beforeEach(async () => {
    await TestBed.configureTestingModule({
      imports: [PaymentStatusSuccess]
    })
    .compileComponents();

    fixture = TestBed.createComponent(PaymentStatusSuccess);
    component = fixture.componentInstance;
    fixture.detectChanges();
  });

  it('should create', () => {
    expect(component).toBeTruthy();
  });
});
