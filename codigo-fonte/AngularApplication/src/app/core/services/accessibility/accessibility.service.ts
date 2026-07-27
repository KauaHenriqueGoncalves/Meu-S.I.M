import { Injectable } from '@angular/core';
import { AccessibilitySettings, FONT_MAP, FontSize } from './accessibility.info';

@Injectable({
  providedIn: 'root',
})
export class AccessibilityService {
  private STORAGE_KEY = 'accessibility-settings';

  private settings: AccessibilitySettings = {
    fontSize: 'normal',
    nightMode: false
  };

  constructor() {
    this.loadFromStorage();
    this.applyAll();
  }

  get fontSize(): FontSize {
    return this.settings.fontSize;
  }

  get nightMode(): boolean {
    return this.settings.nightMode;
  }

  setFontSize(size: FontSize): void {
    this.settings.fontSize = size;
    this.applyFontSize();
    this.persist();
  }

  toggleNightMode(): void {
    this.settings.nightMode = !this.settings.nightMode;
    this.applyNightMode();
    this.persist();
  }

  private applyAll(): void {
    this.applyFontSize();
    this.applyNightMode();
  }

  private applyFontSize(): void {
    document.documentElement.style.fontSize = FONT_MAP[this.settings.fontSize];
  }

  private applyNightMode(): void {
    document.documentElement.classList.toggle('night-mode', this.settings.nightMode);
  }

  private persist(): void {
    localStorage.setItem(this.STORAGE_KEY, JSON.stringify(this.settings));
  }

  private loadFromStorage(): void {
    try {
      const raw = localStorage.getItem(this.STORAGE_KEY);
      if (raw) {
        this.settings = { ...this.settings, ...JSON.parse(raw) };
      }
    } catch {
      // ignora, mantém defaults
    }
  }
}
