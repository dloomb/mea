package com.meamobile.printicular;

import android.graphics.PorterDuff;
import android.os.Bundle;
import android.support.v7.app.ActionBarActivity;
import android.view.Menu;
import android.view.MenuItem;
import android.widget.Button;

import com.meamobile.photokit.core.Collection;
import com.meamobile.photokit.core.UserDefaults;
import com.meamobile.photokit.user_interface.ExplorerFragment;

public class MainActivity extends ActionBarActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        //Setup UserDefaults Singleton
        UserDefaults.getInstance().setContext(this);


        Button nextButton = (Button) findViewById(R.id.nextButton);
        nextButton.getBackground().setColorFilter(0xFFF20017, PorterDuff.Mode.MULTIPLY);


        ExplorerFragment fragment = ExplorerFragment.newInstance(Collection.RootCollection());
        fragment.ContainerId = R.id.fragmentContainer;
        getSupportFragmentManager().beginTransaction()
//                .setCustomAnimations(R.anim.abc_slide_in_top, R.anim.abc_slide_out_bottom)
                .replace(fragment.ContainerId, fragment)
                .addToBackStack(null)
                .commit();
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.menu_main, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        int id = item.getItemId();

        if (id == R.id.action_settings) {
            return true;
        }

        return super.onOptionsItemSelected(item);
    }
}
