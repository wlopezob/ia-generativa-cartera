import { Component, EventEmitter, Input, Output } from '@angular/core';
import { FormsModule } from '@angular/forms';

@Component({
  selector: 'app-select-law-modal',
  standalone: true,
  imports: [FormsModule],
  templateUrl: './select-law-modal.component.html',
  styleUrls: ['./select-law-modal.component.css']
})
export class SelectLawModalComponent {
  @Input() isVisible = false;
  @Input() title = 'Seleccionar tipo de ley';
  @Input() confirmButtonText = 'Aceptar';
  @Input() cancelButtonText = 'Cancelar';
  
  @Output() confirm = new EventEmitter<string>();
  @Output() cancel = new EventEmitter<void>();
  
  selectedLaw = 'LEY GENERAL DE CONTRATACIONES PUBLICAS';
  laws = ['LEY GENERAL DE CONTRATACIONES PUBLICAS'];
  
  onConfirm(): void {
    this.confirm.emit(this.selectedLaw);
  }
  
  onCancel(): void {
    this.cancel.emit();
  }
} 