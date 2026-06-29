import { ComponentFixture, TestBed } from '@angular/core/testing';

import { Classrooms } from './classrooms';

describe('Classrooms', () => {
  let component: Classrooms;
  let fixture: ComponentFixture<Classrooms>;

  beforeEach(async () => {
    await TestBed.configureTestingModule({
      imports: [Classrooms]
    })
    .compileComponents();

    fixture = TestBed.createComponent(Classrooms);
    component = fixture.componentInstance;
    fixture.detectChanges();
  });

  it('should create', () => {
    expect(component).toBeTruthy();
  });
});
