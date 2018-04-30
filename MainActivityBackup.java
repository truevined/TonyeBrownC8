package com.example.tonyebrown.calc8;

import android.app.ActionBar;
import android.content.Context;
import android.graphics.Typeface;
import android.os.Vibrator;
import android.support.annotation.StringRes;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.text.method.ScrollingMovementMethod;
import android.view.Menu;
import android.view.MenuItem;
import android.view.MotionEvent;
import android.view.View;
import android.view.Window;
import android.widget.Button;
import android.widget.ImageButton;
import android.widget.TextView;

public class MainActivity extends AppCompatActivity {

    TextView display, hDisplay, activeF1, activeF2, activeF3, activeF4, activeF5;

    Vibrator feedback;

    int start = 0;
    int shiftListener = 0;
    int degListener = 0;
    int radListener = 0;
    String history = "";
    String temp = "";

    int decimalPointCount = 0;
    int zeroCount = 0;
    int addCount = 0;
    int subtractCount = 0;
    int divideCount = 0;
    int multiplyCount = 0;

    float number1;
    float number2;
    String result;

    int isFirst = 0;
    int isSecond = 0;

    //  Buttons for basic calculations
    Button buttonZero, buttonOne, buttonTwo, buttonThree, buttonFour, buttonFive, buttonSix, buttonSeven, buttonEight, buttonNine;
    Button buttonDecimalPoint, buttonPlusMinus, buttonMultiply, buttonDivide, buttonSubtract, buttonAdd;
    Button buttonXquared, buttonCE, buttonHelp, buttonEquals;

    //  Buttons for scientific calculations
    Button buttonShift, buttonDeg, buttonRad, buttonPi, buttonRand, buttonSin, buttonCos, buttonTan;
    Button buttonLog, buttonMPlus, buttonYPowerX, buttonSquareRoot, buttonLeftBracket, buttonRightBracket, buttonMR;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        feedback = (Vibrator) getSystemService(Context.VIBRATOR_SERVICE); //vibrator service start

        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        // get all views to display stuff on
        display = (TextView)findViewById(R.id.textViewLCD);
        hDisplay = (TextView)findViewById(R.id.textViewHistory);
        activeF1 = (TextView)findViewById(R.id.textViewActiveFn1);
        activeF2 = (TextView)findViewById(R.id.textViewActiveFn2);
        activeF3 = (TextView)findViewById(R.id.textViewActiveFn3);
        activeF4 = (TextView)findViewById(R.id.textViewActiveFn4);
        activeF5 = (TextView)findViewById(R.id.textViewActiveFn5);

        // set font for all displays
        Typeface font = Typeface.createFromAsset(getAssets(), "fonts/quartzbold.ttf");
        display.setTypeface(font);
        hDisplay.setTypeface(font);
        activeF1.setTypeface(font);
        activeF2.setTypeface(font);
        activeF3.setTypeface(font);
        activeF4.setTypeface(font);
        activeF5.setTypeface(font);

        /*
         *     I'm using an onTouchListener to control button image changes
         */

        //numbers
        findViewById(R.id.buttonOne).setOnTouchListener(handleButtons2);
        findViewById(R.id.buttonTwo).setOnTouchListener(handleButtons2);
        findViewById(R.id.buttonThree).setOnTouchListener(handleButtons2);
        findViewById(R.id.buttonFour).setOnTouchListener(handleButtons2);
        findViewById(R.id.buttonFive).setOnTouchListener(handleButtons2);
        findViewById(R.id.buttonSix).setOnTouchListener(handleButtons2);
        findViewById(R.id.buttonSeven).setOnTouchListener(handleButtons2);
        findViewById(R.id.buttonEight).setOnTouchListener(handleButtons2);
        findViewById(R.id.buttonNine).setOnTouchListener(handleButtons2);
        findViewById(R.id.buttonZero).setOnTouchListener(handleButtons2);
        findViewById(R.id.buttonDecimalPoint).setOnTouchListener(handleButtons2);
        findViewById(R.id.buttonPlusMinus).setOnTouchListener(handleButtons2);

