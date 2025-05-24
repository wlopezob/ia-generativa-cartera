import { Component, Input, Output, EventEmitter, OnInit, OnDestroy, OnChanges, SimpleChanges, ChangeDetectionStrategy, ChangeDetectorRef } from '@angular/core';
import { CommonModule } from '@angular/common';
import { UIStateService } from '../../services/ui-state.service';
import { SidebarComponent } from '../../../layout/sidebar/sidebar.component';
import { Subscription } from 'rxjs';
import { trigger, state, style, animate, transition, AnimationEvent } from '@angular/animations';

@Component({
  selector: 'app-sidebar-orchestrator',
  standalone: true,
  imports: [CommonModule, SidebarComponent],
  templateUrl: './sidebar-orchestrator.component.html',
  styleUrls: ['./sidebar-orchestrator.component.css'],
  changeDetection: ChangeDetectionStrategy.OnPush,
  animations: [
    trigger('sidebarAnimation', [
      state('visible', style({
        transform: 'translateX(0)'
      })),
      state('hidden', style({
        transform: 'translateX(-100%)'
      })),
      transition('visible <=> hidden', [
        animate('300ms cubic-bezier(0.4, 0, 0.2, 1)')
      ])
    ]),
    trigger('overlayAnimation', [
      state('visible', style({
        opacity: 1,
        visibility: 'visible'
      })),
      state('hidden', style({
        opacity: 0,
        visibility: 'hidden'
      })),
      transition('visible => hidden', [
        animate('200ms cubic-bezier(0.4, 0, 0.2, 1)')
      ]),
      transition('hidden => visible', [
        style({ visibility: 'visible', opacity: 0 }),
        animate('200ms cubic-bezier(0.4, 0, 0.2, 1)')
      ])
    ])
  ]
})
export class SidebarOrchestratorComponent implements OnInit, OnDestroy, OnChanges {
  @Input() rooms: any[] = [];
  @Input() activeRoomIndex = 0;
  @Input() isMobile = false;

  sidebarVisible = true;
  private subscription = new Subscription();
  private animationComplete = true;

  constructor(
    private uiState: UIStateService,
    private cdr: ChangeDetectorRef
  ) {}

  ngOnChanges(changes: SimpleChanges): void {
    // Force change detection when rooms or activeRoomIndex changes
    if (changes['rooms'] || changes['activeRoomIndex']) {
      this.cdr.markForCheck();
    }
  }

  ngOnInit(): void {
    this.subscription.add(
      this.uiState.sidebarVisible$.subscribe(visible => {
        if (this.sidebarVisible !== visible) {
          this.sidebarVisible = visible;
          
          // Añadir/quitar clase a body para prevenir scroll cuando el sidebar está abierto en móvil
          if (this.isMobile) {
            if (visible) {
              document.body.classList.add('sidebar-open');
            } else {
              document.body.classList.remove('sidebar-open');
            }
          }
          
          // Force change detection after sidebar visibility change
          this.cdr.markForCheck();
        }
      })
    );
  }

  ngOnDestroy(): void {
    this.subscription.unsubscribe();
  }

  toggleSidebar(): void {
    // Only toggle if animation is complete to prevent rapid toggling
    if (this.animationComplete) {
      this.animationComplete = false;
      this.uiState.toggleSidebar();
    }
  }

  onAnimationStart(event: AnimationEvent): void {
    this.animationComplete = false;
  }

  onAnimationDone(event: AnimationEvent): void {
    this.animationComplete = true;
    
    // Force change detection after animation completes
    // This ensures room list is updated in the view
    this.cdr.detectChanges();
  }

  onAddRoom(): void {
    // Explicitly emit the event
    this.addRoom.emit();
    
    // Force change detection after emitting event
    this.cdr.detectChanges();
  }

  onRemoveRoom(index: number): void {
    this.removeRoom.emit(index);
  }

  onSetActiveRoom(index: number): void {
    this.setActiveRoom.emit(index);
    
    // En móviles, cerrar automáticamente el sidebar al seleccionar una sala
    if (this.isMobile) {
      this.uiState.setSidebarVisible(false);
    }
  }

  // Declaración de eventos de salida
  @Output() addRoom = new EventEmitter<void>();
  @Output() removeRoom = new EventEmitter<number>();
  @Output() setActiveRoom = new EventEmitter<number>();
} 