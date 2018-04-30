package tonyebrown.calc8;

import android.content.Context;
import android.content.Intent;
import android.graphics.Color;
import android.graphics.Typeface;
import android.net.Uri;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.view.View;
import android.widget.ImageButton;
import android.widget.TextView;
import android.widget.Toast;

import com.example.tonyebrown.calc8.R;

import java.io.FileOutputStream;
import java.io.IOException;

/**
 * Controls help functionality of Calc8's help page
 * Created by Tonye Brown on 10/12/2015.
 */
public class Calc8HelpActivity extends AppCompatActivity {

    View themeButtonLight;
    View themeButtonNight;

    TextView website;
    String FILENAME = "calc8Theme";



    @Override
    protected void onCreate(Bundle savedInstanceState) {

        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_calc8help);

      /*  themeButtonLight = findViewById(R.id.imageButtonLight);
        themeButtonNight = findViewById(R.id.imageButtonNight);*/

        //FILENAME = "calc8Theme";

        TextView helpHeader = (TextView) findViewById(R.id.textViewHelpHeader);
        website = (TextView) findViewById(R.id.textViewWebsite);
        TextView author = (TextView) findViewById(R.id.textViewAuthor);
        website.setTextColor(Color.parseColor("#6ac7f6"));

        // set font for all displays
        Typeface font = Typeface.createFromAsset(getAssets(), "fonts/quartzbold.ttf");
        helpHeader.setTypeface(font);
        website.setTypeface(font);
        author.setTypeface(font);

        /*findViewById(R.id.imageButtonLight).setOnClickListener(handleTheme);
        findViewById(R.id.imageButtonNight).setOnClickListener(handleTheme);*/
        website.setOnClickListener(handleWebsite);
    }

    private View.OnClickListener handleTheme = new View.OnClickListener() {

        /*******
         * Controls theme switch on each click
         */
        public void onClick(View current) {
            ImageButton btn = (ImageButton) current;

            Context context = getApplicationContext();

            if (btn.getId() == R.id.imageButtonLight) {
                Toast.makeText(context, "Switched to light theme", Toast.LENGTH_SHORT);
                themeButtonLight.setBackgroundColor(Color.parseColor("#000000"));
                themeButtonNight.setBackgroundColor(Color.parseColor("#D3F2B0"));

                String light = "Light";

                try {
                    FileOutputStream fos = openFileOutput(FILENAME, Context.MODE_PRIVATE);
                    fos.write(light.getBytes());
                    fos.close();
                } catch (IOException e) {
                    e.printStackTrace();
                }
            }

            if (btn.getId() == R.id.imageButtonNight) {
                Toast.makeText(context, "Switched to Night theme", Toast.LENGTH_SHORT);
                themeButtonLight.setBackgroundColor(Color.parseColor("#D3F2B0"));
                themeButtonNight.setBackgroundColor(Color.parseColor("#000000"));

                String night = "Night";

                try {
                    FileOutputStream fos = openFileOutput(FILENAME, Context.MODE_PRIVATE);
                    fos.write(night.getBytes());
                    fos.close();
                } catch (IOException e) {
                    e.printStackTrace();
                }
            }
        }
    };

    private View.OnClickListener handleWebsite = new View.OnClickListener() {

        /*******
         * Controls theme switch on each click
         */
        public void onClick(View current) {
            TextView btn = (TextView) current;

            if (btn.getId() == R.id.textViewWebsite) {
                btn.setTextColor(Color.parseColor("#FFBB00"));
                Uri uriUrl = Uri.parse("http://www.tonyebrown.com");
                btn.setTextColor(Color.parseColor("#A8DEF9"));
                Intent launchBrowser = new Intent(Intent.ACTION_VIEW, uriUrl);
                startActivity(launchBrowser);

                /*Intent browserIntent = new Intent(Intent.ACTION_VIEW, Uri.parse("http://www.tonyebrown.com"));
                startActivity(browserIntent);*/
            }
        }
    };


    @Override
    public void onBackPressed() {
        super.onBackPressed(); //go back to main activity
    }

    @Override
    protected void onPause() {
        super.onPause();
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        //setContentView(R.layout.activity_main);//open help activity
    }

    @Override
    protected void onStop() {
        super.onStop();
    }

    @Override
    protected void onResume() {
        super.onResume();
    }

    @Override
    protected void onRestart() {
        super.onRestart();
    }
}