        //basicOperations
        findViewById(R.id.buttonMultiply).setOnTouchListener(handleButtons2);
        findViewById(R.id.buttonDivide).setOnTouchListener(handleButtons2);
        findViewById(R.id.buttonSubtraction).setOnTouchListener(handleButtons2);
        findViewById(R.id.buttonAddition).setOnTouchListener(handleButtons2);
        findViewById(R.id.buttonEquals).setOnTouchListener(handleButtons2);

        //resets
        findViewById(R.id.buttonCLR).setOnTouchListener(handleButtons2);
        findViewById(R.id.buttonDel).setOnTouchListener(handleButtons2);

        //scifi operations (lol)
        findViewById(R.id.buttonShift).setOnTouchListener(handleButtons2);
        findViewById(R.id.buttonDeg).setOnTouchListener(handleButtons2);
        findViewById(R.id.buttonRad).setOnTouchListener(handleButtons2);
        findViewById(R.id.buttonPi).setOnTouchListener(handleButtons2);
        findViewById(R.id.buttonRand).setOnTouchListener(handleButtons2);
        findViewById(R.id.buttonSin).setOnTouchListener(handleButtons2);
        findViewById(R.id.buttonCos).setOnTouchListener(handleButtons2);
        findViewById(R.id.buttonTan).setOnTouchListener(handleButtons2);
        findViewById(R.id.buttonLog).setOnTouchListener(handleButtons2);
        findViewById(R.id.buttonMPlus).setOnTouchListener(handleButtons2);
        findViewById(R.id.buttonXSquared).setOnTouchListener(handleButtons2);
        findViewById(R.id.buttonSquareRoot).setOnTouchListener(handleButtons2);
        findViewById(R.id.buttonLeftBracket).setOnTouchListener(handleButtons2);
        findViewById(R.id.buttonRightBracket).setOnTouchListener(handleButtons2);
        findViewById(R.id.buttonMR).setOnTouchListener(handleButtons2);

        /*
         *     I'm using an onClickListener to control calculations, input and more
         */

        //numbers
        findViewById(R.id.buttonOne).setOnClickListener(handleButtons);
        findViewById(R.id.buttonTwo).setOnClickListener(handleButtons);
        findViewById(R.id.buttonThree).setOnClickListener(handleButtons);
        findViewById(R.id.buttonFour).setOnClickListener(handleButtons);
        findViewById(R.id.buttonFive).setOnClickListener(handleButtons);
        findViewById(R.id.buttonSix).setOnClickListener(handleButtons);
        findViewById(R.id.buttonSeven).setOnClickListener(handleButtons);
        findViewById(R.id.buttonEight).setOnClickListener(handleButtons);
        findViewById(R.id.buttonNine).setOnClickListener(handleButtons);
        findViewById(R.id.buttonZero).setOnClickListener(handleButtons);
        findViewById(R.id.buttonDecimalPoint).setOnClickListener(handleButtons);
        findViewById(R.id.buttonPlusMinus).setOnClickListener(handleButtons);

        //basicOperations
        findViewById(R.id.buttonMultiply).setOnClickListener(handleButtons);
        findViewById(R.id.buttonDivide).setOnClickListener(handleButtons);
        findViewById(R.id.buttonSubtraction).setOnClickListener(handleButtons);
        findViewById(R.id.buttonAddition).setOnClickListener(handleButtons);
        findViewById(R.id.buttonEquals).setOnClickListener(handleButtons);

        //resets
        findViewById(R.id.buttonCLR).setOnClickListener(handleButtons);
        findViewById(R.id.buttonDel).setOnClickListener(handleButtons);

        //scifi operations (lol)
        findViewById(R.id.buttonShift).setOnClickListener(handleButtons);
        findViewById(R.id.buttonDeg).setOnClickListener(handleButtons);
        findViewById(R.id.buttonRad).setOnClickListener(handleButtons);
        findViewById(R.id.buttonPi).setOnClickListener(handleButtons);
        findViewById(R.id.buttonRand).setOnClickListener(handleButtons);
        findViewById(R.id.buttonSin).setOnClickListener(handleButtons);
        findViewById(R.id.buttonCos).setOnClickListener(handleButtons);
        findViewById(R.id.buttonTan).setOnClickListener(handleButtons);
        findViewById(R.id.buttonLog).setOnClickListener(handleButtons);
        findViewById(R.id.buttonMPlus).setOnClickListener(handleButtons);
        findViewById(R.id.buttonXSquared).setOnClickListener(handleButtons);
        findViewById(R.id.buttonSquareRoot).setOnClickListener(handleButtons);
        findViewById(R.id.buttonLeftBracket).setOnClickListener(handleButtons);
        findViewById(R.id.buttonRightBracket).setOnClickListener(handleButtons);
        findViewById(R.id.buttonMR).setOnClickListener(handleButtons);

