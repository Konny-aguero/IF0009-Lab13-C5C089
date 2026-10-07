import { Component, inject, signal } from '@angular/core';
import { FormBuilder, ReactiveFormsModule, Validators } from '@angular/forms';
import { Router } from '@angular/router';
import { AuthService } from '../services/auth.service';

@Component({
  selector: 'app-login',
  imports: [ReactiveFormsModule],
  template: `
    <h2>Iniciar sesión</h2>
    <form [formGroup]="form" (ngSubmit)="entrar()">
      <label>Usuario <input formControlName="username" autocomplete="username"></label>
      @if (form.controls.username.touched && form.controls.username.invalid) {
        <p class="error">El usuario es requerido.</p>
      }
      <label>Contraseña <input type="password" formControlName="password" autocomplete="current-password"></label>
      @if (form.controls.password.touched && form.controls.password.invalid) {
        <p class="error">La contraseña debe tener al menos 6 caracteres.</p>
      }
      <button type="submit" [disabled]="enviando()">Ingresar</button>
      @if (error()) { <p class="error" role="alert">{{ error() }}</p> }
    </form>
  `
})
export class LoginComponent {
  private auth = inject(AuthService);
  private router = inject(Router);
  form = inject(FormBuilder).nonNullable.group({
    username: ['', Validators.required],
    password: ['', [Validators.required, Validators.minLength(6)]]
  });
  error = signal('');
  enviando = signal(false);

  entrar() {
    this.form.markAllAsTouched();
    if (this.form.invalid || this.enviando()) return;
    this.error.set('');
    this.enviando.set(true);
    this.auth.login(this.form.getRawValue()).subscribe({
      next: () => { this.enviando.set(false); this.router.navigate(['/recetas']); },
      error: err => {
        this.enviando.set(false);
        this.error.set(err.error?.detail || 'No se pudo iniciar sesión. Revise la conexión.');
      }
    });
  }
}
