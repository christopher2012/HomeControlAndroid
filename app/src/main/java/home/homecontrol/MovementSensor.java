package home.homecontrol;

import org.joda.time.DateTime;

import java.util.ArrayList;
import java.util.HashSet;
import java.util.Set;

public class MovementSensor {
    boolean isOn;
    boolean isCustomSettOn;
    DateTime sinceTime;
    DateTime toTime;
    int weekDays;

    public MovementSensor() {

    }



    public MovementSensor(boolean isOn) {
        this.isOn = isOn;
        this.isCustomSettOn = false;
        setSinceTime(new DateTime().withTime(0,0,0,0));
        setToTime(new DateTime().withTime(23,59,0,0));
    }

    public MovementSensor(boolean isOn, boolean isCustomSettOn, int weekDays) {
        this.isOn = isOn;
        this.isCustomSettOn = isCustomSettOn;
        this.weekDays = weekDays;
    }

    public DateTime getSinceTime() {
        return sinceTime;
    }

    public void setSinceTime(DateTime sinceTime) {
        this.sinceTime = sinceTime;
    }

    public DateTime getToTime() {
        return toTime;
    }

    public void setToTime(DateTime toTime) {
        this.toTime = toTime;
    }

    public boolean isOn() {
        return isOn;
    }

    public void setIsOn(boolean isOn) {
        this.isOn = isOn;
    }

    public boolean isCustomSettOn() {
        return isCustomSettOn;
    }

    public void setIsCustomSettOn(boolean isCustomSettOn) {
        this.isCustomSettOn = isCustomSettOn;
    }

    public int getWeekDays() {
        return weekDays;
    }

    public void setWeekDays(int weekDays) {
        this.weekDays = weekDays;
    }
}