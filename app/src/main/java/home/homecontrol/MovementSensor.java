package home.homecontrol;

import java.util.ArrayList;
import java.util.HashSet;
import java.util.Set;

public class MovementSensor{
        boolean isOn;
        boolean isCustomSettOn;
        /*
        * 1 - Monday, 2 - Tuesday...
        */
        ArrayList<Integer> weekDays;

        public MovementSensor(){

        }

        public MovementSensor(boolean isOn) {
            this.isOn = isOn;
            this.isCustomSettOn = false;
        }

        public MovementSensor(boolean isOn, boolean isCustomSettOn, ArrayList<Integer> weekDays){
            this.isOn = isOn;
            this.isCustomSettOn = isCustomSettOn;
            Set<Integer> hs = new HashSet<>();
            hs.addAll(weekDays);
            this.weekDays.addAll(hs);
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

        public ArrayList<Integer> getWeekDays() {
            return weekDays;
        }

        public void setWeekDays(ArrayList<Integer> weekDays) {
            this.weekDays = weekDays;
        }
    }