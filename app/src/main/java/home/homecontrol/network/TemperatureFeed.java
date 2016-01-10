package home.homecontrol.network;

import android.util.Log;

import com.jjoe64.graphview.series.DataPointInterface;

import org.joda.time.DateTime;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;

/**
 * Created by HP on 2016-01-10.
 */
public class TemperatureFeed  {

    private static String LOG_TAG = TemperatureFeed.class.getName();


    private static String THINGSPEAK_DATE_FORMAT = "yyyy-MM-dd'T'HH:mm:ss'Z'";
    Date createdDate;
    int entry;
    double value;


    public TemperatureFeed(String createdDate, int entry, double value){
        try {
            SimpleDateFormat sdf = new SimpleDateFormat(THINGSPEAK_DATE_FORMAT);
            Date date =  sdf.parse( createdDate );
            this.createdDate = date;
        } catch (ParseException e) {
            e.printStackTrace();
        }

        this.entry = entry;
        this.value = value;
    }

    public Date getCreatedDate() {
        return createdDate;
    }

    public void setCreatedDate(Date createdDate) {
        this.createdDate = createdDate;
    }

    public int getEntry() {
        return entry;
    }

    public void setEntry(int entry) {
        this.entry = entry;
    }

    public double getValue() {
        return value;
    }

    public void setValue(double value) {
        this.value = value;
    }
}
