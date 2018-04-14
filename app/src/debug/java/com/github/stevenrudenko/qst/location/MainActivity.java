package com.github.stevenrudenko.qst.location;

import android.app.Activity;

/** Main activity. */
public class MainActivity extends Activity {

    @Override
    protected void onStart() {
        super.onStart();
        LocationTileService.start(this);
    }
}
