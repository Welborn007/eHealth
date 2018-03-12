package kesari.com.kesarie_healthmonitoring.PulseOximeter;

import android.app.Service;
import android.bluetooth.BluetoothAdapter;
import android.bluetooth.BluetoothDevice;
import android.bluetooth.BluetoothSocket;
import android.content.Intent;
import android.os.AsyncTask;
import android.os.Handler;
import android.os.IBinder;
import android.os.Message;
import android.os.Messenger;
import android.os.RemoteException;
import android.util.Log;

import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;

import kesari.com.kesarie_healthmonitoring.PulseOximeter.cms50dj_jar.DevicePackManager;
import kesari.com.kesarie_healthmonitoring.PulseOximeter.jar.contec08a.DeviceCommand;


public class BluetoothHDPService extends Service implements Runnable {
    public static String ABPM = "ABPM";
    public static String BPM = "BPM";
    public static final int CONNECTION_FAILURE = 1002;
    public static String CONTEC = "Contec";
    public static final int DataArrived = 1001;
    public static String GLUCOMETER = "Glucometer";
    public static final int LOG_MSG = 1000;
    public static final int MSG_CONNECT_CHANNEL = 400;
    public static final int MSG_DISCONNECT_CHANNEL = 401;
    public static final int MSG_REG_CLIENT = 200;
    public static final int MSG_REG_HEALTH_APP = 300;
    public static final int MSG_SEND_DATA = 501;
    public static final int MSG_UNREG_CLIENT = 201;
    public static final int MSG_UNREG_HEALTH_APP = 301;
    public static String OXIMETER = "Oximeter";
    public static String SAMICO = "Samico";
    public static final int STATUS_CREATE_CHANNEL = 102;
    public static final int STATUS_DESTROY_CHANNEL = 103;
    public static final int STATUS_HEALTH_APP_REG = 100;
    public static final int STATUS_HEALTH_APP_UNREG = 101;
    public static final int STATUS_READ_DATA = 104;
    public static final int STATUS_READ_DATA_DONE = 105;
    private static final String TAG = "HSSHDP";
    public static String THERMOMETER = "Thermometer";
    public static String Urine = "Urine";
    BluetoothSocket blueSocket = null;
    private String deviceName;
    DevicePackManager devicePackManager = null;
    protected InputStream inStream;
    protected boolean keepThreadAlive = true;
    private Messenger mClient;
    final Messenger mMessenger = new Messenger(new IncomingHandler());
    private String manufacturer;
    private String measurementType;
    private OutputStream outStream;

    final class IncomingHandler extends Handler {
        IncomingHandler() {
        }

        public void handleMessage(Message msg) {
            switch (msg.what) {
                case 104:
                    BluetoothHDPService.this.requestData();
                    break;
                case 200:
                    Log.w(BluetoothHDPService.TAG, "Activity client registered");
                    BluetoothHDPService.this.mClient = msg.replyTo;
                    return;
                case BluetoothHDPService.MSG_UNREG_CLIENT /*201*/:
                    BluetoothHDPService.this.mClient = null;
                    return;
                case 300:
                case BluetoothHDPService.MSG_UNREG_HEALTH_APP /*301*/:
                    return;
                case BluetoothHDPService.MSG_CONNECT_CHANNEL /*400*/:
                    BluetoothDevice device = (BluetoothDevice) msg.obj;
                    new connectToBluetooth().execute(new BluetoothDevice[]{device});
                    return;
                case BluetoothHDPService.MSG_DISCONNECT_CHANNEL /*401*/:
                    break;
                case BluetoothHDPService.MSG_SEND_DATA /*501*/:
                    Object[] pair = (Object[]) msg.obj;
                    BluetoothHDPService.this.sendData((BluetoothDevice) pair[0], (byte[]) pair[1]);
                    return;
                default:
                    super.handleMessage(msg);
                    return;
            }
            BluetoothHDPService.this.disconnectChannel((BluetoothDevice) msg.obj);
        }
    }

    private class connectToBluetooth extends AsyncTask<BluetoothDevice, Integer, Integer> {
        private connectToBluetooth() {
        }

        protected Integer doInBackground(BluetoothDevice... device) {
            BluetoothHDPService.this.connectChannel(device[0]);
            return Integer.valueOf(0);
        }

        protected void onProgressUpdate(Integer... progress) {
        }

