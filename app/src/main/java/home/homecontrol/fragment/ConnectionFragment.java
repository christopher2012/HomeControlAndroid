package home.homecontrol.fragment;

import android.app.DialogFragment;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

import butterknife.Bind;
import butterknife.ButterKnife;
import home.homecontrol.R;
import home.homecontrol.network.NetworkData;

/**
 * Created by HP on 2016-01-09.
 */
public class ConnectionFragment extends DialogFragment {

    public static final String LOG_TAG = ConnectionFragment.class.getName();
    public static final String FRAGMENT_TAG= "ConnectionFragment";

    @Bind(R.id.okButtonConnection)
    Button okConnect;
    @Bind(R.id.ipEditTextConnection)
    EditText ipEditText;

    OnConnectDevice callback;

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_connection, container, false);
        ButterKnife.bind(this, view);
        callback = (OnConnectDevice) getTargetFragment();
        okConnect.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                int ip = -1;
                try {
                    ip = Integer.valueOf(ipEditText.getText().toString());
                } catch (NumberFormatException e) {

                }

                if (ip < 255 && ip > 0) {
                    NetworkData.setIpSet(Integer.toString(ip));
                    callback.connectDevice();
                } else {
                    Toast.makeText(getActivity(), "Zły adress IP", Toast.LENGTH_SHORT).show();
                }
                dismiss();
            }
        });

        return view;
    }

    interface OnConnectDevice{
       void connectDevice();
    }
}
