package kesari.com.kesarie_healthmonitoring.WeightScale;

import android.bluetooth.BluetoothDevice;
import android.content.Intent;
import android.support.annotation.NonNull;
import android.util.Log;

public class WeighingScaleReadingParser {
    public static final String KEY_BMI_SCALE_READING = "BMIScaleReading";
    public static final String KEY_FAT_SCALE_READING = "FatScaleReading";
    private static final String TAG = WeighingScaleReadingParser.class.getSimpleName();

    private String parseBMIScaleReading(@NonNull BluetoothDevice paramBluetoothDevice, @NonNull Intent paramIntent, @NonNull byte[] paramArrayOfByte) {
        String weightData;
        BMIScaleReading localBMIScaleReading = new BMIScaleReading("Kgs");
        localBMIScaleReading.setDeviceInfo(paramBluetoothDevice);
        if ((byte) -53 == paramArrayOfByte[0]) {
            localBMIScaleReading.setFinalValue(true);
        } else {
            localBMIScaleReading.setFinalValue(false);
        }
        localBMIScaleReading.setUnit("Kgs");
        localBMIScaleReading.setWeight((double) (((paramArrayOfByte[2] & 255) << 8) | (paramArrayOfByte[3] & 255)));
        localBMIScaleReading.setWeight(0.1d * localBMIScaleReading.getWeight());
        if (paramArrayOfByte[1] == (byte) 1) {
            localBMIScaleReading.setWeight(localBMIScaleReading.getWeight() / 2.2046000957489014d);
        } else if (paramArrayOfByte[1] == (byte) 2) {
            localBMIScaleReading.setWeight(localBMIScaleReading.getWeight() / 0.1574700027704239d);
        }
        paramIntent.putExtra(KEY_BMI_SCALE_READING, localBMIScaleReading);
        paramIntent.putExtra("weight", localBMIScaleReading.getWeight());
        Log.d("rpm", "Weight - " + localBMIScaleReading.getWeight());

        weightData = "Weight - " + localBMIScaleReading.getWeight();
        return weightData;
    }

    public String parseData(@NonNull BluetoothDevice paramBluetoothDevice, @NonNull Intent paramIntent, @NonNull byte[] paramArrayOfByte) {
        String str = paramBluetoothDevice.getName();
        return parseBMIScaleReading(paramBluetoothDevice, paramIntent, paramArrayOfByte);
    }
}
