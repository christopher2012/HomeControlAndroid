package home.homecontrol;

import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.Menu;
import android.view.MenuItem;

import home.homecontrol.fragment.MainPanelFragment;
import home.homecontrol.fragment.SettingsFragment;

public class MainActivity extends AppCompatActivity {

    public static ActualStatus actualStatus;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        setExampleActualStatus();

        getFragmentManager()
                .beginTransaction()
                .replace(R.id.mainFragmentContainer, new MainPanelFragment(), MainPanelFragment.FRAGMENT_TAG)
                .addToBackStack(MainPanelFragment.FRAGMENT_TAG)
                .commit();
    }

    public static void setExampleActualStatus() {
        actualStatus = new ActualStatus();
        actualStatus.setLightOn(true);
        actualStatus.setBrightness(55);
        actualStatus.setInsideTemperature(22.55f);
        actualStatus.setOutsideTemperature(-11.50f);
        MovementSensor movementSensor = new MovementSensor(true);
        actualStatus.setMovementAlarm(movementSensor);
        actualStatus.setAutoSwitchOffLight(movementSensor);
        actualStatus.setAutoSwitchOnLight(movementSensor);
        actualStatus.setSmokeAlarm(true);
        actualStatus.setMonoxideAlarm(false);
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.menu_main, menu);
        return true;
    }

    @Override
    public void onBackPressed() {
        if (getFragmentManager().getBackStackEntryCount() > 1)
            getFragmentManager().popBackStack();
        else
            super.onBackPressed();
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        // Handle action bar item clicks here. The action bar will
        // automatically handle clicks on the Home/Up button, so long
        // as you specify a parent activity in AndroidManifest.xml.
        int id = item.getItemId();

        if (id == R.id.action_settings) {
            getFragmentManager()
                    .beginTransaction()
                    .replace(R.id.mainFragmentContainer, new SettingsFragment(), SettingsFragment.FRAGMENT_TAG)
                    .addToBackStack(SettingsFragment.FRAGMENT_TAG)
                    .commit();
        }

        return super.onOptionsItemSelected(item);
    }
}
