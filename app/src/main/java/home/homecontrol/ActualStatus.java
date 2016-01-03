package home.homecontrol;

import java.util.ArrayList;
import java.util.HashSet;
import java.util.Set;

/**
 * Created by HP on 2016-01-03.
 */
public class ActualStatus {

    String apiKey;
    boolean isLightOn;
    int brightness;
    float insideTemperature;
    float outsideTemperature;
    MovementSensor movementAlarm;
    MovementSensor autoSwitchOffLight;
    MovementSensor autoSwitchOnLight;
    boolean smokeAlarm;
    boolean monoxideAlarm;

    public String getApiKey() {
        return apiKey;
    }

    public void setApiKey(String apiKey) {
        this.apiKey = apiKey;
    }

    public boolean isLightOn() {
        return isLightOn;
    }

    public void setLightOn(boolean isLightOn) {
        this.isLightOn = isLightOn;
    }

    public int getBrightness() {
        return brightness;
    }

    public void setBrightness(int brightness) {
        this.brightness = brightness;
    }

    public float getInsideTemperature() {
        return insideTemperature;
    }

    public void setInsideTemperature(float insideTemperature) {
        this.insideTemperature = insideTemperature;
    }

    public float getOutsideTemperature() {
        return outsideTemperature;
    }

    public void setOutsideTemperature(float outsideTemperature) {
        this.outsideTemperature = outsideTemperature;
    }

    public MovementSensor getMovementAlarm() {
        return movementAlarm;
    }

    public void setMovementAlarm(MovementSensor movementAlarm) {
        this.movementAlarm = movementAlarm;
    }

    public MovementSensor getAutoSwitchOffLight() {
        return autoSwitchOffLight;
    }

    public void setAutoSwitchOffLight(MovementSensor autoSwitchOffLight) {
        this.autoSwitchOffLight = autoSwitchOffLight;
    }

    public MovementSensor getAutoSwitchOnLight() {
        return autoSwitchOnLight;
    }

    public void setAutoSwitchOnLight(MovementSensor autoSwitchOnLight) {
        this.autoSwitchOnLight = autoSwitchOnLight;
    }

    public boolean isSmokeAlarm() {
        return smokeAlarm;
    }

    public void setSmokeAlarm(boolean smokeAlarm) {
        this.smokeAlarm = smokeAlarm;
    }

    public boolean isMonoxideAlarm() {
        return monoxideAlarm;
    }

    public void setMonoxideAlarm(boolean monoxideAlarm) {
        this.monoxideAlarm = monoxideAlarm;
    }
}


