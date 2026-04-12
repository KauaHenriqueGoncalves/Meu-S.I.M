import { TestBed } from '@angular/core/testing';

import { SchoolPlanApi } from './school-plan-api';

describe('SchoolPlanApi', () => {
  let service: SchoolPlanApi;

  beforeEach(() => {
    TestBed.configureTestingModule({});
    service = TestBed.inject(SchoolPlanApi);
  });

  it('should be created', () => {
    expect(service).toBeTruthy();
  });
});
