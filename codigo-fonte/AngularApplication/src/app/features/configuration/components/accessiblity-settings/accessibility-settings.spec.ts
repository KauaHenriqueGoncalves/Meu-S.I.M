import { ComponentFixture, TestBed } from '@angular/core/testing';

import { AccessibilitySettings } from './accessibility-settings';

describe('AccessiblitySettings', () => {
  let component: AccessibilitySettings;
  let fixture: ComponentFixture<AccessibilitySettings>;

  beforeEach(async () => {
    await TestBed.configureTestingModule({
      imports: [AccessibilitySettings]
    })
    .compileComponents();

    fixture = TestBed.createComponent(AccessibilitySettings);
    component = fixture.componentInstance;
    fixture.detectChanges();
  });

  it('should create', () => {
    expect(component).toBeTruthy();
  });
});
