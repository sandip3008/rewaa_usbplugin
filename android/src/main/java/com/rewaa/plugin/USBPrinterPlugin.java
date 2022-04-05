package com.rewaa.plugin;

import android.app.PendingIntent;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.hardware.usb.UsbConstants;
import android.hardware.usb.UsbDevice;
import android.hardware.usb.UsbDeviceConnection;
import android.hardware.usb.UsbEndpoint;
import android.hardware.usb.UsbInterface;
import android.hardware.usb.UsbManager;
import android.util.Base64;
import android.util.Log;
import android.widget.Toast;

import androidx.localbroadcastmanager.content.LocalBroadcastManager;

import com.getcapacitor.JSObject;
import com.getcapacitor.Plugin;
import com.getcapacitor.PluginCall;
import com.getcapacitor.PluginMethod;
import com.getcapacitor.annotation.CapacitorPlugin;

import java.util.List;

@CapacitorPlugin(name = "USBPrinter")
public class USBPrinterPlugin extends Plugin {

//    private Example implementation = new Example();
    protected static USBPrinterPlugin sPlugin;
    private Context mContext;
    private UsbManager mUSBManager;
    private PendingIntent mPermissionIndent;
    private UsbDevice mUsbDevice;
    private String TAG = "USBPrinterPlugin";

    private UsbDeviceConnection mUsbDeviceConnection;
    private UsbInterface mUsbInterface;
    private UsbEndpoint mEndPoint;

    private static final String ACTION_USB_PERMISSION = "com.rewaa.plugin.action.ACTION_USB_PERMISSION";

    private final BroadcastReceiver mUsbDeviceReceiver = new BroadcastReceiver() {
        @Override
        public void onReceive(Context context, Intent intent) {
            String action = intent.getAction();
            if (ACTION_USB_PERMISSION.equals(action)) {
                synchronized (this) {
                    UsbDevice usbDevice = intent.getParcelableExtra(UsbManager.EXTRA_DEVICE);
                    if (intent.getBooleanExtra(UsbManager.EXTRA_PERMISSION_GRANTED, false)) {
                        Log.i(TAG, "success to grant permission for device " + usbDevice.getDeviceId() + ", vendor_id: " + Integer.toString(usbDevice.getVendorId(), 16) + " product_id: " + Integer.toString(usbDevice.getProductId(), 16));
//                        Toast.makeText(context, "success to grant permission for device " + usbDevice.getDeviceId() + ", vendor_id: " + Integer.toString(usbDevice.getVendorId(), 16) + " product_id: " + Integer.toString(usbDevice.getProductId(), 16), Toast.LENGTH_LONG).show();
                        mUsbDevice = usbDevice;

                        JSObject ret = new JSObject();
                        ret.put("connectionStatus", "true");
                        ret.put("deviceName", usbDevice.getProductName());
                        ret.put("vid", Integer.toString(usbDevice.getVendorId(), 16));
                        ret.put("pid", Integer.toString(usbDevice.getProductId(), 16));
                        sendEvent(ret);

                    } else {
                        Toast.makeText(context, "You cannot able to use USB printer without permissions", Toast.LENGTH_LONG).show();
                    }
                }
            } else if (UsbManager.ACTION_USB_DEVICE_DETACHED.equals(action)) {
                UsbDevice usbDevice = intent.getParcelableExtra(UsbManager.EXTRA_DEVICE);
                if (usbDevice != null) {
                    Toast.makeText(context, "Attached USB printer disconnected", Toast.LENGTH_LONG).show();
                    closeConnectionIfExists();

                    JSObject ret = new JSObject();
                    ret.put("deviceName", usbDevice.getProductName());
                    sendDisconnectEvent(ret);

                }
            } else if (UsbManager.ACTION_USB_ACCESSORY_ATTACHED.equals(action) || UsbManager.ACTION_USB_DEVICE_ATTACHED.equals(action)) {
                synchronized (this) {
                    if (mContext != null) {
                        UsbDevice device = intent.getParcelableExtra(UsbManager.EXTRA_DEVICE);
                        if (device != null) {
//                            Toast.makeText(context, "Device attached: "+device.getManufacturerName(), Toast.LENGTH_LONG).show();

                            if(mUSBManager!=null && !mUSBManager.hasPermission(device)){
                                closeConnectionIfExists();
                                mUSBManager.requestPermission(device, mPermissionIndent);
                                return;
                            }

                            Log.e("onBrOADCASTRECEIVE","connect: true"+" devicename: "+device.getManufacturerName());
                            JSObject ret = new JSObject();
                            ret.put("connectionStatus", "true");
                            ret.put("deviceName", device.getProductName());
                            ret.put("vid", Integer.toString(device.getVendorId(), 16));
                            ret.put("pid", Integer.toString(device.getProductId(), 16));
                            sendEvent(ret);
                        }


                    }
                }
            }
        }
    };

    public void closeConnectionIfExists() {
        if (mUsbDeviceConnection != null) {
            mUsbDeviceConnection.releaseInterface(mUsbInterface);
            mUsbDeviceConnection.close();
            mUsbInterface = null;
            mEndPoint = null;
            mUsbDeviceConnection = null;
        }
    }


   @PluginMethod()
    public void getPrinterStatus(PluginCall call) {
        String value = call.getString("value");
        Log.i("TAG", "getPrinterStatus: " + call );
        JSObject ret = new JSObject();
        ret.put("value1", "Abc##");
        ret.put("status", "connected");
        call.resolve(ret);
    }


