package kesari.com.kesarie_healthmonitoring.WeightScale;

import android.bluetooth.BluetoothDevice;
import android.os.Parcel;
import android.os.Parcelable;
import android.support.annotation.Nullable;

public abstract class BaseReading implements Parcelable {
    private String deviceAddress;
    private String deviceManufacturer;
    private String deviceName;
    private int googleFitDeviceType;

    @Nullable
    public final String getDeviceAddress() {
        return this.deviceAddress;
    }

    @Nullable
    public final String getDeviceManufacturer() {
        return this.deviceManufacturer;
    }

    @Nullable
    public final String getDeviceName() {
        return this.deviceName;
    }

    public final int getGoogleFitDeviceType() {
        return this.googleFitDeviceType;
    }

    protected void readFromParcel(Parcel paramParcel) {
        this.deviceManufacturer = paramParcel.readString();
        this.deviceName = paramParcel.readString();
        this.deviceAddress = paramParcel.readString();
        this.googleFitDeviceType = paramParcel.readInt();
    }

    public final void setDeviceInfo(@Nullable BluetoothDevice paramBluetoothDevice) {
        if (paramBluetoothDevice != null) {
            this.deviceName = paramBluetoothDevice.getName();
            this.deviceAddress = paramBluetoothDevice.getAddress();
        }
    }

    public final void setDeviceManufacturer(@Nullable String paramString) {
        this.deviceManufacturer = paramString;
    }

    public final void setGoogleFitDeviceType(int paramInt) {
        this.googleFitDeviceType = paramInt;
    }

    public void writeToParcel(Parcel paramParcel, int paramInt) {
        paramParcel.writeString(this.deviceManufacturer);
        paramParcel.writeString(this.deviceName);
        paramParcel.writeString(this.deviceAddress);
        paramParcel.writeInt(this.googleFitDeviceType);
    }
}
