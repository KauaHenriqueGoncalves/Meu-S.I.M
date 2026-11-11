import { TestBed } from '@angular/core/testing';

import { LessonApi } from './lesson-api';

describe('LessonApi', () => {
  let service: LessonApi;

  beforeEach(() => {
    TestBed.configureTestingModule({});
    service = TestBed.inject(LessonApi);
  });

  it('should be created', () => {
    expect(service).toBeTruthy();
  });
});
