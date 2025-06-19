import { NgModule } from '@angular/core';
import { CommonModule } from '@angular/common';
import { WebSocketService } from './services/websocket.service';

@NgModule({
  imports: [
    CommonModule
  ],
  providers: [
    WebSocketService
  ]
})
export class SharedModule { } 