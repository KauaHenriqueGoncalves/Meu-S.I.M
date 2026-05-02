import { TestBed } from '@angular/core/testing';

import { SubscriptionPaymentApi } from './subscription-payment.api';

describe('SubscriptionPaymentApi', () => {
  let service: SubscriptionPaymentApi;

  beforeEach(() => {
    TestBed.configureTestingModule({});
    service = TestBed.inject(SubscriptionPaymentApi);
  });

  it('should be created', () => {
    expect(service).toBeTruthy();
  });
});
