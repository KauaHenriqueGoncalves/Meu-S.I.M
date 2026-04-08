import { ComponentFixture, TestBed } from '@angular/core/testing';

import { PublicSidebar } from './public-sidebar';

describe('PublicSidebar', () => {
  let component: PublicSidebar;
  let fixture: ComponentFixture<PublicSidebar>;

  beforeEach(async () => {
    await TestBed.configureTestingModule({
      imports: [PublicSidebar]
    })
    .compileComponents();

    fixture = TestBed.createComponent(PublicSidebar);
    component = fixture.componentInstance;
    fixture.detectChanges();
  });

  it('should create', () => {
    expect(component).toBeTruthy();
  });
});
