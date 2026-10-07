import { Injectable } from '@angular/core';
import { ApiService } from '../../../core/services/api/api.service';
import { CacheResetService } from '../../../core/services/cache-reset/cache-reset.service';
import { CurrentUserDto } from '../dto/current-user.dto';
import { Observable, shareReplay } from 'rxjs';

@Injectable({
  providedIn: 'root',
})
export class UserApi {
  private cache = new Map<string, any>();

  constructor(
    private apiService: ApiService,
    private cacheReset: CacheResetService
  ) { 
    this.cacheReset.register(() => this.cache.clear());
  }

  getCurrentUser(): Observable<CurrentUserDto> {
    const key = 'currentUser';
    if (!this.cache.has(key)) {
      const request$ = this.apiService.get<CurrentUserDto>(
        '/users/me'
      ).pipe(shareReplay(1)) as unknown as Observable<CurrentUserDto>;
      this.cache.set(key, request$);
    }
    return this.cache.get(key);
  }
}
