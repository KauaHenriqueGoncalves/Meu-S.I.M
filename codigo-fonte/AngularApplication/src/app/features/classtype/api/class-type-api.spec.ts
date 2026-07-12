import { TestBed } from '@angular/core/testing';

import { ClassTypeApi } from './class-type-api';

describe('ClassTypeApi', () => {
  let service: ClassTypeApi;

  beforeEach(() => {
    TestBed.configureTestingModule({});
    service = TestBed.inject(ClassTypeApi);
  });

  it('should be created', () => {
    expect(service).toBeTruthy();
  });
});
