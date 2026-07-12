import { AbstractControl, ValidationErrors, ValidatorFn } from '@angular/forms';

export function cnpjValidator(): ValidatorFn {
  return (control: AbstractControl): ValidationErrors | null => {
    let cnpj = (control.value || '').replace(/\D/g, '');

    if (!cnpj || cnpj.length !== 14) {
      return { invalidCnpj: true };
    }

    if (/^(\d)\1+$/.test(cnpj)) {
      return { invalidCnpj: true };
    }

    let size = cnpj.length - 2;
    let numbers = cnpj.substring(0, size);
    let digits = cnpj.substring(size);

    let sum = 0;
    let pos = size - 7;

    for (let i = size; i >= 1; i--) {
      sum += Number(numbers.charAt(size - i)) * pos--;
      if (pos < 2) pos = 9;
    }

    let result = sum % 11 < 2 ? 0 : 11 - (sum % 11);
    if (result !== Number(digits.charAt(0))) {
      return { invalidCnpj: true };
    }

    size = size + 1;
    numbers = cnpj.substring(0, size);

    sum = 0;
    pos = size - 7;

    for (let i = size; i >= 1; i--) {
      sum += Number(numbers.charAt(size - i)) * pos--;
      if (pos < 2) pos = 9;
    }

    result = sum % 11 < 2 ? 0 : 11 - (sum % 11);

    return result === Number(digits.charAt(1)) ? null : { invalidCnpj: true };
  };
}