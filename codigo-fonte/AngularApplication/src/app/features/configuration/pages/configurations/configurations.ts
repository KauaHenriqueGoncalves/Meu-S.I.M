import { Component } from '@angular/core';
import { AccessibilitySettings } from '../../components/accessiblity-settings/accessibility-settings';
import { SettingsNavItem } from '../../components/settings-nav-item/settings-nav-item';

@Component({
  selector: 'app-configurations',
  imports: [SettingsNavItem, AccessibilitySettings],
  templateUrl: './configurations.html',
  styleUrl: './configurations.sass',
})
export class Configurations { }
