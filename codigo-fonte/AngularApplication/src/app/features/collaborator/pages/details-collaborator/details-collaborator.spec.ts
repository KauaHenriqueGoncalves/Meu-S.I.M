import { ComponentFixture, TestBed } from '@angular/core/testing';

import { DetailsCollaborator } from './details-collaborator';

describe('DetailsCollaborator', () => {
  let component: DetailsCollaborator;
  let fixture: ComponentFixture<DetailsCollaborator>;

  beforeEach(async () => {
    await TestBed.configureTestingModule({
      imports: [DetailsCollaborator]
    })
    .compileComponents();

    fixture = TestBed.createComponent(DetailsCollaborator);
    component = fixture.componentInstance;
    fixture.detectChanges();
  });

  it('should create', () => {
    expect(component).toBeTruthy();
  });
});
