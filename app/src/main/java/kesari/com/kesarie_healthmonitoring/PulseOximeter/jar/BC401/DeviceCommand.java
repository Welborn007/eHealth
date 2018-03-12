package kesari.com.kesarie_healthmonitoring.PulseOximeter.jar.BC401;

import java.util.Calendar;

public class DeviceCommand {
    public static byte[] doPack(byte[] pbyte) {
        int _size = pbyte.length;
        int _checkSum = 0;
        for (int i = 2; i < _size - 1; i++) {
            _checkSum += pbyte[i];
        }
        pbyte[_size - 1] = (byte) _checkSum;
        return pbyte;
    }

    public static byte[] Request_AllData() {
        byte[] _request = new byte[7];
        _request[0] = (byte) -109;
        _request[1] = (byte) -114;
        _request[2] = (byte) 4;
        _request[4] = (byte) 9;
        _request[5] = (byte) 5;
        _request[6] = (byte) 18;
        return _request;
    }

    public static byte[] Delete_AllData() {
        byte[] _request = new byte[7];
        _request[0] = (byte) -109;
        _request[1] = (byte) -114;
        _request[2] = (byte) 4;
        _request[4] = (byte) 9;
        _request[5] = (byte) 6;
        _request[6] = (byte) 19;
        return _request;
    }

    public static byte[] Synchronous_Time() {
        int mYear = Calendar.getInstance().get(1) - 2000;
        int mMonth = Calendar.getInstance().get(2) + 1;
        int mDay = Calendar.getInstance().get(5);
        int mHours = Calendar.getInstance().get(11);
        int mMinutes = Calendar.getInstance().get(12);
        byte[] _times = new byte[12];
        _times[0] = (byte) -109;
        _times[1] = (byte) -114;
        _times[2] = (byte) 9;
        _times[4] = (byte) 9;
        _times[5] = (byte) 2;
        _times[6] = (byte) mYear;
        _times[7] = (byte) mMonth;
        _times[8] = (byte) mDay;
        _times[9] = (byte) mHours;
        _times[10] = (byte) mMinutes;
        return doPack(_times);
    }
}
