import { ComponentFixture, TestBed } from '@angular/core/testing';

import { RegisterStepSchool } from './register-step-school';

describe('RegisterStepSchool', () => {
  let component: RegisterStepSchool;
  let fixture: ComponentFixture<RegisterStepSchool>;

  beforeEach(async () => {
    await TestBed.configureTestingModule({
      imports: [RegisterStepSchool]
    })
    .compileComponents();

    fixture = TestBed.createComponent(RegisterStepSchool);
    component = fixture.componentInstance;
    fixture.detectChanges();
  });

  it('should create', () => {
    expect(component).toBeTruthy();
  });
});
