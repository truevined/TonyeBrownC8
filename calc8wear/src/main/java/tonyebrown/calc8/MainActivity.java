package tonyebrown.calc8;

import android.Manifest;
import android.annotation.TargetApi;
import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.pm.PackageManager;
import android.graphics.Color;
import android.os.Build;
import android.os.Handler;
import android.os.Vibrator;
import android.speech.RecognitionListener;
import android.speech.RecognizerIntent;
import android.speech.SpeechRecognizer;
import android.speech.tts.TextToSpeech;
import android.support.v4.app.ActivityCompat;
import android.support.v4.content.ContextCompat;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.ImageButton;
import android.widget.ProgressBar;
import android.widget.Toast;

import java.util.ArrayList;
import java.util.Locale;
import java.util.Stack;
import java.util.UUID;

public class MainActivity extends Activity implements RecognitionListener {
    private static final String LOG_TAG = "Main Activity";
    private static final int MY_PERMISSIONS_REQUEST_RECORD_AUDIO = 3;
    private ProgressBar progressBar;
    private SpeechRecognizer speech = null;
    private Intent recognizerIntent;

    Vibrator feedback;

    String speechResult = ""; //holds the string value of speech detected
    char current; //current character from string display
    boolean calculating = false;
    private boolean mIsListening;

    Stack<Float> MemoryFunction = new Stack<>(); //holds values user stores in memory

    private Handler adHandler;       // Handler to display the ad on the UI thread
    private Runnable displayAd;     // Code to execute to perform this operation

    TextToSpeech t1;
    Boolean shouldVibrate = true; // whether buttons should vibrate on press

    /**********************
     * overriden onCreate method initializing textviews, buttons, etc.
     *
     * @param savedInstanceState helps with preserving data on resume and on start
     */
    @Override
    protected void onCreate(Bundle savedInstanceState) {


        feedback = (Vibrator) getSystemService(Context.VIBRATOR_SERVICE); //vibrator service start

        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        //sets status bar and navigation bar color to #212121
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP) {
            getWindow().setNavigationBarColor(Color.parseColor("#212121"));
            getWindow().setStatusBarColor(Color.parseColor("#212121"));
        }

        findViewById(R.id.buttonEquals).setOnClickListener(handleButtons2);

        /*
         *     I'm using an onClickListener to control calculations, input and main calculator logic
         */

        //findViewById(R.id.buttonEquals).setOnClickListener(handleButtons); //equals button

        //progress bar
        progressBar = (ProgressBar) findViewById(R.id.progressBar1);
        progressBar.setVisibility(View.INVISIBLE);

        //get permission to use microphone
        // Here, thisActivity is the current activity
        if (ContextCompat.checkSelfPermission(this,
                Manifest.permission.RECORD_AUDIO)
                != PackageManager.PERMISSION_GRANTED) {

            // Should we show an explanation?
            if (ActivityCompat.shouldShowRequestPermissionRationale(this,
                    Manifest.permission.RECORD_AUDIO)) {

                // Show an expanation to the user *asynchronously* -- don't block
                // this thread waiting for the user's response! After the user
                // sees the explanation, try again to request the permission.

            } else {


                ActivityCompat.requestPermissions(this,
                        new String[]{Manifest.permission.RECORD_AUDIO},
                        MY_PERMISSIONS_REQUEST_RECORD_AUDIO);

            }
        }

        //text to speech stuff
        t1 = new TextToSpeech(getApplicationContext(), new TextToSpeech.OnInitListener() {
            @Override
            public void onInit(int status) {
                setTextToSpeechLanguage();
            }
        });

