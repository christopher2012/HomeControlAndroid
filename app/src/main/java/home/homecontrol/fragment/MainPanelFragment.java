package home.homecontrol.fragment;

import android.app.DialogFragment;
import android.app.Fragment;
import android.content.Context;
import android.graphics.Color;
import android.os.Bundle;
import android.support.v4.widget.SwipeRefreshLayout;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.ViewTreeObserver;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.ScrollView;
import android.widget.SeekBar;
import android.widget.TextView;
import android.widget.Toast;

import com.loopj.android.http.AsyncHttpClient;
import com.loopj.android.http.AsyncHttpResponseHandler;

import org.joda.time.DateTime;

import java.io.IOException;
import java.sql.Connection;

import butterknife.Bind;
import butterknife.ButterKnife;
import cz.msebera.android.httpclient.Header;
import home.homecontrol.MainActivity;
import home.homecontrol.MovementSensor;
import home.homecontrol.R;
import home.homecontrol.network.NetworkData;

/**
 * A simple {@link Fragment} subclass.
 */
public class MainPanelFragment extends Fragment
        implements MovementSensorSettingsFragment.OnPIRSetup,
        NetworkData.OnServerResponse,
        ConnectionFragment.OnConnectDevice{

    public static final String FRAGMENT_TAG = "MainPanelFragment";
    private static final String LOG_TAG = MainPanelFragment.class.getName();
    private static final int ON_START_REQUEST_CODE = 0;
    private static final int REDO_CONNECTION_REQUEST_CODE = 1;

    @Bind(R.id.seekBarBrightness)
    SeekBar seekBarBrightness;
    @Bind(R.id.bulbImage)
    ImageView bulbImage;
    @Bind(R.id.switchLightOffButton)
    Button switchLightOffButton;
    @Bind(R.id.switchLightOnButton)
    Button getSwitchLightOnButton;
    @Bind(R.id.tempInsideButton)
    Button tempInsideButton;
    @Bind(R.id.tempOutsideButton)
    Button tempOutsideButton;
    @Bind(R.id.moveAlarmMainPanel)
    Button moveAlarm;
    @Bind(R.id.autoOffLightMainPanel)
    Button autoOffLight;
    @Bind(R.id.autoOnLightMainPanel)
    Button autoOnLight;
    @Bind(R.id.sensor1_main_panel_button)
    Button smokeSensorButton;
    @Bind(R.id.sensor1_main_panel_text)
    TextView smokeSensorText;
    @Bind(R.id.sensor2_main_panel_button)
    Button monoxideSensorButton;
    @Bind(R.id.sensor2_main_panel_text)
    TextView monoxideSensorText;
    @Bind(R.id.swipeRefresh)
    SwipeRefreshLayout swipeRefreshLayout;
    @Bind(R.id.scrollView)
    ScrollView scrollView;

    AsyncHttpClient client;
    Context context;
    NetworkData networkData;


    public MainPanelFragment() {

    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
    }

    @Override
    public void onActivityCreated(Bundle savedInstanceState) {
        super.onActivityCreated(savedInstanceState);
        context = getActivity();
        client = new AsyncHttpClient();
        client.setTimeout(5000);
        networkData = new NetworkData(this);
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_main_panel, container, false);
        ButterKnife.bind(this, view);

        return view;
    }


    void showConnectionDialog() {
        DialogFragment newConnectionFragment = new ConnectionFragment();
        newConnectionFragment.setTargetFragment(this, ON_START_REQUEST_CODE);
        newConnectionFragment.show(getFragmentManager(), ConnectionFragment.FRAGMENT_TAG);
    }

    @Override
    public void onStart() {
        super.onStart();
        updateStatus();

        swipeRefreshLayout.setOnRefreshListener(
                new SwipeRefreshLayout.OnRefreshListener() {
                    @Override
                    public void onRefresh() {
                        networkData.updateData();
                    }
                }
        );

        scrollView.getViewTreeObserver().addOnScrollChangedListener(
                new ViewTreeObserver.OnScrollChangedListener() {

                    @Override
                    public void onScrollChanged() {
                        int scrollY = scrollView.getScrollY();
                        if (scrollY == 0)
                            swipeRefreshLayout.setEnabled(true);
                        else
                            swipeRefreshLayout.setEnabled(false);
                    }
                }
        );

        seekBarBrightness.setMax(100);
        seekBarBrightness.setOnSeekBarChangeListener(
                new SeekBar.OnSeekBarChangeListener() {
                    @Override
                    public void onProgressChanged(SeekBar seekBar, int progress, boolean fromUser) {
                        bulbImage.setBackgroundColor(Color.rgb(255, 255, 255 - (int) (progress * 2.5)));
                        Log.d(LOG_TAG, "progress: " + progress);
                    }

                    @Override
                    public void onStartTrackingTouch(SeekBar seekBar) {

                    }

                    @Override
                    public void onStopTrackingTouch(SeekBar seekBar) {
                        changeBrightness(seekBar.getProgress());
                        Log.d(LOG_TAG, "progressStop: " + seekBar.getProgress());
                    }
                }
        );

        switchLightOffButton.setOnClickListener(
                new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        if (MainActivity.actualStatus.getBrightness() !=0) {
                            seekBarBrightness.setProgress(0);
                            networkData.sendCommand(NetworkData.CMD_CHANGE_BRIGHTNESS, "0");
                            MainActivity.actualStatus.setBrightness(0);
                        }
                    }
                }
        );

        getSwitchLightOnButton.setOnClickListener(
                new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        if (MainActivity.actualStatus.getBrightness()!=100) {
                            seekBarBrightness.setProgress(100);
                            networkData.sendCommand(NetworkData.CMD_CHANGE_BRIGHTNESS, "100");
                            MainActivity.actualStatus.setBrightness(100);
                        }
                    }
                }
        );

        tempInsideButton.setOnClickListener(
                new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        getFragmentManager()
                                .beginTransaction()
                                .replace(R.id.mainFragmentContainer, new TemperatureFragment(), TemperatureFragment.FRAGMENT_TAG)
                                .addToBackStack(TemperatureFragment.FRAGMENT_TAG)
                                .commit();
                    }
                }
        );

        moveAlarm.setOnClickListener(
                new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        DialogFragment mSensorSett = MovementSensorSettingsFragment
                                .newInstance(getString(R.string.alarm_title_settings_fragment),
                                        MainPanelFragment.this, MovementSensorSettingsFragment.ALARM_REQUEST_CODE);
                        mSensorSett.show(getFragmentManager(), MovementSensorSettingsFragment.FRAGMENT_TAG);
                    }
                }
        );

        autoOnLight.setOnClickListener(
                new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        DialogFragment mSensorSett = MovementSensorSettingsFragment
                                .newInstance(getString(R.string.auto_on_settings_fragment),
                                        MainPanelFragment.this, MovementSensorSettingsFragment.AUTO_LIHGT_REQUEST_CODE);
                        mSensorSett.show(getFragmentManager(), MovementSensorSettingsFragment.FRAGMENT_TAG);
                    }
                }
        );

        smokeSensorButton.setOnClickListener(
                new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        if (MainActivity.actualStatus.isSmokeAlarm()) {
                            MainActivity.actualStatus.setSmokeAlarm(false);
                            networkData.sendCommand(NetworkData.CMD_SMOKE_ALARM, "0");
                            smokeSensorButton.setText(getString(R.string.alarm_off_sensor));
                        }else{
                            MainActivity.actualStatus.setSmokeAlarm(true);
                            networkData.sendCommand(NetworkData.CMD_SMOKE_ALARM, "1");
                            smokeSensorButton.setText(getString(R.string.alarm_on_sensor));
                        }
                    }
                }
        );

        monoxideSensorButton.setOnClickListener(
                new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        if (MainActivity.actualStatus.isMonoxideAlarm()) {
                            MainActivity.actualStatus.setMonoxideAlarm(false);
                            networkData.sendCommand(NetworkData.CMD_MONOXIDE_ALARM, "0");
                            monoxideSensorButton.setText(getString(R.string.alarm_off_sensor));
                        } else {
                            MainActivity.actualStatus.setMonoxideAlarm(true);
                            networkData.sendCommand(NetworkData.CMD_MONOXIDE_ALARM, "1");
                            monoxideSensorButton.setText(getString(R.string.alarm_on_sensor));
                        }

                    }
                }
        );
    }

    public void changeBrightness(final int progress) {
        seekBarBrightness.setProgress(progress);
        client.get(NetworkData.getIpServer() + NetworkData.ANDROID_COMMAND + NetworkData.CMD_CHANGE_BRIGHTNESS + validateProgress(progress),
                new AsyncHttpResponseHandler() {
                    @Override
                    public void onSuccess(int statusCode, Header[] headers, byte[] responseBody) {
                        MainActivity.actualStatus.setBrightness(progress);
                    }

                    @Override
                    public void onFailure(int statusCode, Header[] headers, byte[] responseBody, Throwable error) {
                        Toast.makeText(context, "Brak połączenie z urządzeniem!!", Toast.LENGTH_LONG).show();
                        bulbImage.setBackgroundColor(Color.rgb(255, 255, 255));
                    }
                });
    }

    public String validateProgress(int progress) {
        if (progress < 10)
            return "00" + progress;
        else if (progress < 100)
            return "0" + progress;
        else
            return progress + "";
    }

    public String validateTime(int time){
        if (time < 10)
            return "0" + time;
        else
            return time + "";
    }

    void updateStatus() {
        seekBarBrightness.setProgress(MainActivity.actualStatus.getBrightness());
        bulbImage.setBackgroundColor(Color.rgb(255, 255, 255 - (int) (MainActivity.actualStatus.getBrightness() * 2.5)));

        tempInsideButton.setText("" + MainActivity.actualStatus.getInsideTemperature());
        tempOutsideButton.setText("" + MainActivity.actualStatus.getOutsideTemperature());
        moveAlarm.setText(MainActivity.actualStatus.getMovementAlarm().isOn() ?
                getString(R.string.alarm_on_sensor) : getString(R.string.alarm_off_sensor));
        autoOnLight.setText(MainActivity.actualStatus.getAutoSwitchOnLight().isOn() ?
                getString(R.string.auto_light_on) : getString(R.string.auto_light_off));
        smokeSensorButton.setText(MainActivity.actualStatus.isSmokeAlarm() ?
                getString(R.string.alarm_on_sensor) : getString(R.string.alarm_off_sensor));
        monoxideSensorButton.setText(MainActivity.actualStatus.isMonoxideAlarm() ?
                getString(R.string.alarm_on_sensor) : getString(R.string.alarm_off_sensor));
    }

    @Override
    public void pitSetup(int requestCode, MovementSensor movementSensor) {
        Log.d(LOG_TAG, "reqCode: " + requestCode);
        Log.d(LOG_TAG, "status: " + movementSensor.isOn());
        if (requestCode == MovementSensorSettingsFragment.ALARM_REQUEST_CODE) {
            if (!((movementSensor.isOn() && (!MainActivity.actualStatus.getMovementAlarm().isOn()))
                    ^ (movementSensor.isOn() && (!MainActivity.actualStatus.getMovementAlarm().isOn())))) {

                 if (movementSensor.isOn())
                    moveAlarm.setText(getString(R.string.alarm_on_sensor));
                else
                    moveAlarm.setText(getString(R.string.alarm_off_sensor));

                String command="";
                command += movementSensor.isOn() ? "1" : "0";
                command += movementSensor.isCustomSettOn() ?  "1" : "0";
                command += validateProgress(movementSensor.getWeekDays());
                command += validateTime(movementSensor.getSinceTime().getHourOfDay());
                command += validateTime(movementSensor.getSinceTime().getMinuteOfHour());
                command += validateTime(movementSensor.getToTime().getHourOfDay());
                command += validateTime(movementSensor.getToTime().getMinuteOfHour());
                Log.d(LOG_TAG, "command: " + command);
                MainActivity.actualStatus.setMovementAlarm(movementSensor);
                networkData.sendCommand(NetworkData.CMD_ALARM, command);
            }

        } else {
            if (!((movementSensor.isOn() && (!MainActivity.actualStatus.getAutoSwitchOnLight().isOn()))
                    ^ (movementSensor.isOn() && (!MainActivity.actualStatus.getAutoSwitchOnLight().isOn())))) {

                Log.d(LOG_TAG, "sending request: " + NetworkData.getIpServer()
                        + NetworkData.ANDROID_COMMAND + (movementSensor.isOn() ? NetworkData.CMD_AUTO_LIGHT_ON + "1" : NetworkData.CMD_AUTO_LIGHT_ON + "0"));
                if (movementSensor.isOn())
                    autoOnLight.setText(getString(R.string.auto_light_on));
                else
                    autoOnLight.setText(getString(R.string.auto_light_off));

                String command="";
                command += movementSensor.isOn() ? "1" : "0";
                command += movementSensor.isCustomSettOn() ?  "1" : "0";
                command += validateProgress(movementSensor.getWeekDays());
                command += validateTime(movementSensor.getSinceTime().getHourOfDay());
                command += validateTime(movementSensor.getSinceTime().getMinuteOfHour());
                command += validateTime(movementSensor.getToTime().getHourOfDay());
                command += validateTime(movementSensor.getToTime().getMinuteOfHour());
                Log.d(LOG_TAG, "command: " + command);
                MainActivity.actualStatus.setAutoSwitchOnLight(movementSensor);
                Log.d(LOG_TAG, "weekDays: " + movementSensor.getWeekDays());
                networkData.sendCommand(NetworkData.CMD_AUTO_LIGHT_ON, command);
            }
        }
    }

    @Override
    public void serverResponse(NetworkData.NetworkStatus networkStatus, NetworkData.ResponseType responseType) {
        if (responseType == NetworkData.ResponseType.CONFIRMATION) {

        } else {
            switch (networkStatus) {
                case CONNECTION_ERROR:
                    Toast.makeText(context, "Brak połączenia z urządzeniem!", Toast.LENGTH_LONG).show();
                    break;
                case PARSING_OK:
                    Toast.makeText(context, "Urządzenie zostało sparsowane!", Toast.LENGTH_LONG).show();
                    updateStatus();
                    break;
                case RESPONSE_ERROR:
                    Toast.makeText(context, "Nieprawidłowa odpowiedz serwera!", Toast.LENGTH_SHORT).show();
                    break;
                case SETTING_ERROR:
                    showConnectionDialog();
                    break;
            }
            updateStatus();
            swipeRefreshLayout.setRefreshing(false);
        }
    }

    @Override
    public void connectDevice() {
        networkData.parseDevice();
    }
}
