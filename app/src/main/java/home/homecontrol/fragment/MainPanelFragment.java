package home.homecontrol.fragment;

import android.content.Context;
import android.graphics.Color;
import android.os.Bundle;
import android.app.Fragment;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.SeekBar;
import android.widget.Toast;

import com.loopj.android.http.AsyncHttpClient;
import com.loopj.android.http.AsyncHttpResponseHandler;

import java.io.IOException;

import butterknife.Bind;
import butterknife.ButterKnife;
import cz.msebera.android.httpclient.Header;
import home.homecontrol.R;
import home.homecontrol.network.NetworkData;

/**
 * A simple {@link Fragment} subclass.
 */
public class MainPanelFragment extends Fragment {

    public static final String FRAGMENT_TAG = "MainPanelFragment";
    private static final String LOG_TAG = MainPanelFragment.class.getName();

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

    AsyncHttpClient client;
    Context context;


    public MainPanelFragment() {

    }

    @Override
    public void onActivityCreated(Bundle savedInstanceState) {
        super.onActivityCreated(savedInstanceState);
        context = getActivity();
        client = new AsyncHttpClient();
        client.setTimeout(5000);
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_main_panel, container, false);
        ButterKnife.bind(this, view);

        return view;
    }

    @Override
    public void onStart() {
        super.onStart();
        seekBarBrightness.setMax(100);
        seekBarBrightness.setOnSeekBarChangeListener(new SeekBar.OnSeekBarChangeListener() {
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
        });

        switchLightOffButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                seekBarBrightness.setProgress(0);
                client.get(NetworkData.getIpServer() + NetworkData.LIGHT + "L0", new AsyncHttpResponseHandler() {
                    @Override
                    public void onSuccess(int statusCode, Header[] headers, byte[] responseBody) {
                        String str;
                        try {
                            str = new String(responseBody, "UTF-8");
                            Log.d("response", str);
                        } catch (IOException e) {
                            e.printStackTrace();
                        }
                    }

                    @Override
                    public void onFailure(int statusCode, Header[] headers, byte[] responseBody, Throwable error) {
                        Toast.makeText(context, "Brak połączenia z urządzeniem!!", Toast.LENGTH_LONG).show();
                    }
                });
            }
        });

        getSwitchLightOnButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                client.get(NetworkData.getIpServer() + NetworkData.LIGHT + "L1", new AsyncHttpResponseHandler() {
                    @Override
                    public void onSuccess(int statusCode, Header[] headers, byte[] responseBody) {
                        seekBarBrightness.setProgress(100);
                    }

                    @Override
                    public void onFailure(int statusCode, Header[] headers, byte[] responseBody, Throwable error) {
                        Toast.makeText(context, "Brak połączenie z urządzeniem!!", Toast.LENGTH_LONG).show();
                    }
                });
            }
        });

        tempInsideButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                getFragmentManager()
                        .beginTransaction()
                        .replace(R.id.mainFragmentContainer, new TemperatureFragment(), TemperatureFragment.FRAGMENT_TAG)
                        .addToBackStack(TemperatureFragment.FRAGMENT_TAG)
                        .commit();
            }
        });
    }

    public void changeBrightness(int progress) {
        client.get(NetworkData.getIpServer() + NetworkData.LIGHT + "B" + validateProgress(progress),
                new AsyncHttpResponseHandler() {
            @Override
            public void onSuccess(int statusCode, Header[] headers, byte[] responseBody) {

            }

            @Override
            public void onFailure(int statusCode, Header[] headers, byte[] responseBody, Throwable error) {
                Toast.makeText(context, "Brak połączenie z urządzeniem!!", Toast.LENGTH_LONG).show();
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
}
