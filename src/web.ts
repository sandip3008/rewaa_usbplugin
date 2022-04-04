import { WebPlugin } from '@capacitor/core';

import type { USBPrinterPlugin } from './definitions';

export class USBPrinterWeb extends WebPlugin implements USBPrinterPlugin {
  async getPrinterStatus(options: { value: string }) {
    console.log('getPrinterStatus web 7', options);
    return options;
  }
  async echo(options: { value: string }) {
    console.log('ECHO', options);
    return options;
  }
}
