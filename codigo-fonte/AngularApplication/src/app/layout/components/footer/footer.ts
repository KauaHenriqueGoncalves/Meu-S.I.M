import { ChangeDetectorRef, Component } from '@angular/core';
import { RouterLink } from '@angular/router';

@Component({
  selector: 'app-footer',
  imports: [RouterLink],
  templateUrl: './footer.html',
  styleUrl: './footer.sass',
})
export class Footer {
  copiedItem: string | null = null;

  constructor(
    private cdr: ChangeDetectorRef
  ) { }

  copyToClipboard(text: string, type: string) {
    navigator.clipboard.writeText(text).then(() => {
      this.copiedItem = type;
      this.cdr.detectChanges();
      setTimeout(() => {
        this.copiedItem = null;
        this.cdr.detectChanges();
      }, 2000);
    }).catch(err => {
      console.error('Erro ao copiar: ', err);
    });
  }
}
