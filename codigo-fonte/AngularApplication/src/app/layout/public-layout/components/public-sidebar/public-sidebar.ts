import { Component, Renderer2 } from '@angular/core';
import { RouterLink } from "@angular/router";

@Component({
  selector: 'app-public-sidebar',
  imports: [RouterLink],
  templateUrl: './public-sidebar.html',
  styleUrl: './public-sidebar.sass',
})
export class PublicSidebar {
  isMenuOpen: boolean = false;

  constructor(private renderer: Renderer2) { }

  toggleMenu(): void {
    this.isMenuOpen = !this.isMenuOpen;

    const html = document.documentElement;
    const body = document.body;

    if (this.isMenuOpen) {
      this.renderer.addClass(html, 'no-scroll');
      this.renderer.addClass(body, 'no-scroll');
    } else {
      this.renderer.removeClass(html, 'no-scroll');
      this.renderer.removeClass(body, 'no-scroll');
    }
  }
}
