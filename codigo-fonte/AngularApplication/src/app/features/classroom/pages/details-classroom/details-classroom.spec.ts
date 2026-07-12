import { ComponentFixture, TestBed } from '@angular/core/testing';

import { DetailsClassroom } from './details-classroom';

describe('DetailsClassroom', () => {
  let component: DetailsClassroom;
  let fixture: ComponentFixture<DetailsClassroom>;

  beforeEach(async () => {
    await TestBed.configureTestingModule({
      imports: [DetailsClassroom]
    })
    .compileComponents();

    fixture = TestBed.createComponent(DetailsClassroom);
    component = fixture.componentInstance;
    fixture.detectChanges();
  });

  it('should create', () => {
    expect(component).toBeTruthy();
  });
});
