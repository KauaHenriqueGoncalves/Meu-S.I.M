import { TestBed } from '@angular/core/testing';

import { SubjectApi } from './subject-api';

describe('SubjectApi', () => {
  let service: SubjectApi;

  beforeEach(() => {
    TestBed.configureTestingModule({});
    service = TestBed.inject(SubjectApi);
  });

  it('should be created', () => {
    expect(service).toBeTruthy();
  });
});
