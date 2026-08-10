import { TestBed } from '@angular/core/testing';

import { AdminSchoolPlanApi } from './admin-school-plan-api';

describe('AdminSchoolPlanApi', () => {
  let service: AdminSchoolPlanApi;

  beforeEach(() => {
    TestBed.configureTestingModule({});
    service = TestBed.inject(AdminSchoolPlanApi);
  });

  it('should be created', () => {
    expect(service).toBeTruthy();
  });
});
