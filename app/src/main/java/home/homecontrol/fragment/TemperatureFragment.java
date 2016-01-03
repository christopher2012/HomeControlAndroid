package home.homecontrol.fragment;

import android.app.DialogFragment;
import android.app.Fragment;
import android.content.Context;
import android.graphics.Color;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.Toast;

import com.jjoe64.graphview.GraphView;
import com.jjoe64.graphview.helper.DateAsXAxisLabelFormatter;
import com.jjoe64.graphview.series.DataPoint;
import com.jjoe64.graphview.series.DataPointInterface;
import com.jjoe64.graphview.series.LineGraphSeries;
import com.jjoe64.graphview.series.OnDataPointTapListener;
import com.jjoe64.graphview.series.Series;
import com.loopj.android.http.AsyncHttpClient;
import com.loopj.android.http.AsyncHttpResponseHandler;

import org.joda.time.DateTime;
import org.joda.time.format.DateTimeFormat;
import org.joda.time.format.DateTimeFormatter;
import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.IOException;
import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;

import butterknife.Bind;
import butterknife.ButterKnife;
import cz.msebera.android.httpclient.Header;
import home.homecontrol.R;
import home.homecontrol.network.NetworkData;

/**
 * Created by HP on 2015-12-20.
 */
public class TemperatureFragment extends Fragment
        implements TimePickerFragment.OnTimePick,
        DatePickerFragment.OnDatePick {

    public static final String FRAGMENT_TAG = "TemperatureFragment";
    private static final String LOG_TAG = TemperatureFragment.class.getName();
    private static final String DATE_BUTTON_PATTERN = "yyyy-MM-dd HH:mm";
    private static final int SINCE_DATE_REQUEST_CODE = 1;
    private static final int TO_DATE_REQUEST_CODE = 2;

    private DateTime sinceDate;
    private DateTime toDate;

    @Bind(R.id.graph)
    GraphView graphView;
    @Bind(R.id.graphDataSince)
    Button graphDataSinceButton;
    @Bind(R.id.graphDataTo)
    Button graphDataToButton;

    AsyncHttpClient client;
    Context context;

    @Override
    public void onActivityCreated(Bundle savedInstanceState) {
        super.onActivityCreated(savedInstanceState);
        client = new AsyncHttpClient();
        client.get(NetworkData.TEMPERATURE_FEEDS, new AsyncHttpResponseHandler() {
            @Override
            public void onSuccess(int statusCode, Header[] headers, byte[] responseBody) {
                String str;
                try {
                    str = new String(responseBody, "UTF-8");
                    JSONObject jsonObject = new JSONObject(str);
                    String feeds = jsonObject.getString("feeds");
                    JSONArray jsonArray = new JSONArray(feeds);
                    for (int i = 0; i < jsonArray.length(); i++) {
                        JSONObject temp = new JSONObject(jsonArray.getString(i));
                        Log.d(LOG_TAG, "Temp: " + temp.getString("field1"));
                    }
                    Log.d("response", str);
                } catch (IOException e) {
                    e.printStackTrace();
                } catch (JSONException e) {
                    e.printStackTrace();
                }
            }

            @Override
            public void onFailure(int statusCode, Header[] headers, byte[] responseBody, Throwable error) {
                Toast.makeText(context, "Brak połączenia z Internetem!!", Toast.LENGTH_LONG).show();
            }
        });
    }

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_temperature, container, false);
        ButterKnife.bind(this, view);
        return view;
    }

    @Override
    public void onStart() {
        super.onStart();

        // generate Dates
        Calendar calendar = Calendar.getInstance();
        Date d1 = calendar.getTime();
        calendar.add(Calendar.DATE, 1);
        Date d2 = calendar.getTime();
        calendar.add(Calendar.DATE, 1);
        Date d3 = calendar.getTime();
        calendar.add(Calendar.DATE, 1);
        Date d4 = calendar.getTime();
        calendar.add(Calendar.DATE, 1);
        Date d5 = calendar.getTime();
        calendar.add(Calendar.DATE, 1);
        Date d6 = calendar.getTime();
        calendar.add(Calendar.DATE, 1);
        Date d7 = calendar.getTime();

        GraphView graph = graphView;

// you can directly pass Date objects to DataPoint-Constructor
// this will convert the Date to double via Date#getTime()
        LineGraphSeries<DataPoint> series = new LineGraphSeries<DataPoint>(new DataPoint[] {
                new DataPoint(d1, 1),
                new DataPoint(d2, 5),
                new DataPoint(d3, 3),
                new DataPoint(d4, 2),
                new DataPoint(d5, 1),
                new DataPoint(d6, 1),
                new DataPoint(d7, 6)
        });
        graph.addSeries(series);

// set date label formatter
        SimpleDateFormat format = new SimpleDateFormat("dd.MM");
        DateFormat df = DateFormat.getInstance();
       // df.format("MM-dd");
        graph.getGridLabelRenderer().setLabelFormatter(new DateAsXAxisLabelFormatter(getActivity(), format));
        graph.getGridLabelRenderer().setNumHorizontalLabels(5); // only 4 because of the space

// set manual x bounds to have nice steps
        graph.getViewport().setMinX(d1.getTime());
        graph.getViewport().setMaxX(d3.getTime());
        graph.getViewport().setXAxisBoundsManual(true);

        /*
        LineGraphSeries<DataPoint> series = new LineGraphSeries<DataPoint>(new DataPoint[]{
                new DataPoint(0, 4),
                new DataPoint(1, 5),
                new DataPoint(2, 3),
                new DataPoint(3, 2),
                new DataPoint(4, 3),
                new DataPoint(5, 2),
                new DataPoint(6, 2),
                new DataPoint(10, 2),
                new DataPoint(11, 2),
                new DataPoint(12, 6)
        });
        LineGraphSeries<DataPoint> seriesErr = new LineGraphSeries<DataPoint>(new DataPoint[]{
                new DataPoint(7, 2),
                new DataPoint(8, 2),
                new DataPoint(9, 2)
        });



        series.setOnDataPointTapListener(new OnDataPointTapListener() {
            @Override
            public void onTap(Series series, DataPointInterface dataPoint) {
                Toast.makeText(getActivity(), "Series1: On Data Point clicked: " + dataPoint, Toast.LENGTH_SHORT).show();
            }
        });

        graphView.addSeries(series);
        graphView.addSeries(seriesErr);
        series.setTitle("Random Curve 1");
        seriesErr.setTitle("Error Curve 1");
        seriesErr.setColor(Color.RED);
        series.setDrawDataPoints(true);
        series.setDataPointsRadius(4);
        series.setThickness(6);
        seriesErr.setThickness(6);


        graphView.addSeries(seriesDate);
        graphView.getViewport().setScalable(true);
        graphView.getViewport().setScrollable(true);
        graphView.getViewport().setXAxisBoundsManual(true);
        //graphView.getViewport().setMinX(0.5);
        //graphView.getViewport().setMaxX(3.5);
*/

        /**
         * View port
         * wartość minimalna to jest sinceDate, ustawiamy wielkosc okna w dniach lub godzinach
         * wartość maksymalna musi zostać obliczana na postawie rokładu dni lub godzin,
         * trzeba pamietać o warunku żeby wartość maksymalna nie wyszła poza zakres.
         */

        DateTime date = new DateTime();
        sinceDate = date;
        toDate = date;

        DateTimeFormatter dtf = DateTimeFormat.forPattern(DATE_BUTTON_PATTERN);
        graphDataSinceButton.setText(dtf.print(sinceDate));
        graphDataToButton.setText(dtf.print(toDate));

        graphDataSinceButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                showDatePickerDialog(SINCE_DATE_REQUEST_CODE, sinceDate);
                showTimePickerDialog(SINCE_DATE_REQUEST_CODE, sinceDate);
            }
        });

        graphDataToButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                showDatePickerDialog(TO_DATE_REQUEST_CODE, toDate);
                showTimePickerDialog(TO_DATE_REQUEST_CODE, toDate);
            }
        });
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
        newTimeFragment.setTargetFragment(TemperatureFragment.this, requestCode);
        newTimeFragment.show(getFragmentManager(), TimePickerFragment.FRAGMENT_TAG);
    }

    public void showDatePickerDialog(int requestCode) {
        showTimePickerDialog(requestCode, new DateTime());
    }

    public void showDatePickerDialog(int requestCode, DateTime dateTime) {
        DialogFragment newDateFragment = new DatePickerFragment();
        Bundle args = new Bundle();
        args.putInt(DatePickerFragment.YEAR_KEY, dateTime.getYear());
        args.putInt(DatePickerFragment.MONTH_KEY, dateTime.getMonthOfYear()-1);
        args.putInt(DatePickerFragment.DAY_KEY, dateTime.getDayOfMonth());
        newDateFragment.setArguments(args);
        newDateFragment.setTargetFragment(TemperatureFragment.this, requestCode);
        newDateFragment.show(getFragmentManager(), DatePickerFragment.FRAGMENT_TAG);
    }

    @Override
    public void timePick(int hour, int minute, int requestCode) {

        if (requestCode == SINCE_DATE_REQUEST_CODE) {
            sinceDate = new DateTime(sinceDate.getYear(), sinceDate.getMonthOfYear(),
                    sinceDate.getDayOfMonth(), hour, minute);
            graphDataSinceButton.setText(
                    DateTimeFormat.forPattern(DATE_BUTTON_PATTERN).print(sinceDate));
        } else {
            toDate = new DateTime(toDate.getYear(), toDate.getMonthOfYear(),
                    toDate.getDayOfMonth(), hour, minute);
            graphDataToButton.setText(
                    DateTimeFormat.forPattern(DATE_BUTTON_PATTERN).print(toDate));
        }
    }

    @Override
    public void datePick(int year, int month, int day, int requestCode) {

        if (requestCode == SINCE_DATE_REQUEST_CODE) {
            sinceDate = new DateTime(year, month,
                    day, sinceDate.getHourOfDay(), sinceDate.getMinuteOfHour());
            graphDataSinceButton.setText(
                    DateTimeFormat.forPattern(DATE_BUTTON_PATTERN).print(sinceDate));
        } else {
            toDate = new DateTime(year, month,
                    day, toDate.getHourOfDay(), toDate.getMinuteOfHour());
            graphDataToButton.setText(
                    DateTimeFormat.forPattern(DATE_BUTTON_PATTERN).print(toDate));
        }
    }
}
