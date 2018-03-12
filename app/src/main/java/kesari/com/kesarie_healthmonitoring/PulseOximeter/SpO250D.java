package kesari.com.kesarie_healthmonitoring.PulseOximeter;

public class SpO250D extends SpO2 {
    public SpO250D processData(byte[] paramArrayOfByte) {
        SpO250D localSpO250D = this;
        int i = paramArrayOfByte[0];
        if (i > 9) {
            localSpO250D.YEAR = "20" + i;
        } else {
            localSpO250D.YEAR = "200" + i;
        }
        if (paramArrayOfByte[1] <= (byte) 9) {
            localSpO250D.MONTH = "0" + paramArrayOfByte[1];
        } else {
            localSpO250D.MONTH = "" + paramArrayOfByte[1];
        }
        if (paramArrayOfByte[2] <= (byte) 9) {
            localSpO250D.DAY = "0" + paramArrayOfByte[2];
        } else {
            localSpO250D.DAY = "" + paramArrayOfByte[2];
        }
        if (paramArrayOfByte[3] <= (byte) 9) {
            localSpO250D.HOUR = "" + paramArrayOfByte[3];
        } else {
            localSpO250D.HOUR = "0" + paramArrayOfByte[3];
        }
        if (paramArrayOfByte[4] <= (byte) 9) {
            localSpO250D.MIN = "0" + paramArrayOfByte[4];
        } else {
            localSpO250D.MIN = "" + paramArrayOfByte[4];
        }
        if (paramArrayOfByte[5] <= (byte) 9) {
            localSpO250D.SEC = "0" + paramArrayOfByte[5];
        } else {
            localSpO250D.SEC = "" + paramArrayOfByte[5];
        }
        localSpO250D.SpO2 = "" + ((short) (paramArrayOfByte[6] & 255));
        localSpO250D.PR = "" + ((short) (paramArrayOfByte[7] & 255));
        return localSpO250D;
    }
}
