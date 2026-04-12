import { TestBed } from '@angular/core/testing';

import { SubscriptionApi } from './subscription-api';

describe('SubscriptionApi', () => {
  let service: SubscriptionApi;

  beforeEach(() => {
    TestBed.configureTestingModule({});
    service = TestBed.inject(SubscriptionApi);
  });

  it('should be created', () => {
    expect(service).toBeTruthy();
  });
});
