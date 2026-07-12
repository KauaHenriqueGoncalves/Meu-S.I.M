import { TestBed } from '@angular/core/testing';
import { SubscriptionCheckService } from './subscription-check.service';

describe('SubscriptionCheckService', () => {
  let service: SubscriptionCheckService;

  beforeEach(() => {
    TestBed.configureTestingModule({});
    service = TestBed.inject(SubscriptionCheckService);
  });

  it('should be created', () => {
    expect(service).toBeTruthy();
  });
});
