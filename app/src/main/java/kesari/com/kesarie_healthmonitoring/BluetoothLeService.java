package kesari.com.kesarie_healthmonitoring;

import android.app.Service;
import android.bluetooth.BluetoothAdapter;
import android.bluetooth.BluetoothDevice;
import android.bluetooth.BluetoothGatt;
import android.bluetooth.BluetoothGattCallback;
import android.bluetooth.BluetoothGattCharacteristic;
import android.bluetooth.BluetoothGattDescriptor;
import android.bluetooth.BluetoothGattService;
import android.bluetooth.BluetoothManager;
import android.content.Intent;
import android.os.Binder;
import android.os.Bundle;
import android.os.Handler;
import android.os.IBinder;
import android.os.Looper;
import android.support.v4.view.MotionEventCompat;
import android.util.Log;

import java.util.Arrays;
import java.util.Calendar;
import java.util.List;
import java.util.UUID;

import kesari.com.kesarie_healthmonitoring.Utils.IEEENumber;
import kesari.com.kesarie_healthmonitoring.Utils.Utils;
import kesari.com.kesarie_healthmonitoring.Utils.eHealthSmartCareGattAttributes;
import kesari.com.kesarie_healthmonitoring.WeightScale.WeighingScaleReadingParser;

public class BluetoothLeService extends Service {
    public static final String ACTION_DATA_AVAILABLE = "com.example.bluetooth.le.ACTION_DATA_AVAILABLE";
    public static final String ACTION_GATT_CONNECTED = "com.example.bluetooth.le.ACTION_GATT_CONNECTED";
    public static final String ACTION_GATT_DISCONNECTED = "com.example.bluetooth.le.ACTION_GATT_DISCONNECTED";
    public static final String ACTION_GATT_SERVICES_DISCOVERED = "com.example.bluetooth.le.ACTION_GATT_SERVICES_DISCOVERED";
    public static final String CHOLESTROL = "CHOLESTROL";
    public static final String EXTRA_DATA = "com.example.bluetooth.le.EXTRA_DATA";
    public static final String GLUCOSE = "GLUCOSE";
    private static final int STATE_CONNECTED = 2;
    private static final int STATE_CONNECTING = 1;
    private static final int STATE_DISCONNECTED = 0;
    private static final String TAG = BluetoothLeService.class.getSimpleName();
    public static final String TEMPERATURE_CELCIUS = "TEMPERATURE_CELCIUS";
    public static final String TEMPERATURE_FAHRENHEIT = "TEMPERATURE_FAHRENHEIT";
    private BluetoothGattCharacteristic commandCharacteristic;
    BluetoothDevice device;
    private final IBinder mBinder = new LocalBinder();
    private BluetoothAdapter mBluetoothAdapter;
    private String mBluetoothDeviceAddress;
    private BluetoothGatt mBluetoothGatt;
    private BluetoothManager mBluetoothManager;
    private int mConnectionState = 0;
    private final BluetoothGattCallback mGattCallback = new C02664();
    public Handler mHandler = null;
    eHealthSmartCareGattAttributes.OximeterState oximeterState;
    boolean shouldRecordOximData;
    int Flag = 0,Systolic_High = 0,Systolic_Low = 0,Diastolic_High = 0,Diastolic_Low = 0,Map_High = 0,Map_Low = 0,Pulse_Rate_High = 0,Pulse_Rate_Low = 0,User_ID = 0,Meas_Status_High = 0,Meas_Status_Low = 0;

    class C02621 implements Runnable {
        C02621() {
        }

        public void run() {
            if (BluetoothLeService.this.mBluetoothGatt != null) {
                BluetoothGattService localBluetoothGattService = BluetoothLeService.this.mBluetoothGatt.getService(ADGattUUID.DeviceInformationService);
                if (localBluetoothGattService != null) {
                    BluetoothGattCharacteristic localBluetoothGattCharacteristic = localBluetoothGattService.getCharacteristic(ADGattUUID.FirmwareRevisionString);
                    if (localBluetoothGattCharacteristic != null) {
                        Log.d(BluetoothLeService.TAG, "requestReadFirmRevision: " + BluetoothLeService.this.mBluetoothGatt.readCharacteristic(localBluetoothGattCharacteristic));
                    }
                }
            }
        }
    }

