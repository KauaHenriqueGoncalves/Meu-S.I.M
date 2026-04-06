import { ComponentFixture, TestBed } from '@angular/core/testing';

import { LogInUser } from './log-in-user';

describe('LogInUser', () => {
  let component: LogInUser;
  let fixture: ComponentFixture<LogInUser>;

  beforeEach(async () => {
    await TestBed.configureTestingModule({
      imports: [LogInUser]
    })
    .compileComponents();

    fixture = TestBed.createComponent(LogInUser);
    component = fixture.componentInstance;
    fixture.detectChanges();
  });

  it('should create', () => {
    expect(component).toBeTruthy();
  });
});
