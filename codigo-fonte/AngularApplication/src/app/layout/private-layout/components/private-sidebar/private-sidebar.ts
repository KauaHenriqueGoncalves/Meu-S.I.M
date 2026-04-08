import { Component, EventEmitter, Input, OnInit, Output} from '@angular/core';
import { RouterLink } from '@angular/router';
import { MENU_CONFIG } from './menu.config';
import { Role } from './role.type';
import { MenuItem } from './menu-item.model';
import { DashboardSvg } from '../../../../shared/components/svg/dashboard.svg';
import { AcademicSvg } from "../../../../shared/components/svg/academic.svg";
import { AngleDownSvg } from '../../../../shared/components/svg/angle-down.svg';
import { UsersSvg } from '../../../../shared/components/svg/users.svg';
import { CardSvg } from '../../../../shared/components/svg/card.svg';
import { GearSvg } from '../../../../shared/components/svg/gear.svg';
import { LogoutSvg } from '../../../../shared/components/svg/logout.svg';

@Component({
  selector: 'app-private-sidebar',
  imports: [
    RouterLink, 
    DashboardSvg, 
    AcademicSvg, 
    AngleDownSvg, 
    UsersSvg, 
    CardSvg,
    GearSvg,
    LogoutSvg
  ],
  templateUrl: './private-sidebar.html',
  styleUrl: './private-sidebar.sass',
})
export class PrivateSidebar implements OnInit {
  @Input() role!: string | null | undefined;
  @Input() isTopbarHidden: boolean = false;
  @Output() next = new EventEmitter<any>();

  menu: any[] = [];

  isMenuOpen: boolean = false;

  constructor() { }

  ngOnInit(): void {
    console.log(this.role as Role)
    this.menu = MENU_CONFIG[this.role as Role] || [];
  }

  toggleOverlay(): void {
    this.isMenuOpen = !this.isMenuOpen;
    
    if (this.isMenuOpen) {
      document.body.style.overflow = 'hidden';
    } 
    else {
      document.body.style.overflow = '';
    }
  }

  toggleSubmenu(item: MenuItem): void {
    if (item.children) {
      this.menu.forEach(m => {
        if (m !== item) m.expanded = false;
      });
      item.expanded = !item.expanded;
    }
  }

  resetMenus(): void {
    //this.menu.forEach(item => item.expanded = false);
  }

  logout(): void {
    this.next.emit();
  }
}
