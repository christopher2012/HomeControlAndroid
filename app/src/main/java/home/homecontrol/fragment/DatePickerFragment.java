package home.homecontrol.fragment;

import android.app.DatePickerDialog;
import android.app.Dialog;
import android.app.DialogFragment;
import android.content.DialogInterface;
import android.os.Bundle;
import android.util.Log;
import android.widget.DatePicker;

import java.util.Calendar;

public class DatePickerFragment extends DialogFragment
        implements DatePickerDialog.OnDateSetListener {

    public static final String YEAR_KEY = "year";
    public static final String MONTH_KEY = "month";
    public static final String DAY_KEY = "day";
    public static final String FRAGMENT_TAG = "DatePickerFragment";
    private static final String LOG_TAG = DatePickerFragment.class.getName();
    private boolean isCancel = false;

    OnDatePick callback;

    @Override
    public Dialog onCreateDialog(Bundle savedInstanceState) {

        int year;
        int month;
        int day;

        callback = (OnDatePick) getTargetFragment();

        Bundle args = getArguments();

        if (!args.isEmpty()) {
            year = args.getInt(YEAR_KEY);
            month = args.getInt(MONTH_KEY);
            day = args.getInt(DAY_KEY);
        } else {
            final Calendar c = Calendar.getInstance();
            year = c.get(Calendar.YEAR);
            month = c.get(Calendar.MONTH);
            day = c.get(Calendar.DAY_OF_MONTH);
        }

        return new DatePickerDialog(getActivity(), this, year, month, day);
    }

    @Override
    public void onCancel(DialogInterface dialog) {
        isCancel = true;
        Log.d(LOG_TAG, "onCancelMethod");
        super.onCancel(dialog);
    }

    public void onDateSet(DatePicker view, int year, int month, int day) {
        if (!isCancel)
            callback.datePick(year, month + 1, day, getTargetRequestCode());
    }


    public interface OnDatePick {
        void datePick(int year, int month, int day, int requestCode);
    }
}