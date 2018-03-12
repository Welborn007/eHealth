package kesari.com.kesarie_healthmonitoring.PulseOximeter.cms50dj_jar;

import android.util.Log;

public class DevicePackManager {
    private static final String TAG = "com.contec.cms50dj_jar.DevicePackManager";
    public static int mCount;
    public static byte[] mPack = new byte[1024];
    private byte[] mData50dj = new byte[8];
    private int mDataLength = 0;
    private byte[] mDataPedometerDay = new byte[9];
    private byte[] mDataPedometerMin = new byte[4];
    private byte[] mDataPedometerMinStartDate = new byte[4];
    private int mDaySumNum = 0;
    private int mNowDay = 0;
    private int mPackLen;
    private int mPackageNum = 0;
    private int mSumPackage = 0;
    private byte m_CheckData;
    private DeviceData50DJ_Jar m_DeviceData;
    private DeviceDataPedometerJar m_DeviceDataPedometers;

    public DevicePackManager() {
        initCmdPosition();
    }

    public void initCmdPosition() {
        this.m_DeviceData = new DeviceData50DJ_Jar();
        this.m_DeviceDataPedometers = new DeviceDataPedometerJar();
    }

    public DeviceData50DJ_Jar getDeviceData50dj() {
        return this.m_DeviceData;
    }

    public DeviceDataPedometerJar getM_DeviceDataPedometers() {
        return this.m_DeviceDataPedometers;
    }

    public int arrangeMessage(byte[] buf, int length) {
        for (int i = 0; i < length; i++) {
            printPack(buf[i]);
        }
        switch (DeviceCommand.mWhichCommand) {
            case 1:
                return getDeviceNum(buf, length, 0);
            case 2:
                return deviceTimeCorrect(buf, length, 0);
            case 3:
                return progressSpo2Data(buf, length, 0);
            case 4:
                return processGetNextSp02Data(buf, length, 0);
            case 5:
                return processPedometerDayData(buf, length, 0);
            case 6:
                return processGetNextDayPedometerData(buf, length, 0);
            case 7:
                return processMinPedometerData(buf, length, 0);
            case 8:
                return processGetNextMinPedometerData(buf, length, 0);
            case 9:
                return ProcessPedometerSet(buf, length, 0);
            default:
                return 0;
        }
    }

    private int processGetNextMinPedometerData(byte[] buf, int length, int _isover) {
        for (int i = 0; i < length; i++) {
            mPack[mCount] = buf[i];
            mCount++;
            if (mCount > 2 && mCount < 8) {
                this.m_CheckData = (byte) (this.m_CheckData + buf[i]);
            }
        }
        if (mCount != 8) {
            return _isover;
        }
        if ((mPack[mCount - 1] & 255) == (this.m_CheckData & 255) && mPack[mCount - 2] == (byte) 1) {
            this.m_CheckData = (byte) 0;
            if (this.mSumPackage == this.mPackageNum && this.mDaySumNum == this.mNowDay) {
                Log.d(TAG, " 以分为单位 数据上传完成 ");
                this.mDataLength = 0;
                this.mPackageNum = 0;
                this.mSumPackage = 0;
                this.mDaySumNum = 0;
                this.mNowDay = 0;
                return 16;
            }
            Log.d(TAG, " 以分为单位 请求下一包数据    ");
            this.mDataLength = 0;
            this.mDataPedometerMinStartDate = null;
            this.mDataPedometerMinStartDate = new byte[4];
            return 15;
        }
        this.mDataLength = 0;
        this.m_CheckData = (byte) 0;
        this.mPackageNum = 0;
        return 19;
    }