    /*public String startTimer(String value) {
        new Timer().scheduleAtFixedRate(new TimerTask(){
            @Override
            public void run(){
                Log.i("tag", "A Log every 3 seconds");
                JSObject ret = new JSObject();
                ret.put("value1", "Abc##");
                ret.put("value2", String.valueOf(System.currentTimeMillis()));
                notifyListeners("usbPrinterConnect", ret);
            }
        },0,3000);
        return value;
    }*/

    public void sendEvent(JSObject data) {
        notifyListeners("usbPrinterConnect", data);
    }


    public void sendDisconnectEvent(JSObject data) {
        notifyListeners("usbPrinterDisconnect", data);
    }

    @Override
    public void load() {
        super.load();
        sPlugin = this;
        this.mContext = getContext();
        this.mUSBManager = (UsbManager) this.mContext.getSystemService(Context.USB_SERVICE);
        this.mPermissionIndent = PendingIntent.getBroadcast(mContext, 0, new Intent(ACTION_USB_PERMISSION), PendingIntent.FLAG_MUTABLE);
        IntentFilter filter = new IntentFilter(ACTION_USB_PERMISSION);
        filter.addAction(UsbManager.ACTION_USB_DEVICE_DETACHED);
        filter.addAction(UsbManager.ACTION_USB_ACCESSORY_ATTACHED);
        filter.addAction(UsbManager.ACTION_USB_DEVICE_ATTACHED);
        mContext.registerReceiver(mUsbDeviceReceiver, filter);
        Log.e(TAG, "initialized");

//        LocalBroadcastManager.getInstance(getContext()).registerReceiver(aLBReceiver,
//                new IntentFilter("onUsbConnect"));
    }

    @Override
    protected void handleOnPause() {
        super.handleOnPause();
//        LocalBroadcastManager.getInstance(getContext()).unregisterReceiver(aLBReceiver);
    }

//    private BroadcastReceiver aLBReceiver = new BroadcastReceiver() {
//        @Override
//        public void onReceive(Context context, Intent intent) {
//            Toast.makeText(context, "onreceived aLBReceiver", Toast.LENGTH_SHORT).show();
//            String action = intent.getAction();
//            Boolean isConenct = false;
//            String deviceName = "";
//            if(action.equals("onUsbConnect")){
//                isConenct = intent.getBooleanExtra("isConnect",false);
//                deviceName = intent.getStringExtra("deviceName");
//                Log.e("onBrOADCASTRECEIVE","connect: "+isConenct+" devicename: "+deviceName);
//                JSObject ret = new JSObject();
//                ret.put("connectionStatus", isConenct.toString());
//                ret.put("deviceName", deviceName);
//                sendEvent(ret);
//            }
//        }
//    };

    private boolean openConnection() {
        if (mUsbDevice == null) {
            Log.e(TAG, "USB Deivce is not initialized");
            return false;
        }
        if (mUSBManager == null) {
            Log.e(TAG, "USB Manager is not initialized");
            return false;
        }

        if (mUsbDeviceConnection != null) {
            Log.e(TAG, "USB Connection already connected");
            return true;
        }

        UsbInterface usbInterface = mUsbDevice.getInterface(0);
        for (int i = 0; i < usbInterface.getEndpointCount(); i++) {
            final UsbEndpoint ep = usbInterface.getEndpoint(i);
            if (ep.getType() == UsbConstants.USB_ENDPOINT_XFER_BULK) {
                if (ep.getDirection() == UsbConstants.USB_DIR_OUT) {
                    UsbDeviceConnection usbDeviceConnection = mUSBManager.openDevice(mUsbDevice);
                    if (usbDeviceConnection == null) {
                        Log.e(TAG, "failed to open USB Connection");
                        return false;
                    }
                    if (usbDeviceConnection.claimInterface(usbInterface, true)) {

                        mEndPoint = ep;
                        mUsbInterface = usbInterface;
                        mUsbDeviceConnection = usbDeviceConnection;
                        Log.w(TAG, "Device connected");
                        return true;
                    } else {
                        usbDeviceConnection.close();
                        Log.e(TAG, "failed to claim usb connection");
                        return false;
                    }
                }
            }
        }
        return true;
    }


//    public void printRawData(String data, Callback errorCallback) {
//        final String rawData = data;
//        Log.v(LOG_TAG, "start to print raw data " + data);
//        boolean isConnected = openConnection();
//        if (isConnected) {
//            Log.v(LOG_TAG, "Connected to device");
//            new Thread(new Runnable() {
//                @Override
//                public void run() {
//                    byte[] bytes = Base64.decode(rawData, Base64.DEFAULT);
//                    int b = mUsbDeviceConnection.bulkTransfer(mEndPoint, bytes, bytes.length, 100000);
//                    Log.i(LOG_TAG, "Return Status: b-->" + b);
//                }
//            }).start();
//        } else {
//            String msg = "failed to connected to device";
//            Log.v(LOG_TAG, msg);
//            errorCallback.invoke(msg);
//        }
//    }

    @PluginMethod()
    public void printTest(PluginCall call) {
        Log.v(TAG, "start to print test data ");
        boolean isConnected = openConnection();
        if (isConnected) {
            Log.w(TAG, "Connected to device");
            new Thread(new Runnable() {
                @Override
                public void run() {
                    byte[] bytes = Base64.decode("This is test Print", Base64.DEFAULT);
                    int b = mUsbDeviceConnection.bulkTransfer(mEndPoint, bytes, bytes.length, 100000);
                    Log.w(TAG, "Return Status: b-->" + b);
                    call.resolve();
                }
            }).start();
        } else {
            String msg = "failed to connected to device";
            Log.e(TAG, msg);
            call.errorCallback(msg);
        }
    }
}
