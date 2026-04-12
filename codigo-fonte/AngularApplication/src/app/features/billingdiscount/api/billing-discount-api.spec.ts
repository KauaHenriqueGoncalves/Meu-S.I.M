import { TestBed } from '@angular/core/testing';

import { BillingDiscountApi } from './billing-discount-api';

describe('BillingDiscountApi', () => {
  let service: BillingDiscountApi;

  beforeEach(() => {
    TestBed.configureTestingModule({});
    service = TestBed.inject(BillingDiscountApi);
  });

  it('should be created', () => {
    expect(service).toBeTruthy();
  });
});
