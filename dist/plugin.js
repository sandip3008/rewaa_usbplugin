var capacitorExample = (function (exports, core) {
    'use strict';

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

    Object.defineProperty(exports, '__esModule', { value: true });

    return exports;

})({}, capacitorExports);
//# sourceMappingURL=plugin.js.map