    private int processMinPedometerData(byte[] buf, int length, int _isover) {
        for (int i = 0; i < length; i++) {
            mCount++;
            if (mCount == 3) {
                this.m_CheckData = (byte) (this.m_CheckData + buf[i]);
                this.mPackLen = buf[i] & 255;
                Log.d(TAG, "分分分   mPackLen：" + this.mPackLen);
            } else if (mCount > 3 && mCount <= 11) {
                this.m_CheckData = (byte) (this.m_CheckData + buf[i]);
                if (mCount == 7) {
                    if (this.mPackLen == 7) {
                        this.m_CheckData = (byte) 0;
                        mCount = 0;
                        return 18;
                    }
                    this.mDaySumNum = buf[i];
                    Log.d(TAG, "分分分   总天数：" + this.mDaySumNum);
                } else if (mCount == 8) {
                    this.mNowDay = buf[i];
                    Log.d(TAG, "分分分 第几天：" + this.mNowDay);
                } else if (mCount == 9) {
                    this.mSumPackage = buf[i];
                    Log.d(TAG, "分分分   当天总包数：" + buf[i]);
                } else if (mCount == 10) {
                    this.mPackageNum = buf[i];
                    Log.d(TAG, "分分分 当天包数的第几包：" + buf[i]);
                } else if (mCount == 11) {
                    Log.d(TAG, "分分分    当前包的组数：" + buf[i]);
                }
            } else if (mCount <= 11) {
                continue;
            } else if (mCount >= 12 && mCount < 16) {
                switch (mCount) {
                    case 12:
                        this.mDataPedometerMinStartDate[0] = buf[i];
                        break;
                    case 13:
                        this.mDataPedometerMinStartDate[1] = buf[i];
                        break;
                    case 14:
                        this.mDataPedometerMinStartDate[2] = buf[i];
                        break;
                    case 15:
                        this.mDataPedometerMinStartDate[3] = buf[i];
                        byte[] startdate = this.m_DeviceDataPedometers.getMinData().mStartDate;
                        if (startdate != null) {
                            if (!(this.mDataPedometerMinStartDate[0] == startdate[0] || this.mDataPedometerMinStartDate[1] == startdate[1] || this.mDataPedometerMinStartDate[2] == startdate[2] || this.mDataPedometerMinStartDate[3] == startdate[3])) {
                                this.m_DeviceDataPedometers.getMinData().mStartDate = this.mDataPedometerMinStartDate;
                                break;
                            }
                        }
                        this.m_DeviceDataPedometers.getMinData().mStartDate = this.mDataPedometerMinStartDate;
                        break;
                }
                this.m_CheckData = (byte) (this.m_CheckData + buf[i]);
                Log.d(TAG, "分分分   开始时间 ：" + buf[i]);
            } else if (mCount == 16) {
                this.m_CheckData = (byte) (this.m_CheckData + buf[i]);
                this.m_DeviceDataPedometers.getMinData().mEndTime = buf[i];
                Log.d(TAG, "分分分   结束时间 ：" + buf[i] + "  dataLength");
                this.mDataLength = 0;
            } else {
                this.mDataPedometerMin[this.mDataLength] = buf[i];
                this.mDataLength++;
                if (this.mDataLength == 4) {
                    this.mDataLength = 0;
                    this.m_DeviceDataPedometers.getMinData().mMinDataList.add(this.mDataPedometerMin);
                    Log.d(TAG, "********************" + this.m_DeviceDataPedometers.getMinString(this.mDataPedometerMin));
                    this.mDataPedometerMin = null;
                    this.mDataPedometerMin = new byte[4];
                }
                if (mCount != this.mPackLen + 3) {
                    this.m_CheckData = (byte) (this.m_CheckData + buf[i]);
                } else if ((this.m_CheckData & 255) == (buf[i] & 255)) {
                    Log.d(TAG, "分分分   校验成功：m_CheckData:" + this.m_CheckData + " buf[i]: " + buf[i]);
                    if (this.mSumPackage == this.mPackageNum && this.mDaySumNum == this.mNowDay) {
                        this.m_DeviceDataPedometers.addMinPedometerData(this.m_DeviceDataPedometers.getMinData());
                        this.mDataLength = 0;
                        _isover = 14;
                    } else {
                        this.mDataLength = 0;
                        _isover = 15;
                    }
                    this.m_CheckData = (byte) 0;
                    return _isover;
                } else {
                    Log.d(TAG, "分分分   校验失败：m_CheckData:" + this.m_CheckData + " buf[i]: " + buf[i]);
                    this.m_CheckData = (byte) 0;
                    this.mDataLength = 0;
                    return 19;
                }
            }
        }
        return _isover;
    }