        //Check if the Application is Running for the First time
        SharedPreferences appIntro = getSharedPreferences("vibrate", 0);  //load the preferences
        //shouldVibrate = appIntro.getBoolean("shouldVibrate", false); //see if it's run before, default no

    }

    private boolean ready;

    @TargetApi(Build.VERSION_CODES.LOLLIPOP)
    private void setTextToSpeechLanguage() {

        Locale language = null;// = t1.getVoice().getLocale();

        // Check if we're running on Android 5.0 or higher
        //if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.KITKAT) {
        language = t1.getVoice().getLocale();

        if (language == null) {
            this.ready = false;
            Toast.makeText(this, "No language selected!", Toast.LENGTH_SHORT).show();
            return;
        }
        int result = t1.setLanguage(language);
        if (result == TextToSpeech.LANG_MISSING_DATA) {
            this.ready = false;
            Toast.makeText(this, "Missing language data; cannot speak result!", Toast.LENGTH_SHORT).show();
            return;
        } else if (result == TextToSpeech.LANG_NOT_SUPPORTED) {
            this.ready = false;
            Toast.makeText(this, "Your language is not supported!", Toast.LENGTH_SHORT).show();
            return;
        } else {
            this.ready = true;
            Locale currentLanguage = t1.getVoice().getLocale();
            //Toast.makeText(this, "Language " + currentLanguage, Toast.LENGTH_SHORT).show();
        }

        if (Build.VERSION.SDK_INT < Build.VERSION_CODES.LOLLIPOP) {

            if (speech != null)
                speech.destroy();//destroy speech listener


            Toast.makeText(MainActivity.this, "Speech Recognition is not available on this device! A newer " +
                    "version of Android is required!", Toast.LENGTH_LONG).show();
        }
    }


    @Override
    public void onRequestPermissionsResult(int requestCode,
                                           String permissions[], int[] grantResults) {
        switch (requestCode) {
            case MY_PERMISSIONS_REQUEST_RECORD_AUDIO: {
                // If request is cancelled, the result arrays are empty.
                if (grantResults.length > 0
                        && grantResults[0] == PackageManager.PERMISSION_GRANTED) {

                    Toast.makeText(MainActivity.this, "You\'re awesome!", Toast.LENGTH_SHORT).show();

                } else {
                    Toast.makeText(MainActivity.this, "Text to speech function disabled!", Toast.LENGTH_SHORT).show();
                }
                return;
            }

            // other 'case' lines to check for other
            // permissions this app might request
        }
    }

    @Override
    protected void onPause() {
        super.onPause();
        if (speech != null) {
            speech.destroy();
            Log.i(LOG_TAG, "destroy");
        }

        if (t1 != null) {
            t1.stop();
            t1.shutdown();
        }

    }

    @Override
    public void onBeginningOfSpeech() {
        Log.i(LOG_TAG, "onBeginningOfSpeech");
        Toast.makeText(MainActivity.this, "Say something to calculate!", Toast.LENGTH_SHORT).show();
        progressBar.setIndeterminate(false);
        progressBar.setMax(10);
        progressBar.setVisibility(View.VISIBLE);
    }

    @Override
    public void onBufferReceived(byte[] buffer) {
        Log.i(LOG_TAG, "onBufferReceived: " + buffer);
    }

    @Override
    public void onEndOfSpeech() {
        Log.i(LOG_TAG, "onEndOfSpeech");
        //progressBar.setIndeterminate(true);

        //***** MAYBE
        //***** I'm just testing here
        progressBar.setIndeterminate(false);
        progressBar.setVisibility(View.INVISIBLE);
    }

    protected long mSpeechRecognizerStartListeningTime = 0;

    protected synchronized void speechRecognizerStartListening(Intent intent) {
        if (speech != null) {
            this.mSpeechRecognizerStartListeningTime = System.currentTimeMillis();
            Log.d(LOG_TAG, "speechRecognizerStartListening");
            this.speech.startListening(intent);
        }
    }

    @Override
    public synchronized void onError(int error) {
        //Log.i(LOG_TAG, this.hashCode() + " - onError:" + error);

        boolean mSuccess = true;
        // Sometime onError will get called after onResults so we keep a boolean to ignore error also
        if (mSuccess) {
            Log.w(LOG_TAG, "Already success, ignoring error");

            if (error != 9)
                return;
        }

        long duration = System.currentTimeMillis() - mSpeechRecognizerStartListeningTime;
        if (duration < 500 && ((error == SpeechRecognizer.ERROR_NO_MATCH) || (error == SpeechRecognizer.ERROR_RECOGNIZER_BUSY))) {
            Log.w(LOG_TAG, "Doesn't seem like the system tried to listen at all. duration = " + duration + "ms. This might be a bug with onError and startListening methods of SpeechRecognizer");
            Log.w(LOG_TAG, "Going to ignore the error");

            if (error != 9)
                return;
        }

    /*
    // -- actual error handing code goes here.
    }

    @Override
    public void onError(int error) {
*/

        /*speech.cancel();*/


        String issue = null;
        if (error == 1) {
            issue = "ERROR_NETWORK_TIMEOUT";
        }
        if (error == 2) {
            issue = "ERROR_NETWORK";
        }
        if (error == 3) {
            issue = "ERROR_AUDIO";
        }
        if (error == 4) {
            issue = "ERROR_SERVER";
        }
        if (error == 5) {
            issue = "ERROR_CLIENT";
        }
        if (error == 6) {
            issue = "ERROR_SPEECH_TIMEOUT";
        }
        if (error == 7) {
            issue = "ERROR_NO_MATCH";
        }
        if (error == 8) {
            issue = "ERROR_RECOGNIZER_BUSY";
            speech.cancel();
            speech.startListening(recognizerIntent);
        }

        if (error == 9) {
            issue = "ERROR_INSUFFICIENT_PERMISSIONS";
        }
    }

    @Override
    public void onEvent(int arg0, Bundle arg1) {
        Log.i(LOG_TAG, "onEvent");
    }

    @Override
    public void onPartialResults(Bundle arg0) {
        Log.i(LOG_TAG, "onPartialResults");
        mIsListening = false;
    }

    @Override
    public void onReadyForSpeech(Bundle arg0) {
        Log.i(LOG_TAG, "onReadyForSpeech");
    }

    @Override
    public void onResults(Bundle results) {
        Log.i(LOG_TAG, "onResults");
        mIsListening = false;

        ArrayList<String> matches = results.getStringArrayList(SpeechRecognizer.RESULTS_RECOGNITION);
        String text = "";
        for (String result : matches)
            text += result + "\n";

        speechResult = matches.get(0);


        String speechCalculation = "";// = null;

        //speak results
        String finalToSpeak = speechResult + " is " + speechCalculation; //making the result more human

        if (finalToSpeak.contains("E"))
            finalToSpeak.replace("E", "raised to the power ");

        if (finalToSpeak.contains("-"))
            finalToSpeak.replace("-", "negative");

        if (speechCalculation.equals(""))
            finalToSpeak = "Please try again; I couldn\'t understand you!";

        // A random String (Unique ID).
        String utteranceId = UUID.randomUUID().toString();

        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP) {
            t1.speak(finalToSpeak, TextToSpeech.QUEUE_FLUSH, null, utteranceId);
        } else
            t1.speak(finalToSpeak, TextToSpeech.QUEUE_FLUSH, null);

    }

    @Override
    public void onRmsChanged(float rmsdB) {
        //Log.i(LOG_TAG, "onRmsChanged: " + rmsdB);
        progressBar.setProgress((int) rmsdB);
    }


    /******
     * onTouchListener handleButtons2 controls the button image "animation": A different image for when an image is pressed, and released
     * Used onTouchListener instead of XML to control button image changes based on my personal preference.
     * onTouchListener does not function in a way capable of accurately determining clicks for some functions (e.g shift toggle, etc.) in the Calculator,
     * so I'll use an onCLickListener to capture and process inputs.
     */
    private View.OnClickListener handleButtons2 = new View.OnClickListener() {
        public void onClick(View current) {
            ImageButton btn = (ImageButton) current;

            if ((btn.getId() == R.id.buttonEquals)) {
                if (shouldVibrate) {
                    feedback.vibrate(50);
                }
                calc8Muscles(btn);
            }
        }
    };

    /********
     * Controls what happens to each calculator button when it is pressed.
     * This methd is called from the handleButtons onClickListener's onClick method above. Soli Deo Gloria
     *
     * @param btn
     */
    private void calc8Muscles(ImageButton btn) {
        try {

        } //catch any exception that might occur when numbers are being entered
        catch (Exception e) {
            Log.d(LOG_TAG, "Calc8 muscles exception: " + e.toString());
        }
    }

    private View.OnLongClickListener handleSpeech = new View.OnLongClickListener() {
        @Override
        public boolean onLongClick(View v) {
            ImageButton btn = (ImageButton) v;


            speech = SpeechRecognizer.createSpeechRecognizer(MainActivity.this);
            speech.setRecognitionListener(MainActivity.this);


            recognizerIntent = new Intent(RecognizerIntent.ACTION_RECOGNIZE_SPEECH);
            recognizerIntent.putExtra(RecognizerIntent.EXTRA_LANGUAGE_PREFERENCE, "en");
            recognizerIntent.putExtra(RecognizerIntent.EXTRA_CALLING_PACKAGE, getApplication().getPackageName());
            recognizerIntent.putExtra(RecognizerIntent.EXTRA_LANGUAGE_MODEL, RecognizerIntent.LANGUAGE_MODEL_WEB_SEARCH);
            //recognizerIntent.putExtra(RecognizerIntent.EXTRA_SPEECH_INPUT_COMPLETE_SILENCE_LENGTH_MILLIS, new Long(1000));
            recognizerIntent.putExtra(RecognizerIntent.EXTRA_MAX_RESULTS, 3);

            //if (!mIsListening) {
            mIsListening = true;
            speech.startListening(recognizerIntent);
            //}

            Log.d(LOG_TAG, "Start listening to input");


            return false;
        }


        /********************
         * Notifies the user that an error has occurred by setting the opacity/alpha of activeF5 to 1
         * Receives error message as string. If string received is empty, no toast is own, else, string is toasted to user.
         *
         * @return boolean true
         */
        private boolean calculatorError(String toastErrorMessage) {
            if (toastErrorMessage != null && toastErrorMessage != "")
                Toast.makeText(MainActivity.this, toastErrorMessage, Toast.LENGTH_SHORT).show();//toast appropriate error message
            return true;
        }
    };
}
