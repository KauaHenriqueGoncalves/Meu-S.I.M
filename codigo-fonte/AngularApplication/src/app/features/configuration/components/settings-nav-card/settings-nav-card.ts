import { Component, Input } from '@angular/core';
import { RouterLink } from '@angular/router';
import { AngleDownSvg } from '../../../../shared/components/svg/angle-down.svg';

@Component({
  selector: 'app-settings-nav-card',
  imports: [
    RouterLink,
    AngleDownSvg
  ],
  templateUrl: './settings-nav-card.html',
  styleUrl: './settings-nav-card.sass',
})
export class SettingsNavCard {
  @Input() title: string = '';
  @Input() description: string = '';
  @Input() route: string = '';
}
