import { ErrorHandler, Injectable } from "@angular/core";

@Injectable()
export class GlobalErrorHandler implements ErrorHandler {
  handleError(error: any): void {
    if (error?.message?.includes('Script error')) {
      return; // ignora
    }
    console.error(error);
  }
}