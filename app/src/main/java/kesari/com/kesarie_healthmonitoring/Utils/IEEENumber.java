package kesari.com.kesarie_healthmonitoring.Utils;

import android.support.v4.media.TransportMediator;
import android.support.v4.view.GravityCompat;
import android.support.v4.view.InputDeviceCompat;
import android.support.v4.view.ViewCompat;

public class IEEENumber {
    public static double IEEEFloatToDouble(byte[] paramArrayOfByte) throws IllegalArgumentException {
        if (paramArrayOfByte.length != 4) {
            throw new IllegalArgumentException("Input is not IEEE- 754 FLOAT");
        }
        int i = (int) Math.pow(2.0d, 8.0d);
        int j = paramArrayOfByte[3] & 255;
        if (j > TransportMediator.KEYCODE_MEDIA_PAUSE) {
            j += InputDeviceCompat.SOURCE_ANY;
        }
        int k = ((paramArrayOfByte[0] & 255) + ((paramArrayOfByte[1] & 255) * i)) + (((paramArrayOfByte[2] & 255) * i) * i);
        if (k >= GravityCompat.RELATIVE_LAYOUT_DIRECTION) {
            k = -(ViewCompat.MEASURED_STATE_TOO_SMALL - k);
        }
        return ((double) k) * Math.pow(10.0d, (double) j);
    }

    public static double IEEESFloatToDouble(byte[] paramArrayOfByte) throws IllegalArgumentException {
        if (paramArrayOfByte.length != 2) {
            throw new IllegalArgumentException("Input is not IEEE- 754 SFLOAT");
        }
        int i = paramArrayOfByte[0] + (paramArrayOfByte[1] << 8);
        int j = i & 4095;
        if (j > 2048) {
            j = -(4096 - j);
        }
        int k = (61440 & i) >> 12;
        if (k > 8) {
            k = -(16 - k);
        }
        return ((double) j) * Math.pow(10.0d, (double) k);
    }
}
