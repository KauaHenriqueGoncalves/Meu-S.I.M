import { Component } from '@angular/core';
import { RouterLink } from '@angular/router';

@Component({
  selector: 'app-private-sidebar',
  imports: [RouterLink],
  templateUrl: './private-sidebar.html',
  styleUrl: './private-sidebar.sass',
})
export class PrivateSidebar {
  menu: any[] = [];

  constructor() {}
}
