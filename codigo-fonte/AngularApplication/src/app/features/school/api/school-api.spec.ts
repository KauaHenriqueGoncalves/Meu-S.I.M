import { TestBed } from '@angular/core/testing';

import { SchoolApi } from './school-api';

describe('SchoolApi', () => {
  let service: SchoolApi;

  beforeEach(() => {
    TestBed.configureTestingModule({});
    service = TestBed.inject(SchoolApi);
  });

  it('should be created', () => {
    expect(service).toBeTruthy();
  });
});
