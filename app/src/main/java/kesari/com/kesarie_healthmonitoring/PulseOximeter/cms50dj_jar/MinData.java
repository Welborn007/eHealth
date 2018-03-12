package kesari.com.kesarie_healthmonitoring.PulseOximeter.cms50dj_jar;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.List;

public class MinData implements Serializable {
    private static final long serialVersionUID = 1;
    public byte mEndTime;
    public List<byte[]> mMinDataList = new ArrayList();
    public byte[] mStartDate;
}