    class C02664 extends BluetoothGattCallback {
        C02664() {
        }

        public void onConnectionStateChange(BluetoothGatt gatt, int status, int newState) {
            String intentAction;
            if (newState == 2) {
                intentAction = BluetoothLeService.ACTION_GATT_CONNECTED;
                BluetoothLeService.this.mConnectionState = 2;
                BluetoothLeService.this.broadcastUpdate(intentAction);
                Log.i(BluetoothLeService.TAG, "Connected to GATT server.");
                Log.d("SN", "Device Address : " + BluetoothLeService.this.mBluetoothGatt.getDevice().getAddress());
                Log.d("SN", "Bond State: " + BluetoothLeService.this.mBluetoothGatt.getDevice().getBondState());
                BluetoothLeService.this.mBluetoothGatt.discoverServices();
            } else if (newState == 0) {
                intentAction = BluetoothLeService.ACTION_GATT_DISCONNECTED;
                BluetoothLeService.this.mConnectionState = 0;
                if (BluetoothLeService.this.mBluetoothGatt != null) {
                    BluetoothLeService.this.mBluetoothGatt.close();
                }
                Log.i(BluetoothLeService.TAG, "Disconnected from GATT server.");
                BluetoothLeService.this.broadcastUpdate(intentAction);
            }
        }

        public void onServicesDiscovered(final BluetoothGatt gatt, final int status) {
            new Handler(Looper.getMainLooper()).postDelayed(new Runnable() {
                public void run() {
                    BluetoothLeService.this.mBluetoothGatt = gatt;
                    if (status != 0) {
                        Log.w(BluetoothLeService.TAG, "onServicesDiscovered received: " + status);
                    } else if (BluetoothLeService.this.mBluetoothGatt.getDevice().getName().equals("Medical")) {
                        BluetoothLeService.this.enableCharacteristicsNotification(eHealthSmartCareGattAttributes.OXIMETER_SERVICE_UUID, eHealthSmartCareGattAttributes.CLIENT_CHARACTERISTIC_CONFIG, eHealthSmartCareGattAttributes.OXIMETER_NOTIFICATION_UUID, BluetoothGattDescriptor.ENABLE_NOTIFICATION_VALUE);
                    } else if (BluetoothLeService.this.mBluetoothGatt.getDevice().getName().equals("Samico GL")) {
                        Log.i("Samico GL","Samico GL");
                        BluetoothLeService.this.enableCharacteristicsNotification(eHealthSmartCareGattAttributes.GLUCOSE_CUSTOM_SERVICE_UUID, eHealthSmartCareGattAttributes.CLIENT_CHARACTERISTIC_CONFIG, eHealthSmartCareGattAttributes.GLUCOSE_CUSTOM_NOTIFICATION_UUID, BluetoothGattDescriptor.ENABLE_NOTIFICATION_VALUE);
                    } else if (BluetoothLeService.this.mBluetoothGatt.getDevice().getName().startsWith("A&D_UA-651BLE")) {
                        BluetoothLeService.this.requestReadFirmRevision();
                    } else if (BluetoothLeService.this.mBluetoothGatt.getDevice().getName().startsWith("A&D_UC-352BLE")) {
                        BluetoothLeService.this.requestReadFirmRevision();
                    } else if (BluetoothLeService.this.mBluetoothGatt.getDevice().getName().startsWith("BPM_01")) {
                        BluetoothLeService.this.enableCharacteristicsNotification(eHealthSmartCareGattAttributes.BPM_SERVICE_UUID, eHealthSmartCareGattAttributes.CLIENT_CHARACTERISTIC_CONFIG, eHealthSmartCareGattAttributes.BPM_INDICATION_UUID, BluetoothGattDescriptor.ENABLE_NOTIFICATION_VALUE);
                    } else if (BluetoothLeService.this.mBluetoothGatt.getDevice().getName().startsWith("Samico Scales")) {
                        BluetoothLeService.this.enableCharacteristicsNotification(eHealthSmartCareGattAttributes.WEIGHT_SERVICE_UUID, eHealthSmartCareGattAttributes.CLIENT_CHARACTERISTIC_CONFIG, eHealthSmartCareGattAttributes.WEIGHT_INDICATION_UUID, BluetoothGattDescriptor.ENABLE_NOTIFICATION_VALUE);
                    } else if (BluetoothLeService.this.mBluetoothGatt.getDevice().getName().startsWith("Tem BH")) {
                        BluetoothLeService.this.enableCharacteristicsNotification(eHealthSmartCareGattAttributes.THERMOMETER_SERVICE_UUID, eHealthSmartCareGattAttributes.CLIENT_CHARACTERISTIC_CONFIG, eHealthSmartCareGattAttributes.THERMOMETER_INDICATION_UUID, BluetoothGattDescriptor.ENABLE_INDICATION_VALUE);
                        BluetoothLeService.this.broadcastUpdate(BluetoothLeService.ACTION_GATT_SERVICES_DISCOVERED);
                    }
                }
            }, 1000);
        }

