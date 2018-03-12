package kesari.com.kesarie_healthmonitoring.PulseOximeter;

import android.bluetooth.BluetoothAdapter;
import android.bluetooth.BluetoothDevice;
import android.bluetooth.BluetoothManager;
import android.content.ComponentName;
import android.content.Intent;
import android.content.ServiceConnection;
import android.os.Bundle;
import android.os.Handler;
import android.os.IBinder;
import android.os.Message;
import android.os.Messenger;
import android.os.RemoteException;
import android.support.v7.app.AppCompatActivity;
import android.text.format.DateFormat;
import android.util.Log;
import android.view.View;
import android.widget.TextView;
import android.widget.Toast;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import kesari.com.kesarie_healthmonitoring.R;
import kesari.com.kesarie_healthmonitoring.Utils.FireToast;
import kesari.com.kesarie_healthmonitoring.Utils.IOUtils;
import kesari.com.kesarie_healthmonitoring.Utils.SharedPrefUtil;
import mehdi.sakout.fancybuttons.FancyButton;


public class PulseOximeterActivity extends AppCompatActivity {

    private static final String ARG_PARAM1 = "param1";
    private static final String ARG_PARAM2 = "param2";
    private static final int REQUEST_ENABLE_BT = 1;
    private static final String TAG = "eHealthSmartCare";
    public static StringBuilder logs = new StringBuilder();
    int connectionAttempt = 0;
    SpO250D item;
    private BluetoothAdapter mBluetoothAdapter;
    private ServiceConnection mConnection = new C03175();
    private BluetoothDevice mDevice;
    Handler mHandler = new Handler();
    private Messenger mHealthService;
    private boolean mHealthServiceBound;
    private final Handler mIncomingHandler = new C03164();
    private final Messenger mMessenger = new Messenger(this.mIncomingHandler);
    private String mParam1;
    private String mParam2;
    TextView textBPM;
    FancyButton register;
    TextView deregister,spo2,date,bpm;
    Intent intent;
    FancyButton save,clear;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_pulse_oximeter);

        register = (FancyButton) findViewById(R.id.register);
        deregister = (TextView) findViewById(R.id.deregister);
        spo2 = (TextView) findViewById(R.id.spo2);
        date = (TextView) findViewById(R.id.date);
        bpm = (TextView) findViewById(R.id.bpm);

        save = (FancyButton) findViewById(R.id.save);
        clear = (FancyButton) findViewById(R.id.clear);

        save.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                String SPO2 = spo2.getText().toString().trim();
                String BPM = bpm.getText().toString().trim();

                if(!SPO2.isEmpty() && !BPM.isEmpty())
                {
                    post_EHealth_Data("http://192.168.1.220:3000/route/F21_SLMS/setEhealthData",SPO2,BPM);
                }
                else if(SPO2.isEmpty() && BPM.isEmpty())
                {
                    Toast.makeText(PulseOximeterActivity.this, "Please Fetch Oximeter Details!!", Toast.LENGTH_SHORT).show();
                }
            }
        });

        clear.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                clearValues();
            }
        });

        intent = new Intent(PulseOximeterActivity.this, Oximeter_Contec_08_Service.class);
        startService(intent);
        bindService(intent, this.mConnection, 1);


        register.setOnClickListener(new C03131());

        deregister.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                disconnectChannel();
            }
        });
    }

    private void clearValues()
    {
        spo2.setText("");
        bpm.setText("");
        date.setText("");
    }

    private void post_EHealth_Data(String link,String spo2,String bpm) {
        try {

            Log.i("JsonLink", link);

            JSONObject jsonParam = new JSONObject();

            try {

                jsonParam.put("rsid", SharedPrefUtil.getUser(PulseOximeterActivity.this).getData().getF21_SRNO());

                JSONObject jsonObject = new JSONObject();
                JSONObject PO = new JSONObject();

                PO.put("SPO2",spo2);
                PO.put("BPM",bpm);

                jsonObject.put("PO",PO);
                jsonParam.put("EHEALTH",jsonObject);

                Log.d("json created", jsonParam.toString());

            } catch (JSONException e) {
                e.printStackTrace();
            }

            IOUtils ioUtils = new IOUtils();

            ioUtils.sendJSONObjectRequest(PulseOximeterActivity.this, link, jsonParam, new IOUtils.VolleyCallback() {
                @Override
                public void onSuccess(String result) {

                    Log.d("RESPONSE*******", result.toString());
                    getEhealth_DataResponse(result);
                }
            });

        } catch (Exception e) {
            e.printStackTrace();
        }

    }

    private void getEhealth_DataResponse(String Response)
    {
        try
        {
            JSONObject jsonObject = new JSONObject(Response);

            String message = jsonObject.getString("message");

            if(message.equalsIgnoreCase("SAVE"))
            {
                Toast.makeText(PulseOximeterActivity.this, "Saved!!!", Toast.LENGTH_SHORT).show();
                clearValues();
            }
        }catch (Exception e)
        {
            e.printStackTrace();
        }
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();

        stopService(intent);
    }

    class C03131 implements View.OnClickListener {

        class C03121 implements Runnable {
            C03121() {
            }

            public void run() {
                if(mDevice != null)
                {
                    connectChannel();
                }
                else
                {
                    FireToast.customDefiniteSnackbar(PulseOximeterActivity.this,"Device Not Paired", "");
                    //Toast.makeText(PulseOximeterActivity.this, "Device Not Paired", Toast.LENGTH_SHORT).show();
                }

            }
        }

        C03131() {
        }

        public void onClick(View view) {
            FireToast.customDefiniteSnackbar(PulseOximeterActivity.this,"Fetching Oximeter data from Device", "");
            //Toast.makeText(PulseOximeterActivity.this, "Fetching Oximeter data from Device", Toast.LENGTH_SHORT).show();
            findPairedDevice();
            mHandler.post(new C03121());
        }
    }

    private void initialize() {

        this.mBluetoothAdapter = ((BluetoothManager) getSystemService("bluetooth")).getAdapter();
        if (this.mBluetoothAdapter == null || !this.mBluetoothAdapter.isEnabled()) {
            startActivityForResult(new Intent("android.bluetooth.adapter.action.REQUEST_ENABLE"), 1);
        }
        do {
        } while (!this.mBluetoothAdapter.isEnabled());
        findPairedDevice();
    }

    void findPairedDevice() {
        for (BluetoothDevice device : (BluetoothDevice[]) this.mBluetoothAdapter.getBondedDevices().toArray(new BluetoothDevice[0])) {
            logMessage(device.getName());
            if (device.getName().startsWith("SpO208")) {
                logMessage("Paired device found");
                this.mDevice = device;
                break;
            }
        }
        if (this.mDevice == null) {
            logMessage("Device not paired");
        }
    }

    private void logMessage(String message) {
        FireToast.customDefiniteSnackbar(PulseOximeterActivity.this,message, "");
        //Toast.makeText(this, message, Toast.LENGTH_SHORT).show();
    }

    public void connectChannel() {
        sendMessageWithDevice(BluetoothHDPService.MSG_CONNECT_CHANNEL);
    }

    private void disconnectChannel() {
        sendMessageWithDevice(BluetoothHDPService.MSG_DISCONNECT_CHANNEL);
    }

    private void sendMessage(int what, int value) {
        if (this.mHealthService != null) {
            try {
                this.mHealthService.send(Message.obtain(null, what, value, 0));
            } catch (RemoteException e) {
                Log.w("eHealthSmartCare", "Unable to reach service.");
                logMessage("Unable to reach service.");
                e.printStackTrace();
            }
        }
    }

    private void sendMessageWithDevice(int what) {
        if (this.mHealthService != null) {
            try {
                this.mHealthService.send(Message.obtain(null, what, this.mDevice));
            } catch (RemoteException e) {
                Log.w("eHealthSmartCare", "Unable to reach service.");
                logMessage("Unable to reach service.");
                e.printStackTrace();
            }
        }
    }

    class C03175 implements ServiceConnection {
        C03175() {
        }

        public void onServiceConnected(ComponentName name, IBinder service) {
            mHealthServiceBound = true;
            Message msg = Message.obtain(null, 200);
            msg.replyTo = mMessenger;
            mHealthService = new Messenger(service);
            try {
                mHealthService.send(msg);
                initialize();
            } catch (RemoteException e) {
                Log.w("eHealthSmartCare", "Unable to register client to service.");
                e.printStackTrace();
            }
        }

        public void onServiceDisconnected(ComponentName name) {
            mHealthService = null;
            mHealthServiceBound = false;
        }
    }


    class C03164 extends Handler {
        boolean isConnecting = false;

        C03164() {
        }

        public void handleMessage(Message msg) {
            switch (msg.what) {
                case 100:
                    if (!this.isConnecting) {
                        connectChannel();
                        this.isConnecting = true;
                        return;
                    }
                    return;
                case 101:
                case 102:
                case 104:
                case 105:
                    return;
                case 103:
                    findPairedDevice();
                    return;
                case 1000:
                    if (msg.obj.toString().equals("Connected to device successfully")) {
                        connectionAttempt = 0;
                    } else if (msg.obj.toString().equals("No Pending Data")) {

                    }

                    return;
                case 1001:
                    if (msg.obj != null) {
                        item = (SpO250D) msg.obj;


                        spo2.setText(String.valueOf(item.SpO2));
                        bpm.setText(String.valueOf(item.PR));
                        date.setText(DateFormat.format("dd-MM-yyyy HH:mm:ss", item.getDate()).toString());

                        /*contec_Oximeter_Fragment.this.textSpo.setText(String.valueOf(contec_Oximeter_Fragment.this.item.SpO2));
                        contec_Oximeter_Fragment.this.textBPM.setText(String.valueOf(contec_Oximeter_Fragment.this.item.PR));
                        contec_Oximeter_Fragment.this.textTime.setText(DateFormat.format("dd-MM-yyyy HH:mm:ss", item.getDate()).toString());*/
                        return;
                    }

                    return;
                case 1002:
                    /*contec_Oximeter_Fragment com_anonimous_eHealthSmartCare_contec_Oximeter_Fragment = contec_Oximeter_Fragment.this;
                    com_anonimous_eHealthSmartCare_contec_Oximeter_Fragment.connectionAttempt++;
                    if (contec_Oximeter_Fragment.this.connectionAttempt < 3) {
                        contec_Oximeter_Fragment.this.connectChannel();
                        return;
                    }
                    contec_Oximeter_Fragment.this.connectionAttempt = 0;
                    contec_Oximeter_Fragment.this.fabProgressCircle.hide();
                    contec_Oximeter_Fragment.this.findPairedDevice();
                    Snackbar.make(contec_Oximeter_Fragment.this.fab1, msg.obj.toString(), 0).setAction((CharSequence) "Action", null).show();*/
                    return;
                default:
                    super.handleMessage(msg);
                    return;
            }
        }
    }
}
