package home.homecontrol.network;

import android.app.Fragment;
import android.util.Log;

import com.loopj.android.http.AsyncHttpClient;
import com.loopj.android.http.AsyncHttpResponseHandler;

import org.json.JSONException;
import org.json.JSONObject;

import java.io.IOException;

import cz.msebera.android.httpclient.Header;
import home.homecontrol.MainActivity;

/**
 * Created by HP on 2015-12-05.
 */
public class NetworkData {

    public static final String LOG_TAG = NetworkData.class.getName();

    public static final String ANDROID_COMMAND = "/android?command=";
    public static final String IP_SERVER = "http://192.168.0.";
    public static final String LIGHT = "/light?state=";
    public static final String MOVEMENT = "/pir?state=";
    public static final String SETTINGS = "/settings";
    public static final String OK_MSG = "OK";
    public static final String TEMPERATURE_FEEDS = "http://api.thingspeak.com/channels/72588/feed.json?start=2011-11-11%2010:10:10";

    public static final String CMD_SWITCH_LIGHT = "A";
    public static final String CMD_CHANGE_BRIGHTNESS = "B";
    public static final String CMD_GET_DATA = "D";
    public static final String CMD_AUTO_LIGHT_ON = "C";
    public static final String CMD_SMOKE_ALARM = "E";
    public static final String CMD_MONOXIDE_ALARM = "F";
    public static final String CMD_ALARM = "G";

    static String IP_SET = "";

    public static String getIpSet() {
        return IP_SET;
    }

    AsyncHttpClient client;
    OnServerResponse callback;

    public NetworkData(Fragment fragment){
        client = new AsyncHttpClient();
        client.setTimeout(5000);
        callback = (OnServerResponse) fragment;
    }

    public enum NetworkStatus{
        CONNECTION_ERROR,
        CONNECTION_OK,
        RESPONSE_ERROR
    }

    public enum ResponseType{
        DATA,
        CONFIRMATION
    }

    public static void setIpSet(String ipSet) {
        IP_SET = ipSet;
    }

    public static String getIpServer() {
        return IP_SERVER + IP_SET;
    }

    public void updateData(){
        if(!IP_SET.equals("")){
            String request = NetworkData.getIpServer() + NetworkData.SETTINGS;
            client.get(request, new AsyncHttpResponseHandler() {
                @Override
                public void onSuccess(int statusCode, Header[] headers, byte[] responseBody) {
                    String str = null;
                    try {
                        str = new String(responseBody, "UTF-8");
                        Log.d(LOG_TAG, "response: " + str);
                        JSONObject object = new JSONObject(str);
                        if (object.getString("STATUS").equals("OK")) {
                            JSONParser.setActualStatus(object);
                            callback.serverResponse(NetworkStatus.CONNECTION_OK, ResponseType.DATA);
                        } else {
                            callback.serverResponse(NetworkStatus.CONNECTION_ERROR, ResponseType.DATA);
                        }
                    } catch (IOException e) {
                        e.printStackTrace();
                    } catch (JSONException e) {
                        e.printStackTrace();
                        callback.serverResponse(NetworkStatus.RESPONSE_ERROR, ResponseType.DATA);
                    }
                }

                @Override
                public void onFailure(int statusCode, Header[] headers, byte[] responseBody, Throwable error) {
                    callback.serverResponse(NetworkStatus.CONNECTION_ERROR, ResponseType.DATA);
                }
            });
        }
    }



    public void sendCommand(final String command, final String state){
        if(!IP_SET.equals("")){
            String request = NetworkData.getIpServer() + NetworkData.ANDROID_COMMAND + command + state;
            Log.d(LOG_TAG, "sending data: " + request);
            client.get(request, new AsyncHttpResponseHandler() {
                @Override
                public void onSuccess(int statusCode, Header[] headers, byte[] responseBody) {
                    switch(command){
                        case CMD_SWITCH_LIGHT:
                            if(state.equals("0")) {
                                MainActivity.actualStatus.setLightOn(false);
                            }else{
                                MainActivity.actualStatus.setLightOn(true);
                            }
                            break;
                    }
                    Log.d(LOG_TAG, "data sent");
                }

                @Override
                public void onFailure(int statusCode, Header[] headers, byte[] responseBody, Throwable error) {
                }
            });

        }
    }

    public interface OnServerResponse{
        void serverResponse(NetworkStatus networkStatus, ResponseType responseType);
    }

}
