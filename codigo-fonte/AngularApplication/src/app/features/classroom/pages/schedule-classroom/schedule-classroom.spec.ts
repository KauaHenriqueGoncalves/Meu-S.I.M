import { ComponentFixture, TestBed } from '@angular/core/testing';

import { ScheduleClassroom } from './schedule-classroom';

describe('ScheduleClassroom', () => {
  let component: ScheduleClassroom;
  let fixture: ComponentFixture<ScheduleClassroom>;

  beforeEach(async () => {
    await TestBed.configureTestingModule({
      imports: [ScheduleClassroom]
    })
    .compileComponents();

    fixture = TestBed.createComponent(ScheduleClassroom);
    component = fixture.componentInstance;
    fixture.detectChanges();
  });

  it('should create', () => {
    expect(component).toBeTruthy();
  });
});
