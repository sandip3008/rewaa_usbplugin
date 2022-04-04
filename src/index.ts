import { registerPlugin } from '@capacitor/core';

import type { USBPrinterPlugin } from './definitions';

const USBPrinter = registerPlugin<USBPrinterPlugin>('USBPrinter', {
  web: () => import('./web').then(m => new m.USBPrinterWeb()),
});

export * from './definitions';
export { USBPrinter };
