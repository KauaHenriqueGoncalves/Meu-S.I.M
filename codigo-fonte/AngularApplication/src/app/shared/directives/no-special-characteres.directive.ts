import { Directive, ElementRef, HostListener } from '@angular/core';

@Directive({
    selector: '[noSpecialCharacteres]'
})
export class NoSpecialCharacteresDirective {
    constructor(
        private el: ElementRef<HTMLInputElement>
    ) { }

    private sanitize(value: string): string {
        return value.replace(/[^\p{L}\p{N}\s]/gu, '');
    }

    @HostListener('input', ['$event'])
    onInput(event: any) {
        const input = event.target as HTMLInputElement;
        const sanitizedValue = this.sanitize(input.value);
        if (input.value !== sanitizedValue) {
            input.value = sanitizedValue;
            input.dispatchEvent(new Event('input', { bubbles: true }));
        }
    }

    @HostListener('paste', ['$event'])
    onPaste(event: ClipboardEvent) {
        event.preventDefault();
        const pastedText = event.clipboardData?.getData('text') || '';
        const sanitizedText = this.sanitize(pastedText);
        const input = this.el.nativeElement;
        const start = input.selectionStart ?? 0;
        const end = input.selectionEnd ?? 0;
        input.value =
            input.value.substring(0, start) +
            sanitizedText +
            input.value.substring(end);

        input.dispatchEvent(new Event('input', { bubbles: true }));
    }
}