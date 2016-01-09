package home.homecontrol.network;

import android.widget.Toast;

import org.json.JSONException;
import org.json.JSONObject;

import home.homecontrol.MainActivity;
import home.homecontrol.MovementSensor;

/**
 * Created by HP on 2016-01-08.
 */
public class JSONParser {

    public static void setActualStatus(JSONObject jsonObject) throws JSONException {
        MainActivity.actualStatus.setLightOn(jsonObject.getInt("LIGHT") == 1 ? true : false);
        MainActivity.actualStatus.setBrightness(jsonObject.getInt("BRIGHTNESS"));
        MainActivity.actualStatus.setInsideTemperature(jsonObject.getDouble("TEMP_IN"));
        MainActivity.actualStatus.setOutsideTemperature(jsonObject.getDouble("TEMP_OUT"));
        MovementSensor movementSensorAlarm = new MovementSensor(jsonObject.getInt("ALARM") == 1 ? true : false);
        MovementSensor movementSensorLight = new MovementSensor(jsonObject.getInt("AUTO_ON") == 1 ? true : false);
        MainActivity.actualStatus.setMovementAlarm(movementSensorAlarm);
        MainActivity.actualStatus.setAutoSwitchOnLight(movementSensorLight);
        MainActivity.actualStatus.setSmokeAlarm(jsonObject.getInt("SMOKE_ALARM") == 1 ? true : false);
        MainActivity.actualStatus.setMonoxideAlarm(jsonObject.getInt("MONOXIDE_ALARM") == 1 ? true : false);
    }
}
