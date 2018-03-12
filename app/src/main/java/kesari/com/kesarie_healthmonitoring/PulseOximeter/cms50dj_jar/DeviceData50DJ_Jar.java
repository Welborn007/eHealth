package kesari.com.kesarie_healthmonitoring.PulseOximeter.cms50dj_jar;

import android.util.Log;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.List;

public class DeviceData50DJ_Jar implements Serializable {
    private static final long serialVersionUID = 2376901827057666502L;
    private List<byte[]> mSp02DataList = new ArrayList();
    private byte[] mSpoData;

    public void addData(byte[] data) {
        this.mSpoData = data;
        this.mSp02DataList.add(this.mSpoData);
        Log.d("数据信息：", toString());
    }

    public List<byte[]> getmSp02DataList() {
        return this.mSp02DataList;
    }

    public byte[] getmSpoData() {
        return this.mSpoData;
    }

    public void setmSp02DataList(List<byte[]> mSp02DataList) {
        this.mSp02DataList = mSp02DataList;
    }

    public void setmSpoData(byte[] mSpoData) {
        this.mSpoData = mSpoData;
    }

    public String toString() {
        return "year:" + this.mSpoData[0] + " month:" + this.mSpoData[1] + "  day:" + this.mSpoData[2] + " hour:" + this.mSpoData[3] + "  min:" + this.mSpoData[4] + "  second:" + this.mSpoData[5] + "  spo:" + this.mSpoData[6] + "  pluse:" + this.mSpoData[7];
    }
}
