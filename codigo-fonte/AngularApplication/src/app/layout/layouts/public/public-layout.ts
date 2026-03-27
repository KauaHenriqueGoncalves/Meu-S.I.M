import { Component } from '@angular/core';
import { PublicSidebar } from "../../components/public-sidebar/public-sidebar";
import { RouterOutlet } from "@angular/router";

@Component({
  selector: 'app-public-layout',
  imports: [PublicSidebar, RouterOutlet],
  templateUrl: './public-layout.html',
  styleUrl: './public-layout.sass',
})
export class PublicLayout { }
