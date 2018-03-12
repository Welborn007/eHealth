package kesari.com.kesarie_healthmonitoring.PulseOximeter.cms50dj_jar;

import android.util.Log;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.List;

public class DeviceDataPedometerJar implements Serializable {
    private static final long serialVersionUID = -180027759780429050L;
    private String TAG = "DeviceDataPedometer";
    MinData mMinData = new MinData();
    private List<byte[]> mPedometerDataDayList = new ArrayList();
    private List<MinData> mPedometerDataMinList = new ArrayList();

    public void addDayPedometerData(byte[] pdayData) {
        this.mPedometerDataDayList.add(pdayData);
        Log.d(this.TAG, getDayString(pdayData));
    }

    public void addMinPedometerData(MinData pminData) {
        this.mPedometerDataMinList.add(pminData);
        this.mMinData = null;
        this.mMinData = new MinData();
    }

    public List<byte[]> getmPedometerDataDayList() {
        return this.mPedometerDataDayList;
    }

    public List<MinData> getmPedometerDataMinList() {
        return this.mPedometerDataMinList;
    }

    public void setmMinData(MinData mMinData) {
        this.mMinData = mMinData;
    }

    public void setmPedometerDataDayList(List<byte[]> mPedometerDataDayList) {
        this.mPedometerDataDayList = mPedometerDataDayList;
    }

    public void setmPedometerDataMinList(List<MinData> mPedometerDataMinList) {
        this.mPedometerDataMinList = mPedometerDataMinList;
    }

    public String getMinString(byte[] pminData) {
        return " cal:" + ((pminData[1] & 255) | ((pminData[0] & 255) << 8)) + "  steps:" + ((pminData[3] & 255) | ((pminData[2] & 255) << 8));
    }

    public MinData getMinData() {
        return this.mMinData;
    }

    public String getDayString(byte[] pdayData) {
        return "year:" + pdayData[0] + " month:" + pdayData[1] + "  day:" + pdayData[2] + " steps:" + ((pdayData[4] & 255) | ((pdayData[3] & 255) << 8)) + "  cal_target:" + ((pdayData[6] & 255) | ((pdayData[5] & 255) << 8)) + "  cal:" + ((pdayData[8] & 255) | ((pdayData[7] & 255) << 8));
    }
}
