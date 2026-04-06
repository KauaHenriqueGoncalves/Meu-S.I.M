import { Component, OnInit, signal } from '@angular/core';
import { RouterOutlet } from '@angular/router';
import { Toast } from "./shared/components/toast/toast";
import { AuthApiService } from './core/services/api/auth/auth.api.service';
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
    private authApi: AuthApiService,
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
