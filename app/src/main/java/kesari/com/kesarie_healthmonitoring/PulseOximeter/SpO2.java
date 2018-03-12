package kesari.com.kesarie_healthmonitoring.PulseOximeter;

import java.util.Calendar;
import java.util.Date;

public class SpO2 {
    public String DAY;
    public String HOUR;
    public String MIN;
    public String MONTH;
    public String PR;
    public String SEC;
    public String SpO2;
    public String YEAR;

    public Date getDate() {
        Calendar cal = Calendar.getInstance();
        cal.set(1, Integer.valueOf(this.YEAR).intValue());
        cal.set(2, Integer.valueOf(this.MONTH).intValue() - 1);
        cal.set(5, Integer.valueOf(this.DAY).intValue());
        cal.set(11, Integer.valueOf(this.HOUR).intValue());
        cal.set(12, Integer.valueOf(this.MIN).intValue());
        cal.set(13, Integer.valueOf(this.SEC).intValue());
        cal.set(14, 0);
        return cal.getTime();
    }
}
