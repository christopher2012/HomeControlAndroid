package home.homecontrol.fragment;


import android.app.Dialog;
import android.app.DialogFragment;
import android.app.TimePickerDialog;
import android.os.Bundle;
import android.app.Fragment;
import android.text.format.DateFormat;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TimePicker;

import com.jjoe64.graphview.GraphView;

import java.util.Calendar;

import home.homecontrol.R;

public class TimePickerFragment extends DialogFragment
        implements TimePickerDialog.OnTimeSetListener {

    public static final String HOUR_KEY = "hour";
    public static final String MINUTE_KEY = "minute";
    public static final String FRAGMENT_TAG = "TimePickerFragment";
    private static final String LOG_TAG = TimePickerFragment.class.getName();
    OnTimePick callback;

    public TimePickerFragment() {

    }

    @Override
    public Dialog onCreateDialog(Bundle savedInstanceState) {

        int hour;
        int minute;

        callback = (OnTimePick) getTargetFragment();

        Bundle args = getArguments();
        if(!args.isEmpty()){
            hour = args.getInt(HOUR_KEY);
            minute = args.getInt(MINUTE_KEY);
        }else{
            final Calendar c = Calendar.getInstance();
            hour = c.get(Calendar.HOUR_OF_DAY);
            minute = c.get(Calendar.MINUTE);
        }

        return new TimePickerDialog(getActivity(), this, hour, minute,
                DateFormat.is24HourFormat(getActivity()));
    }

    public void onTimeSet(TimePicker view, int hourOfDay, int minute) {
        callback.timePick(hourOfDay, minute, getTargetRequestCode());
    }

    public interface OnTimePick{
        void timePick(int hour, int minute, int requestCode );
    }
}
