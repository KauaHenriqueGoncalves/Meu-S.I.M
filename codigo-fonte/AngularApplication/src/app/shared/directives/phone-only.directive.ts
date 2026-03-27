import { Directive, HostListener } from '@angular/core';

@Directive({
  selector: '[appPhoneOnly]'
})
export class PhoneOnlyDirective {
  private allowedRegex = /^[0-9+\-()\s]*$/;

  @HostListener('input', ['$event'])
  onInput(event: Event) {
    const input = event.target as HTMLInputElement;

    if (!this.allowedRegex.test(input.value)) {
      input.value = input.value.replace(/[^0-9+\-()]/g, '');
    }
  }

  @HostListener('paste', ['$event'])
  onPaste(event: ClipboardEvent) {
    const pasted = event.clipboardData?.getData('text') ?? '';

    if (!this.allowedRegex.test(pasted)) {
      event.preventDefault();

      const cleaned = pasted.replace(/[^0-9+\-()]/g, '');
      document.execCommand('insertText', false, cleaned);
    }
  }
}