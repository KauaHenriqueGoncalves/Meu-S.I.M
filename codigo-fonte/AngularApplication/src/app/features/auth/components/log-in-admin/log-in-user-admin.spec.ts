import { ComponentFixture, TestBed } from '@angular/core/testing';

import { LogInUserAdmin } from './log-in-user-admin';

describe('LogInUserAdmin', () => {
  let component: LogInUserAdmin;
  let fixture: ComponentFixture<LogInUserAdmin>;

  beforeEach(async () => {
    await TestBed.configureTestingModule({
      imports: [LogInUserAdmin]
    })
    .compileComponents();

    fixture = TestBed.createComponent(LogInUserAdmin);
    component = fixture.componentInstance;
    fixture.detectChanges();
  });

  it('should create', () => {
    expect(component).toBeTruthy();
  });
});