        protected void onPostExecute(Long result) {
        }
    }

    public String getMeasurementType() {
        return this.measurementType;
    }

    public void setMeasurementType(String measurementType) {
        this.measurementType = measurementType;
    }

    public String getDeviceName() {
        return this.deviceName;
    }

    public void setDeviceName(String deviceName) {
        this.deviceName = deviceName;
    }

    public String getManufacturer() {
        return this.manufacturer;
    }

    public void setManufacturer(String manufacturer) {
        this.manufacturer = manufacturer;
    }

    public void requestData() {
    }

    protected void disconnectChannel(BluetoothDevice obj) {
        if (this.blueSocket != null && this.blueSocket.isConnected()) {
            try {
                this.blueSocket.close();
                sendMessageToUI(103, 103, "disconnected");
            } catch (IOException exception) {
                sendMessageToUI(1000, 1000, "Error disconnecting - " + exception.getMessage());
            }
        }
        this.keepThreadAlive = false;
        cancel();
        this.blueSocket = null;
    }

    public void onCreate() {
        super.onCreate();
        BluetoothAdapter mBluetoothAdapter = BluetoothAdapter.getDefaultAdapter();
        if (mBluetoothAdapter == null || !mBluetoothAdapter.isEnabled()) {
            stopSelf();
        }
    }

    public int onStartCommand(Intent intent, int flags, int startId) {
        Log.w(TAG, "BluetoothHDPService is running.");
        return 1;
    }

    public IBinder onBind(Intent intent) {
        return this.mMessenger.getBinder();
    }

    private void connectChannel(BluetoothDevice dev) {
        Log.w(TAG, "connectChannel()");
        try {
            if (this.blueSocket != null && this.blueSocket.isConnected()) {
                disconnectChannel(null);
            }
            if (this.blueSocket == null || !this.blueSocket.isConnected()) {
                this.blueSocket = dev.createRfcommSocketToServiceRecord(dev.getUuids()[0].getUuid());
                if (!this.blueSocket.isConnected()) {
                    try {
                        this.blueSocket.connect();
                        sendMessageToUI(1000, 1000, "Connected to device successfully");
                        onConnect();
                    } catch (IOException e) {
                        sendMessageToUI(1002, 1002, "Couldn't connect to device");
                        this.blueSocket = null;
                        e.printStackTrace();
                    }
                }
            }
        } catch (IOException e2) {
            e2.printStackTrace();
        }
    }

    public void onConnect() {
        try {
            this.inStream = this.blueSocket.getInputStream();
            this.outStream = this.blueSocket.getOutputStream();
            new Thread(this).start();
            initiatHandshake();
        } catch (IOException e) {
            disconnectChannel(null);
            sendMessageToUI(1002, 1002, "Device connection lost");
            e.printStackTrace();
        }
    }

    public void initiatHandshake() {
    }

    public byte[] getInitiateCommand() {
        return DeviceCommand.REQUEST_HANDSHAKE();
    }

    public void write(byte[] bytes) {
        try {
            this.outStream.write(bytes);
            this.outStream.flush();
        } catch (IOException e) {
        }
    }

    public void cancel() {
        try {
            if (this.blueSocket != null && this.blueSocket.isConnected()) {
                this.blueSocket.close();
            }
        } catch (IOException e) {
        }
    }

    public void run() {
    }

    public static String getBytesInHexString(byte[] data, int count) {
        String temp = "";
        for (int i = 0; i < count; i++) {
            temp = temp + " " + Integer.toHexString(data[i] & 255);
        }
        return temp;
    }

    public void writeData(byte[] data) {
        write(data);
    }

    protected void sendData(BluetoothDevice dev, byte[] data) {
        try {
            writeData(data);
            Log.i(TAG, "Sent Data[" + getBytesInHexString(data, data.length) + "]");
        } catch (Exception e) {
            Log.w(TAG, "Could not send data to HDP");
        }
    }

    protected void sendMessageToUI(int what, int value, Object obj) {
        if (this.mClient == null) {
            Log.w(TAG, "No clients registered.");
            return;
        }
        try {
            Message msg = Message.obtain(null, what, Integer.valueOf(value));
            msg.obj = obj;
            this.mClient.send(msg);
        } catch (RemoteException e) {
            e.printStackTrace();
        }
    }
}
