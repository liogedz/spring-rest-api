import {Component, effect, HostBinding, signal} from '@angular/core';
import {RouterOutlet} from '@angular/router';

@Component({
  selector: 'app-root',
  imports: [RouterOutlet],
  templateUrl: './app.html',
  styleUrl: './app.css'
})
export class App {
  protected readonly title = signal('frontend');

  darkMode = signal<boolean>(
    JSON.parse(window.localStorage.getItem("darkMode") ?? "false")
  );

  @HostBinding("class.dark") get mode() {
    return this.darkMode();
  }

  constructor() {
    effect(() => {
      window.localStorage.setItem("darkMode", JSON.stringify(this.darkMode()));
    });
  }
}
