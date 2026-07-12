import { Injectable } from '@angular/core';

@Injectable({
  providedIn: 'root',
})
export class CacheResetService {
  private resetCallbacks: (() => void)[] = [];

  register(resetFn: () => void): void {
    this.resetCallbacks.push(resetFn);
  }

  resetAll(): void {
    this.resetCallbacks.forEach(fn => {
      fn()
    });
  }
}
