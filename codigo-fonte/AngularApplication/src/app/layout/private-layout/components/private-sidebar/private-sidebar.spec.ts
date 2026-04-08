import { ComponentFixture, TestBed } from '@angular/core/testing';

import { PrivateSidebar } from './private-sidebar';

describe('PrivateSidebar', () => {
  let component: PrivateSidebar;
  let fixture: ComponentFixture<PrivateSidebar>;

  beforeEach(async () => {
    await TestBed.configureTestingModule({
      imports: [PrivateSidebar]
    })
    .compileComponents();

    fixture = TestBed.createComponent(PrivateSidebar);
    component = fixture.componentInstance;
    fixture.detectChanges();
  });

  it('should create', () => {
    expect(component).toBeTruthy();
  });
});
