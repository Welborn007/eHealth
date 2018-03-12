package kesari.com.kesarie_healthmonitoring.Utils;

public class Utils {
    public static float celsiusToFarenheit(float paramFloat) {
        return 32.0f + ((9.0f * paramFloat) / 5.0f);
    }

    public static double fahrenheitToCelsius(double paramDouble) {
        return (5.0d * (paramDouble - 32.0d)) / 9.0d;
    }

    public static byte[] hexToBytes(String paramString) {
        int i = paramString.length();
        byte[] arrayOfByte = new byte[(i / 2)];
        for (int j = 0; j < i; j += 2) {
            arrayOfByte[j / 2] = (byte) ((Character.digit(paramString.charAt(j), 16) << 4) + Character.digit(paramString.charAt(j + 1), 16));
        }
        return arrayOfByte;
    }
}
