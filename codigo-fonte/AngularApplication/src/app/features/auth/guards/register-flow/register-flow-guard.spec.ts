import { TestBed } from '@angular/core/testing';
import { CanActivateFn } from '@angular/router';

import { registerFlowGuard } from './register-flow-guard';

describe('registerFlowGuard', () => {
  const executeGuard: CanActivateFn = (...guardParameters) => 
      TestBed.runInInjectionContext(() => registerFlowGuard(...guardParameters));

  beforeEach(() => {
    TestBed.configureTestingModule({});
  });

  it('should be created', () => {
    expect(executeGuard).toBeTruthy();
  });
});
