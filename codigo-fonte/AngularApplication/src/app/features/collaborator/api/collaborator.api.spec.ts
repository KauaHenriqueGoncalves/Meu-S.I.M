import { TestBed } from '@angular/core/testing';

import { CollaboratorApi } from './collaborator.api';

describe('CollaboratorApi', () => {
  let service: CollaboratorApi;

  beforeEach(() => {
    TestBed.configureTestingModule({});
    service = TestBed.inject(CollaboratorApi);
  });

  it('should be created', () => {
    expect(service).toBeTruthy();
  });
});
