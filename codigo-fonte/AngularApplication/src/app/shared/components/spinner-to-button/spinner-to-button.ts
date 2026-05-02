import { NgStyle } from '@angular/common';
import { Component, Input } from '@angular/core';

@Component({
  selector: 'app-spinner-to-button',
  standalone: true,
  imports: [NgStyle],
  templateUrl: './spinner-to-button.html',
  styleUrl: './spinner-to-button.sass',
})
export class SpinnerToButton { 
  @Input() size: number = 16;
  @Input() border: number = 2; 
  @Input() color: string = 'white';
  @Input() speed: number = 0.6; 
}
