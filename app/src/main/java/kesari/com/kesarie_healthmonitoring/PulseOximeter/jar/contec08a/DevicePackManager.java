package kesari.com.kesarie_healthmonitoring.PulseOximeter.jar.contec08a;

import android.support.v4.media.TransportMediator;
import android.util.Log;

import java.util.ArrayList;

public class DevicePackManager {
    public static int Flag_User = 0;
    public static final int e_pack_blood = 70;
    public static final int e_pack_hand_back = 72;
    public static final int e_pack_is_new = 65;
    public static final int e_pack_oxyen = 71;
    public static final int e_pack_replay_confirm = 74;
    int Blood_count;
    int Oxyen_count;
    boolean bGetPackId = false;
    byte[] curPack = new byte[9];
    int f12i;
    int f13k = 0;
    int len = 0;
    public DeviceData mDeviceData = new DeviceData();
    public ArrayList<DeviceData> mDeviceDatas = new ArrayList();
    byte value;

    public static int packlength(int pHead) {
        switch (pHead) {
            case 65:
                return 5;
            case 70:
                return -6;
            case 71:
                return -6;
            case 72:
                return 5;
            case 74:
                return 4;
            default:
                return 0;
        }
    }

    public byte arrangeMessage(byte[] buf, int length, int pType) {
        PrintBytes.printData(buf, length);
        byte _return = (byte) 0;
        this.f12i = 0;
        while (this.f12i < length) {
            this.value = buf[this.f12i];
            byte[] bArr;
            int i;
            if (this.bGetPackId) {
                bArr = this.curPack;
                i = this.f13k;
                this.f13k = i + 1;
                bArr[i] = this.value;
                if (this.f13k >= this.len) {
                    if (this.curPack[0] != (byte) 71 && this.curPack[0] != (byte) 70) {
                        this.bGetPackId = false;
                        _return = processData(this.curPack, pType);
                    } else if (this.len < 10) {
                        int j;
                        if (this.curPack[0] == (byte) 71) {
                            this.len = Data_len(new byte[]{this.curPack[2], this.curPack[3], this.curPack[4], this.curPack[5]});
                        } else if (this.curPack[0] == (byte) 70) {
                            this.len = Data_len(new byte[]{this.curPack[2], this.curPack[3], this.curPack[4]});
                        }
                        byte[] _data = new byte[this.curPack.length];
                        for (j = 0; j < this.curPack.length; j++) {
                            _data[j] = this.curPack[j];
                        }
                        this.curPack = new byte[this.len];
                        for (j = 0; j < _data.length; j++) {
                            this.curPack[j] = _data[j];
                        }
                    } else {
                        this.bGetPackId = false;
                        _return = processData(this.curPack, pType);
                    }
                }
            } else if (this.value >= (byte) 0 && packlength(this.value) > 0) {
                this.bGetPackId = true;
                this.f13k = 0;
                this.len = packlength(this.value);
                this.curPack = new byte[this.len];
                bArr = this.curPack;
                i = this.f13k;
                this.f13k = i + 1;
                bArr[i] = this.value;
                if (this.len == 1) {
                    _return = processData(this.curPack, pType);
                    this.bGetPackId = false;
                }
            } else if (this.value >= (byte) 0 && packlength(this.value) < 0) {
                this.bGetPackId = true;
                this.f13k = 0;
                this.len = 6;
                this.curPack = new byte[this.len];
                bArr = this.curPack;
                i = this.f13k;
                this.f13k = i + 1;
                bArr[i] = this.value;
            }
            this.f12i++;
        }
        return _return;
    }

