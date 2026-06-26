import { TestBed } from '@angular/core/testing';

import { LegalGuardianApi } from './legal-guardian-api';

describe('LegalGuardianApi', () => {
  let service: LegalGuardianApi;

  beforeEach(() => {
    TestBed.configureTestingModule({});
    service = TestBed.inject(LegalGuardianApi);
  });

  it('should be created', () => {
    expect(service).toBeTruthy();
  });
});
