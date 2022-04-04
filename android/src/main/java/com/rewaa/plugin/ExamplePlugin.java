package com.rewaa.plugin;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.hardware.usb.UsbDevice;
import android.hardware.usb.UsbDeviceConnection;
import android.hardware.usb.UsbManager;
import android.os.SystemClock;
import android.util.Log;
import android.widget.Toast;

import androidx.core.content.ContextCompat;
import androidx.localbroadcastmanager.content.LocalBroadcastManager;

import com.getcapacitor.JSObject;
import com.getcapacitor.Plugin;
import com.getcapacitor.PluginCall;
import com.getcapacitor.PluginHandle;
import com.getcapacitor.PluginMethod;
import com.getcapacitor.annotation.CapacitorPlugin;

import org.json.JSONObject;

import java.util.Timer;
import java.util.TimerTask;

@CapacitorPlugin(name = "Example")
public class ExamplePlugin extends Plugin {

//    private Example implementation = new Example();



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
        LocalBroadcastManager.getInstance(getContext()).registerReceiver(aLBReceiver,
                new IntentFilter("onUsbConnect"));
    }

    @Override
    protected void handleOnPause() {
        super.handleOnPause();
//        LocalBroadcastManager.getInstance(getContext()).unregisterReceiver(aLBReceiver);
    }

    private BroadcastReceiver aLBReceiver = new BroadcastReceiver() {
        @Override
        public void onReceive(Context context, Intent intent) {
            Toast.makeText(context, "onreceived aLBReceiver", Toast.LENGTH_SHORT).show();
            String action = intent.getAction();
            Boolean isConenct = false;
            String deviceName = "";
            if(action.equals("onUsbConnect")){
                isConenct = intent.getBooleanExtra("isConnect",false);
                deviceName = intent.getStringExtra("deviceName");
                Log.e("onBrOADCASTRECEIVE","connect: "+isConenct+" devicename: "+deviceName);
                JSObject ret = new JSObject();
                ret.put("connectionStatus", isConenct.toString());
                ret.put("deviceName", deviceName);
                sendEvent(ret);
            }
        }
    };
}
