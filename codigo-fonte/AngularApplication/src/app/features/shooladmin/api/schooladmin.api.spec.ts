import { TestBed } from '@angular/core/testing';

import { SchooladminApi } from './schooladmin.api';

describe('Schooladmin', () => {
  let service: SchooladminApi;

  beforeEach(() => {
    TestBed.configureTestingModule({});
    service = TestBed.inject(SchooladminApi);
  });

  it('should be created', () => {
    expect(service).toBeTruthy();
  });
});
