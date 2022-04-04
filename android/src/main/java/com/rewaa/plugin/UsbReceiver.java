package com.rewaa.plugin;

import android.app.PendingIntent;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.hardware.usb.UsbDevice;
import android.hardware.usb.UsbDeviceConnection;
import android.hardware.usb.UsbManager;
import android.util.Log;
import android.widget.Toast;

import androidx.core.content.ContextCompat;
import androidx.localbroadcastmanager.content.LocalBroadcastManager;

import com.getcapacitor.Bridge;
import com.getcapacitor.JSObject;
import com.getcapacitor.Plugin;
import com.getcapacitor.PluginHandle;


import org.json.JSONObject;

public class UsbReceiver extends BroadcastReceiver {
    private static final String TAG = "UsbReceiver";
    private UsbManager mUsbManager;
    private UsbDevice mUsbDevice;
//    ExamplePlugin examplePlugin;
//    Bridge bridge;


    @Override
    public void onReceive(Context context, Intent intent) {
        Log.e(TAG,"onreceived");


        Toast.makeText(context, "onreceived", Toast.LENGTH_SHORT).show();
        String action = intent.getAction();

        Intent intentBroadcast = new Intent("onUsbConnect");
//        if (ACTION_USB_PERMISSION.equals(action)) {
//            synchronized (this) {
//                UsbDevice usbDevice = intent.getParcelableExtra(UsbManager.EXTRA_DEVICE);
//                if (intent.getBooleanExtra(UsbManager.EXTRA_PERMISSION_GRANTED, false)) {
//                    Log.e("UsbReceiver", "success to grant permission for device " + usbDevice.getDeviceId() + ", vendor_id: " + usbDevice.getVendorId() + " product_id: " + usbDevice.getProductId());
//                    Toast.makeText(context, "success to grant permission for device " + usbDevice.getDeviceId() + ", vendor_id: " + usbDevice.getVendorId() + " product_id: " + usbDevice.getProductId(), Toast.LENGTH_LONG).show();
//                    Toast.makeText(context, "Man name: " + usbDevice.getManufacturerName(), Toast.LENGTH_LONG).show();
//                    this.mUsbDevice = usbDevice;
//                } else {
//                    Toast.makeText(context, "User refuses to obtain USB device permissions" + usbDevice.getDeviceName(), Toast.LENGTH_LONG).show();
//                }
//            }
//        }else
            if (UsbManager.ACTION_USB_DEVICE_DETACHED.equals(action)) {
            Toast.makeText(context, "USB device has been turned off", Toast.LENGTH_LONG).show();
            intentBroadcast.putExtra("isConnect", false);
            Toast.makeText(context, "Disconnect", Toast.LENGTH_SHORT).show();
            UsbDevice device = intent.getParcelableExtra(UsbManager.EXTRA_DEVICE);
            if (device != null) {
                intentBroadcast.putExtra("deviceName", device.getDeviceName());
                Toast.makeText(context, "device name: "+device.getDeviceName(), Toast.LENGTH_SHORT).show();
                Log.e("ACTION_USB_DEVICE_DETACHED", device.getDeviceName()+"");
//                JSObject ret = new JSObject();
//                ret.put("event", "deattached");
//                ret.put("deviceName", device.getDeviceName());
//                examplePlugin.sendEvent(ret);
//                printStatus(context,context.getString(R.string.status_removed));
//                printDeviceDescription(context,device);
            }else{
                Toast.makeText(context, "device null", Toast.LENGTH_SHORT).show();
                intentBroadcast.putExtra("deviceName", "");
            }

        } else if (UsbManager.ACTION_USB_DEVICE_ATTACHED.equals(action)) {
            intentBroadcast.putExtra("isConnect", true);
            UsbDevice device = intent.getParcelableExtra(UsbManager.EXTRA_DEVICE);
            if (device != null) {
                intentBroadcast.putExtra("deviceName", device.getDeviceName());
                Toast.makeText(context, "Connected: "+device.getDeviceName(), Toast.LENGTH_SHORT).show();
                Log.e("ACTION_USB_DEVICE_ATTACHED", device.getDeviceName()+"");



//                printDeviceDescription(context,device);
//                Log.e("device usbReceiver l41", String.valueOf(device));
                 JSONObject data = getDeviceDetails2(device);
                // if(data!=null){
                //     Toast.makeText(context, ">>> "+data.toString(), Toast.LENGTH_LONG).show();
                //     Log.e("device data",data.toString());
                // }else{
                //     Toast.makeText(context, "data found null", Toast.LENGTH_LONG).show();
                // }

            }else {
                Toast.makeText(context, "device null", Toast.LENGTH_SHORT).show();
                intentBroadcast.putExtra("deviceName", "");
            }
        }
        LocalBroadcastManager.getInstance(context).sendBroadcast(intentBroadcast);
    }



//    private void printStatus(Context context,String status) {
//        Log.i(TAG, "printStatus: "+status);
//        Toast.makeText(context, status, Toast.LENGTH_SHORT).show();
//    }

//    private void getPrinterStatus(Context context,String status) {
//        Log.i(TAG, "getPrinterStatus:");
//        Toast.makeText(context, status, Toast.LENGTH_SHORT).show();
//    }

    private void printDeviceDescription(Context context,UsbDevice device) {
        String result = UsbHelper.readDevice(device) + "\n\n";
        Log.i(TAG, "printDeviceDescription: "+result);
        Toast.makeText(context, result, Toast.LENGTH_SHORT).show();
    }

    private JSONObject getDeviceDetails2(UsbDevice device) {
        UsbDeviceConnection connection = mUsbManager.openDevice(device);

        JSONObject data = new JSONObject();
        try {
            //Parse the raw device descriptor
            data = DeviceDescriptor.fromDeviceConnection2(connection);
        } catch (IllegalArgumentException e) {
            Log.w(TAG, "Invalid device descriptor", e);
        } catch (Exception e){
            e.printStackTrace();
        }
        if(connection!=null)
        connection.close();
        return data;
    }

}