    private int processGetNextDayPedometerData(byte[] buf, int length, int _isover) {
        for (int i = 0; i < length; i++) {
            mPack[mCount] = buf[i];
            mCount++;
            if (mCount > 2 && mCount < 8) {
                this.m_CheckData = (byte) (this.m_CheckData + buf[i]);
            }
        }
        if (mCount != 8) {
            return _isover;
        }
        if ((mPack[mCount - 1] & 255) == (this.m_CheckData & 255) && mPack[mCount - 2] == (byte) 1) {
            this.m_CheckData = (byte) 0;
            if (this.mSumPackage == this.mPackageNum) {
                this.mDataLength = 0;
                this.mPackageNum = 0;
                this.mSumPackage = 0;
                return 12;
            }
            this.mDataLength = 0;
            return 11;
        }
        this.m_CheckData = (byte) 0;
        this.mPackageNum = 0;
        this.m_CheckData = (byte) 0;
        return 13;
    }

    private int processPedometerDayData(byte[] buf, int length, int _isover) {
        for (int i = 0; i < length; i++) {
            mCount++;
            if (mCount == 3) {
                this.m_CheckData = (byte) (this.m_CheckData + buf[i]);
                this.mPackLen = buf[i] & 255;
            } else if (mCount > 3 && mCount <= 9) {
                this.m_CheckData = (byte) (this.m_CheckData + buf[i]);
                if (mCount == 7) {
                    if (buf[i] == (byte) 0 && this.mPackLen == 7) {
                        mCount = 0;
                        this.m_CheckData = (byte) 0;
                        return 17;
                    }
                    this.mSumPackage = buf[i];
                    Log.d(TAG, "天天天天天 总共的包数：" + this.mSumPackage);
                } else if (mCount == 8) {
                    this.mPackageNum = buf[i];
                    Log.d(TAG, "天天天天天 当前的包数：" + this.mSumPackage);
                } else if (mCount == 9) {
                    Log.d(TAG, "天天天天天  当前的包数中的组数：" + buf[i]);
                }
            } else if (mCount > 9) {
                Log.d(TAG, "天天天天天  数据部分：" + buf[i] + " mDataLength:" + this.mDataLength);
                this.mDataPedometerDay[this.mDataLength] = buf[i];
                this.mDataLength++;
                if (this.mDataLength == 9) {
                    this.m_DeviceDataPedometers.addDayPedometerData(this.mDataPedometerDay);
                    this.mDataLength = 0;
                    this.mDataPedometerDay = null;
                    this.mDataPedometerDay = new byte[9];
                }
                if (mCount != this.mPackLen + 3) {
                    this.m_CheckData = (byte) (this.m_CheckData + buf[i]);
                } else if ((this.m_CheckData & 255) == (buf[i] & 255)) {
                    this.m_CheckData = (byte) 0;
                    if (this.mSumPackage == this.mPackageNum) {
                        this.mDataLength = 0;
                        _isover = 10;
                    } else {
                        this.mDataLength = 0;
                        _isover = 11;
                    }
                    return _isover;
                } else {
                    this.mDataLength = 0;
                    this.m_CheckData = (byte) 0;
                    return 13;
                }
            } else {
                continue;
            }
        }
        return _isover;
    }

    private int ProcessPedometerSet(byte[] buf, int length, int _isover) {
        for (int i = 0; i < length; i++) {
            mPack[mCount] = buf[i];
            mCount++;
            if (mCount > 2 && mCount < 8) {
                this.m_CheckData = (byte) (this.m_CheckData + buf[i]);
            } else if (mCount == 8) {
                if ((this.m_CheckData & 255) != (buf[i] & 255)) {
                    this.m_CheckData = (byte) 0;
                    return 9;
                } else if (mPack[mCount - 2] == (byte) 1) {
                    this.m_CheckData = (byte) 0;
                    return 8;
                } else {
                    this.m_CheckData = (byte) 0;
                    return 9;
                }
            }
        }
        return _isover;
    }

    private int processGetNextSp02Data(byte[] buf, int length, int _isover) {
        for (int i = 0; i < length; i++) {
            mPack[mCount] = buf[i];
            mCount++;
            if (mCount > 2 && mCount < 8) {
                this.m_CheckData = (byte) (this.m_CheckData + buf[i]);
            }
        }
        if (mCount == 8) {
            if ((mPack[mCount - 1] & 255) == (this.m_CheckData & 255) && mPack[mCount - 2] == (byte) 1) {
                this.m_CheckData = (byte) 0;
                Log.e(TAG, "mSumPackage:" + this.mSumPackage + "  mPackageNum:" + this.mPackageNum);
                if (this.mSumPackage == this.mPackageNum) {
                    this.m_CheckData = (byte) 0;
                    this.mPackageNum = 0;
                    this.mSumPackage = 0;
                    this.mDataLength = 0;
                    mCount = 0;
                    return 5;
                }
                this.m_CheckData = (byte) 0;
                this.mDataLength = 0;
                mCount = 0;
                return 20;
            }
            mCount = 0;
            this.mDataLength = 0;
            this.m_CheckData = (byte) 0;
            this.mPackageNum = 0;
            this.mSumPackage = 0;
            _isover = 7;
        }
        return _isover;
    }

