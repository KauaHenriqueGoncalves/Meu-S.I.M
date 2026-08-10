import { ComponentFixture, TestBed } from '@angular/core/testing';

import { LogInAdmin } from './log-in-admin';

describe('LogInAdmin', () => {
  let component: LogInAdmin;
  let fixture: ComponentFixture<LogInAdmin>;

  beforeEach(async () => {
    await TestBed.configureTestingModule({
      imports: [LogInAdmin]
    })
    .compileComponents();

    fixture = TestBed.createComponent(LogInAdmin);
    component = fixture.componentInstance;
    fixture.detectChanges();
  });

  it('should create', () => {
    expect(component).toBeTruthy();
  });
});
