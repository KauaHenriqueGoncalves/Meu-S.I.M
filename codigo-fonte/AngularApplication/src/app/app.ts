import { Component, OnInit, signal } from '@angular/core';
import { RouterOutlet } from '@angular/router';
import { Toast } from "./shared/components/toast/toast";
import { AuthApi } from './features/auth/api/auth.api';
import { AuthStore } from './core/auth/store/auth-store.service';
import { environment } from '../environments/environment';

@Component({
  selector: 'app-root',
  imports: [RouterOutlet, Toast],
  templateUrl: './app.html',
  styleUrl: './app.sass'
})
export class App implements OnInit {
  protected readonly title = signal('Meu S.I.M');

  constructor(
    private authApi: AuthApi,
    private authStore: AuthStore
  ) { }

  ngOnInit(): void {
    // this.authApi.refresh().subscribe({
    //   next: (res: any) => {
    //     console.log('[App] Sessão restaurada');
    //     this.authStore.setToken(res.accessToken);
    //   },
    //   error: () => {
    //     console.log('[App] Não autenticado');
    //     this.authStore.clear();
    //   }
    // });
  }
}
