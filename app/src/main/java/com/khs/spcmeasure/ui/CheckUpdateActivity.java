package com.khs.spcmeasure.ui;

// 23 Mar 2020 - AndroidX
// import android.app.Activity;
import android.app.Fragment;
import android.app.FragmentManager;
import android.os.Bundle;
import android.util.Log;
import android.view.MenuItem;

import androidx.appcompat.app.AppCompatActivity;

/**
 * Created by Mark on 05/16/2015.
 * handles version check and update action
 */
public class CheckUpdateActivity extends AppCompatActivity {
    private final String TAG = "CheckUpdateActivity";

    private static final String TAG_CHECK_UPDATE_FRAGMENT = "check_update_fragment";

    // bundle keys
    public static final String KEY_EXIT_IF_OK = "exit_if_ok";

    // preference keys
    public static final String KEY_PREF_SHOW_NOTIFICATIONS      = "key_pref_show_notifications";
    public static final String KEY_PREF_IN_CONTROL_AUTO_MOVE    = "key_pref_in_control_auto_move";
    public static final String KEY_PREF_IN_CONTROL_DELAY        = "key_pref_in_control_delay";

    // exit if version is ok
    private boolean mExitIfOk = false;

    // check update fragment is maintained across activity re-creates due to configuration changes
    private CheckUpdateFragment mChkUpdFrag;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        Log.d(TAG, "onCreate");

        // TODO remove action bar progress later
        // add progress circle to Action Bar - must be done before content added
        // requestWindowFeature(Window.FEATURE_INDETERMINATE_PROGRESS);

        // extract intent arguments, if any
        getArguments(getIntent().getExtras());

        // extract saved instance state arguments, if any
        getArguments(savedInstanceState);

        // show the Up button in the action bar.
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);

        // look for an existing check update fragment
        FragmentManager fm = getFragmentManager();
        mChkUpdFrag = (CheckUpdateFragment) fm.findFragmentByTag(TAG_CHECK_UPDATE_FRAGMENT);

        // create a new check update fragment if not found
        if (mChkUpdFrag == null) {
            // create fragment with bundle
            // see:
            // http://stackoverflow.com/questions/12739909/send-data-from-activity-to-fragment-in-android
            Bundle bundle = new Bundle();
            bundle.putBoolean(KEY_EXIT_IF_OK, mExitIfOk);
            mChkUpdFrag = new CheckUpdateFragment();
            mChkUpdFrag.setArguments(bundle);
        }

        // Display the fragment as the main content.
        fm.beginTransaction().replace(android.R.id.content, mChkUpdFrag, TAG_CHECK_UPDATE_FRAGMENT).commit();
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        // Handle action bar item clicks here. The action bar will
        // automatically handle clicks on the Home/Up button, so long
        // as you specify a parent activity in AndroidManifest.xml.
        int id = item.getItemId();

        switch(id) {
            case android.R.id.home:
                Log.d(TAG, "Home");
                finish();
                // NavUtils.navigateUpFromSameTask(this);
                return true;
            default:
                return super.onOptionsItemSelected(item);
        }
    }

    @Override
    public void onAttachFragment(Fragment fragment) {
        super.onAttachFragment(fragment);

        // TODO remove action bar progress later
        // start the progress bar spinning
//        setProgressBarIndeterminateVisibility(true);

    }

    // extracts arguments from provided Bundle
    private void getArguments(Bundle args) {
        // extract piece id
        if (args != null) {
            if (args.containsKey(KEY_EXIT_IF_OK)) {
                mExitIfOk = args.getBoolean(KEY_EXIT_IF_OK, false);
            }
        }
    }

}
