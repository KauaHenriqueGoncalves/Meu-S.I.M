import { Component } from '@angular/core';
import { RouterLink } from "@angular/router";

@Component({
  selector: 'app-public-sidebar',
  imports: [RouterLink],
  templateUrl: './public-sidebar.html',
  styleUrl: './public-sidebar.sass',
})
export class PublicSidebar { }
