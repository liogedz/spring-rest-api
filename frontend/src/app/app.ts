import {Component, OnInit} from '@angular/core';
import {RouterOutlet} from '@angular/router';
import {DarkThemeSelectorService} from './services/dark-theme-selector-service';

@Component({
  selector: 'app-root',
  imports: [RouterOutlet],
  templateUrl: './app.html',
  styleUrl: './app.css',
})
export class App {
  constructor(protected darkThemeSelectorService: DarkThemeSelectorService) {
  }

}
