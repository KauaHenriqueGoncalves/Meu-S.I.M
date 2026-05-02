import { Component } from "@angular/core";
import { BaseSvgIcon } from "./base/base-svg-icon.directive";

@Component({
  selector: 'app-svg-arrow-left',
  template: `
    <svg 
      xmlns="http://www.w3.org/2000/svg" 
      [attr.width]="size" 
      [attr.height]="size" 
      [attr.fill]="color"
      viewBox="0 0 24 24" 
      stroke="currentColor">

      <path stroke-linecap="round" stroke-linejoin="round" stroke-width="2" d="M10 19l-7-7m0 0l7-7m-7 7h18" />
    </svg>
  `
})
export class ArrowLeftSvg extends BaseSvgIcon { }