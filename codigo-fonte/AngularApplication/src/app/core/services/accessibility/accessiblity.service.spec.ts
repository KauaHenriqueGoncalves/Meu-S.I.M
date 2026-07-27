import { TestBed } from '@angular/core/testing';

import { AccessibilityService } from './accessibility.service';

describe('AccessiblityService', () => {
  let service: AccessibilityService;

  beforeEach(() => {
    TestBed.configureTestingModule({});
    service = TestBed.inject(AccessibilityService);
  });

  it('should be created', () => {
    expect(service).toBeTruthy();
  });
});
