import { TestBed } from '@angular/core/testing';

import { ScheduleApi } from './schedule-api';

describe('ScheduleApi', () => {
  let service: ScheduleApi;

  beforeEach(() => {
    TestBed.configureTestingModule({});
    service = TestBed.inject(ScheduleApi);
  });

  it('should be created', () => {
    expect(service).toBeTruthy();
  });
});
