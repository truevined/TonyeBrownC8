package tonyebrown.calc8;

import android.app.Activity;
import android.content.Intent;
import android.graphics.Color;
import android.graphics.Typeface;
import android.os.Build;
import android.os.Bundle;
import android.os.Handler;
import android.widget.TextView;

import com.example.tonyebrown.calc8.R;

/**
 * Created by Tonye Brown on 10/12/2015.
 */

public class SplashActivity extends Activity {

    // Splash screen timer
    private static int SPLASH_TIME_OUT = 2000;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_splash);

        TextView credits = (TextView) findViewById(R.id.textViewSplashHeader);

        //sets status bar and navigation bar color to #212121
        if (android.os.Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP) {
            getWindow().setNavigationBarColor(Color.parseColor("#212121"));
            getWindow().setStatusBarColor(Color.parseColor("#212121"));
        }

        // set font for all displays
        Typeface font = Typeface.createFromAsset(getAssets(), "fonts/quartzbold.ttf");
        credits.setTypeface(font);

        new Handler().postDelayed(new Runnable() {

         /*
          * Showing splash screen with a timer. This will be useful when you
          * want to show case your app logo / company
          */

            @Override
            public void run() {
                // This method will be executed once the timer is over
                // Start your app main activity
                Intent i = new Intent(SplashActivity.this, MainActivity.class);
                startActivity(i);

                // close this activity
                finish();
            }
        }, SPLASH_TIME_OUT);
    }
}
