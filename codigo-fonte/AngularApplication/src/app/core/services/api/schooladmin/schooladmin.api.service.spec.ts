import { TestBed } from '@angular/core/testing';

import { SchooladminApiService } from './schooladmin.api.service';

describe('Schooladmin', () => {
  let service: SchooladminApiService;

  beforeEach(() => {
    TestBed.configureTestingModule({});
    service = TestBed.inject(SchooladminApiService);
  });

  it('should be created', () => {
    expect(service).toBeTruthy();
  });
});
