import { ComponentFixture, TestBed } from '@angular/core/testing';

import { SettingsNavItem } from './settings-nav-item';

describe('SettingsNavItem', () => {
  let component: SettingsNavItem;
  let fixture: ComponentFixture<SettingsNavItem>;

  beforeEach(async () => {
    await TestBed.configureTestingModule({
      imports: [SettingsNavItem]
    })
    .compileComponents();

    fixture = TestBed.createComponent(SettingsNavItem);
    component = fixture.componentInstance;
    fixture.detectChanges();
  });

  it('should create', () => {
    expect(component).toBeTruthy();
  });
});
