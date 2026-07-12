import { Component } from "@angular/core";
import { BaseSvgIcon } from "./base/base-svg-icon.directive";

@Component({
  selector: 'app-svg-plus',
  standalone: true,
  template: `
    <svg 
        xmlns="http://www.w3.org/2000/svg" 
        [attr.width]="size"
        [attr.height]="size" 
        viewBox="0 0 24 24">

        <path d="M6 12H18M12 6V18" [attr.stroke]="color" stroke-width="2" stroke-linecap="round" stroke-linejoin="round"/>
    </svg>
  `
})
export class PlusSvg extends BaseSvgIcon { }