import { ComponentFixture, TestBed } from '@angular/core/testing';

import { CreateClassroom } from './create-classroom';

describe('CreateClassroom', () => {
  let component: CreateClassroom;
  let fixture: ComponentFixture<CreateClassroom>;

  beforeEach(async () => {
    await TestBed.configureTestingModule({
      imports: [CreateClassroom]
    })
    .compileComponents();

    fixture = TestBed.createComponent(CreateClassroom);
    component = fixture.componentInstance;
    fixture.detectChanges();
  });

  it('should create', () => {
    expect(component).toBeTruthy();
  });
});
