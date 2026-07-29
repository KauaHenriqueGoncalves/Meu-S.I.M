import { TestBed } from '@angular/core/testing';

import { AdminBillingdiscountApi } from './admin-billingdiscount-api';

describe('AdminBillingdiscountApi', () => {
  let service: AdminBillingdiscountApi;

  beforeEach(() => {
    TestBed.configureTestingModule({});
    service = TestBed.inject(AdminBillingdiscountApi);
  });

  it('should be created', () => {
    expect(service).toBeTruthy();
  });
});