        public void onCharacteristicRead(BluetoothGatt gatt, BluetoothGattCharacteristic characteristic, int status) {
            if (status == 0) {
                Log.d("SN", "onCharacteristicRead()" + gatt.getDevice().getAddress() + ", " + gatt.getDevice().getName() + "characteristic=" + characteristic.getUuid().toString());
                BluetoothLeService.this.broadcastUpdate(BluetoothLeService.ACTION_DATA_AVAILABLE, characteristic);
            }
        }

        public void onCharacteristicWrite(BluetoothGatt paramAnonymousBluetoothGatt, BluetoothGattCharacteristic paramAnonymousBluetoothGattCharacteristic, int paramAnonymousInt) {
            Log.d("SN", "onCharacteristicWrite()" + paramAnonymousBluetoothGatt.getDevice().getAddress() + ", " + paramAnonymousBluetoothGatt.getDevice().getName() + "characteristic=" + paramAnonymousBluetoothGattCharacteristic.getUuid().toString());
            BluetoothLeService.this.setNotificationSetting(BluetoothLeService.this.mBluetoothGatt, true);
        }

        public void onCharacteristicChanged(BluetoothGatt gatt, BluetoothGattCharacteristic characteristic) {
            BluetoothLeService.this.broadcastUpdate(BluetoothLeService.ACTION_DATA_AVAILABLE, characteristic);
        }
    }

    public class LocalBinder extends Binder {
        BluetoothLeService getService() {
            return BluetoothLeService.this;
        }
    }

    public void requestReadFirmRevision() {
        this.mHandler.postDelayed(new C02621(), 500);
    }

    public void setNotificationSetting(final BluetoothGatt paramBluetoothGatt, final boolean paramBoolean) {
        this.mHandler.postDelayed(new Runnable() {
            public void run() {
                BluetoothGattService localBluetoothGattService = BluetoothLeService.this.getGattSearvice(paramBluetoothGatt);
                if (localBluetoothGattService != null) {
                    BluetoothGattCharacteristic localBluetoothGattCharacteristic = BluetoothLeService.this.getGattMeasuCharacteristic(localBluetoothGattService);
                    if (localBluetoothGattCharacteristic != null) {
                        boolean bool = paramBluetoothGatt.setCharacteristicNotification(localBluetoothGattCharacteristic, paramBoolean);
                        BluetoothGattDescriptor localBluetoothGattDescriptor = localBluetoothGattCharacteristic.getDescriptor(ADGattUUID.ClientCharacteristicConfiguration);
                        localBluetoothGattDescriptor.setValue(BluetoothGattDescriptor.ENABLE_INDICATION_VALUE);
                        paramBluetoothGatt.writeDescriptor(localBluetoothGattDescriptor);
                        if (!bool) {
                        }
                    }
                }
            }
        }, Long.MIN_VALUE);
    }