    private int getDeviceNum(byte[] buf, int length, int _isover) {
        for (int i = 0; i < length; i++) {
            mPack[mCount] = buf[i];
            mCount++;
            if (mCount == 4) {
                DeviceCommand.mDeviceNum[0] = buf[i];
            } else if (mCount == 5) {
                DeviceCommand.mDeviceNum[1] = buf[i];
            } else if (mCount == 11) {
                _isover = 1;
            }
        }
        return _isover;
    }

    private int deviceTimeCorrect(byte[] buf, int length, int _isover) {
        for (int i = 0; i < length; i++) {
            mPack[mCount] = buf[i];
            mCount++;
            if (mCount > 2 && mCount < 13) {
                this.m_CheckData = (byte) (this.m_CheckData + buf[i]);
            }
        }
        if (mCount != 13) {
            return _isover;
        }
        if (mPack[6] != DeviceCommand.mDateTimeArrays[0] || mPack[7] != DeviceCommand.mDateTimeArrays[1] || mPack[8] != DeviceCommand.mDateTimeArrays[2] || mPack[9] != DeviceCommand.mDateTimeArrays[3] || mPack[10] != DeviceCommand.mDateTimeArrays[4] || mPack[11] != DeviceCommand.mDateTimeArrays[5]) {
            return 3;
        }
        if ((mPack[mCount - 1] & 255) == (this.m_CheckData & 255)) {
            this.m_CheckData = (byte) 0;
            return 2;
        }
        this.m_CheckData = (byte) 0;
        return 3;
    }

    private int progressSpo2Data(byte[] buf, int length, int _isover) {
        for (int i = 0; i < length; i++) {
            mCount++;
            if (mCount == 3) {
                this.m_CheckData = (byte) (this.m_CheckData + buf[i]);
                this.mPackLen = buf[i] & 255;
                Log.d(TAG, "包数据长度：" + this.mPackLen);
            } else if (mCount > 3 && mCount <= 9) {
                this.m_CheckData = (byte) (this.m_CheckData + buf[i]);
                if (mCount == 7) {
                    if (buf[i] == (byte) 0) {
                        this.m_CheckData = (byte) 0;
                        mCount = 0;
                        return 4;
                    }
                    this.mSumPackage = buf[i];
                    Log.d(TAG, "总共的包数：" + this.mSumPackage);
                } else if (mCount == 8) {
                    this.mPackageNum = buf[i];
                    Log.d(TAG, "当前的包数：" + this.mSumPackage);
                } else if (mCount == 9) {
                    Log.d(TAG, "当前的包数中的组数：" + buf[i]);
                }
            } else if (mCount > 9) {
                this.mData50dj[this.mDataLength] = buf[i];
                this.mDataLength++;
                if (this.mDataLength == 8) {
                    this.mDataLength = 0;
                    this.m_DeviceData.addData(this.mData50dj);
                    this.mData50dj = null;
                    this.mData50dj = new byte[8];
                }
                Log.d(TAG, "mCount：" + mCount + "  mPackLen: " + this.mPackLen);
                if (mCount != this.mPackLen + 3) {
                    this.m_CheckData = (byte) (this.m_CheckData + buf[i]);
                } else if ((this.m_CheckData & 255) == (buf[i] & 255)) {
                    this.mDataLength = 0;
                    this.m_CheckData = (byte) 0;
                    return 6;
                } else {
                    this.mDataLength = 0;
                    this.m_CheckData = (byte) 0;
                    return 7;
                }
            } else {
                continue;
            }
        }
        return _isover;
    }

    static void printPack(byte pack) {
        Log.i(TAG, "this is printpack :" + new StringBuilder(String.valueOf("" + Integer.toHexString(pack))).append(" ").toString() + "  mcount:" + mCount);
    }
}
