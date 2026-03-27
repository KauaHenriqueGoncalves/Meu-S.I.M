import { Directive, HostListener } from '@angular/core';
import { NgControl } from '@angular/forms';

@Directive({
    selector: '[numbersOnly]'
})
export class NumbersOnlyDirective {
    constructor(private control: NgControl) { }

    @HostListener('input', ['$event'])
    onInput(event: Event) {
        const input = event.target as HTMLInputElement;
        input.value = input.value.replace(/\D/g, '');
        this.control.control?.setValue(input.value, { emitEvent: false });
    }
}