    public BluetoothGattService getGattSearvice(BluetoothGatt paramBluetoothGatt) {
        BluetoothGattService localBluetoothGattService = null;
        for (UUID service : ADGattUUID.ServicesUUIDs) {
            localBluetoothGattService = paramBluetoothGatt.getService(service);
            if (localBluetoothGattService != null) {
                break;
            }
        }
        return localBluetoothGattService;
    }

    private BluetoothGattCharacteristic getGattMeasuCharacteristic(BluetoothGattService paramBluetoothGattService) {
        BluetoothGattCharacteristic localBluetoothGattCharacteristic = null;
        for (UUID characteristic : ADGattUUID.MeasuCharacUUIDs) {
            localBluetoothGattCharacteristic = paramBluetoothGattService.getCharacteristic(characteristic);
            if (localBluetoothGattCharacteristic != null) {
                break;
            }
        }
        return localBluetoothGattCharacteristic;
    }

    public void setDateTimeSetting(final BluetoothGatt paramBluetoothGatt, final Calendar paramCalendar) {
        this.mHandler.postDelayed(new Runnable() {
            public void run() {
                BluetoothGattService localBluetoothGattService = BluetoothLeService.this.getGattSearvice(paramBluetoothGatt);
                Log.d("SN", "setDateTimeSetting " + paramCalendar.getTime());
                if (localBluetoothGattService != null) {
                    BluetoothGattCharacteristic localBluetoothGattCharacteristic = localBluetoothGattService.getCharacteristic(ADGattUUID.DateTime);
                    if (localBluetoothGattCharacteristic != null) {
                        boolean bool = paramBluetoothGatt.writeCharacteristic(BluetoothLeService.this.writeCharacteristic(localBluetoothGattCharacteristic, paramCalendar));
                    }
                }
            }
        }, Long.MIN_VALUE);
    }

    public BluetoothGattCharacteristic writeCharacteristic(BluetoothGattCharacteristic paramBluetoothGattCharacteristic, Calendar paramCalendar) {
        int i = paramCalendar.get(1);
        int j = paramCalendar.get(2) + 1;
        int k = paramCalendar.get(5);
        int m = paramCalendar.get(11);
        int n = paramCalendar.get(12);
        int i1 = paramCalendar.get(13);
        paramBluetoothGattCharacteristic.setValue(new byte[]{(byte) (i & 255), (byte) (i >> 8), (byte) j, (byte) k, (byte) m, (byte) n, (byte) i1});
        return paramBluetoothGattCharacteristic;
    }

    private void broadcastUpdate(String action) {
        sendBroadcast(new Intent(action));
    }

    public void enableCharacteristicsNotification(UUID serviceUUID, UUID descriptorUUID, UUID characteristicsUUID, byte[] data) {
        if (this.mBluetoothAdapter == null || this.mBluetoothGatt == null) {
            Log.w(TAG, "BluetoothAdapter not initialized");
            return;
        }
        BluetoothGattCharacteristic localBluetoothGattCharacteristic = null;
        BluetoothGattService localBluetoothGattService = this.mBluetoothGatt.getService(serviceUUID);
        if (localBluetoothGattService != null) {
            localBluetoothGattCharacteristic = localBluetoothGattService.getCharacteristic(characteristicsUUID);
            this.mBluetoothGatt.setCharacteristicNotification(localBluetoothGattCharacteristic, true);
        } else {
            localBluetoothGattService = this.mBluetoothGatt.getService(characteristicsUUID);
            if (localBluetoothGattService != null) {
                localBluetoothGattCharacteristic = localBluetoothGattService.getCharacteristic(characteristicsUUID);
                this.mBluetoothGatt.setCharacteristicNotification(localBluetoothGattCharacteristic, true);
            }
        }
        BluetoothGattDescriptor localBluetoothGattDescriptor = localBluetoothGattCharacteristic.getDescriptor(descriptorUUID);
        localBluetoothGattDescriptor.setValue(data);
        this.mBluetoothGatt.writeDescriptor(localBluetoothGattDescriptor);
    }

