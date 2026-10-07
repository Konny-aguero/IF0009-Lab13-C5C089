import { AbstractControl, ValidationErrors } from '@angular/forms';

export function positivoValidator(control: AbstractControl): ValidationErrors | null {
  const valor = control.value;
  return typeof valor === 'number' && Number.isInteger(valor) && valor > 0
    ? null : { positivo: true };
}
