import { WebPlugin } from '@capacitor/core';
import type { USBPrinterPlugin } from './definitions';
export declare class USBPrinterWeb extends WebPlugin implements USBPrinterPlugin {
    getPrinterStatus(options: {
        value: string;
    }): Promise<{
        value: string;
    }>;
    echo(options: {
        value: string;
    }): Promise<{
        value: string;
    }>;
}
