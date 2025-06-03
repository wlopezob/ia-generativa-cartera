import { Injectable, PLATFORM_ID, Inject } from '@angular/core';
import { BehaviorSubject, Observable } from 'rxjs';
import { isPlatformBrowser } from '@angular/common';

interface UIState {
  sidebarVisible: boolean;
}

const defaultState: UIState = {
  sidebarVisible: true
};

@Injectable({
  providedIn: 'root'
})
export class UIStateService {
  private readonly STORAGE_KEY = 'legalaco_ui_state';
  private stateSubject: BehaviorSubject<UIState>;
  private isBrowser: boolean;
  
  constructor(@Inject(PLATFORM_ID) private platformId: Object) {
    this.isBrowser = isPlatformBrowser(this.platformId);
    const savedState = this.loadState();
    this.stateSubject = new BehaviorSubject<UIState>(savedState);
  }
  
  private loadState(): UIState {
    if (!this.isBrowser) {
      return defaultState;
    }
    
    try {
      const savedState = localStorage.getItem(this.STORAGE_KEY);
      return savedState ? { ...defaultState, ...JSON.parse(savedState) } : defaultState;
    } catch (error) {
      console.error('Error loading UI state from localStorage:', error);
      return defaultState;
    }
  }
  
  private saveState(state: UIState): void {
    if (!this.isBrowser) {
      return;
    }
    
    try {
      localStorage.setItem(this.STORAGE_KEY, JSON.stringify(state));
    } catch (error) {
      console.error('Error saving UI state to localStorage:', error);
    }
  }
  
  get state$(): Observable<UIState> {
    return this.stateSubject.asObservable();
  }
  
  get sidebarVisible$(): Observable<boolean> {
    return new Observable<boolean>(observer => {
      this.state$.subscribe(state => {
        observer.next(state.sidebarVisible);
      });
    });
  }
  
  setSidebarVisible(visible: boolean): void {
    const currentState = this.stateSubject.value;
    const newState = { ...currentState, sidebarVisible: visible };
    this.stateSubject.next(newState);
    this.saveState(newState);
  }
  
  toggleSidebar(): void {
    const currentState = this.stateSubject.value;
    const newState = { ...currentState, sidebarVisible: !currentState.sidebarVisible };
    this.stateSubject.next(newState);
    this.saveState(newState);
  }
} 