package kesari.com.kesarie_healthmonitoring.PulseOximeter.jar.BC401;

import android.support.v4.media.TransportMediator;

public class DevicePackManager {
    public int Percent = 0;
    boolean bGetPackId = false;
    byte[] curPack = new byte[12];
    int f10i;
    int f11k = 0;
    int len = 0;
    public BC401_Data mBc401_Data = new BC401_Data();
    int mCurrent_Pack = 0;
    int mDataLen = 0;
    int mPack_Data_Count = 0;
    int mRec_DataCount = 0;
    int mTotal_Pack = 0;
    byte value;

    public int PackLength(byte pOrder) {
        switch (pOrder) {
            case (byte) -109:
                return 6;
            case (byte) 2:
                return 12;
            case (byte) 5:
                return 9;
            case (byte) 6:
                return 7;
            case (byte) 8:
                return 8;
            default:
                return 0;
        }
    }

    public byte arrangeMessage(byte[] buf, int length) {
        byte _return = (byte) 0;
        this.f10i = 0;
        while (this.f10i < length) {
            this.value = buf[this.f10i];
            byte[] bArr;
            int i;
            if (this.bGetPackId) {
                bArr = this.curPack;
                i = this.f11k;
                this.f11k = i + 1;
                bArr[i] = this.value;
                if (this.f11k >= this.len) {
                    byte[] _data;
                    int j;
                    if (this.len == 6) {
                        this.len = PackLength(this.curPack[5]);
                        _data = new byte[this.curPack.length];
                        for (j = 0; j < this.curPack.length; j++) {
                            _data[j] = this.curPack[j];
                        }
                        this.curPack = new byte[this.len];
                        for (j = 0; j < _data.length; j++) {
                            this.curPack[j] = _data[j];
                        }
                    } else if (this.len == 9) {
                        this.len = ((this.curPack[8] * 14) + 9) + 1;
                        _data = new byte[this.curPack.length];
                        for (j = 0; j < this.curPack.length; j++) {
                            _data[j] = this.curPack[j];
                        }
                        this.curPack = new byte[this.len];
                        for (j = 0; j < _data.length; j++) {
                            this.curPack[j] = _data[j];
                        }
                    } else {
                        this.bGetPackId = false;
                        _return = processData(this.curPack);
                    }
                }
            } else if (PackLength(this.value) > 0) {
                this.bGetPackId = true;
                this.f11k = 0;
                this.len = PackLength(this.value);
                this.curPack = new byte[this.len];
                bArr = this.curPack;
                i = this.f11k;
                this.f11k = i + 1;
                bArr[i] = this.value;
            }
            this.f10i++;
        }
        return _return;
    }

    public byte processData(byte[] pack) {
        switch (pack[5]) {
            case (byte) 2:
                return (byte) 2;
            case (byte) 5:
                this.mBc401_Data.Percent = (((pack[7] & 255) + 1) * 100) / (pack[6] & 255);
                this.Percent = (((pack[7] & 255) + 1) * 100) / (pack[6] & 255);
                dealDPack(pack);
                if (this.Percent == 100) {
                    return (byte) 5;
                }
                return (byte) 0;
            case (byte) 6:
                return (byte) 6;
            case (byte) 8:
                return (byte) 8;
            default:
                return (byte) 0;
        }
    }

    public void dealDPack(byte[] pPack) {
        int _dataCount = pPack[8] & 255;
        for (int i = 0; i < _dataCount; i++) {
            byte[] _data = new byte[14];
            for (int j = 0; j < 14; j++) {
                _data[j] = pPack[(j + 9) + (i * 14)];
            }
            this.mBc401_Data.Structs.add(unPack(_data));
        }
    }

