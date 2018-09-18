package kesari.com.kesarie_healthmonitoring.Stethescope;

import android.media.MediaRecorder;
import android.net.wifi.WifiManager;
import android.os.Bundle;
import android.os.Handler;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;

import java.io.File;
import java.io.IOException;
import java.text.SimpleDateFormat;
import java.util.Calendar;

import kesari.com.kesarie_healthmonitoring.R;

public class KAndroidAudioPlayerActivity extends AppCompatActivity {

    VisualizerView visualizerView;
    MediaRecorder mRecorder;
    File saveFile;
    private Handler handler = new Handler();
    final Runnable updater = new C04523();
    WifiManager mainWifi;
    //WifiReceiver receiverWifi;
    String wifiName = "ekuorePro_e145d3";

    //KeKuorePro miEkuorePro = new KeKuorePro();

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_kandroid_audio_player);

        this.visualizerView = (VisualizerView) findViewById(R.id.visualizer);

        /*this.mainWifi = (WifiManager) getSystemService("wifi");
        this.receiverWifi = new WifiReceiver();
        registerReceiver(this.receiverWifi, new IntentFilter("android.net.wifi.SCAN_RESULTS"));
        if (!this.mainWifi.isWifiEnabled()) {
            this.mainWifi.setWifiEnabled(true);
        }*/

        //this.scanWiFi();

        //miEkuorePro.startRecording();
        //this.handler.post(this.updater);
        startRecording();
        //connectToWifi("ekuorePro_e145d3");
    }

    /*private void scanWiFi() {
        this.mainWifi.startScan();
    }

    class WifiReceiver extends BroadcastReceiver {
        WifiReceiver() {
        }

        public void onReceive(Context c, Intent intent) {
            ArrayList<String> connections = new ArrayList();
            ArrayList<Float> Signal_Strenth = new ArrayList();
            List<ScanResult> wifiList = mainWifi.getScanResults();
            for (int i = 0; i < wifiList.size(); i++) {
                if (((ScanResult) wifiList.get(i)).SSID.startsWith("ekuorePro_")) {
                    wifiName = ((ScanResult) wifiList.get(i)).SSID;
                    miEkuorePro.startRecording();
                    return;
                }
                connections.add(((ScanResult) wifiList.get(i)).SSID);
            }
        }
    }*/

    private void startRecording() {
        this.mRecorder = new MediaRecorder();
        this.mRecorder.setAudioSource(1);
        this.mRecorder.setOutputFormat(2);
        File dir = new File(KAndroidAudioPlayerActivity.this.getExternalFilesDir(null), "Welborn123");
        if (!dir.exists()) {
            dir.mkdir();
        }
        File dir2 = new File(dir.getAbsolutePath() +  new SimpleDateFormat("dd_MM_yyyy_HH_mm_ss").format(Calendar.getInstance().getTime()) + ".mp4");
        this.mRecorder.setOutputFile(dir2.getAbsolutePath());
        this.saveFile = dir2;
        this.mRecorder.setAudioEncoder(3);
        try {
            this.mRecorder.prepare();
        } catch (IOException e) {
            Log.e("stetho", "prepare() failed");
        }
        this.mRecorder.start();
        this.handler.post(this.updater);
    }

    private void stopRecording() {
        this.handler.removeCallbacks(this.updater);
        this.mRecorder.stop();
        this.mRecorder.release();
        this.mRecorder = null;
    }

    class C04523 implements Runnable {
        C04523() {
        }

        public void run() {
            handler.postDelayed(this, 1);
            int maxAmplitude = mRecorder.getMaxAmplitude();
            Log.d("AMP", "run: " + maxAmplitude);
            visualizerView.addAmplitude(maxAmplitude);
        }
    }

}

