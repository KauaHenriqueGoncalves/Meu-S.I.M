export type FontSize = 'small' | 'normal' | 'large' | 'xlarge';

export const FONT_MAP: Record<FontSize, string> = {
  small: '13px',
  normal: '16px',
  large: '18px',
  xlarge: '20px'
};

export interface AccessibilitySettings {
  fontSize: FontSize;
  nightMode: boolean;
}