    private Bundle processValuesAsGlucose(byte[] paramArrayOfByte) {
        return processValuesAsGlucoseCustom(paramArrayOfByte);
    }

    private Bundle processValuesAsGlucoseCustom(byte[] paramArrayOfByte) {
        if (paramArrayOfByte.length < 5) {
            return null;
        }
        int i = paramArrayOfByte[0];
        int j = 0;
        double d1 = 0.0d;
        double d2 = 0.0d;
        if (i != 0 && i != 1) {
            return null;
        }
        if (i == 0) {
            j = 1;
            d1 = (double) (((paramArrayOfByte[1] & 255) * 256) + (paramArrayOfByte[2] & 255));
            d2 = (double) (((paramArrayOfByte[3] & 255) * 256) + (paramArrayOfByte[4] & 255));
        } else if (i == 1) {
            d1 = (double) ((paramArrayOfByte[1] & 255) + ((paramArrayOfByte[2] & 255) / 100));
            d2 = (double) ((paramArrayOfByte[3] & 255) + ((paramArrayOfByte[4] & 255) / 100));
            j = 0;
        }
        Bundle localBundle = new Bundle();
        localBundle.putDouble(GLUCOSE, d1);
        localBundle.putDouble(CHOLESTROL, d2);
        localBundle.putInt("unit ch/gl", j);
        localBundle.putInt("type string", 32);

        Log.i("Glucose", String.valueOf(d1));
        Log.i("CHOLESTROL", String.valueOf(d2));
        return localBundle;
    }

    private Bundle processValuesAsGlucoseStandard(byte[] paramArrayOfByte) {
        int i = paramArrayOfByte[0];
        Bundle localBundle = new Bundle();
        int j = 10;
        double d = 0.0d;
        if ((i & 2) == 2) {
            int k = 10 + 3;
            int n = 0;
            if ((i & 1) == 1) {
                n = 1;
                j = 10 + 2;
                k += 2;
            }
            if ((i & 4) != 4) {
                int i3 = 0;
                if ((i & 8) == 8) {
                    i3 = 1;
                    k += 2;
                }
                if ((i & 16) != 16 || (i & 32) != 32 || k != paramArrayOfByte.length) {
                    return null;
                }
                int i2;
                if (n != 0) {
                    int i10 = (paramArrayOfByte[10] << 8) + paramArrayOfByte[10];
                    if (i10 > 32768) {
                        i2 = -(65536 - i10);
                    }
                }
                d = IEEENumber.IEEESFloatToDouble(Arrays.copyOfRange(paramArrayOfByte, j, j + 2));
                int i4 = j + 2;
                int i5 = paramArrayOfByte[i4];
                int i6 = (MotionEventCompat.ACTION_POINTER_INDEX_MASK & i5) >> 2;
                int i7 = i5 & 255;
                int i8 = i4 + 1;
                if (i3 != 0) {
                    i2 = (paramArrayOfByte[i8] & 255) + ((paramArrayOfByte[i8] & 255) << 8);
                }
            }
        }
        localBundle.putDouble(GLUCOSE, d);
        localBundle.putDouble(CHOLESTROL, 0.0d);
        localBundle.putInt("type string", 31);
        return localBundle;
    }

