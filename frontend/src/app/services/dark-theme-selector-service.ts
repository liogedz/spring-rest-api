import {Injectable, signal} from '@angular/core';

export enum AppTheme {
  LIGHT = 'light',
  DARK = 'dark',
}

const CLIENT_RENDER = typeof window !== 'undefined';
const SS_THEME = 'theme';

let selectedTheme: AppTheme | undefined = undefined;
if (CLIENT_RENDER) {
  selectedTheme = localStorage.getItem(SS_THEME) as AppTheme || undefined;
}

@Injectable({
  providedIn: 'root'
})
export class DarkThemeSelectorService {
  currentTheme = signal<AppTheme | undefined>(selectedTheme);

  toggleTheme() {
    const current = this.currentTheme();
    if (current === undefined) {
      this.setDarkTheme();
    } else if (current === AppTheme.DARK) {
      this.setLightTheme();
    } else {
      this.setSystemTheme();
    }
  }

  setLightTheme() {
    this.currentTheme.set(AppTheme.LIGHT);
    this.setToLocalStorage(AppTheme.LIGHT);
    this.removeClassFromHtml('dark');
  }

  setDarkTheme() {
    this.currentTheme.set(AppTheme.DARK);
    this.setToLocalStorage(AppTheme.DARK);
    this.addClassToHtml('dark');
  }

  setSystemTheme() {
    this.currentTheme.set(undefined);
    this.removeFromLocalStorage();
    if (isSystemDark()) {
      this.addClassToHtml('dark');
    } else {
      this.removeClassFromHtml('dark');
    }
  }

  private addClassToHtml(className: string) {
    if (CLIENT_RENDER) {
      document.documentElement.classList.add(className);
    }
  }

  private removeClassFromHtml(className: string) {
    if (CLIENT_RENDER) {
      document.documentElement.classList.remove(className);
    }
  }

  private setToLocalStorage(theme: AppTheme) {
    if (CLIENT_RENDER) {
      localStorage.setItem(SS_THEME, theme);
    }
  }

  private removeFromLocalStorage() {
    if (CLIENT_RENDER) {
      localStorage.removeItem(SS_THEME);
    }
  }
}

function isSystemDark() {
  return CLIENT_RENDER && window.matchMedia('(prefers-color-scheme: dark)').matches;
}
