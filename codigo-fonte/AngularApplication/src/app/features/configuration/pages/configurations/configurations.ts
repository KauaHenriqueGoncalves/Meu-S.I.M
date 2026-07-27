import { Component } from '@angular/core';
import { AccessibilitySettings } from '../../components/accessiblity-settings/accessibility-settings';
import { SettingsNavCard } from '../../components/settings-nav-card/settings-nav-card';

@Component({
  selector: 'app-configurations',
  imports: [SettingsNavCard, AccessibilitySettings],
  templateUrl: './configurations.html',
  styleUrl: './configurations.sass',
})
export class Configurations { }