    private void broadcastUpdate(String action, BluetoothGattCharacteristic characteristic) {
        Intent intent = new Intent(action);
        if (eHealthSmartCareGattAttributes.HEART_RATE_MEASUREMENT.equals(characteristic.getUuid())) {
            int format;
            if ((characteristic.getProperties() & 1) != 0) {
                format = 18;
            } else {
                format = 17;
            }
            int heartRate = characteristic.getIntValue(format, 1).intValue();
            intent.putExtra(EXTRA_DATA, String.valueOf(heartRate));
        } else if (eHealthSmartCareGattAttributes.GLUCOSE_CUSTOM_NOTIFICATION_UUID.equals(characteristic.getUuid()) && this.mBluetoothGatt.getDevice().getName().equals("Samico GL")) {
            //intent.putExtra("data", processValuesAsGlucose(characteristic.getValue()));
            //Log.i("GL_DATA", String.valueOf(characteristic.getValue()));
            broadcastGlucoseIntent(processValuesAsGlucose(characteristic.getValue()));
        } else if (eHealthSmartCareGattAttributes.BPM_INDICATION_UUID.equals(characteristic.getUuid()) && this.mBluetoothGatt.getDevice().getName().equals("BPM_01")) {
            byte[] paramArrayOfByte = characteristic.getValue();

            Log.i("BLOOD_PRESSSURE", String.valueOf(characteristic.getUuid()));
            int flag = characteristic.getProperties();
            int format = -1;
            if ((flag & 0x01) != 0) {
                format = BluetoothGattCharacteristic.FORMAT_UINT16;
                Log.d(TAG, "BLOOD_PRESSSURE format UINT16.");
            } else {
                format = BluetoothGattCharacteristic.FORMAT_UINT8;
                Log.d(TAG, "BLOOD_PRESSSURE format UINT8.");
            }

            if(characteristic.getIntValue(format, 1) != null)
            {
                Flag = characteristic.getIntValue(format, 1);
            }

            if(characteristic.getIntValue(format, 2) != null)
            {
                Systolic_High = characteristic.getIntValue(format, 2);
            }

            if(characteristic.getIntValue(format, 3) != null)
            {
                Systolic_Low = characteristic.getIntValue(format, 3);
            }

            if(characteristic.getIntValue(format, 4) != null)
            {
                Diastolic_High = characteristic.getIntValue(format, 4);
            }

            if(characteristic.getIntValue(format, 5) != null)
            {
                Diastolic_Low = characteristic.getIntValue(format, 5);
            }

            if(characteristic.getIntValue(format, 6) != null)
            {
                Map_High = characteristic.getIntValue(format, 6);
            }

            if(characteristic.getIntValue(format, 7) != null)
            {
                Map_Low = characteristic.getIntValue(format, 7);
            }

            if(characteristic.getIntValue(format, 8) != null)
            {
                Pulse_Rate_High = characteristic.getIntValue(format, 8);
            }

            if(characteristic.getIntValue(format, 9) != null)
            {
                Pulse_Rate_Low = characteristic.getIntValue(format, 9);
            }

            if(characteristic.getIntValue(format, 10) != null)
            {
                User_ID = characteristic.getIntValue(format, 10);
            }

            if(characteristic.getIntValue(format, 11) != null)
            {
                Meas_Status_High = characteristic.getIntValue(format, 11);
            }

            if(characteristic.getIntValue(format, 12) != null)
            {
                Meas_Status_Low = characteristic.getIntValue(format, 12);
            }


            Log.d(TAG, String.format("Received Flag: %d", Flag));
            Log.d(TAG, String.format("Received Systolic_High: %d", Systolic_High));
            Log.d(TAG, String.format("Received Systolic_Low: %d", Systolic_Low));
            Log.d(TAG, String.format("Received Diastolic_High: %d", Diastolic_High));
            Log.d(TAG, String.format("Received Diastolic_Low: %d", Diastolic_Low));
            Log.d(TAG, String.format("Received Map_High: %d", Map_High));
            Log.d(TAG, String.format("Received Map_Low: %d", Map_Low));
            Log.d(TAG, String.format("Received Pulse_Rate_High: %d", Pulse_Rate_High));
            Log.d(TAG, String.format("Received Pulse_Rate_Low: %d", Pulse_Rate_Low));
            Log.d(TAG, String.format("Received User_ID: %d", User_ID));
            Log.d(TAG, String.format("Received Meas_Status_High: %d", Meas_Status_High));
            Log.d(TAG, String.format("Received Meas_Status_Low: %d", Meas_Status_Low));
            intent.putExtra(EXTRA_DATA, String.valueOf(Flag));

            broadcastBPMIntent(String.valueOf(Flag),String.valueOf(Systolic_High),String.valueOf(Systolic_Low),String.valueOf(Diastolic_High),String.valueOf(Diastolic_Low),String.valueOf(Map_High),String.valueOf(Map_Low),String.valueOf(Pulse_Rate_High),String.valueOf(Pulse_Rate_Low),String.valueOf(User_ID),String.valueOf(Meas_Status_High),String.valueOf(Meas_Status_Low));

        } else if (eHealthSmartCareGattAttributes.THERMOMETER_INDICATION_UUID.equals(characteristic.getUuid())) {
            double fTemp;
            double cTemp;
            final byte[] paramArrayOfByte = characteristic.getValue();
            int j = paramArrayOfByte[0];
            double d = IEEENumber.IEEEFloatToDouble(Arrays.copyOfRange(paramArrayOfByte, 1, 5));
            if ((j & 1) == 1) {
                fTemp = d;
                cTemp = Utils.fahrenheitToCelsius(fTemp);
            } else {
                cTemp = d;
                fTemp = (double) Utils.celsiusToFarenheit((float) cTemp);
            }

            Log.i("Celcius", String.valueOf(cTemp));
            Log.i("Farenheit", String.valueOf(fTemp));

            broadcastThermometerIntent(String.valueOf(cTemp),String.valueOf(fTemp));

            //intent.putExtra(TEMPERATURE_CELCIUS, cTemp);
            //intent.putExtra(TEMPERATURE_FAHRENHEIT, fTemp);
        } else if (ADGattUUID.BloodPressureMeasurement.equals(characteristic.getUuid())) {
            //intent.putExtra("data", BloodPressureMeasurement.readCharacteristic(characteristic));
        } else if (eHealthSmartCareGattAttributes.WEIGHT_INDICATION_UUID.equals(characteristic.getUuid()) && this.mBluetoothGatt.getDevice().getName().equals("Samico Scales")) {
            //new WeighingScaleReadingParser().parseData(this.device, intent, characteristic.getValue());

            final byte[] data = characteristic.getValue();

            if ((byte) -53 == data[0]) {
                Log.i("DATA", "FINAL VALUE");
            } else {
                Log.i("DATA", new WeighingScaleReadingParser().parseData(this.device, intent, characteristic.getValue()));
                broadcastWeightIntent(new WeighingScaleReadingParser().parseData(this.device, intent, characteristic.getValue()));
            }

        } else if (ADGattUUID.FirmwareRevisionString.equals(characteristic.getUuid())) {
            setDateTimeSetting(this.mBluetoothGatt, Calendar.getInstance());
            return;
        } else {
            byte[] data = characteristic.getValue();
            if (data != null && data.length > 0) {
                StringBuilder stringBuilder = new StringBuilder(data.length);
                int length = data.length;
                for (int i = 0; i < length; i++) {
                    stringBuilder.append(String.format("%02X ", new Object[]{Byte.valueOf(data[i])}));
                }
                intent.putExtra(EXTRA_DATA, new String(data) + "\n" + stringBuilder.toString());
                return;
            }
            return;
        }
        sendBroadcast(intent);
    }

