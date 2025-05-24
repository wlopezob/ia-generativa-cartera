import { Component } from '@angular/core';
import { SvgLogoComponent } from '../shared/svg-logo/svg-logo.component';

@Component({
  selector: 'app-header',
  imports: [SvgLogoComponent],
  templateUrl: './header.component.html',
  styleUrl: './header.component.css'
})
export class HeaderComponent {

}
