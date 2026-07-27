import { ComponentFixture, TestBed } from '@angular/core/testing';

import { SettingsNavCard } from './settings-nav-card';

describe('SettingsNavCard', () => {
  let component: SettingsNavCard;
  let fixture: ComponentFixture<SettingsNavCard>;

  beforeEach(async () => {
    await TestBed.configureTestingModule({
      imports: [SettingsNavCard]
    })
    .compileComponents();

    fixture = TestBed.createComponent(SettingsNavCard);
    component = fixture.componentInstance;
    fixture.detectChanges();
  });

  it('should create', () => {
    expect(component).toBeTruthy();
  });
});
