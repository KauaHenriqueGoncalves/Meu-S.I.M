import { Component, signal } from '@angular/core';
import { RouterOutlet } from '@angular/router';
import { Toast } from "./shared/components/toast/toast";
import { AuthApi } from './features/auth/api/auth.api';
import { AuthStore } from './core/auth/store/auth-store.service';
import { AccessibilityService } from './core/services/accessibility/accessibility.service';

@Component({
  selector: 'app-root',
  imports: [RouterOutlet, Toast],
  templateUrl: './app.html',
  styleUrl: './app.sass'
})
export class App {
  protected readonly title = signal('Meu S.I.M');

  constructor(
    private authApi: AuthApi,
    private authStore: AuthStore,
    private accessibilityService: AccessibilityService
  ) { }
}
