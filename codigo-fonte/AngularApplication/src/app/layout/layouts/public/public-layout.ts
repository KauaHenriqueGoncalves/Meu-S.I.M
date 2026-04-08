import { Component } from '@angular/core';
import { PublicSidebar } from "../../components/public-sidebar/public-sidebar";
import { RouterOutlet } from "@angular/router";
import { Footer } from "../../components/footer/footer";

@Component({
  selector: 'app-public-layout',
  imports: [PublicSidebar, RouterOutlet, Footer],
  templateUrl: './public-layout.html',
  styleUrl: './public-layout.sass',
})
export class PublicLayout { }
