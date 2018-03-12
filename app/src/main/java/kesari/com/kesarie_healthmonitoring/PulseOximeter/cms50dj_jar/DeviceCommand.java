package kesari.com.kesarie_healthmonitoring.PulseOximeter.cms50dj_jar;

import android.util.Log;

import java.util.Calendar;

public class DeviceCommand {
    private static String TAG = "DeviceCommand";
    public static byte[] mDateTimeArrays;
    public static byte[] mDeviceNum = new byte[2];
    public static int mWhichCommand = 0;

    public static byte[] deviceConfirmCommand() {
        mWhichCommand = 1;
        DevicePackManager.mCount = 0;
        byte[] _command = new byte[11];
        _command[0] = (byte) 83;
        _command[1] = (byte) 78;
        _command[2] = (byte) 8;
        _command[4] = (byte) 2;
        _command[5] = (byte) 1;
        _command[6] = (byte) 83;
        _command[7] = (byte) 73;
        _command[8] = (byte) 78;
        _command[9] = (byte) 79;
        _command[10] = (byte) 68;
        return _command;
    }

    public static byte[] correctionDateTime() {
        DevicePackManager.mPack = null;
        DevicePackManager.mPack = new byte[1024];
        DevicePackManager.mCount = 0;
        mWhichCommand = 2;
        mDateTimeArrays = getTimeArray();
        return new byte[]{(byte) -109, (byte) -114, (byte) 10, mDeviceNum[0], mDeviceNum[1], (byte) 2, mDateTimeArrays[0], mDateTimeArrays[1], mDateTimeArrays[2], mDateTimeArrays[3], mDateTimeArrays[4], mDateTimeArrays[5], (byte) (((((((((mDeviceNum[0] + 10) + mDeviceNum[1]) + 2) + mDateTimeArrays[0]) + mDateTimeArrays[1]) + mDateTimeArrays[2]) + mDateTimeArrays[3]) + mDateTimeArrays[4]) + mDateTimeArrays[5])};
    }

    public static byte[] getDataFromDevice() {
        DevicePackManager.mPack = null;
        DevicePackManager.mPack = new byte[1024];
        DevicePackManager.mCount = 0;
        mWhichCommand = 3;
        return new byte[]{(byte) -109, (byte) -114, (byte) 4, mDeviceNum[0], mDeviceNum[1], (byte) 4, (byte) (((mDeviceNum[0] + 4) + mDeviceNum[1]) + 4)};
    }

    public static byte[] dataUploadSuccessCommand() {
        DevicePackManager.mPack = null;
        DevicePackManager.mPack = new byte[1024];
        DevicePackManager.mCount = 0;
        mWhichCommand = 4;
        return new byte[]{(byte) -109, (byte) -114, (byte) 5, mDeviceNum[0], mDeviceNum[1], (byte) 5, (byte) 1, (byte) ((((mDeviceNum[0] + 5) + mDeviceNum[1]) + 5) + 1)};
    }

    public static byte[] dayPedometerDataCommand() {
        DevicePackManager.mPack = null;
        DevicePackManager.mPack = new byte[1024];
        DevicePackManager.mCount = 0;
        mWhichCommand = 5;
        return new byte[]{(byte) -109, (byte) -114, (byte) 4, mDeviceNum[0], mDeviceNum[1], (byte) 17, (byte) (((mDeviceNum[0] + 4) + mDeviceNum[1]) + 17)};
    }

    public static byte[] dayPedometerDataSuccessCommand() {
        DevicePackManager.mPack = null;
        DevicePackManager.mPack = new byte[1024];
        DevicePackManager.mCount = 0;
        mWhichCommand = 6;
        return new byte[]{(byte) -109, (byte) -114, (byte) 4, mDeviceNum[0], mDeviceNum[1], (byte) 18, (byte) (((mDeviceNum[0] + 4) + mDeviceNum[1]) + 18)};
    }

    public static byte[] minPedometerDataCommand() {
        DevicePackManager.mPack = null;
        DevicePackManager.mPack = new byte[1024];
        DevicePackManager.mCount = 0;
        mWhichCommand = 7;
        return new byte[]{(byte) -109, (byte) -114, (byte) 4, mDeviceNum[0], mDeviceNum[1], (byte) 19, (byte) (((mDeviceNum[0] + 4) + mDeviceNum[1]) + 19)};
    }

    public static byte[] minPedometerDataSuccessCommand() {
        DevicePackManager.mPack = null;
        DevicePackManager.mPack = new byte[1024];
        DevicePackManager.mCount = 0;
        mWhichCommand = 8;
        return new byte[]{(byte) -109, (byte) -114, (byte) 4, mDeviceNum[0], mDeviceNum[1], (byte) 20, (byte) (((mDeviceNum[0] + 4) + mDeviceNum[1]) + 20)};
    }

    public static byte[] setPedometerInfo(String height, String weight, int startTime, int overTime, int calTarget, int dayNum, int sensitivity) {
        mWhichCommand = 9;
        DevicePackManager.mPack = null;
        DevicePackManager.mPack = new byte[1024];
        DevicePackManager.mCount = 0;
        byte _height = (byte) 0;
        if (height.length() > 3) {
            _height = (byte) Integer.parseInt(height.substring(0, height.length() - 3));
        }
        byte _weitghtL = (byte) 0;
        byte _weitghtH = (byte) 0;
        if (weight.length() > 3) {
            _weitghtH = (byte) Integer.parseInt(weight.substring(0, weight.length() - 3));
            _weitghtL = (byte) Integer.parseInt(weight.substring(weight.length() - 2, weight.length()));
        }
        byte _calTargetL = (byte) (calTarget & 255);
        byte _calTargetH = (byte) ((calTarget >> 8) & 255);
        return new byte[]{(byte) -109, (byte) -114, (byte) 13, mDeviceNum[0], mDeviceNum[1], (byte) 16, _height, _weitghtH, _weitghtL, (byte) startTime, (byte) overTime, _calTargetH, _calTargetL, (byte) dayNum, (byte) sensitivity, (byte) ((((((((((((mDeviceNum[0] + 13) + mDeviceNum[1]) + 16) + _height) + _weitghtH) + _weitghtL) + ((byte) startTime)) + ((byte) overTime)) + _calTargetH) + _calTargetL) + ((byte) dayNum)) + ((byte) sensitivity))};
    }

    public static byte[] getTimeArray() {
         byte[] _timeArr = new byte[6];
        Calendar _c = Calendar.getInstance();
        _timeArr[0] = (byte) (_c.get(1) - 2000);
        _timeArr[1] = (byte) (_c.get(2) + 1);
        _timeArr[2] = (byte) _c.get(5);
        _timeArr[3] = (byte) _c.get(11);
        _timeArr[4] = (byte) _c.get(12);
        _timeArr[5] = (byte) _c.get(13);
        Log.e("DeviceCommand", "yy:" + _timeArr[0] + " month:" + _timeArr[1] + " day:" + _timeArr[2] + " HH:" + _timeArr[3] + " mm:" + _timeArr[4] + " ss:" + _timeArr[5]);
        return _timeArr;
    }
}
