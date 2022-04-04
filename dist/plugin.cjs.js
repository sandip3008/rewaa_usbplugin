'use strict';

Object.defineProperty(exports, '__esModule', { value: true });

var core = require('@capacitor/core');

const USBPrinter = core.registerPlugin('USBPrinter', {
    web: () => Promise.resolve().then(function () { return web; }).then(m => new m.USBPrinterWeb()),
});

class USBPrinterWeb extends core.WebPlugin {
    async getPrinterStatus(options) {
        console.log('getPrinterStatus web 7', options);
        return options;
    }
    async echo(options) {
        console.log('ECHO', options);
        return options;
    }
}

var web = /*#__PURE__*/Object.freeze({
    __proto__: null,
    USBPrinterWeb: USBPrinterWeb
});

exports.USBPrinter = USBPrinter;
//# sourceMappingURL=plugin.cjs.js.map
