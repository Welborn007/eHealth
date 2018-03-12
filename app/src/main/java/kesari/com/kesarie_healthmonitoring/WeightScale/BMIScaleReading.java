package kesari.com.kesarie_healthmonitoring.WeightScale;

import android.os.Parcel;
import android.os.Parcelable.Creator;
import java.util.Date;

public class BMIScaleReading extends BaseReading {
    public static final Creator<BMIScaleReading> CREATOR = new C02741();
    private static final double INVALID_WEIGHT = -100.0d;
    private Date createdAt;
    private boolean isFinalValue;
    private String unit;
    private double weight;

    static class C02741 implements Creator {
        C02741() {
        }

        public BMIScaleReading createFromParcel(Parcel paramAnonymousParcel) {
            return new BMIScaleReading(paramAnonymousParcel);
        }

        public BMIScaleReading[] newArray(int paramAnonymousInt) {
            return new BMIScaleReading[paramAnonymousInt];
        }
    }

    public BMIScaleReading(Parcel paramParcel) {
        readFromParcel(paramParcel);
    }

    public BMIScaleReading(String paramMeasurementUnit) {
        this(false, paramMeasurementUnit, INVALID_WEIGHT);
    }

    public BMIScaleReading(boolean paramBoolean, String paramMeasurementUnit, double paramDouble) {
        this.isFinalValue = paramBoolean;
        this.unit = paramMeasurementUnit;
        this.weight = paramDouble;
        this.createdAt = new Date();
    }

    public void clearReading() {
        setWeight(INVALID_WEIGHT);
    }

    public int describeContents() {
        return 0;
    }

    public Date getCreatedAt() {
        return this.createdAt;
    }

    public String getUnit() {
        return this.unit;
    }

    public double getWeight() {
        return this.weight;
    }

    public boolean isFinalValue() {
        return this.isFinalValue;
    }

    protected void readFromParcel(Parcel paramParcel) {
        boolean z = true;
        super.readFromParcel(paramParcel);
        if (paramParcel.readInt() == 1) {
            if (1 != 1) {
                z = false;
            }
            this.isFinalValue = z;
            this.unit = paramParcel.readString();
            this.weight = paramParcel.readDouble();
            this.createdAt = (Date) paramParcel.readSerializable();
        }
        if (1 != 1) {
            z = false;
        }
        this.isFinalValue = z;
        this.unit = paramParcel.readString();
        this.weight = paramParcel.readDouble();
        this.createdAt = (Date) paramParcel.readSerializable();
    }

    public void setCreatedAt(Date paramDate) {
        this.createdAt = paramDate;
    }

    public void setFinalValue(boolean paramBoolean) {
        this.isFinalValue = paramBoolean;
    }

    public void setUnit(String paramMeasurementUnit) {
        this.unit = paramMeasurementUnit;
    }

    public void setWeight(double paramDouble) {
        this.weight = paramDouble;
    }

    public void writeToParcel(Parcel paramParcel, int paramInt) {
        super.writeToParcel(paramParcel, paramInt);
        if (this.isFinalValue) {
            paramParcel.writeInt(1);
            paramParcel.writeSerializable(this.unit);
            paramParcel.writeDouble(this.weight);
            paramParcel.writeSerializable(this.createdAt);
        } else {
            paramParcel.writeInt(1);
            paramParcel.writeSerializable(this.unit);
            paramParcel.writeDouble(this.weight);
            paramParcel.writeSerializable(this.createdAt);
        }
    }
}
