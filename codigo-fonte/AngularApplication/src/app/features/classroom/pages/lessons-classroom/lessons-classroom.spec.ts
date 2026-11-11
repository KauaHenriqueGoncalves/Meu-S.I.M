import { ComponentFixture, TestBed } from '@angular/core/testing';

import { LessonsClassroom } from './lessons-classroom';

describe('LessonsClassroom', () => {
  let component: LessonsClassroom;
  let fixture: ComponentFixture<LessonsClassroom>;

  beforeEach(async () => {
    await TestBed.configureTestingModule({
      imports: [LessonsClassroom]
    })
    .compileComponents();

    fixture = TestBed.createComponent(LessonsClassroom);
    component = fixture.componentInstance;
    fixture.detectChanges();
  });

  it('should create', () => {
    expect(component).toBeTruthy();
  });
});
