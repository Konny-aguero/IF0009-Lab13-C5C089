import { Component, computed, inject, signal } from '@angular/core';
import { DatePipe } from '@angular/common';
import { RecetaService } from '../services/receta.service';
import { Receta } from '../models';

@Component({
  selector: 'app-recetas-list',
  imports: [DatePipe],
  template: `
    <h2>Recetas</h2>
    <label>Estado
      <select [value]="estado()" (change)="estado.set($any($event.target).value)">
        <option>TODAS</option><option>PENDIENTE</option><option>DESPACHADA</option><option>CANCELADA</option>
      </select>
    </label>
    @if (error()) { <p class="error" role="alert">{{ error() }}</p> }
    <div class="tabla">
      <table>
        <thead><tr><th>Código</th><th>Paciente</th><th>Médico</th><th>Fecha</th><th>Medicamentos</th><th>Estado</th><th>Acciones</th></tr></thead>
        <tbody>
          @for (receta of filtradas(); track receta.id) {
            <tr>
              <td>{{ receta.codigoReceta }}</td><td>{{ receta.pacienteNombre }}</td>
              <td>{{ receta.medico.nombreCompleto }}</td><td>{{ receta.fechaEmision | date:'dd/MM/yyyy HH:mm' }}</td>
              <td>@for (detalle of receta.detalles; track $index) {
                <p>{{ detalle.medicamento.nombre }}: {{ detalle.cantidad }}<br>{{ detalle.dosisIndicada }}</p>
              }</td>
              <td><span [class]="'estado ' + receta.estado">{{ receta.estado }}</span></td>
              <td>@if (receta.estado === 'PENDIENTE') {
                <button (click)="cambiar(receta.id, 'DESPACHADA')" [disabled]="procesando()">Despachar</button>
                <button (click)="cambiar(receta.id, 'CANCELADA')" [disabled]="procesando()">Cancelar</button>
              }</td>
            </tr>
          } @empty { <tr><td colspan="7">No hay recetas para mostrar.</td></tr> }
        </tbody>
      </table>
    </div>
  `
})
export class RecetasListComponent {
  private servicio = inject(RecetaService);
  recetas = signal<Receta[]>([]);
  estado = signal('TODAS');
  error = signal('');
  procesando = signal(false);
  filtradas = computed(() => this.recetas().filter(r => this.estado() === 'TODAS' || r.estado === this.estado()));

  constructor() {
    this.servicio.listar().subscribe({
      next: recetas => this.recetas.set(recetas),
      error: err => this.error.set(err.error?.detail || 'No se pudieron cargar las recetas.')
    });
  }

  cambiar(id: number, estado: string) {
    this.error.set('');
    this.procesando.set(true);
    this.servicio.cambiarEstado(id, estado).subscribe({
      next: receta => {
        this.recetas.update(lista => lista.map(r => r.id === id ? receta : r));
        this.procesando.set(false);
      },
      error: err => {
        this.error.set(err.error?.detail || 'No se pudo cambiar el estado.');
        this.procesando.set(false);
      }
    });
  }
}
