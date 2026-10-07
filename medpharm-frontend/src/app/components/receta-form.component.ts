import { Component, inject, signal } from '@angular/core';
import { FormBuilder, ReactiveFormsModule, Validators } from '@angular/forms';
import { Router, RouterLink } from '@angular/router';
import { RecetaService } from '../services/receta.service';
import { Medicamento } from '../models';
import { positivoValidator } from '../validators/positivo.validator';

@Component({
  selector: 'app-receta-form',
  imports: [ReactiveFormsModule, RouterLink],
  template: `
    <h2>Nueva receta</h2>
    <form [formGroup]="form" (ngSubmit)="guardar()">
      <label>Nombre del paciente <input formControlName="pacienteNombre"></label>
      @if (form.controls.pacienteNombre.touched && form.controls.pacienteNombre.invalid) {
        <p class="error">Ingrese el nombre del paciente (entre 5 y 120 caracteres).</p>
      }
      <div formArrayName="detalles">
        @for (detalle of detalles.controls; track detalle; let i = $index) {
          <fieldset [formGroupName]="i">
            <legend>Medicamento {{ i + 1 }}</legend>
            <label>Medicamento
              <select formControlName="medicamentoId">
                <option [ngValue]="null">Seleccione un medicamento</option>
                @for (med of medicamentos(); track med.id) {
                  <option [ngValue]="med.id">{{ med.nombre }} (stock: {{ med.stock }})</option>
                }
              </select>
            </label>
            <label>Cantidad <input type="number" min="1" step="1" formControlName="cantidad"></label>
            <label>Dosis indicada <input formControlName="dosisIndicada" maxlength="255"></label>
            @if (detalle.touched && detalle.invalid) {
              <p class="error">Seleccione un medicamento, indique la dosis y una cantidad entera mayor que 0.</p>
            }
            <button type="button" (click)="detalles.removeAt(i)" [disabled]="detalles.length === 1">Quitar</button>
          </fieldset>
        }
      </div>
      <button type="button" (click)="agregar()">Agregar medicamento</button>
      <button type="submit" [disabled]="enviando()">Guardar receta</button>
      <a routerLink="/recetas">Volver</a>
      @if (error()) { <p class="error" role="alert">{{ error() }}</p> }
    </form>
  `
})
export class RecetaFormComponent {
  private fb = inject(FormBuilder);
  private servicio = inject(RecetaService);
  private router = inject(Router);
  medicamentos = signal<Medicamento[]>([]);
  error = signal('');
  enviando = signal(false);
  form = this.fb.group({
    pacienteNombre: ['', [Validators.required, Validators.minLength(5), Validators.maxLength(120)]],
    detalles: this.fb.array([this.crearDetalle()])
  });
  get detalles() { return this.form.controls.detalles; }

  constructor() {
    this.servicio.medicamentos().subscribe({
      next: medicamentos => this.medicamentos.set(medicamentos),
      error: err => this.error.set(err.error?.detail || 'No se pudo cargar el catálogo.')
    });
  }

  private crearDetalle() {
    return this.fb.group({
      medicamentoId: this.fb.control<number | null>(null, Validators.required),
      cantidad: [1, [Validators.required, positivoValidator]],
      dosisIndicada: ['', [Validators.required, Validators.maxLength(255)]]
    });
  }
  agregar() { this.detalles.push(this.crearDetalle()); }

  guardar() {
    this.form.markAllAsTouched();
    if (this.form.invalid || this.enviando()) return;
    const datos = this.form.getRawValue();
    this.enviando.set(true);
    this.error.set('');
    this.servicio.crear({
      pacienteNombre: datos.pacienteNombre!,
      detalles: datos.detalles.map(d => ({
        medicamentoId: d.medicamentoId!, cantidad: d.cantidad!, dosisIndicada: d.dosisIndicada!
      }))
    }).subscribe({
      next: () => { this.enviando.set(false); this.router.navigate(['/recetas']); },
      error: err => {
        this.enviando.set(false);
        this.error.set(err.error?.detail || 'No se pudo guardar la receta.');
      }
    });
  }
}
