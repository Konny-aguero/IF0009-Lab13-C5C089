import { Injectable, inject } from '@angular/core';
import { HttpClient } from '@angular/common/http';
import { API, Medicamento, NuevaReceta, Receta } from '../models';

@Injectable({ providedIn: 'root' })
export class RecetaService {
  private http = inject(HttpClient);
  listar() { return this.http.get<Receta[]>(`${API}/recetas`); }
  medicamentos() { return this.http.get<Medicamento[]>(`${API}/medicamentos`); }
  crear(receta: NuevaReceta) { return this.http.post<Receta>(`${API}/recetas`, receta); }
  cambiarEstado(id: number, estado: string) {
    return this.http.patch<Receta>(`${API}/recetas/${id}/estado`, { estado });
  }
}