    public byte processData(byte[] pack, int pType) {
        byte _return = (byte) 0;
        int i;
        switch (pack[0]) {
            case (byte) 65:
                return pack[0];
            case (byte) 70:
                this.mDeviceData.mData_blood.clear();
                Log.e("DevicePackManager", ">>>>>>>>>>>>>>>>>>>" + this.Blood_count);
                _return = pack[0];
                if (pType == 7) {
                    if ((pack[13] & TransportMediator.KEYCODE_MEDIA_PAUSE) == 3) {
                        _return = pack[0];
                    } else {
                        _return = (byte) 0;
                    }
                }
                byte[] _data;
                byte[] _blood;
                int y;
                if (pType == 1 || pType == 7) {
                    _data = pack_Blood(pack);
                    for (i = 0; i < this.Blood_count; i++) {
                        _blood = new byte[]{_data[((i * 14) + 14) + 1], _data[((i * 14) + 14) + 2], _data[((i * 14) + 14) + 3], _data[((i * 14) + 14) + 4], _data[((i * 14) + 14) + 5], _data[((i * 14) + 14) + 6], _data[((i * 14) + 14) + 7], _data[((i * 14) + 14) + 9], _data[((i * 14) + 14) + 10], _data[((i * 14) + 14) + 11], _data[((i * 14) + 14) + 12], _data[((i * 14) + 14) + 13]};
                        this.mDeviceData.mData_blood.add(_blood);
                        y = ((_blood[0] << 8) | _blood[1]) & 255;
                        Log.e("JAR", "blooddi:" + (_blood[2] & 255) + "  bloodgao:" + y);
                    }
                } else if (pType == 6) {
                    _data = pack_Blood(pack);
                    for (i = 0; i < this.Blood_count; i++) {
                        _blood = new byte[]{_data[((i * 14) + 14) + 1], _data[((i * 14) + 14) + 2], _data[((i * 14) + 14) + 3], _data[((i * 14) + 14) + 4], _data[((i * 14) + 14) + 5], _data[((i * 14) + 14) + 6], _data[((i * 14) + 14) + 7], _data[((i * 14) + 14) + 9], _data[((i * 14) + 14) + 10], _data[((i * 14) + 14) + 11], _data[((i * 14) + 14) + 12], _data[((i * 14) + 14) + 13]};
                        this.mDeviceData.mData_normal_blood.add(_blood);
                        y = (_blood[0] << 8) | _blood[1];
                        Log.e("JAR", "blooddi:" + _blood[2] + "  bloodgao:" + y);
                    }
                }
                Log.e("JAR", "e_pack_blood");
                return _return;
            case (byte) 71:
                _return = pack[0];
                if (pType == 8) {
                    if ((pack[15] & TransportMediator.KEYCODE_MEDIA_PAUSE) == 3) {
                        _return = pack[0];
                    } else {
                        _return = (byte) 0;
                    }
                }
                Log.e("JAR", "e_pack_oxyen");
                byte[] _oxygen_pack = pack_Oxygen(pack);
                for (i = 0; i < this.Oxyen_count; i++) {
                    byte[] _oxygen = new byte[]{_oxygen_pack[((i * 11) + 16) + 1], _oxygen_pack[((i * 11) + 16) + 2], _oxygen_pack[((i * 11) + 16) + 3], _oxygen_pack[((i * 11) + 16) + 4], _oxygen_pack[((i * 11) + 16) + 5], _oxygen_pack[((i * 11) + 16) + 6], _oxygen_pack[((i * 11) + 16) + 7], _oxygen_pack[((i * 11) + 16) + 9], _oxygen_pack[((i * 11) + 16) + 10]};
                    this.mDeviceData.mData_oxygen.add(_oxygen);
                    Log.e("JAR", "Oxygen:" + (_oxygen[0] & 255) + "  Plus:" + (_oxygen[1] & 255));
                }
                return _return;
            case (byte) 72:
                _return = pack[0];
                PrintBytes.printData(pack);
                Flag_User = pack[3];
                Log.e("JAR", "e_pack_hand_back");
                return _return;
            case (byte) 74:
                switch (pType) {
                    case 1:
                        if (pack[2] != (byte) 1) {
                            _return = (byte) 17;
                            break;
                        }
                        _return = (byte) 16;
                        break;
                    case 2:
                        if (pack[2] != (byte) 1) {
                            _return = (byte) 33;
                            break;
                        }
                        _return = (byte) 32;
                        break;
                    case 3:
                        if (pack[1] == (byte) 67) {
                            if (pack[2] != (byte) 1) {
                                _return = (byte) 49;
                                break;
                            }
                            _return = (byte) 48;
                            break;
                        }
                        Log.e("DevicePackManager", "校正时间返回");
                        if (pack[3] != (byte) 0) {
                            Log.e("DevicePackManager", "校正时间返回pack[3]=1");
                            _return = (byte) 66;
                            break;
                        }
                        Log.e("DevicePackManager", "校正时间返回pack[3]=0");
                        if (pack[2] != (byte) 1) {
                            _return = (byte) 65;
                            break;
                        }
                        _return = (byte) 64;
                        break;
                    case 5:
                        if (pack[2] != (byte) 1) {
                            _return = (byte) 81;
                            break;
                        }
                        _return = (byte) 80;
                        break;
                }
                Log.e("JAR", "e_pack_replay_confirm");
                return _return;
            default:
                return (byte) 0;
        }
    }

