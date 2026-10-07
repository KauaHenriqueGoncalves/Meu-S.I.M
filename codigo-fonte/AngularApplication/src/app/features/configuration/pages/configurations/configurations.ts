import { Component, OnInit } from '@angular/core';
import { AccessibilitySettings } from '../../components/accessiblity-settings/accessibility-settings';
import { SettingsNavItem } from '../../components/settings-nav-item/settings-nav-item';
import { TokenPayload } from '../../../../core/auth/payload/token.payload';
import { AuthService } from '../../../../core/auth/service/auth.service';
import { Router } from '@angular/router';
import { UserApi } from '../../../user/api/user-api';
import { CurrentUserDto } from '../../../user/dto/current-user.dto';
import { NotificationService } from '../../../../core/services/notification/notification.service';

@Component({
  selector: 'app-configurations',
  imports: [SettingsNavItem, AccessibilitySettings],
  templateUrl: './configurations.html',
  styleUrl: './configurations.sass',
})
export class Configurations implements OnInit {
  userTokenPayload!: TokenPayload | null;
  role!: string | null;
  currentUser!: CurrentUserDto | null;

  constructor(
    private authService: AuthService,
    private router: Router,
    private userApi: UserApi,
    private notificationService: NotificationService
  ) { }

  ngOnInit(): void {
    this.userTokenPayload = this.authService.getPayload();
    this.role = this.userTokenPayload?.scope!;
    this.loadCurrentuser();
  }

  loadCurrentuser(): void {
    this.userApi.getCurrentUser()
      .subscribe({
        next: (res: CurrentUserDto) => {
          this.currentUser = res;
        },
        error: (err) => {
          this.notificationService.notify({
            type: 'error',
            text: err.error?.message || 'Erro ao carregar o usuario atual'
          });
        }
      })
  }

  navigateToProfile() {
    if (this.role === 'school_admin') {
      this.router.navigate(
        ['/app/details-school-admin'], 
        { state: { id: this.currentUser?.id } }
      );
    }
  }
}
