package kesari.com.kesarie_healthmonitoring;

import android.annotation.TargetApi;
import android.app.ProgressDialog;
import android.bluetooth.BluetoothAdapter;
import android.bluetooth.BluetoothAdapter.LeScanCallback;
import android.bluetooth.BluetoothDevice;
import android.bluetooth.BluetoothManager;
import android.bluetooth.le.BluetoothLeScanner;
import android.bluetooth.le.ScanCallback;
import android.bluetooth.le.ScanResult;
import android.content.BroadcastReceiver;
import android.content.ComponentName;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.content.ServiceConnection;
import android.graphics.drawable.AnimationDrawable;
import android.os.Build.VERSION;
import android.os.Bundle;
import android.os.Handler;
import android.os.IBinder;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentActivity;
import android.util.Log;
import android.view.View;
import android.widget.Toast;

import java.util.List;

@TargetApi(21)
public class BLEFragmentBase extends Fragment {
    private static final int REQUEST_ENABLE_BT = 1;
    protected static final long SCAN_PERIOD = 20000;
    AnimationDrawable animation;
    //protected CircleImageView circleImageView;
    //protected CircleImageView circleImageViewBluetooth;
    protected boolean deviceFound = false;
    protected boolean ganttConnected = false;
    protected boolean ganttServiceDiscovered = false;
    boolean isAtOriginalPosition = true;
    protected BluetoothAdapter mBluetoothAdapter;
    protected BluetoothLeService mBluetoothLeService;
    protected String mDeviceAddress;
    private final BroadcastReceiver mGattUpdateReceiver = new C02606();
    protected Handler mHandler = new Handler();
    protected boolean mHealthServiceBound;
    private BluetoothLeScanner mLEScanner;
    private LeScanCallback mLeScanCallback = new C02595();
    //protected OnFragmentInteractionListener mListener;
    Object mScanCallbackObject = null;
    protected boolean mScanning;
    protected final ServiceConnection mServiceConnection = new C02551();
    int[] originalPos;
    int xDelta;
    int yDelta = 0;
    ProgressDialog pd;

    class C02551 implements ServiceConnection {
        C02551() {
        }

        public void onServiceConnected(ComponentName componentName, IBinder service) {
            onServiceConnection(service);
        }

        void onServiceConnection(IBinder service) {
            //Snackbar.make(BLEFragmentBase.this.getView(), (CharSequence) "Service started", 0).show();
            Toast.makeText(getActivity(), "Service started", Toast.LENGTH_SHORT).show();
            BLEFragmentBase.this.mBluetoothLeService = ((BluetoothLeService.LocalBinder) service).getService();
            BLEFragmentBase.this.mBluetoothLeService.mHandler = BLEFragmentBase.this.mHandler;
            if (!BLEFragmentBase.this.mBluetoothLeService.initialize()) {
                //Snackbar.make(BLEFragmentBase.this.getView(), (CharSequence) "Unable to initialize Bluetooth", 0).show();
                Toast.makeText(getActivity(), "Unable to initialize Bluetooth", Toast.LENGTH_SHORT).show();
                BLEFragmentBase.this.getActivity().finish();
            }
            FragmentActivity activity = BLEFragmentBase.this.getActivity();
            BLEFragmentBase.this.getActivity();
            BluetoothManager bluetoothManager = (BluetoothManager) activity.getSystemService("bluetooth");
            BLEFragmentBase.this.mBluetoothAdapter = bluetoothManager.getAdapter();
            if (BLEFragmentBase.this.mBluetoothAdapter == null || !BLEFragmentBase.this.mBluetoothAdapter.isEnabled()) {
                BLEFragmentBase.this.startActivityForResult(new Intent("android.bluetooth.adapter.action.REQUEST_ENABLE"), 1);
            } else if (BLEFragmentBase.this.mBluetoothAdapter.isEnabled()) {
                BLEFragmentBase.this.requestPermissions(new String[]{"android.permission.ACCESS_COARSE_LOCATION"}, 1);
            }
        }

        public void onServiceDisconnected(ComponentName componentName) {
            BLEFragmentBase.this.mBluetoothLeService = null;
        }
    }

    class C02562 implements Runnable {
        C02562() {
        }

        public void run() {
            BLEFragmentBase.this.scanLeDevice(true);
        }
    }

    class C02573 extends ScanCallback {
        C02573() {
        }

        public void onScanResult(int callbackType, ScanResult result) {
            BluetoothDevice device = result.getDevice();
            Log.i("callbackType", String.valueOf(callbackType));
            //Log.i(DataProvider.REPORT_RESULT, result.toString());
            BLEFragmentBase.this.deviceFound(device);
        }

        public void onBatchScanResults(List<ScanResult> results) {
            for (ScanResult sr : results) {
                Log.i("ScanResult - Results", sr.toString());
            }
        }