    public IBinder onBind(Intent intent) {
        return this.mBinder;
    }

    public boolean onUnbind(Intent intent) {
        close();
        return super.onUnbind(intent);
    }

    public boolean initialize() {
        if (this.mBluetoothManager == null) {
            this.mBluetoothManager = (BluetoothManager) getSystemService("bluetooth");
            if (this.mBluetoothManager == null) {
                Log.e(TAG, "Unable to initialize BluetoothManager.");
                return false;
            }
        }
        this.mBluetoothAdapter = this.mBluetoothManager.getAdapter();
        if (this.mBluetoothAdapter != null) {
            return true;
        }
        Log.e(TAG, "Unable to obtain a BluetoothAdapter.");
        return false;
    }

    public boolean connect(String address) {
        if (this.mBluetoothAdapter == null || address == null) {
            Log.w(TAG, "BluetoothAdapter not initialized or unspecified address.");
            return false;
        } else if (this.mBluetoothDeviceAddress == null || !address.equals(this.mBluetoothDeviceAddress) || this.mBluetoothGatt == null) {
            this.device = this.mBluetoothAdapter.getRemoteDevice(address);
            if (this.device == null) {
                return false;
            }
            Log.d(TAG, "Trying to create a new connection.");
            this.mBluetoothGatt = this.device.connectGatt(this, false, this.mGattCallback);
            this.mBluetoothDeviceAddress = address;
            this.mConnectionState = 1;
            return true;
        } else if (!this.mBluetoothGatt.connect()) {
            return false;
        } else {
            this.mConnectionState = 1;
            return true;
        }
    }

