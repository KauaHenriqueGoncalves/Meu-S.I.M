import { Component, Input, OnInit } from '@angular/core';
import { RouterLink } from '@angular/router';
import { MENU_CONFIG, Role } from './menu.config';

@Component({
  selector: 'app-private-sidebar',
  imports: [RouterLink],
  templateUrl: './private-sidebar.html',
  styleUrl: './private-sidebar.sass',
})
export class PrivateSidebar implements OnInit {
  @Input() role!: string | null | undefined;
  menu: any[] = [];

  constructor() { }

  ngOnInit(): void {
    console.log(this.role as Role)
    this.menu = MENU_CONFIG[this.role as Role] || [];
  }
}