        public void onScanFailed(int errorCode) {
            Log.e("Scan Failed", "Error Code: " + errorCode);
        }
    }

    class C02584 implements Runnable {
        C02584() {
        }

        public void run() {
            BLEFragmentBase.this.mScanning = false;
            if (VERSION.SDK_INT <= 21) {
                BLEFragmentBase.this.mBluetoothAdapter.stopLeScan(BLEFragmentBase.this.mLeScanCallback);
            } else {
                BLEFragmentBase.this.mLEScanner.stopScan((ScanCallback) BLEFragmentBase.this.mScanCallbackObject);
            }
            if (!BLEFragmentBase.this.deviceFound) {
                BLEFragmentBase.this.revertBluetoothToOriginalLocationAnimation(BLEFragmentBase.this.deviceFound);
            }
        }
    }

    class C02595 implements LeScanCallback {
        C02595() {
        }

        public void onLeScan(BluetoothDevice device, int rssi, byte[] scanRecord) {
            BLEFragmentBase.this.deviceFound(device);
        }
    }

    class C02606 extends BroadcastReceiver {
        C02606() {
        }

        public void onReceive(Context context, Intent intent) {
            String action = intent.getAction();
            if ("android.bluetooth.adapter.action.STATE_CHANGED".equals(action)) {
                if (intent.getIntExtra("android.bluetooth.adapter.extra.STATE", Integer.MIN_VALUE) != 12) {
                }
            } else if (BluetoothLeService.ACTION_DATA_AVAILABLE.equals(action)) {
                BLEFragmentBase.this.onDataArrived(intent);
                pd.dismiss();
            } else if (BluetoothLeService.ACTION_GATT_CONNECTED.equals(action)) {
                BLEFragmentBase.this.ganttConnected = true;
                BLEFragmentBase.this.revertBluetoothToOriginalLocationAnimation(true);
            } else if (BluetoothLeService.ACTION_GATT_SERVICES_DISCOVERED.equals(action)) {
                BLEFragmentBase.this.ganttServiceDiscovered = true;
                BLEFragmentBase.this.revertBluetoothToOriginalLocationAnimation(true);
            } else if (BluetoothLeService.ACTION_GATT_DISCONNECTED.equals(action)) {
                BLEFragmentBase.this.deviceFound = false;
                BLEFragmentBase.this.ganttServiceDiscovered = false;
                BLEFragmentBase.this.ganttConnected = false;
                BLEFragmentBase.this.mBluetoothLeService.disconnect();
                BLEFragmentBase.this.revertBluetoothToOriginalLocationAnimation(false);
                pd.dismiss();
            }
        }
    }

    class Starter implements Runnable {
        Starter() {
        }

        public void run() {
            BLEFragmentBase.this.animation.setOneShot(false);
            BLEFragmentBase.this.animation.start();
        }
    }

    class Terminator implements Runnable {
        Terminator() {
        }

