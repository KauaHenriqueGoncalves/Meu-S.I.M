import { TestBed } from '@angular/core/testing';
import { CanActivateFn } from '@angular/router';

import { loginGuest } from '../login-guest-guard';

describe('loginGuest', () => {
  const executeGuard: CanActivateFn = (...guardParameters) => 
      TestBed.runInInjectionContext(() => loginGuest(...guardParameters));

  beforeEach(() => {
    TestBed.configureTestingModule({});
  });

  it('should be created', () => {
    expect(executeGuard).toBeTruthy();
  });
});
