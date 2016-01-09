package home.homecontrol.fragment;

import android.content.Context;
import android.os.Bundle;
import android.app.Fragment;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

import com.loopj.android.http.AsyncHttpClient;
import com.loopj.android.http.AsyncHttpResponseHandler;

import org.json.JSONException;
import org.json.JSONObject;

import java.io.IOException;

import butterknife.Bind;
import butterknife.ButterKnife;
import cz.msebera.android.httpclient.Header;
import home.homecontrol.ActualStatus;
import home.homecontrol.MainActivity;
import home.homecontrol.MovementSensor;
import home.homecontrol.R;
import home.homecontrol.network.NetworkData;

/**
 * A simple {@link Fragment} subclass.
 */
public class SettingsFragment extends Fragment
        implements NetworkData.OnServerResponse {

    public static final String FRAGMENT_TAG = "SettingsFragment";
    private static final String LOG_TAG = SettingsFragment.class.getName();

    @Bind(R.id.settingIP)
    EditText settingIPEdit;
    @Bind(R.id.settingIPButton)
    Button settingIpButton;
    Context context;

    NetworkData networkData;

    public SettingsFragment() {

    }

    @Override
    public void onActivityCreated(Bundle savedInstanceState) {
        super.onActivityCreated(savedInstanceState);
        context = getActivity();
        networkData = new NetworkData(this);
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_settings, container, false);
        ButterKnife.bind(this, view);

        return view;
    }

    @Override
    public void onStart() {
        super.onStart();

        settingIpButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                int ip = -1;
                try {
                    ip = Integer.valueOf(settingIPEdit.getText().toString());
                } catch (NumberFormatException e) {

                }

                if (ip < 255 && ip > 0) {
                    NetworkData.setIpSet(Integer.toString(ip));
                    networkData.parseDevice();
                } else {
                    Toast.makeText(context, "Zły adress IP", Toast.LENGTH_SHORT).show();
                }
            }
        });
    }

    @Override
    public void serverResponse(NetworkData.NetworkStatus networkStatus, NetworkData.ResponseType responseType) {
        if (networkStatus == NetworkData.NetworkStatus.PARSING_OK)
            Toast.makeText(context, "Urządzenie zostało sparsowane!", Toast.LENGTH_LONG).show();
        else if(networkStatus == NetworkData.NetworkStatus.RESPONSE_ERROR)
            Toast.makeText(context, "Nieprawidłowa odpowiedz serwera!", Toast.LENGTH_SHORT).show();
    }
}