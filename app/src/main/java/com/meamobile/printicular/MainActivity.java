package com.meamobile.printicular;

import android.graphics.PorterDuff;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v7.app.ActionBarActivity;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.widget.Button;
import android.widget.Toast;

import com.meamobile.photokit.core.Collection;
import com.meamobile.photokit.core.UserDefaults;
import com.meamobile.photokit.user_interface.ExplorerFragment;

import java.util.List;

public class MainActivity extends ActionBarActivity {

    private long lastBackTap = 0;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        //Setup UserDefaults Singleton
        UserDefaults.getInstance().setContext(this);

        setTitle("Select a Source");

        Button nextButton = (Button) findViewById(R.id.nextButton);
        nextButton.getBackground().setColorFilter(0xFFF20017, PorterDuff.Mode.MULTIPLY);


        ExplorerFragment fragment = ExplorerFragment.newInstance(Collection.RootCollection());
        fragment.ContainerId = R.id.fragmentContainer;
        getSupportFragmentManager().beginTransaction()
                .replace(fragment.ContainerId, fragment)
                .addToBackStack("tag")
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


    //--------------------------
    //        Button Input
    //--------------------------


    @Override
    public void onBackPressed()
    {
        Log.d("!!", "Back");
        FragmentManager fragmentManager = getSupportFragmentManager();

        //Pop Stack
        if (fragmentManager.getBackStackEntryCount() > 1)
        {
            List<Fragment> fragments = fragmentManager.getFragments();
            Fragment current = fragments.get(fragments.size() - 1);

            if (current instanceof ExplorerFragment)
            {
                fragmentManager.popBackStack();

//                ExplorerFragment explorerFragment = (ExplorerFragment) current;
//                explorerFragment.onFragmentWillAppear();
            }
        }
        //Exit App
        else
        {
            long now = System.currentTimeMillis();
            long diff = now - lastBackTap;

            if (diff < 2000)
            {
                moveTaskToBack(true);
            }
            else
            {
                Toast.makeText(this, "Tap again to exit", Toast.LENGTH_SHORT).show();
                lastBackTap = now;
            }
        }
    }
}
