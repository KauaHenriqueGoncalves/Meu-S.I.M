import { ComponentFixture, TestBed } from '@angular/core/testing';

import { SpinnerToButton } from './spinner-to-button';

describe('SpinnerToButton', () => {
  let component: SpinnerToButton;
  let fixture: ComponentFixture<SpinnerToButton>;

  beforeEach(async () => {
    await TestBed.configureTestingModule({
      imports: [SpinnerToButton]
    })
    .compileComponents();

    fixture = TestBed.createComponent(SpinnerToButton);
    component = fixture.componentInstance;
    fixture.detectChanges();
  });

  it('should create', () => {
    expect(component).toBeTruthy();
  });
});
