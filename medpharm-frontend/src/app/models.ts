export interface Sesion { token: string; username: string; rol: string; }
export interface Medicamento { id: number; codigo: string; nombre: string; stock: number; precioUnitario: number; }
export interface Receta {
  id: number; codigoReceta: string; pacienteNombre: string; estado: string; fechaEmision: string;
  medico: { nombreCompleto: string };
  detalles: { medicamento: Medicamento; cantidad: number; dosisIndicada: string }[];
}
export interface NuevaReceta {
  pacienteNombre: string;
  detalles: { medicamentoId: number; cantidad: number; dosisIndicada: string }[];
}
export const API = 'http://localhost:8080/api/v1';
