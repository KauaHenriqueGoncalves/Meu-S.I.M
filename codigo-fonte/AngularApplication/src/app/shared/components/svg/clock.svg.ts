import { Component } from "@angular/core";
import { BaseSvgIcon } from "./base/base-svg-icon.directive";

@Component({
  selector: 'app-svg-clock',
  standalone: true,
  template: `
    <svg xmlns="http://www.w3.org/2000/svg"
      [attr.width]="size"
      [attr.height]="size"  
      viewBox="0 0 24 24" 
      fill="none"
      [attr.stroke]="color"
      stroke-width="2" 
      stroke-linecap="round" 
      stroke-linejoin="round">
        <circle cx="12" cy="12" r="9"/>
        <polyline points="12 7 12 12 15.5 14"/>
    </svg>
  `
})
export class ClockSvg extends BaseSvgIcon { }