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
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;

import butterknife.Bind;
import butterknife.ButterKnife;
import cz.msebera.android.httpclient.Header;
import home.homecontrol.R;
import home.homecontrol.network.NetworkData;
import home.homecontrol.network.TemperatureFeed;

/**
 * Created by HP on 2015-12-20.
 */
public class TemperatureFragment extends Fragment
        implements TimePickerFragment.OnTimePick,
        DatePickerFragment.OnDatePick {

    public static final String FRAGMENT_TAG = "TemperatureFragment";
    private static final String LOG_TAG = TemperatureFragment.class.getName();
    private static final String DATE_BUTTON_PATTERN = "yyyy-MM-dd";
    private static final int SINCE_DATE_REQUEST_CODE = 1;
    private static final int TO_DATE_REQUEST_CODE = 2;

    private DateTime sinceDate;

    @Bind(R.id.graph)
    GraphView graphView;
    @Bind(R.id.graphData)
    Button graphDataSinceButton;


    AsyncHttpClient client;
    Context context;
    ArrayList<TemperatureFeed> temperatureFeeds;

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_temperature, container, false);
        client = new AsyncHttpClient();
        temperatureFeeds = new ArrayList<>();
        ButterKnife.bind(this, view);
        return view;
    }

    @Override
    public void onStart() {
        super.onStart();


// set manual x bounds to have nice steps
        //graph.getViewport().setMinX(d1.getTime());
        //graph.getViewport().setMaxX(d3.getTime());
        //graph.getViewport().setXAxisBoundsManual(true);


        /**
         * View port
         * wartość minimalna to jest sinceDate, ustawiamy wielkosc okna w dniach lub godzinach
         * wartość maksymalna musi zostać obliczana na postawie rokładu dni lub godzin,
         * trzeba pamietać o warunku żeby wartość maksymalna nie wyszła poza zakres.
         */

        DateTime date = new DateTime();
        sinceDate = date;

        DateTimeFormatter dtf = DateTimeFormat.forPattern(DATE_BUTTON_PATTERN);
        graphDataSinceButton.setText(dtf.print(sinceDate));

        graphDataSinceButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                showDatePickerDialog(SINCE_DATE_REQUEST_CODE, sinceDate);
                //showTimePickerDialog(SINCE_DATE_REQUEST_CODE, sinceDate);
            }
        });
        Log.d(LOG_TAG, NetworkData.getOneDayTempFeeds(sinceDate));
        client.get(NetworkData.getOneDayTempFeeds(sinceDate), new AsyncHttpResponseHandler() {
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
                        TemperatureFeed feed = new TemperatureFeed(
                                temp.getString("created_at"),
                                temp.getInt("entry_id"),
                                temp.getDouble("field1")
                        );
                        temperatureFeeds.add(feed);

                    }

                    DataPoint[] dataPoints = new DataPoint[temperatureFeeds.size()];
                    for (int i = 0; i < temperatureFeeds.size(); i++) {
                        Log.d(LOG_TAG, "date: " + temperatureFeeds.get(i).getCreatedDate() + ", value: " + temperatureFeeds.get(i).getValue());
                        dataPoints[i] = new DataPoint(temperatureFeeds.get(i).getCreatedDate(), temperatureFeeds.get(i).getValue());
                    }
                    LineGraphSeries<DataPoint> series = new LineGraphSeries<DataPoint>(dataPoints);

                    series.setOnDataPointTapListener(new OnDataPointTapListener() {
                        @Override
                        public void onTap(Series series, DataPointInterface dataPoint) {
                            SimpleDateFormat sdf = new SimpleDateFormat("HH:mm");

                            String message = "";
                            Date date = new Date(((int) dataPoint.getX()));
                            message += "Godzina pomiaru: ";
                            message += sdf.format(date) + "\n";
                            message += "Pomiar: ";
                            message += dataPoint.getY();
                            Toast.makeText(getActivity(), message, Toast.LENGTH_SHORT).show();
                        }
                    });

                    series.setDataPointsRadius(8);
                    series.setThickness(3);
                    graphView.addSeries(series);

                    SimpleDateFormat format = new SimpleDateFormat("HH:mm");

                    graphView.getGridLabelRenderer().setLabelFormatter(new DateAsXAxisLabelFormatter(getActivity(), format));

                    graphView.getViewport().setScalable(true);
                    graphView.getViewport().setScrollable(true);
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
        args.putInt(DatePickerFragment.MONTH_KEY, dateTime.getMonthOfYear() - 1);
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
        }
    }

    @Override
    public void datePick(int year, int month, int day, int requestCode) {

        if (requestCode == SINCE_DATE_REQUEST_CODE) {
            sinceDate = new DateTime(year, month,
                    day, sinceDate.getHourOfDay(), sinceDate.getMinuteOfHour());
            graphDataSinceButton.setText(
                    DateTimeFormat.forPattern(DATE_BUTTON_PATTERN).print(sinceDate));
        }
    }
}
