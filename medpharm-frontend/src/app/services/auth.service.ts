import { Injectable, inject, signal } from '@angular/core';
import { HttpClient } from '@angular/common/http';
import { tap } from 'rxjs';
import { API, Sesion } from '../models';

@Injectable({ providedIn: 'root' })
export class AuthService {
  private http = inject(HttpClient);
  sesion = signal<Sesion | null>(this.leerSesion());

  private leerSesion(): Sesion | null {
    try { return JSON.parse(localStorage.getItem('sesion') || 'null'); }
    catch { return null; }
  }

  login(credentials: { username: string; password: string }) {
    return this.http.post<Sesion>(`${API}/auth/login`, credentials).pipe(tap(sesion => {
      localStorage.setItem('sesion', JSON.stringify(sesion));
      this.sesion.set(sesion);
    }));
  }

  getToken() { return this.sesion()?.token ?? null; }

  logout() {
    localStorage.removeItem('sesion');
    this.sesion.set(null);
  }
}
