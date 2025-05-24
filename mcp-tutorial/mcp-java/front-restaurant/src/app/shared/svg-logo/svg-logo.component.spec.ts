import { ComponentFixture, TestBed } from '@angular/core/testing';

import { SvgLogoComponent } from './svg-logo.component';

describe('SvgLogoComponent', () => {
  let component: SvgLogoComponent;
  let fixture: ComponentFixture<SvgLogoComponent>;

  beforeEach(async () => {
    await TestBed.configureTestingModule({
      imports: [SvgLogoComponent]
    })
    .compileComponents();

    fixture = TestBed.createComponent(SvgLogoComponent);
    component = fixture.componentInstance;
    fixture.detectChanges();
  });

  it('should create', () => {
    expect(component).toBeTruthy();
  });
});
