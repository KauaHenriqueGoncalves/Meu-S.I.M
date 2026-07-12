import { ComponentFixture, TestBed } from '@angular/core/testing';

import { PaymentStatusFailure } from './payment-status-failure';

describe('PaymentStatusFailure', () => {
  let component: PaymentStatusFailure;
  let fixture: ComponentFixture<PaymentStatusFailure>;

  beforeEach(async () => {
    await TestBed.configureTestingModule({
      imports: [PaymentStatusFailure]
    })
    .compileComponents();

    fixture = TestBed.createComponent(PaymentStatusFailure);
    component = fixture.componentInstance;
    fixture.detectChanges();
  });

  it('should create', () => {
    expect(component).toBeTruthy();
  });
});
