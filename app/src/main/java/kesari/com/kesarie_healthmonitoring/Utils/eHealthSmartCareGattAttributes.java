package kesari.com.kesarie_healthmonitoring.Utils;

import java.util.HashMap;
import java.util.UUID;

public class eHealthSmartCareGattAttributes {
    public static final UUID AND_BPM_CUSTOM_SERVICE_UUID = UUID.fromString("00002a08-0000-1000-8000-00805f9b34fb");
    public static final UUID BPM_INDICATION_UUID = UUID.fromString("0000fff4-0000-1000-8000-00805f9b34fb");
    public static final UUID BPM_SERVICE_UUID = UUID.fromString("0000fff0-0000-1000-8000-00805f9b34fb");
    public static UUID CLIENT_CHARACTERISTIC_CONFIG = UUID.fromString("00002902-0000-1000-8000-00805f9b34fb");
    public static final UUID GLUCOSE_CUSTOM_NOTIFICATION_UUID = UUID.fromString("0000fff4-0000-1000-8000-00805f9b34fb");
    public static final UUID GLUCOSE_CUSTOM_SERVICE_UUID = UUID.fromString("0000fff0-0000-1000-8000-00805f9b34fb");
    public static final UUID GLUCOSE_STANDARD_NOTIFICATION_UUID = UUID.fromString("00002a18-0000-1000-8000-00805f9b34fb");
    public static final UUID GLUCOSE_STANDARD_SERVICE_UUID = UUID.fromString("00001808-0000-1000-8000-00805f9b34fb");
    public static String HEART_RATE_MEASUREMENT = "00002a37-0000-1000-8000-00805f9b34fb";
    public static final UUID OXIMETER_NOTIFICATION_UUID = UUID.fromString("CDEACB81-5235-4C07-8846-93A37EE6B86D");
    public static final String OXIMETER_PREPARE_WRITE_ALERT_SETTING = "02";
    public static final String OXIMETER_READ_ALERT_SETTING = "01";
    public static final UUID OXIMETER_SERVICE_UUID = UUID.fromString("CDEACB80-5235-4C07-8846-93A37EE6B86D");
    public static final UUID OXIMETER_WRITE_UUID = UUID.fromString("CDEACB82-5235-4C07-8846-93A37EE6B86D");
    public static final UUID TEMPERATURE_INTERMEDIATE_UUID = UUID.fromString("00002a12-0000-1000-8000-00805f9b34fb");
    public static final UUID THERMOMETER_INDICATION_UUID = UUID.fromString("00002a1c-0000-1000-8000-00805f9b34fb");
    public static final UUID THERMOMETER_SERVICE_UUID = UUID.fromString("00001809-0000-1000-8000-00805f9b34fb");
    public static final UUID WEIGHT_INDICATION_UUID = UUID.fromString("0000fff4-0000-1000-8000-00805f9b34fb");
    public static final UUID WEIGHT_SERVICE_UUID = UUID.fromString("0000fff0-0000-1000-8000-00805f9b34fb");
    private static HashMap<String, String> attributes = new HashMap();

    public enum OximeterState {
        NOT_MEASURING("NOT_MEASURING", 0),
        MEASURING("MEASURING", 1),
        FINISHED("FINISHED", 2);
        
        private int intValue;
        private String stringValue;

        private OximeterState(String toString, int value) {
            this.stringValue = toString;
            this.intValue = value;
        }

        public String toString() {
            return this.stringValue;
        }
    }

    static {
        attributes.put("0000180d-0000-1000-8000-00805f9b34fb", "Heart Rate Service");
        attributes.put("00001809-0000-1000-8000-00805f9b34fb", "Tem BH");
        attributes.put(HEART_RATE_MEASUREMENT, "Heart Rate Measurement");
        attributes.put("00002a29-0000-1000-8000-00805f9b34fb", "Manufacturer Name String");
    }

    public static String lookup(String uuid, String defaultName) {
        String name = (String) attributes.get(uuid);
        return name == null ? defaultName : name;
    }
}
