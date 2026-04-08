import { Component } from '@angular/core';
import { RouterOutlet } from "@angular/router";
import { PublicSidebar } from './components/public-sidebar/public-sidebar';
import { PublicFooter } from './components/public-footer/public-footer';

@Component({
  selector: 'app-public-layout',
  imports: [PublicSidebar, RouterOutlet, PublicFooter],
  templateUrl: './public-layout.html',
  styleUrl: './public-layout.sass',
})
export class PublicLayout { }