    public void disconnect() {
        if (this.mBluetoothAdapter == null || this.mBluetoothGatt == null) {
            Log.w(TAG, "BluetoothAdapter not initialized");
        } else {
            this.mBluetoothGatt.disconnect();
        }
    }

    public void close() {
        if (this.mBluetoothGatt != null) {
            this.mBluetoothGatt.close();
            this.mBluetoothGatt = null;
        }
    }

    public void readCharacteristic(BluetoothGattCharacteristic characteristic) {
        if (this.mBluetoothAdapter == null || this.mBluetoothGatt == null) {
            Log.w(TAG, "BluetoothAdapter not initialized");
        } else {
            this.mBluetoothGatt.readCharacteristic(characteristic);
        }
    }

    public List<BluetoothGattService> getSupportedGattServices() {
        if (this.mBluetoothGatt == null) {
            return null;
        }
        return this.mBluetoothGatt.getServices();
    }


    public void broadcastBPMIntent(String Flag,String Systolic_High,String Systolic_Low,String Diastolic_High,String Diastolic_Low,String Map_High,String Map_Low,String Pulse_Rate_High,String Pulse_Rate_Low,String User_ID, String Meas_Status_High , String Meas_Status_Low){
        Intent intent = new Intent();
        intent.setAction("BloodPressure");

        intent.putExtra("Flag", Flag);
        intent.putExtra("Systolic_High", Systolic_High);
        intent.putExtra("Systolic_Low", Systolic_Low);
        intent.putExtra("Diastolic_High", Diastolic_High);
        intent.putExtra("Diastolic_Low", Diastolic_Low);
        intent.putExtra("Map_High", Map_High);
        intent.putExtra("Map_Low", Map_Low);
        intent.putExtra("Pulse_Rate_High", Pulse_Rate_High);
        intent.putExtra("Pulse_Rate_Low", Pulse_Rate_Low);
        intent.putExtra("User_ID", User_ID);
        intent.putExtra("Meas_Status_High", Meas_Status_High);
        intent.putExtra("Meas_Status_Low", Meas_Status_Low);
        sendBroadcast(intent);
    }

    public void broadcastWeightIntent(String Weight){
        Intent intent = new Intent();
        intent.setAction("WeightScale");
        intent.putExtra("Weight", Weight);
        sendBroadcast(intent);
    }

    public void broadcastGlucoseIntent(Bundle glucoseData){
        Intent intent = new Intent();
        intent.setAction("GlucoseScale");
        intent.putExtra("data", glucoseData);
        sendBroadcast(intent);
    }

    public void broadcastThermometerIntent(String Celcius,String Farenheit){
        Intent intent = new Intent();
        intent.setAction("Thermometer");
        intent.putExtra("Celcius", Celcius);
        intent.putExtra("Farenheit", Farenheit);
        sendBroadcast(intent);
    }
}