        public void run() {
            if (BLEFragmentBase.this.deviceFound) {
                Toast.makeText(getActivity(), "Enabled", Toast.LENGTH_SHORT).show();
                //BLEFragmentBase.this.circleImageViewBluetooth.setImageResource(R.drawable.bluetooth_enabled);
            } else {
                //BLEFragmentBase.this.circleImageViewBluetooth.setImageResource(R.drawable.bluetooth_disabled);
                Toast.makeText(getActivity(), "Disabled", Toast.LENGTH_SHORT).show();
            }
            if (BLEFragmentBase.this.animation != null) {
                BLEFragmentBase.this.animation.stop();
            }
        }
    }

    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        Intent gattServiceIntent = new Intent(getContext(), BluetoothLeService.class);
        getActivity().startService(gattServiceIntent);
        getActivity().bindService(gattServiceIntent, this.mServiceConnection, 1);
    }

    private static IntentFilter makeGattUpdateIntentFilter() {
        IntentFilter intentFilter = new IntentFilter();
        intentFilter.addAction(BluetoothLeService.ACTION_GATT_CONNECTED);
        intentFilter.addAction(BluetoothLeService.ACTION_GATT_DISCONNECTED);
        intentFilter.addAction(BluetoothLeService.ACTION_GATT_SERVICES_DISCOVERED);
        intentFilter.addAction(BluetoothLeService.ACTION_DATA_AVAILABLE);
        return intentFilter;
    }

    public void onAttach(Context context) {
        super.onAttach(context);
        getContext().registerReceiver(this.mGattUpdateReceiver, makeGattUpdateIntentFilter());
    }

    public void Detach() {
        getContext().unregisterReceiver(this.mGattUpdateReceiver);
        getActivity().unbindService(this.mServiceConnection);
        this.mBluetoothLeService.disconnect();
        this.mBluetoothLeService.close();
        //this.mListener = null;
    }

    public void onDestroyView() {
        super.onDestroyView();
        Detach();
    }

    public void onDetach() {
        super.onDetach();
    }

    public void onActivityResult(int requestCode, int resultCode, Intent data) {
        if (requestCode != 1) {
            return;
        }
        if (this.mBluetoothAdapter == null || !this.mBluetoothAdapter.isEnabled()) {
            //Snackbar.make(getView(), (CharSequence) "Unable to initialize Bluetooth", 0).show();
            Toast.makeText(getActivity(), "Unable to initialize Bluetooth", Toast.LENGTH_SHORT).show();
            return;
        }
        requestPermissions(new String[]{"android.permission.ACCESS_FINE_LOCATION"}, 1);
    }

    protected void onDataArrived(Intent data) {
    }

    public void onRequestPermissionsResult(int requestCode, String[] permissions, int[] grantResults) {
        getActivity().runOnUiThread(new C02562());
    }

    protected void scanLeDevice(boolean enable) {
        if (VERSION.SDK_INT >= 21 && this.mLEScanner == null) {
            this.mLEScanner = this.mBluetoothAdapter.getBluetoothLeScanner();
        }
        if (enable) {
            //this.circleImageViewBluetooth.setImageResource(0);
            //this.animation = (AnimationDrawable) this.circleImageViewBluetooth.getBackground();
            //this.circleImageViewBluetooth.post(new Starter());
            if (VERSION.SDK_INT >= 21) {
                this.mScanCallbackObject = new C02573();
            }
            this.mHandler.postDelayed(new C02584(), SCAN_PERIOD);
            this.mScanning = true;
            if (VERSION.SDK_INT <= 21) {
                this.mBluetoothAdapter.startLeScan(this.mLeScanCallback);
                return;
            } else {
                this.mLEScanner.startScan((ScanCallback) this.mScanCallbackObject);
                return;
            }
        }
        this.mScanning = false;
        revertBluetoothToOriginalLocationAnimation(this.deviceFound);
        this.mBluetoothAdapter.stopLeScan(this.mLeScanCallback);
        if (VERSION.SDK_INT <= 21) {
            this.mBluetoothAdapter.stopLeScan(this.mLeScanCallback);
        } else if (this.mScanCallbackObject != null) {
            this.mLEScanner.stopScan((ScanCallback) this.mScanCallbackObject);
        }
    }

    private void startAnimation() {
    }

    private void moveViewToScreenCenter(View view) {
    }

    protected void revertBluetoothToOriginalLocationAnimation(boolean foundDev) {
        try {
            if (!this.deviceFound || getView() == null) {
                //Snackbar.make(getView(), (CharSequence) "Device Disconnected", 0).show();
                Toast.makeText(getActivity(), "Device Disconnected", Toast.LENGTH_SHORT).show();
            } else {
                //Snackbar.make(getView(), (CharSequence) "Device Found, waiting for service", 0).show();
                Toast.makeText(getActivity(), "Device Found, waiting for service", Toast.LENGTH_SHORT).show();
            }
            if (this.ganttServiceDiscovered && this.ganttConnected && getView() != null) {
                //Snackbar.make(getView(), (CharSequence) "Service connected. Start measurement", 0).show();
                Toast.makeText(getActivity(), "Service connected. Start measurement", Toast.LENGTH_SHORT).show();
            }
        } catch (NullPointerException e) {
        }
        //this.circleImageViewBluetooth.post(new Terminator());
    }

    private void deviceFound(BluetoothDevice device) {

        try
        {
            Log.d("rpm", "deviceFound: " + device.getName());
            if (device != null && device.getName() != null && !this.deviceFound && ((((((device.getName().equals("Tem BH") | device.getName().equals("Medical")) | device.getName().equals("Samico GL")) | device.getName().startsWith("A&D_UA-651BLE")) | device.getName().startsWith("BPM_01")) | device.getName().startsWith("Samico Scales")) | device.getName().startsWith("A&D_UC-352BLE"))) {
                if (VERSION.SDK_INT < 21) {
                    this.mBluetoothAdapter.stopLeScan(this.mLeScanCallback);
                } else {
                    this.mLEScanner.stopScan((ScanCallback) this.mScanCallbackObject);
                }
                this.deviceFound = true;
                this.mDeviceAddress = device.getAddress();
                if (!this.deviceFound) {
                    revertBluetoothToOriginalLocationAnimation(this.deviceFound);
                }
                this.mBluetoothLeService.connect(this.mDeviceAddress);
                Log.d("mGattUpdateReceiver", "Connecting to : " + this.mDeviceAddress);

                pd = new ProgressDialog(getActivity());
                pd.setMessage("Fetching Data.....");
                pd.setCanceledOnTouchOutside(false);
                pd.show();
            }
        }catch (Exception e)
        {
            e.printStackTrace();
        }
    }

}
