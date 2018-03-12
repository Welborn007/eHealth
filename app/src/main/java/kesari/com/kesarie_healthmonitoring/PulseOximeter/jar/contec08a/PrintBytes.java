package kesari.com.kesarie_healthmonitoring.PulseOximeter.jar.contec08a;

import android.util.Log;

public class PrintBytes {
    public static void printData(byte[] pack) {
        Log.i("***********************", "************************");
        String _temp = "";
        int i = 0;
        while (i < pack.length) {
            if (i >= 3 && (i - 2) % 7 == 1) {
                Log.i("Data", _temp);
                _temp = "";
            }
            _temp = new StringBuilder(String.valueOf(_temp)).append(" ").append(Integer.toHexString(pack[i])).toString();
            i++;
        }
        Log.i("Data", _temp);
        Log.i("***********************", "************************");
    }

    public static void printData(byte[] pack, int count) {
        String _temp = "";
        for (int i = 0; i < count; i++) {
            _temp = new StringBuilder(String.valueOf(_temp)).append(" ").append(Integer.toHexString(pack[i] & 255)).toString();
        }
        Log.i("Data", _temp);
    }
}
