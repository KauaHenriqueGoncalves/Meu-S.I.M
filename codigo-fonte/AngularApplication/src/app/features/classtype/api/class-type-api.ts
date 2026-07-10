import { Injectable } from '@angular/core';
import { ApiService } from '../../../core/services/api/api.service';
import { CacheResetService } from '../../../core/services/cache-reset/cache-reset.service';
import { ClassTypeResponseDto } from '../dto/class-type-response.dto';
import { ApiConfig } from '../../../core/config/api.config';
import { Observable, shareReplay } from 'rxjs';

@Injectable({
  providedIn: 'root',
})
export class ClassTypeApi {
  private $request: Observable<ClassTypeResponseDto[]> | undefined;

  constructor(
    private apiService: ApiService,
    private cacheReset: CacheResetService
  ) { 
    this.cacheReset.register(() => this.cleanCache());
  }

  findAll(): Observable<ClassTypeResponseDto[]> {
    if (!this.$request) {
      this.$request = this.apiService.get<ClassTypeResponseDto[]>(
        ApiConfig.endpoints.classType.base
      ).pipe(shareReplay(1)) as unknown as Observable<ClassTypeResponseDto[]>;
    }
    return this.$request;
  }

  cleanCache(): void {
    this.$request = undefined;
  }
}
