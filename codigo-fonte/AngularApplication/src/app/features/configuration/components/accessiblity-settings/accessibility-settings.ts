import { Component } from '@angular/core';
import { FontSize } from '../../../../core/services/accessibility/accessibility.info';
import { AccessibilityService } from '../../../../core/services/accessibility/accessibility.service';

@Component({
  selector: 'app-accessibility-settings',
  imports: [],
  templateUrl: './accessibility-settings.html',
  styleUrl: './accessibility-settings.sass',
})
export class AccessibilitySettings {
  fontOptions: { value: FontSize; label: string }[] = [
    { value: 'small', label: 'A-' },
    { value: 'normal', label: 'A' },
    { value: 'large', label: 'A+' },
    { value: 'xlarge', label: 'A++' }
  ];

  constructor(
    public accessibilityService: AccessibilityService
  ) { }

  selectFontSize(size: FontSize): void {
    this.accessibilityService.setFontSize(size);
  }

  toggleNightMode(): void {
    this.accessibilityService.toggleNightMode();
  }
}
