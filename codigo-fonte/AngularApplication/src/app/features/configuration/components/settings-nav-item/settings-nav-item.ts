import { Component, Input } from '@angular/core';
import { RouterLink } from '@angular/router';
import { AngleDownSvg } from '../../../../shared/components/svg/angle-down.svg';

@Component({
  selector: 'app-settings-nav-item',
  imports: [
    RouterLink,
    AngleDownSvg
  ],
  templateUrl: './settings-nav-item.html',
  styleUrl: './settings-nav-item.sass',
})
export class SettingsNavItem {
  @Input() title: string = '';
  @Input() description: string = '';
  @Input() route: string = '';
}
