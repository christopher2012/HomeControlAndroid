package home.homecontrol.fragment;

import android.app.DialogFragment;
import android.app.Fragment;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.CheckBox;

import org.joda.time.DateTime;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;

import butterknife.Bind;
import butterknife.ButterKnife;
import home.homecontrol.MainActivity;
import home.homecontrol.MovementSensor;
import home.homecontrol.R;

/**
 * Created by HP on 2016-01-02.
 */
public class MovementSensorSettingsFragment extends DialogFragment
        implements View.OnClickListener, TimePickerFragment.OnTimePick {

    private static final String TITLE_KEY = "title";
    private static final int SINCE_TIME_REQUEST_CODE = 1;
    private static final int TO_TIME_REQUEST_CODE = 2;
    public static final int ALARM_REQUEST_CODE = 1;
    public static final int AUTO_LIHGT_REQUEST_CODE = 2;
    public static final String FRAGMENT_TAG = "MovementSensorSettingsFragment";
    private static final String LOG_TAG = MovementSensorSettingsFragment.class.getName();
    ArrayList<CheckBox> checkBoxes;
    ArrayList<Integer> binaryWeeks;
    static int MONDAY = 1;//0xb1000000;
    static int TUESDAY = 2;//0xb0100000;
    static int WEDNESDAY = 4;//0xb0010000;
    static int THURSDAY = 8;//0xb0001000;
    static int FRIDAY = 16;//0xb0000100;
    static int SATURDAY = 32;//0xb0000010;
    static int SUNDAY = 64;//0xb0000001;
    DateTime sinceTime;
    DateTime toTime;

    @Bind(R.id.onSwitchAlarmSettings)
    Button onSwitchAlarm;
    @Bind(R.id.offSwitchAlarmSettings)
    Button offSwitchAlarm;
    @Bind(R.id.sinceTimeAlarmSettings)
    Button sinceTimeAlarm;
    @Bind(R.id.toTimeAlarmSettings)
    Button toTimeAlarm;
    @Bind(R.id.customSettings)
    CheckBox customBox;
    @Bind(R.id.mondayAlarmSettings)
    CheckBox monBox;
    @Bind(R.id.tuesdayAlarmSettings)
    CheckBox tueBox;
    @Bind(R.id.wednesdayAlarmSettings)
    CheckBox wedBox;
    @Bind(R.id.thursdayAlarmSettings)
    CheckBox thuBox;
    @Bind(R.id.fridayAlarmSettings)
    CheckBox friBox;
    @Bind(R.id.saturdayAlarmSettings)
    CheckBox satBox;
    @Bind(R.id.sundayAlarmSettings)
    CheckBox sunBox;

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
        checkBoxes = new ArrayList<>();
        checkBoxes.add(monBox);
        checkBoxes.add(tueBox);
        checkBoxes.add(wedBox);
        checkBoxes.add(thuBox);
        checkBoxes.add(friBox);
        checkBoxes.add(satBox);
        checkBoxes.add(sunBox);

        binaryWeeks = new ArrayList<>();
        binaryWeeks.add(MONDAY);
        binaryWeeks.add(TUESDAY);
        binaryWeeks.add(WEDNESDAY);
        binaryWeeks.add(THURSDAY);
        binaryWeeks.add(FRIDAY);
        binaryWeeks.add(SATURDAY);
        binaryWeeks.add(SUNDAY);

        onSwitchAlarm.setOnClickListener(this);
        offSwitchAlarm.setOnClickListener(this);
        sinceTimeAlarm.setOnClickListener(this);
        toTimeAlarm.setOnClickListener(this);

        int temp;
        SimpleDateFormat sdf = new SimpleDateFormat("HH:mm");
        if (getTargetRequestCode() == ALARM_REQUEST_CODE) {
            toTimeAlarm.setText(sdf.format(MainActivity.actualStatus.getMovementAlarm().getToTime().getMillis()));
            sinceTimeAlarm.setText(sdf.format(MainActivity.actualStatus.getMovementAlarm().getSinceTime().getMillis()));
            customBox.setChecked(MainActivity.actualStatus.getMovementAlarm().isCustomSettOn());

            Log.d(LOG_TAG, Integer.toBinaryString(MainActivity.actualStatus.getMovementAlarm().getWeekDays()));
            temp = MainActivity.actualStatus.getMovementAlarm().getWeekDays();
        } else {
            toTimeAlarm.setText(sdf.format(MainActivity.actualStatus.getAutoSwitchOnLight().getToTime().getMillis()));
            sinceTimeAlarm.setText(sdf.format(MainActivity.actualStatus.getAutoSwitchOnLight().getSinceTime().getMillis()));
            customBox.setChecked(MainActivity.actualStatus.getMovementAlarm().isCustomSettOn());

            Log.d(LOG_TAG, Integer.toBinaryString(MainActivity.actualStatus.getAutoSwitchOnLight().getWeekDays()));
            temp = MainActivity.actualStatus.getAutoSwitchOnLight().getWeekDays();
        }

        for (int i = 0; i < binaryWeeks.size(); i++) {
            Log.d(LOG_TAG, temp + "");
            Log.d(LOG_TAG, Integer.toBinaryString(temp));
            if ((temp & binaryWeeks.get(i)) != 0)
                checkBoxes.get(i).setChecked(true);
            else
                checkBoxes.get(i).setChecked(false);
        }
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
        switch (v.getId()) {
            case R.id.onSwitchAlarmSettings: {
                MovementSensor movementSensor = new MovementSensor(true);
                int checkedBoxes = 0;
                for (int i = 0; i < 7; i++) {
                    if (checkBoxes.get(i).isChecked())
                        checkedBoxes |= binaryWeeks.get(i);
                }
                Log.d(LOG_TAG, "checkboxesValue: " + Integer.toBinaryString(checkedBoxes));
                Log.d(LOG_TAG, "int boxes: " + checkedBoxes);

                if (sinceTime != null) {
                    movementSensor.setSinceTime(sinceTime);
                } else
                    movementSensor.setSinceTime(new DateTime().withTime(0, 0, 0, 0));
                if (toTime != null) {
                    movementSensor.setToTime(toTime);
                } else
                    movementSensor.setToTime(new DateTime().withTime(23, 59, 0, 0));

                movementSensor.setWeekDays(checkedBoxes);
                movementSensor.setIsCustomSettOn(customBox.isChecked());
                callback.pitSetup(getTargetRequestCode(), movementSensor);
                dismiss();
                break;
            }
            case R.id.offSwitchAlarmSettings: {
                MovementSensor movementSensor = new MovementSensor(false);
                int checkedBoxes = 0;
                for (int i = 0; i < 7; i++) {
                    if (checkBoxes.get(i).isChecked())
                        checkedBoxes |= binaryWeeks.get(i);
                }
                Log.d(LOG_TAG, "checkboxesValue: " + Integer.toBinaryString(checkedBoxes));
                Log.d(LOG_TAG, "int boxes: " + checkedBoxes);

                if (sinceTime != null) {
                    movementSensor.setSinceTime(sinceTime);
                } else
                    movementSensor.setSinceTime(new DateTime().withTime(0, 0, 0, 0));
                if (toTime != null) {
                    movementSensor.setToTime(toTime);
                } else
                    movementSensor.setToTime(new DateTime().withTime(23, 59, 0, 0));

                movementSensor.setWeekDays(checkedBoxes);
                movementSensor.setIsCustomSettOn(customBox.isChecked());
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
        if (requestCode == SINCE_TIME_REQUEST_CODE) {
            sinceTime = new DateTime().withTime(hour, minute, 0, 0);
            SimpleDateFormat sdf = new SimpleDateFormat("HH:mm");
            sinceTimeAlarm.setText(sdf.format(new Date(sinceTime.getMillis())));
        } else {
            toTime = new DateTime().withTime(hour, minute, 0, 0);
            SimpleDateFormat sdf = new SimpleDateFormat("HH:mm");
            toTimeAlarm.setText(sdf.format(new Date(toTime.getMillis())));
        }
    }

    interface OnPIRSetup {
        void pitSetup(int requestCode, MovementSensor movementSensor);
    }
}
