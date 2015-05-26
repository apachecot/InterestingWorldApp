package world.interesting.panche.interestingworld;

/**
 * Created by Panche on 27/04/2015.
 */

import android.app.Activity;
import android.content.Intent;
import android.content.pm.ActivityInfo;
import android.os.Bundle;
import android.view.Window;

import java.util.Timer;
import java.util.TimerTask;

public class Splash extends Activity {

    // Set the duration of the splash screen
    private static final long SPLASH_SCREEN_DELAY = 5000;
    String[] datos = new String[5];
    Boolean visible = true;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        // Hide title bar
        requestWindowFeature(Window.FEATURE_NO_TITLE);

        setContentView(R.layout.splash);

        TimerTask task = new TimerTask() {
            @Override
            public void run() {
                if(visible==true) {
                    if (datos[0].equals("-1") && datos[1].equals("") && datos[2].equals("") && datos[3].equals("") && datos[4].equals("")) {
                        // Start the next activity
                        Intent mainIntent = new Intent().setClass(
                                Splash.this, MainActivity.class);
                        startActivity(mainIntent);
                    } else {
                        // Start the next activity
                        Intent mainIntent = new Intent().setClass(
                                Splash.this, MainActivityUser.class);
                        startActivity(mainIntent);
                    }
                    finish();
                }


            }
        };

        // Simulate a long loading process on application startup.
        Timer timer = new Timer();
        timer.schedule(task, SPLASH_SCREEN_DELAY);
        datos = Preferences.loadPreferences(this);
    }

    @Override
    protected void onDestroy(){
        visible=false;
        super.onDestroy();
    }

}