    public static byte[] unPack(byte[] pack) {
        int i;
        int len = pack.length;
        for (i = 1; i < 8; i++) {
            pack[i] = (byte) (pack[i] & ((pack[0] << (8 - i)) | TransportMediator.KEYCODE_MEDIA_PAUSE));
        }
        for (i = 9; i < len; i++) {
            pack[i] = (byte) (pack[i] & ((pack[8] << ((len - 8) - (i - 8))) | TransportMediator.KEYCODE_MEDIA_PAUSE));
        }
        return pack;
    }

    public static byte[] doPack(byte[] pack) {
        if (pack == null) {
            return null;
        }
        if (10 <= 0) {
            return pack;
        }
        pack[2] = Byte.MIN_VALUE;
        for (int i = 3; i < 9; i++) {
            pack[1] = (byte) (pack[1] | ((pack[i] & 128) >> (9 - i)));
            pack[i] = (byte) (pack[i] | 128);
        }
        return pack;
    }

    public int Data_len(byte[] pack) {
        int len = pack.length;
        for (int i = 1; i < len; i++) {
            pack[i] = (byte) (pack[i] & ((pack[0] << (8 - i)) | TransportMediator.KEYCODE_MEDIA_PAUSE));
        }
        if (len == 4) {
            int _oxyen_count = (((pack[1] & 255) << 16) | ((pack[2] & 255) << 8)) | (pack[3] & 255);
            this.Oxyen_count = _oxyen_count;
            return (_oxyen_count * 11) + 16;
        } else if (len != 3) {
            return 0;
        } else {
            int _blood_count = ((pack[1] & 255) << 8) | (pack[2] & 255);
            this.Blood_count = _blood_count;
            return (_blood_count * 14) + 14;
        }
    }

    public byte[] pack_Blood(byte[] pBlood) {
        int i;
        for (i = 3; i < 10; i++) {
            pBlood[i] = (byte) (pBlood[i] & ((pBlood[2] << (10 - i)) | TransportMediator.KEYCODE_MEDIA_PAUSE));
        }
        for (i = 10; i < 14; i++) {
            pBlood[i] = (byte) (pBlood[i] & ((pBlood[10] << (14 - i)) | TransportMediator.KEYCODE_MEDIA_PAUSE));
        }
        for (int j = 0; j < this.Blood_count; j++) {
            for (i = (j * 14) + 14; i < ((j * 14) + 14) + 8; i++) {
                pBlood[i] = (byte) (pBlood[i] & ((pBlood[(j * 14) + 14] << ((((j * 14) + 14) + 8) - i)) | TransportMediator.KEYCODE_MEDIA_PAUSE));
            }
            for (i = ((j * 14) + 14) + 8; i < (((j * 14) + 14) + 8) + 6; i++) {
                pBlood[i] = (byte) (pBlood[i] & ((pBlood[((j * 14) + 14) + 8] << (((((j * 14) + 14) + 8) + 6) - i)) | TransportMediator.KEYCODE_MEDIA_PAUSE));
            }
        }
        return pBlood;
    }

    public byte[] pack_Oxygen(byte[] pOxygen) {
        int i;
        for (i = 3; i < 10; i++) {
            pOxygen[i] = (byte) (pOxygen[i] & ((pOxygen[2] << (10 - i)) | TransportMediator.KEYCODE_MEDIA_PAUSE));
        }
        for (i = 10; i < 16; i++) {
            pOxygen[i] = (byte) (pOxygen[i] & ((pOxygen[10] << (16 - i)) | TransportMediator.KEYCODE_MEDIA_PAUSE));
        }
        for (int j = 0; j < this.Oxyen_count; j++) {
            for (i = (j * 11) + 16; i < ((j * 11) + 16) + 8; i++) {
                pOxygen[i] = (byte) (pOxygen[i] & ((pOxygen[(j * 11) + 16] << (((j * 11) + 24) - i)) | TransportMediator.KEYCODE_MEDIA_PAUSE));
            }
            for (i = ((j * 11) + 16) + 8; i < (((j * 11) + 16) + 8) + 3; i++) {
                pOxygen[i] = (byte) (pOxygen[i] & ((pOxygen[((j * 11) + 16) + 8] << (((((j * 11) + 16) + 8) + 3) - i)) | TransportMediator.KEYCODE_MEDIA_PAUSE));
            }
        }
        return pOxygen;
    }
}
