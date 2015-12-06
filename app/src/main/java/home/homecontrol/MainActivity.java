package home.homecontrol;

import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.Menu;
import android.view.MenuItem;

import home.homecontrol.fragment.MainPanelFragment;
import home.homecontrol.fragment.SettingsFragment;

public class MainActivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        getFragmentManager()
                .beginTransaction()
                .replace(R.id.mainFragmentContainer, new MainPanelFragment(), MainPanelFragment.FRAGMENT_TAG)
                .addToBackStack(MainPanelFragment.FRAGMENT_TAG)
                .commit();
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