        //results.setMovementMethod(new ScrollingMovementMethod());
    }

    /*
     * onTouchListener handleButtons2 controls the button image "animation": A different image for when an image is pressed, and released
     * Used onTouchListener instead of XML to control button image changes based on my personal preference
     * onTouchListener is not accurate enough for determining clicks for a Calculator, so I'll use an onCLickListener to capture and process inputs
     */
    private View.OnTouchListener handleButtons2 = new View.OnTouchListener() {
        public boolean onTouch(View current, MotionEvent event) {
            ImageButton btn = (ImageButton)current;

            //Determine if button pressed is SHIFT button
            if (btn.getId() == R.id.buttonShift) {
                //button pressed and released animation
                if (event.getAction() == MotionEvent.ACTION_DOWN) {
                    btn.setBackgroundResource(R.drawable.calc8lightmockuppressed_35);
                } else if (event.getAction() == MotionEvent.ACTION_UP) {
                    btn.setBackgroundResource(R.drawable.calc8lightmockup_35);
                }
            }

            //Determine if button pressed is DEGREE button
            if (btn.getId() == R.id.buttonDeg) {
                if (event.getAction() == MotionEvent.ACTION_DOWN) {
                    btn.setBackgroundResource(R.drawable.calc8lightmockuppressed_37);
                } else if (event.getAction() == MotionEvent.ACTION_UP) {
                    btn.setBackgroundResource(R.drawable.calc8lightmockup_37);
                }
            }

            //Determine if button pressed is RADIAN button
            if (btn.getId() == R.id.buttonRad) {
                //button pressed and released animation
                if (event.getAction() == MotionEvent.ACTION_DOWN) {
                    btn.setBackgroundResource(R.drawable.calc8lightmockuppressed_39);
                } else if (event.getAction() == MotionEvent.ACTION_UP) {
                    btn.setBackgroundResource(R.drawable.calc8lightmockup_39);
                }
            }

            //Determine if button pressed is PI button
            if (btn.getId() == R.id.buttonPi) {
                //button pressed and released animation
                if (event.getAction() == MotionEvent.ACTION_DOWN) {
                    btn.setBackgroundResource(R.drawable.calc8lightmockuppressed_41);
                } else if (event.getAction() == MotionEvent.ACTION_UP) {
                    btn.setBackgroundResource(R.drawable.calc8lightmockup_41);
                }
            }

            //Determine if button pressed is RANDOM button
            if (btn.getId() == R.id.buttonRand) {
                //button pressed and released animation
                if (event.getAction() == MotionEvent.ACTION_DOWN) {
                    btn.setBackgroundResource(R.drawable.calc8lightmockuppressed_43);
                } else if (event.getAction() == MotionEvent.ACTION_UP) {
                    btn.setBackgroundResource(R.drawable.calc8lightmockup_43);
                }
            }

            //Determine if button pressed is SIN button
            if (btn.getId() == R.id.buttonSin) {
                //button pressed and released animation
                if (event.getAction() == MotionEvent.ACTION_DOWN) {
                    btn.setBackgroundResource(R.drawable.calc8lightmockuppressed_57);
                } else if (event.getAction() == MotionEvent.ACTION_UP) {
                    btn.setBackgroundResource(R.drawable.calc8lightmockup_57);
                }
            }

            //Determine if button pressed is COS button
            if (btn.getId() == R.id.buttonCos) {
                //button pressed and released animation
                if (event.getAction() == MotionEvent.ACTION_DOWN) {
                    btn.setBackgroundResource(R.drawable.calc8lightmockuppressed_59);
                } else if (event.getAction() == MotionEvent.ACTION_UP) {
                    btn.setBackgroundResource(R.drawable.calc8lightmockup_59);
                }
            }

            //Determine if button pressed is TAN button
            if (btn.getId() == R.id.buttonTan) {
                //button pressed and released animation
                if (event.getAction() == MotionEvent.ACTION_DOWN) {
                    btn.setBackgroundResource(R.drawable.calc8lightmockuppressed_61);
                } else if (event.getAction() == MotionEvent.ACTION_UP) {
                    btn.setBackgroundResource(R.drawable.calc8lightmockup_61);
                }
            }

            //Determine if button pressed is LOG button
            if (btn.getId() == R.id.buttonLog) {
                //button pressed and released animation
                if (event.getAction() == MotionEvent.ACTION_DOWN) {
                    btn.setBackgroundResource(R.drawable.calc8lightmockuppressed_63);
                } else if (event.getAction() == MotionEvent.ACTION_UP) {
                    btn.setBackgroundResource(R.drawable.calc8lightmockup_63);
                }
            }

            //Determine if button pressed is MemoryPLUS button
            if (btn.getId() == R.id.buttonMPlus) {
                //button pressed and released animation
                if (event.getAction() == MotionEvent.ACTION_DOWN) {
                    btn.setBackgroundResource(R.drawable.calc8lightmockuppressed_65);
                } else if (event.getAction() == MotionEvent.ACTION_UP) {
                    btn.setBackgroundResource(R.drawable.calc8lightmockup_65);
                }
            }

            //Determine if button pressed is X SQUARED button
            if (btn.getId() == R.id.buttonXSquared) {
                //button pressed and released animation
                if (event.getAction() == MotionEvent.ACTION_DOWN) {
                    btn.setBackgroundResource(R.drawable.calc8lightmockuppressed_79);
                } else if (event.getAction() == MotionEvent.ACTION_UP) {
                    btn.setBackgroundResource(R.drawable.calc8lightmockup_79);
                }
            }

            //Determine if button pressed is Square root button
            if (btn.getId() == R.id.buttonSquareRoot) {
                //button pressed and released animation
                if (event.getAction() == MotionEvent.ACTION_DOWN) {
                    btn.setBackgroundResource(R.drawable.calc8lightmockuppressed_81);
                } else if (event.getAction() == MotionEvent.ACTION_UP) {
                    btn.setBackgroundResource(R.drawable.calc8lightmockup_81);
                }
            }

            //Determine if button pressed is left bracket button
            if (btn.getId() == R.id.buttonLeftBracket) {
                //button pressed and released animation
                if (event.getAction() == MotionEvent.ACTION_DOWN) {
                    btn.setBackgroundResource(R.drawable.calc8lightmockuppressed_83);
                } else if (event.getAction() == MotionEvent.ACTION_UP) {
                    btn.setBackgroundResource(R.drawable.calc8lightmockup_83);
                }
            }

            //Determine if button pressed is right bracket button
            if (btn.getId() == R.id.buttonRightBracket) {
                //button pressed and released animation
                if (event.getAction() == MotionEvent.ACTION_DOWN) {
                    btn.setBackgroundResource(R.drawable.calc8lightmockuppressed_85);
                } else if (event.getAction() == MotionEvent.ACTION_UP) {
                    btn.setBackgroundResource(R.drawable.calc8lightmockup_85);
                }
            }

            //Determine if button pressed is Memory R button
            if (btn.getId() == R.id.buttonMR) {
                //button pressed and released animation
                if (event.getAction() == MotionEvent.ACTION_DOWN) {
                    btn.setBackgroundResource(R.drawable.calc8lightmockuppressed_87);
                } else if (event.getAction() == MotionEvent.ACTION_UP) {
                    btn.setBackgroundResource(R.drawable.calc8lightmockup_87);
                }
            }

            //Determine if button pressed is multiply button
            if (btn.getId() == R.id.buttonMultiply) {
                //button pressed and released animation
                if (event.getAction() == MotionEvent.ACTION_DOWN) {
                    btn.setBackgroundResource(R.drawable.calc8lightmockuppressed_112);
                } else if (event.getAction() == MotionEvent.ACTION_UP) {
                    btn.setBackgroundResource(R.drawable.calc8lightmockup_112);
                }
            }

            //Determine if button pressed is seven button
            if (btn.getId() == R.id.buttonSeven) {
                //button pressed and released animation
                if (event.getAction() == MotionEvent.ACTION_DOWN) {
                    btn.setBackgroundResource(R.drawable.calc8lightmockuppressed_114);
                } else if (event.getAction() == MotionEvent.ACTION_UP) {
                    btn.setBackgroundResource(R.drawable.calc8lightmockup_114);
                }
            }

            //Determine if button pressed is eight button
            if (btn.getId() == R.id.buttonEight) {
                //button pressed and released animation
                if (event.getAction() == MotionEvent.ACTION_DOWN) {
                    btn.setBackgroundResource(R.drawable.calc8lightmockuppressed_116);
                } else if (event.getAction() == MotionEvent.ACTION_UP) {
                    btn.setBackgroundResource(R.drawable.calc8lightmockup_116);
                }
            }

            //Determine if button pressed is nine button
            if (btn.getId() == R.id.buttonNine) {
                //button pressed and released animation
                if (event.getAction() == MotionEvent.ACTION_DOWN) {
                    btn.setBackgroundResource(R.drawable.calc8lightmockuppressed_118);
                } else if (event.getAction() == MotionEvent.ACTION_UP) {
                    btn.setBackgroundResource(R.drawable.calc8lightmockup_118);
                }
            }

            //Determine if button pressed is CLR button
            if (btn.getId() == R.id.buttonCLR) {
                //button pressed and released animation
                if (event.getAction() == MotionEvent.ACTION_DOWN) {
                    btn.setBackgroundResource(R.drawable.calc8lightmockuppressed_120);
                } else if (event.getAction() == MotionEvent.ACTION_UP) {
                    btn.setBackgroundResource(R.drawable.calc8lightmockup_120);
                }
            }

            //Determine if button pressed is division button
            if (btn.getId() == R.id.buttonDivide) {
                //button pressed and released animation
                if (event.getAction() == MotionEvent.ACTION_DOWN) {
                    btn.setBackgroundResource(R.drawable.calc8lightmockuppressed_134);
                } else if (event.getAction() == MotionEvent.ACTION_UP) {
                    btn.setBackgroundResource(R.drawable.calc8lightmockup_134);
                }
            }

            //Determine if button pressed is division button
            if (btn.getId() == R.id.buttonDivide) {
                //button pressed and released animation
                if (event.getAction() == MotionEvent.ACTION_DOWN) {
                    btn.setBackgroundResource(R.drawable.calc8lightmockuppressed_134);
                } else if (event.getAction() == MotionEvent.ACTION_UP) {
                    btn.setBackgroundResource(R.drawable.calc8lightmockup_134);
                }
            }

            //Determine if button pressed is FOUR button
            if (btn.getId() == R.id.buttonFour) {
                //button pressed and released animation
                if (event.getAction() == MotionEvent.ACTION_DOWN) {
                    btn.setBackgroundResource(R.drawable.calc8lightmockuppressed_136);
                } else if (event.getAction() == MotionEvent.ACTION_UP) {
                    btn.setBackgroundResource(R.drawable.calc8lightmockup_136);
                }
            }

            //Determine if button pressed is FIVE button
            if (btn.getId() == R.id.buttonFive) {
                //button pressed and released animation
                if (event.getAction() == MotionEvent.ACTION_DOWN) {
                    btn.setBackgroundResource(R.drawable.calc8lightmockuppressed_138);
                } else if (event.getAction() == MotionEvent.ACTION_UP) {
                    btn.setBackgroundResource(R.drawable.calc8lightmockup_138);
                }
            }

            //Determine if button pressed is six button
            if (btn.getId() == R.id.buttonSix) {
                //button pressed and released animation
                if (event.getAction() == MotionEvent.ACTION_DOWN) {
                    btn.setBackgroundResource(R.drawable.calc8lightmockuppressed_140);
                } else if (event.getAction() == MotionEvent.ACTION_UP) {
                    btn.setBackgroundResource(R.drawable.calc8lightmockup_140);
                }
            }

            //Determine if button pressed is delete button
            if (btn.getId() == R.id.buttonDel) {
                //button pressed and released animation
                if (event.getAction() == MotionEvent.ACTION_DOWN) {
                    btn.setBackgroundResource(R.drawable.calc8lightmockuppressed_142);
                } else if (event.getAction() == MotionEvent.ACTION_UP) {
                    btn.setBackgroundResource(R.drawable.calc8lightmockup_142);
                }
            }

            //Determine if button pressed is subtract button
            if (btn.getId() == R.id.buttonSubtraction) {
                //button pressed and released animation
                if (event.getAction() == MotionEvent.ACTION_DOWN) {
                    btn.setBackgroundResource(R.drawable.calc8lightmockuppressed_156);
                } else if (event.getAction() == MotionEvent.ACTION_UP) {
                    btn.setBackgroundResource(R.drawable.calc8lightmockup_156);
                }
            }

            //Determine if button pressed is ONE button
            if (btn.getId() == R.id.buttonOne) {
                //button pressed and released animation
                if (event.getAction() == MotionEvent.ACTION_DOWN) {
                    btn.setBackgroundResource(R.drawable.calc8lightmockuppressed_158);
                } else if (event.getAction() == MotionEvent.ACTION_UP) {
                    btn.setBackgroundResource(R.drawable.calc8lightmockup_158);
                }
            }

            //Determine if button pressed is TWO button
            if (btn.getId() == R.id.buttonTwo) {
                //button pressed and released animation
                if (event.getAction() == MotionEvent.ACTION_DOWN) {
                    btn.setBackgroundResource(R.drawable.calc8lightmockuppressed_160);
                } else if (event.getAction() == MotionEvent.ACTION_UP) {
                    btn.setBackgroundResource(R.drawable.calc8lightmockup_160);
                }
            }

            //Determine if button pressed is THREE button
            if (btn.getId() == R.id.buttonThree) {
                //button pressed and released animation
                if (event.getAction() == MotionEvent.ACTION_DOWN) {
                    btn.setBackgroundResource(R.drawable.calc8lightmockuppressed_162);
                } else if (event.getAction() == MotionEvent.ACTION_UP) {
                    btn.setBackgroundResource(R.drawable.calc8lightmockup_162);
                }
            }

            //Determine if button pressed is Addition button
            if (btn.getId() == R.id.buttonAddition) {
                //button pressed and released animation
                if (event.getAction() == MotionEvent.ACTION_DOWN) {
                    btn.setBackgroundResource(R.drawable.calc8lightmockuppressed_177);
                } else if (event.getAction() == MotionEvent.ACTION_UP) {
                    btn.setBackgroundResource(R.drawable.calc8lightmockup_177);
                }
            }

            //Determine if button pressed is Decimal point button
            if (btn.getId() == R.id.buttonDecimalPoint) {
                //button pressed and released animation
                if (event.getAction() == MotionEvent.ACTION_DOWN) {
                    btn.setBackgroundResource(R.drawable.calc8lightmockuppressed_179);
                } else if (event.getAction() == MotionEvent.ACTION_UP) {
                    btn.setBackgroundResource(R.drawable.calc8lightmockup_179);
                }
            }

            //Determine if button pressed is Zero button
            if (btn.getId() == R.id.buttonZero) {
                //button pressed and released animation
                if (event.getAction() == MotionEvent.ACTION_DOWN) {
                    btn.setBackgroundResource(R.drawable.calc8lightmockuppressed_181);
                } else if (event.getAction() == MotionEvent.ACTION_UP) {
                    btn.setBackgroundResource(R.drawable.calc8lightmockup_181);
                }
            }

            //Determine if button pressed is PLUS MINUS button
            if (btn.getId() == R.id.buttonPlusMinus) {
                //button pressed and released animation
                if (event.getAction() == MotionEvent.ACTION_DOWN) {
                    btn.setBackgroundResource(R.drawable.calc8lightmockuppressed_183);
                } else if (event.getAction() == MotionEvent.ACTION_UP) {
                    btn.setBackgroundResource(R.drawable.calc8lightmockup_183);
                }
            }

            //Determine if button pressed is EQUALS button
            if (btn.getId() == R.id.buttonEquals) {
                //button pressed and released animation
                if (event.getAction() == MotionEvent.ACTION_DOWN) {
                    btn.setBackgroundResource(R.drawable.calc8lightmockuppressed_164);
                } else if (event.getAction() == MotionEvent.ACTION_UP) {
                    btn.setBackgroundResource(R.drawable.calc8lightmockup_164);
                }
            }

            return false;
        }
    };

    /*
     * OnClickListener captures inputs from all 34 buttons and processes each button's function
     */
    private View.OnClickListener handleButtons = new View.OnClickListener(){
        public void onClick(View current) {
            ImageButton btn = (ImageButton)current;
            //results.setText(btn.getResources().getResourceEntryName(btn.getId()));
            //display.append(btn.getContentDescription());

            long[] pattern = { 0, 2000, 0 }; //0 to start now, 200 to vibrate 200 ms, 0 to sleep for 0 ms.

            //Determine if button pressed is SHIFT button
            if (btn.getId() == R.id.buttonShift) {
                shiftListener++; //increase shiftListener to 1 - indicates shift has been pressed
                activeF1.setAlpha(1);
                feedback.vibrate(pattern, 0);

                if (shiftListener > 1) { //if shiftListener is greater than 1, assume user wants to disable shift
                    activeF1.setAlpha((float) 0.2); //set opacity of shift display to 0.2
                    shiftListener = 0;
                }
            }

            //Determine if button pressed is DEGREE button
            if (btn.getId() == R.id.buttonDeg) {
                degListener++; //increase shiftListener to 1 - indicates shift has been pressed
                activeF2.setAlpha(1);

                if (degListener > 1) { //if shiftListener is greater than 1, assume user wants to disable shift
                    activeF2.setAlpha((float) 0.2); //set opacity of shift display to 0.2
                    degListener = 0;
                }
            }

            //Determine if button pressed is RADIAN button
            if (btn.getId() == R.id.buttonRad) {
                radListener++; //increase shiftListener to 1 - indicates shift has been pressed
                activeF3.setAlpha(1);

                if (radListener > 1) { //if shiftListener is greater than 1, assume user wants to disable shift
                    activeF3.setAlpha((float) 0.2); //set opacity of shift display to 0.2
                    radListener = 0;
                }
            }

            //Determine if button pressed is PI button
            if (btn.getId() == R.id.buttonPi) {
                clr();
                display.append(String.valueOf(Math.PI));

            }

            //Determine if button pressed is RANDOM button
            if (btn.getId() == R.id.buttonRand) {
                clr();
                String randomString;
                double random = Math.random();
                randomString = String.valueOf(random);
                display.append(randomString);
            }

            //Determine if button pressed is SIN button
            if (btn.getId() == R.id.buttonSin) {
                display.append(btn.getContentDescription());

            }

            //Determine if button pressed is COS button
            if (btn.getId() == R.id.buttonCos) {
                display.append(btn.getContentDescription());

            }

            //Determine if button pressed is TAN button
            if (btn.getId() == R.id.buttonTan) {
                display.append(btn.getContentDescription());

            }

            //Determine if button pressed is LOG button
            if (btn.getId() == R.id.buttonLog) {
                display.append(btn.getContentDescription());

            }

            //Determine if button pressed is MemoryPLUS button
            if (btn.getId() == R.id.buttonMPlus) {
                display.append(btn.getContentDescription());

            }

            //Determine if button pressed is X SQUARED button
            if (btn.getId() == R.id.buttonXSquared) {
                display.append(btn.getContentDescription());

            }

            //Determine if button pressed is Square root button
            if (btn.getId() == R.id.buttonSquareRoot) {
                display.append("√");

            }

            //Determine if button pressed is left bracket button
            if (btn.getId() == R.id.buttonLeftBracket) {
                display.append(btn.getContentDescription());

            }

            //Determine if button pressed is right bracket button
            if (btn.getId() == R.id.buttonRightBracket) {
                display.append(btn.getContentDescription());

            }

            //Determine if button pressed is Memory R button
            if (btn.getId() == R.id.buttonMR) {
                display.append(btn.getContentDescription());

            }

            //Determine if button pressed is multiply button
            if (btn.getId() == R.id.buttonMultiply) {
                if (start == 0) {
                    display.append("");
                }
                else {
                    display.append("×");
                }
            }

            //Determine if button pressed is seven button
            if (btn.getId() == R.id.buttonSeven) {
                display.append(btn.getContentDescription());

            }

            //Determine if button pressed is eight button
            if (btn.getId() == R.id.buttonEight) {
                display.append(btn.getContentDescription());

            }

            //Determine if button pressed is nine button
            if (btn.getId() == R.id.buttonNine) {
                display.append(btn.getContentDescription());

            }

            //Determine if button pressed is CLR button
            if (btn.getId() == R.id.buttonCLR) {
                clr();
            }

            //Determine if button pressed is division button
            if (btn.getId() == R.id.buttonDivide) {
                if (start == 0) {
                    display.append("");
                }
                else {
                    display.append("÷");
                }
            }

            //Determine if button pressed is FOUR button
            if (btn.getId() == R.id.buttonFour) {
                display.append(btn.getContentDescription());

            }

            //Determine if button pressed is FIVE button
            if (btn.getId() == R.id.buttonFive) {
                display.append(btn.getContentDescription());

            }

            //Determine if button pressed is six button
            if (btn.getId() == R.id.buttonSix) {
                display.append(btn.getContentDescription());

            }

            //Determine if button pressed is delete button
            if (btn.getId() == R.id.buttonDel) {
                display.append(btn.getContentDescription());

            }

            //Determine if button pressed is subtract button
            if (btn.getId() == R.id.buttonSubtraction) {
                if (start == 0) {
                    display.append("");
                }
                else {
                    display.append("-");
                }
            }

            //Determine if button pressed is ONE button
            if (btn.getId() == R.id.buttonOne) {
                display.append(btn.getContentDescription());
                start();
            }

            //Determine if button pressed is TWO button
            if (btn.getId() == R.id.buttonTwo) {
                display.append(btn.getContentDescription());
                start();
            }

            //Determine if button pressed is THREE button
            if (btn.getId() == R.id.buttonThree) {
                display.append(btn.getContentDescription());
                start();
            }

            //Determine if button pressed is Addition button
            if (btn.getId() == R.id.buttonAddition) {
                if (start == 0) {
                    display.append("");
                }
                else {
                    display.append("+");
                }
            }

            //Determine if button pressed is Decimal point button
            if (btn.getId() == R.id.buttonDecimalPoint) {
                if (start == 0) {
                    display.append("0.");
                    start++;
                    decimalPointCount++;
                }

                if (start > 0) {
                    if (decimalPointCount == 0)
                        display.append(".");

                    else {
                        display.append("");
                        decimalPointCount++;
                    }

                }
            }

            //Determine if button pressed is Zero button
            if (btn.getId() == R.id.buttonZero) {
                if (start == 0) {
                    display.append("");
                    //start++;
                    zeroCount++;
                }
                else {
                    display.append("0");
                }
            }

            //Determine if button pressed is PLUS MINUS button
            if (btn.getId() == R.id.buttonPlusMinus) {
                display.append("±");

            }

            //Determine if button pressed is EQUALS button
            if (btn.getId() == R.id.buttonEquals) {
                display.append(btn.getContentDescription());

            }

            /*
             * Calculator logic
             */

            //if basic operator is pressed
            if ((btn.getId() == R.id.buttonAddition) || (btn.getId() == R.id.buttonSubtraction)
                    || (btn.getId() == R.id.buttonMultiply) || (btn.getId() == R.id.buttonDivide)) {

            }

            //if equals button is pressed
            if (btn.getId() == R.id.buttonEquals)
                if (start == 0)//if calculation has not started, display 0
                    display.setText("");
                else {
                    display.setText(String.valueOf(result));
                    start();//get ready for new calculation
                }
        }
    };

    /*final Button button = (Button) findViewById(R.id.buttonOne);
    button.setOnClickListener(new View.OnClickListener() {
        public void onClick(View v) {
            // Perform action on click
        }
    });*/


    /*  CLR Button
        Resets the screen to zero and resets important variables to 0
     */
    public void clr() {
        display.setText("");
        display.setHint("0");
        start = 0;
        history = "";
        result = "0";
        number1 = 0;
        number2 = 0;
    }

    public void start() {
        start = 0;
    }


    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.menu_main, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        // Handle action bar item clicks here. The action bar will
        // automatically handle clicks on the Home/Up button, so long
        // as you specify a parent activity in AndroidManifest.xml.
        int id = item.getItemId();

        //noinspection SimplifiableIfStatement
        if (id == R.id.action_settings) {
            return true;
        }

        return super.onOptionsItemSelected(item);
    }
}
