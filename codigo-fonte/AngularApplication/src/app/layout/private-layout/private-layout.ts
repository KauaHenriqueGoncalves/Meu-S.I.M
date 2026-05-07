import { Component, OnInit } from '@angular/core';
import { Router, RouterOutlet } from "@angular/router";
import { TokenPayload } from '../../core/auth/payload/token.payload';
import { PrivateSidebar } from './components/private-sidebar/private-sidebar';
import { AuthService } from '../../core/auth/service/auth.service';
import { AuthApi } from '../../features/auth/api/auth.api';
import { AuthStore } from '../../core/auth/store/auth-store.service';
import { CacheResetService } from '../../core/services/cache-reset/cache-reset.service';
import { SubscriptionCheckService } from '../../features/subscription/service/subscription-check/subscription-check.service';

@Component({
  selector: 'app-private-layout',
  imports: [RouterOutlet, PrivateSidebar],
  templateUrl: './private-layout.html',
  styleUrl: './private-layout.sass',
})
export class PrivateLayout implements OnInit {
  userTokenPayload: TokenPayload | null | undefined;

  isTopbarHidden: boolean = false;
  lastScrollTop: number = 0;

  constructor(
    private authService: AuthService,
    private authStore: AuthStore,
    private authApi: AuthApi,
    private cacheResetService: CacheResetService,
    private subscriptionCheckService: SubscriptionCheckService,
    private router: Router
  ) { }

  ngOnInit(): void {
    this.userTokenPayload = this.authService.getPayload();
    this.subscriptionCheckService.check();
  }

  onScroll(event: Event): void {
    const target = event.target as HTMLElement;
    const currentScroll = target.scrollTop;
    const delta = currentScroll - this.lastScrollTop;

    if (Math.abs(delta) < 5) return;

    if (delta > 0 && currentScroll > 60) {
      this.isTopbarHidden = true;
    } else if (delta < -10 || currentScroll < 60) {
      this.isTopbarHidden = false;
    }

    this.lastScrollTop = currentScroll;
  }

  logout(): void {
    this.authStore.clear();
    this.authApi.logout();
    this.cacheResetService.resetAll();
    this.router.navigate(['/auth/log-in']);
  }
}
