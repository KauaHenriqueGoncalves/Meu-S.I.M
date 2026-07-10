import { TestBed } from '@angular/core/testing';

import { ClassroomApi } from './classroom-api';

describe('ClassroomApi', () => {
  let service: ClassroomApi;

  beforeEach(() => {
    TestBed.configureTestingModule({});
    service = TestBed.inject(ClassroomApi);
  });

  it('should be created', () => {
    expect(service).toBeTruthy();
  });
});
