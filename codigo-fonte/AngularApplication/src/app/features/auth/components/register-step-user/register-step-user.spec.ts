import { ComponentFixture, TestBed } from '@angular/core/testing';

import { RegisterStepUser } from './register-step-user';

describe('RegisterStepUser', () => {
  let component: RegisterStepUser;
  let fixture: ComponentFixture<RegisterStepUser>;

  beforeEach(async () => {
    await TestBed.configureTestingModule({
      imports: [RegisterStepUser]
    })
    .compileComponents();

    fixture = TestBed.createComponent(RegisterStepUser);
    component = fixture.componentInstance;
    fixture.detectChanges();
  });

  it('should create', () => {
    expect(component).toBeTruthy();
  });
});
