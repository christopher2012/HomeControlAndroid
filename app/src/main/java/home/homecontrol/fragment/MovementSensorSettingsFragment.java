package home.homecontrol.fragment;

import android.app.DialogFragment;
import android.app.Fragment;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;

import org.joda.time.DateTime;

import butterknife.Bind;
import butterknife.ButterKnife;
import home.homecontrol.MovementSensor;
import home.homecontrol.R;

/**
 * Created by HP on 2016-01-02.
 */
public class MovementSensorSettingsFragment extends DialogFragment
        implements View.OnClickListener, TimePickerFragment.OnTimePick{

    private static final String TITLE_KEY="title";
    private static final int SINCE_TIME_REQUEST_CODE = 1;
    private static final int TO_TIME_REQUEST_CODE = 2;
    public static final int ALARM_REQUEST_CODE = 1;
    public static final int AUTO_LIHGT_REQUEST_CODE = 2;
    public static final String FRAGMENT_TAG = "MovementSensorSettingsFragment";
    private static final String LOG_TAG = MovementSensorSettingsFragment.class.getName();

    @Bind(R.id.onSwitchAlarmSettings)
    Button onSwitchAlarm;
    @Bind(R.id.offSwitchAlarmSettings)
    Button offSwitchAlarm;
    @Bind(R.id.sinceTimeAlarmSettings)
    Button sinceTimeAlarm;
    @Bind(R.id.toTimeAlarmSettings)
    Button toTimeAlarm;

    OnPIRSetup callback;

    public static MovementSensorSettingsFragment newInstance(String title, Fragment targetFragment, int requestCode) {

        Bundle args = new Bundle();
        args.putString(TITLE_KEY, title);
        MovementSensorSettingsFragment fragment = new MovementSensorSettingsFragment();
        fragment.setTargetFragment(targetFragment, requestCode);
        fragment.setArguments(args);
        return fragment;
    }

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {

        callback = (OnPIRSetup) getTargetFragment();

        getDialog().setTitle(getArguments().getString(TITLE_KEY));
        View view = inflater.inflate(R.layout.fragment_movement_sensor_settings, container, false);
        ButterKnife.bind(this, view);
        onSwitchAlarm.setOnClickListener(this);
        offSwitchAlarm.setOnClickListener(this);
        sinceTimeAlarm.setOnClickListener(this);
        toTimeAlarm.setOnClickListener(this);


        return view;
    }

    public void showTimePickerDialog(int requestCode) {
        showTimePickerDialog(requestCode, new DateTime());
    }

    public void showTimePickerDialog(int requestCode, DateTime dateTime) {
        DialogFragment newTimeFragment = new TimePickerFragment();
        Bundle args = new Bundle();
        args.putInt(TimePickerFragment.HOUR_KEY, dateTime.getHourOfDay());
        args.putInt(TimePickerFragment.MINUTE_KEY, dateTime.getMinuteOfHour());
        newTimeFragment.setArguments(args);
        newTimeFragment.setTargetFragment(MovementSensorSettingsFragment.this, requestCode);
        newTimeFragment.show(getFragmentManager(), TimePickerFragment.FRAGMENT_TAG);
    }

    @Override
    public void onClick(View v) {
        switch (v.getId()){
            case R.id.onSwitchAlarmSettings: {
                MovementSensor movementSensor = new MovementSensor(true);
                callback.pitSetup(getTargetRequestCode(), movementSensor);
                dismiss();
                break;
            }
            case R.id.offSwitchAlarmSettings:{
                MovementSensor movementSensor = new MovementSensor(false);
                callback.pitSetup(getTargetRequestCode(), movementSensor);
                dismiss();
                break;
            }
            case R.id.sinceTimeAlarmSettings:
                showTimePickerDialog(SINCE_TIME_REQUEST_CODE);
                break;
            case R.id.toTimeAlarmSettings:
                showTimePickerDialog(TO_TIME_REQUEST_CODE);
                break;
        }
    }

    @Override
    public void timePick(int hour, int minute, int requestCode) {
        if(requestCode == SINCE_TIME_REQUEST_CODE){

        }else{

        }
    }

    interface OnPIRSetup{
        void pitSetup(int reqestCode, MovementSensor movementSensor);
    }
}
