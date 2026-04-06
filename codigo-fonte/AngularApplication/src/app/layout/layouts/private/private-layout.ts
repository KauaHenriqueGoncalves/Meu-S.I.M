import { Component, OnInit } from '@angular/core';
import { TokenPayload } from '../../../core/auth/data/token.payload';
import { RouterOutlet } from "@angular/router";
import { PrivateSidebar } from "../../components/private-sidebar/private-sidebar";
import { AuthService } from '../../../core/auth/service/auth.service';

@Component({
  selector: 'app-private-layout',
  imports: [RouterOutlet, PrivateSidebar],
  templateUrl: './private-layout.html',
  styleUrl: './private-layout.sass',
})
export class PrivateLayout implements OnInit {
  userTokenPayload: TokenPayload | null | undefined;
  
  constructor(
    private authService: AuthService
  ) { }

  ngOnInit(): void {
    this.userTokenPayload = this.authService.getPayload();
  
    console.log(this.userTokenPayload)
  }
}
