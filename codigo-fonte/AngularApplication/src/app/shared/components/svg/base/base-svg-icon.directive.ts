import { Input, Directive } from '@angular/core';

@Directive() // usar @Directive evita erro de decorator sem template
export abstract class BaseSvgIcon {
  @Input() size: number = 24;
  @Input() color: string = 'currentColor';
}