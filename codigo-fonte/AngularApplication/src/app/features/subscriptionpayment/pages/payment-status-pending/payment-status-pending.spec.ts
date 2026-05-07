import { ComponentFixture, TestBed } from '@angular/core/testing';

import { PaymentStatusPending } from './payment-status-pending';

describe('PaymentStatusPending', () => {
  let component: PaymentStatusPending;
  let fixture: ComponentFixture<PaymentStatusPending>;

  beforeEach(async () => {
    await TestBed.configureTestingModule({
      imports: [PaymentStatusPending]
    })
    .compileComponents();

    fixture = TestBed.createComponent(PaymentStatusPending);
    component = fixture.componentInstance;
    fixture.detectChanges();
  });

  it('should create', () => {
    expect(component).toBeTruthy();
  });
});
