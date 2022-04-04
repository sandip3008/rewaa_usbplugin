import { registerPlugin } from '@capacitor/core';
const USBPrinter = registerPlugin('USBPrinter', {
    web: () => import('./web').then(m => new m.USBPrinterWeb()),
});
export * from './definitions';
export { USBPrinter };
//# sourceMappingURL=index.js.map