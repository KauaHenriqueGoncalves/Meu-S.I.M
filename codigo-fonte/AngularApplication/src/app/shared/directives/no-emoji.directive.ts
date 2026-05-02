import { Directive, HostListener } from '@angular/core';

@Directive({
    selector: '[noEmoji]'
})
export class NoEmojiDirective {
    private emojiRegex = /[\p{Emoji_Presentation}\p{Extended_Pictographic}]/gu;

    @HostListener('input', ['$event'])
    onInput(event: any) {
        const value = event.target.value;
        const cleaned = value.replace(this.emojiRegex, '');

        if (value !== cleaned) {
            event.target.value = cleaned;
            event.target.dispatchEvent(new Event('input'));
        }
    }

    @HostListener('paste', ['$event'])
    onPaste(event: ClipboardEvent) {
        const pasted = event.clipboardData?.getData('text') || '';

        if (this.emojiRegex.test(pasted)) {
            event.preventDefault();
            const cleaned = pasted.replace(this.emojiRegex, '');

            document.execCommand('insertText', false, cleaned);
        }
    }
}