    public BC401_Struct unPack(byte[] pData) {
        byte[] _data = pData;
        BC401_Struct _BC01 = new BC401_Struct();
        _BC01.ID = (_data[0] | ((_data[1] & 255) << 8)) & 1023;
        _BC01.User = ((_data[1] & 255) >> 2) & 31;
        _BC01.Year = _data[2] & TransportMediator.KEYCODE_MEDIA_PAUSE;
        _BC01.Month = ((((_data[2] & 255) >> 7) & 7) | ((_data[3] & 7) << 1)) & 15;
        _BC01.Date = ((_data[3] & 255) >> 3) & 31;
        _BC01.Hour = _data[4] & 31;
        _BC01.Min = (((_data[4] & 255) >> 5) | (_data[5] << 3)) & 63;
        _BC01.Sec = _data[6] & TransportMediator.KEYCODE_MEDIA_PAUSE;
        _BC01.Item = ((_data[8] & 255) | ((_data[9] & 255) << 8)) & 2047;
        if ((_BC01.Item & 1024) > 0) {
            _BC01.URO = (byte) (((_data[9] & 255) >> 3) & 7);
            _BC01.URO_Real = 0;
        } else {
            _BC01.URO = (byte) 9;
            _BC01.URO_Real = 9;
        }
        if ((_BC01.Item & 512) > 0) {
            _BC01.BLD = (byte) (_data[10] & 7);
            _BC01.BLD_Real = 0;
        } else {
            _BC01.BLD = (byte) 9;
            _BC01.BLD_Real = 9;
        }
        if ((_BC01.Item & 256) > 0) {
            _BC01.BIL = (byte) (((_data[10] & 255) >> 3) & 7);
            _BC01.BIL_Real = 0;
        } else {
            _BC01.BIL = (byte) 9;
            _BC01.BIL_Real = 9;
        }
        if ((_BC01.Item & 128) > 0) {
            _BC01.KET = (byte) ((((_data[10] & 255) >> 6) | ((_data[11] & 1) << 2)) & 7);
            _BC01.KET_Real = 0;
        } else {
            _BC01.KET = (byte) 9;
            _BC01.KET_Real = 9;
        }
        if ((_BC01.Item & 64) > 0) {
            _BC01.GLU = (byte) (((_data[11] & 255) >> 1) & 7);
            _BC01.GLU_Real = 0;
        } else {
            _BC01.GLU = (byte) 9;
            _BC01.GLU_Real = 9;
        }
        if ((_BC01.Item & 32) > 0) {
            _BC01.PRO = (byte) (((_data[11] & 255) >> 4) & 7);
            _BC01.PRO_Real = 0;
        } else {
            _BC01.PRO = (byte) 9;
            _BC01.PRO_Real = 9;
        }
        if ((_BC01.Item & 16) > 0) {
            _BC01.PH = (byte) (_data[12] & 7);
            _BC01.PH_Real = 0;
        } else {
            _BC01.PH = (byte) 9;
            _BC01.PH_Real = 9;
        }
        if ((_BC01.Item & 8) > 0) {
            _BC01.NIT = (byte) (((_data[12] & 255) >> 3) & 7);
            _BC01.NIT_Real = 0;
        } else {
            _BC01.NIT = (byte) 9;
            _BC01.NIT_Real = 9;
        }
        if ((_BC01.Item & 4) > 0) {
            _BC01.LEU = (byte) ((((_data[12] & 255) >> 6) | (_data[13] << 2)) & 7);
            _BC01.LEU_Real = 0;
        } else {
            _BC01.LEU = (byte) 9;
            _BC01.LEU_Real = 9;
        }
        if ((_BC01.Item & 2) > 0) {
            _BC01.SG = (byte) (((_data[13] & 255) >> 1) & 7);
            _BC01.SG_Real = 0;
        } else {
            _BC01.SG = (byte) 9;
            _BC01.SG_Real = 9;
        }
        if ((_BC01.Item & 1) > 0) {
            _BC01.VC = (byte) (((_data[13] & 255) >> 4) & 7);
            _BC01.VC_Real = 0;
        } else {
            _BC01.VC = (byte) 9;
            _BC01.VC_Real = 9;
        }
        return _BC01;
    }
}
