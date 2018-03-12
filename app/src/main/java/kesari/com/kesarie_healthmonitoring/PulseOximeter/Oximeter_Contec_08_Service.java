package kesari.com.kesarie_healthmonitoring.PulseOximeter;

import android.content.Intent;
import android.os.IBinder;

import java.io.IOException;

import kesari.com.kesarie_healthmonitoring.PulseOximeter.cms50dj_jar.DeviceCommand;
import kesari.com.kesarie_healthmonitoring.PulseOximeter.cms50dj_jar.DevicePackManager;


/**
 * Created by kesari on 22/01/18.
 */

public class Oximeter_Contec_08_Service extends BluetoothHDPService {
    DevicePackManager devicePackManager;

    public void onCreate() {
        super.onCreate();
        setManufacturer(CONTEC);
        setDeviceName(OXIMETER);
        setMeasurementType(OXIMETER);
        this.devicePackManager = new DevicePackManager();
    }

    public int onStartCommand(Intent intent, int flags, int startId) {
        return super.onStartCommand(intent, flags, startId);
    }

    public IBinder onBind(Intent intent) {
        return super.onBind(intent);
    }

    byte[] getDeviceConfirmCommand() {
        return DeviceCommand.deviceConfirmCommand();
    }

    byte[] getcorrectionDateTime() {
        return DeviceCommand.correctionDateTime();
    }

    byte[] requestOximeterData() {
        return DeviceCommand.getDataFromDevice();
    }

    byte[] deleteData() {
        return DeviceCommand.dataUploadSuccessCommand();
    }

    public void requestData() {
        sendData(null, requestOximeterData());
    }

    public byte[] getInitiateCommand() {
        return getDeviceConfirmCommand();
    }

    public void initiatHandshake() {
    }

    public void run() {
        byte[] buffer = new byte[1024];
        try {
            this.devicePackManager.initCmdPosition();
            write(getDeviceConfirmCommand());
            int result = this.devicePackManager.arrangeMessage(buffer, this.inStream.read(buffer));
            write(getcorrectionDateTime());
            if (this.devicePackManager.arrangeMessage(buffer, this.inStream.read(buffer)) == 2) {
                writeData(requestOximeterData());
                if (this.devicePackManager.arrangeMessage(buffer, this.inStream.read(buffer)) == 6) {
                    SpO250D spo = new SpO250D();
                    spo.processData(this.devicePackManager.getDeviceData50dj().getmSpoData());
                    writeData(deleteData());
                    result = this.devicePackManager.arrangeMessage(buffer, this.inStream.read(buffer));
                    sendMessageToUI(1001, 1001, spo);
                    return;
                }
                sendMessageToUI(1000, 1000, "No Pending Data");
                return;
            }
            sendMessageToUI(103, 103, "Device connection lost");
            disconnectChannel(null);
        } catch (IOException e) {
            sendMessageToUI(103, 103, "Device connection lost");
            disconnectChannel(null);
            e.printStackTrace();
        }
    }
}