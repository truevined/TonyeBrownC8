package com.example.tonyebrown.calc8;

import android.content.Context;
import android.graphics.Typeface;
import android.os.Vibrator;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.text.InputFilter;
import android.view.Menu;
import android.view.MenuItem;
import android.view.MotionEvent;
import android.view.View;
import android.widget.ImageButton;
import android.widget.TextView;

public class MainActivity extends AppCompatActivity {

    TextView display, hDisplay, activeF1, activeF2, activeF3, activeF4, activeF5;

    Vibrator feedback;

    //toggles
    int start = 0;//checks if calculation has started
    int shiftListener = 0; //helps toggle shift mode, on and off
    int degListener = 0; //helps toggle degree mode, on and off
    int radListener = 0; //helps toggle radian mode, on and off
    String history = ""; //holds the data that will go in history textview

    int decimalPointCount = 0;
    int zeroCount = 0;
    int addCount = 0;
    int subtractCount = 0;
    int divideCount = 0;
    int multiplyCount = 0;

    float number1; //holds first number
    float number2; //holds second number
    String backup; //temporal backup of textview-display's contents
    String result =""; //holds the result
    String lResult = "0";//holds local result for theAnswerIs()

    int isFirst = 0;
    int isSecond = 0;

    int position; //position of current vl
    char current; //current character from string display

    char operator;//tells if current character is an operator or not

    boolean calculating = false;


    /**********************
     * overriden onCreate method initializing textviews, buttons, etc.
     * @param savedInstanceState
     */
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

        //dynamically control the maxLength of mainLCD and history textviews
        InputFilter[] filterArray = new InputFilter[1];
        filterArray[0] = new InputFilter.LengthFilter(16);
        display.setFilters(filterArray);
        hDisplay.setFilters(filterArray);

        /*
         *     I'm using an onTouchListener to control button image state changes - different image
         *     for when an image is being held down, and for when it's released or not being pressed
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
         *     I'm using an onClickListener to control calculations, input and main calculator logic
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
    }

    /*
     * onTouchListener handleButtons2 controls the button image "animation": A different image for when an image is pressed, and released
     * Used onTouchListener instead of XML to control button image changes based on my personal preference.
     * onTouchListener does not function in a way capable of accurately determining clicks for some functions (e.g shift toggle, etc.) in the Calculator,
     * so I'll use an onCLickListener to capture and process inputs.
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

    /************
     * OnClickListener captures inputs from all 34 buttons and processes each button's function
     * @return none - Anonymous function
     */
    private View.OnClickListener handleButtons = new View.OnClickListener(){

        /*******
         * Controls calculator functions on each click
         * @param current
         * @return void
         */
        public void onClick(View current) {
            ImageButton btn = (ImageButton)current;

            long[] pattern = {0, 2000, 0 }; //0 to start now, 200 to vibrate 200 ms, 0 to sleep for 0 ms.
            feedback.vibrate(pattern, 0);

            //Determine if button pressed is SHIFT button
            if (btn.getId() == R.id.buttonShift) {
                shiftListener++; //increase shiftListener to 1 - indicates shift has been pressed
                activeF1.setAlpha(1);

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
                start();
            }

            //Determine if button pressed is RANDOM button
            if (btn.getId() == R.id.buttonRand) {
                clr();
                String randomString;
                int random = (int)(Math.random() * 9999999) + 1;//generate an int random number from 1 to 9999999
                randomString = String.valueOf(random);
                display.append(randomString);
                start();
            }

            //Determine if button pressed is SIN button
            if (btn.getId() == R.id.buttonSin) {
                display.append(btn.getContentDescription());
                start();
            }

            //Determine if button pressed is COS button
            if (btn.getId() == R.id.buttonCos) {
                display.append(btn.getContentDescription());
                start();
            }

            //Determine if button pressed is TAN button
            if (btn.getId() == R.id.buttonTan) {
                display.append(btn.getContentDescription());
                start();
            }

            //Determine if button pressed is LOG button
            if (btn.getId() == R.id.buttonLog) {
                display.append(btn.getContentDescription());
                start();
            }

            //Determine if button pressed is MemoryPLUS button
            if (btn.getId() == R.id.buttonMPlus) {
                display.append(btn.getContentDescription());
                start();
            }

            //Determine if button pressed is X SQUARED button
            if (btn.getId() == R.id.buttonXSquared) {
                display.append(btn.getContentDescription());
                start();
            }

            //Determine if button pressed is Square root button
            if (btn.getId() == R.id.buttonSquareRoot) {
                display.append("√");
                start();
            }

            //Determine if button pressed is left bracket button
            if (btn.getId() == R.id.buttonLeftBracket) {
                display.append(btn.getContentDescription());
                start();
            }

            //Determine if button pressed is right bracket button
            if (btn.getId() == R.id.buttonRightBracket) {
                display.append(btn.getContentDescription());
                start();
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
                    display.append("*");
                }
            }

            //Determine if button pressed is seven button
            if (btn.getId() == R.id.buttonSeven) {
                display.append(btn.getContentDescription());
                start();
            }

            //Determine if button pressed is eight button
            if (btn.getId() == R.id.buttonEight) {
                display.append(btn.getContentDescription());
                start();
            }

            //Determine if button pressed is nine button
            if (btn.getId() == R.id.buttonNine) {
                display.append(btn.getContentDescription());
                start();
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
                    display.append("/");
                }
            }

            //Determine if button pressed is FOUR button
            if (btn.getId() == R.id.buttonFour) {
                display.append(btn.getContentDescription());
                start();
            }

            //Determine if button pressed is FIVE button
            if (btn.getId() == R.id.buttonFive) {
                display.append(btn.getContentDescription());
                start();
            }

            //Determine if button pressed is six button
            if (btn.getId() == R.id.buttonSix) {
                display.append(btn.getContentDescription());
                start();
            }

            //Determine if button pressed is delete button
            if (btn.getId() == R.id.buttonDel) {
                deleteNumber();

            }

            //Determine if button pressed is subtract button
            if (btn.getId() == R.id.buttonSubtraction) {
                if (start()) {
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
                if (start()) {
                    display.append("");
                }
                else {
                    display.append("+");
                }
            }

            //Determine if button pressed is Decimal point button
            if (btn.getId() == R.id.buttonDecimalPoint) {
                if (display.getText().toString() == "") {
                    display.setText("0.");
                    start();
                    decimalPointCount++;
                }

                else {
                    if (decimalPointCount == 0) {
                        display.append(".");
                        decimalPointCount++;
                    }

                    else {
                        display.append("");
                    }

                }
            }

            //Determine if button pressed is Zero button
            if (btn.getId() == R.id.buttonZero) {
                if (display.getText().toString() == "") {
                    display.append("");
                    //start++;
                    //zeroCount++;
                }
                else
                    display.append("0");
            }

            //Determine if button pressed is PLUS MINUS button
            if (btn.getId() == R.id.buttonPlusMinus) {
                display.append("±");
                //display.append("-");
            }


            /*************************************************************
             * Basic Calculator logic
             *************************************************************/

            if (btn.getId() == R.id.buttonAddition) {
                    operator = '+';
                    result = theAnswerIs(operator);
            }

            if (btn.getId() == R.id.buttonSubtraction) {
                operator = '-';
                result = theAnswerIs(operator);
            }

            if (btn.getId() == R.id.buttonMultiply) {
                operator = '*';
                result = theAnswerIs(operator);
            }

            if (btn.getId() == R.id.buttonDivide) {
                operator = '/';
                result = theAnswerIs(operator);
            }

            /*//If a basic operator is pressed, process the calculation and display answer
            if (start() && ((btn.getId() == R.id.buttonAddition) || (btn.getId() == R.id.buttonSubtraction)
                    || (btn.getId() == R.id.buttonMultiply) || (btn.getId() == R.id.buttonDivide)//)){
                    || (btn.getId() == R.id.buttonEquals))) {
                *//*isFirst = 1;
                if (start() && isSecond==0) {
                    //store display's value temporarily
                    backup = display.getText().toString();

                    //get the first number from display
                    getFirstNumber();
                    operator = getOperator();

                    //then update history
                    //updateHistory(backup);//update history display with backup's value
                    isSecond=1;
                }*//*

                //try {
                    switch (btn.getId()) {
                        case R.id.buttonAddition:
                            operator = '+';
                            //store display's value temporarily
                            backup();
                            break;

                        case R.id.buttonSubtraction:
                            operator = '-';
                            //store display's value temporarily
                            backup();
                            break;

                        case R.id.buttonMultiply:
                            operator = '*';
                            //store display's value temporarily
                            backup();
                            break;

                        case R.id.buttonDivide:
                            operator = '/';
                            //store display's value temporarily
                            backup();
                            break;
                    }

                    //backup();

                    //then update history
                    updateHistory(backup);//update history display with backup's value

                    //get first number
                    //getFirstNumber();
                    *//*StringTokenizer tokens = new StringTokenizer(backup, String.valueOf(operator));
                    String num1 = tokens.nextToken();// this will contain "Fruit"
                    String num2 = tokens.nextToken();// this will contain " they taste good"

                    *//**//*String[] str_array = backup.split(String.valueOf(operator));
                    String num1 = str_array[0];
                    String num2 = str_array[1];
*//**//*

                    number1 = Float.parseFloat(num1);
                    number2 = Float.parseFloat(num2);*//*

                    //display.setText("");
                    //display.setHint("");

                    //get operator
                    //getOperator();

                    //get second number
                    //getSecondNumber();

                    //operator = display.getText().charAt(display.getText().length());//get operator

                    //Update history again
                    //updateHistory(String.valueOf(operator));//update history display with backup's value

                    //get position
                    //position = display.getText().toString().indexOf(operator)-1;

                    //isSecond = 1;
                    //number2 = Float.valueOf(display.getText().toString().substring(position, display.getText().toString().length() - 1));
                *//*}
                //catch any exception that might occur when numbers are being entered
                catch (ArrayIndexOutOfBoundsException ae) {
                    calculatorError();
                } catch (ArithmeticException aex) {
                    calculatorError();
                } catch (NumberFormatException nfe) {
                    calculatorError();
                } catch (Exception e) {
                    calculatorError();
                }*//*
            }*/

            //if equals button is pressed, perform calculation and get resuly
            if (btn.getId() == R.id.buttonEquals) {
                //try {
                    //if (!start())//if calculation has not started, and there is no error, then display 0
                      //  display.append("");
                    //else if (!start())
                    //    display.setText("");
                    //else {
                        //resetError(); //reduce alpha/opacity of error notification
                        //updateDisplay(); //update display
                        //start();//get ready for new calculation

                        //check whether the display has any input
                        /*if (display.getText() == "") {
                            calculatorError();//display error
                        }
                        else {*/

                        //number2 = Float.parseFloat(display.getText().toString());

                        //number2 = Float.valueOf(display.getText().toString().substring(0, display.getText().toString().length() - 1));

                            //updateHistory("");//update history with second number
                            //getSecondNumber();

                       // }
                   // }
                /*}
                //catch any exception that might occur when equals is pressed
                catch (ArrayIndexOutOfBoundsException ae) {
                    calculatorError();
                } catch (ArithmeticException aex) {
                    calculatorError();
                } catch (NumberFormatException nfe) {
                    calculatorError();
                } catch (Exception e) {
                    calculatorError();
                }*/
                if (calculating) {
                    result = theAnswerIs(operator);//get the result of calculation
                    updateDisplay();//update display with result
                    start = 0;//resets start
                }

                else
                    result = display.getText().toString();
            }
        }
    };

    /**********************
     * back up data on LCD textview - display
     */
    private void backup() {
        backup = display.getText().toString();
        hDisplay.setHint(backup);
    }

    /************
     * Deletes the last number on the display
     */
    private void deleteNumber() {
        if (display.getText().toString() == null || display.getText().toString().length() == 0) {
            calculatorError();//display error if user tries to delete nothing
        }
        else {
            //using Strings substring function, delete the last character in string
            display.setText(display.getText().toString().substring(0, display.getText().toString().length() - 1));
            updateHistory("");//clear history display
        }
    }


    /***************
     * Calculates and returns the result of a calculation
     * @return
     */
    private String theAnswerIs(char localOperator) {
        backup();

        number1 = Float.parseFloat(display.getText().toString());
        //updateHistory(String.valueOf(number1+localOperator));
        display.setText(String.valueOf(number1+localOperator));


        boolean shouldIoperate = isOperator();


        if ((calculating == true) && shouldIoperate) {

            if (localOperator == '+')
                lResult = String.valueOf(number1 + getSecondNumber());

            if (localOperator == '-')
                lResult = String.valueOf(number1 - getSecondNumber());

            if (localOperator == '*')
                lResult = String.valueOf(number1 * getSecondNumber());

            if (localOperator == '/')
                lResult = String.valueOf(number1 / getSecondNumber());

            if (localOperator == '%')
                lResult = String.valueOf(number1 * (getSecondNumber()/100));

        }
        else
            lResult = String.valueOf(number1);

        return lResult;
    }


    /**************
     * gets the numbers entered on the display
     */
    private void getFirstNumber() {
        position++;
        if (position < backup.length()) {
            current = backup.charAt(position);
            number1 = current;
        }
        else {
            current = '\0';
        }

        /*String num1 = display.getText().toString();
        num1 = num1.substring(0, num1.length()-1);
        position = display.getText().toString().length();
        number1 = Float.valueOf(num1);*/
    }

    /**************
     * gets the second number entered on the display
     */
    private float getSecondNumber() {
        /*position++;
        if (position < backup.length()) {
            current = backup.charAt(position);
            number2 = current;
        }
        else {
            current = '\0';
        }*/
        display.setText("");
        return number2 = Float.valueOf(display.getText().toString());//.substring(position, display.getText().toString().length() - 1));

        /*String num1 = display.getText().toString();
        num1 = num1.substring(0, num1.length()-1);
        position = display.getText().toString().length();
        number1 = Float.valueOf(num1);*/
    }

    /**************
     * gets the operator entered on the display
     * @return boolean
     */
    private boolean isOperator() {
        //String operatorList="+-÷×%^!";
        //int thisOperator = backup.indexOf(position+1);
        //return operator = operatorList.charAt(thisOperator+1);
        if ("+-/*%^!".contains(String.valueOf(operator)))
            return true;
        else
            return false;
        //return "+-/*%^!".contains(String.valueOf(operator) != -1;
    }


    /*****************
     * updates the history display for current calculation
     * @param lBackup
     */
    private void updateHistory(String lBackup) {
        hDisplay.append(String.valueOf(number1+operator+number2));
    }


    /********************
     * Notifies the user that an error has occurred by setting the opacity/alpha of activeF5 to 1
     * @return boolean true
     */
    private boolean calculatorError() {
        activeF5.setAlpha((float) 1); //set error display message visible
        //display.setText('E');
        return true;
    }

    /********************
     * Clears the error display back to default by resetting the opacity of activeF5 to 0.1
     */
    private void resetError() {
        activeF5.setAlpha((float) 0.1); //set error display
    }

    /*************
     * updates textview display with data from result variable
     * Checks if result is an integer or not. Based on this, either displays the result cast as integer or float.
     * Also formats the result to scientific notation to accommodate for lack of space/width issues.
     */
    private void updateDisplay() {
        //calculating = false;
        if(isInteger(result))
            display.setText(String.valueOf(Integer.valueOf(result)));
        else
            display.setText(String.valueOf(result));

        result = "0";
    }

    /********************
     * Checks if a string passed in is an integer or not
     * @param s
     * @return
     */
    public static boolean isInteger(String s) {
        return isInteger(s,10);
    }

    /*************
     * optimized isInteger checker
     * @param s
     * @param radix
     * @return
     */
    public static boolean isInteger(String s, int radix) {
        if(s.isEmpty())
            return false;

        for(int i = 0; i < s.length(); i++) {
            if(i == 0 && s.charAt(i) == '-') {
                if(s.length() == 1)
                    return false;
                else continue;
            }
            if(Character.digit(s.charAt(i),radix) < 0)
                return false;
        }
        return true;
    }


    /******************
        CLR Button
        Resets the display to zero, and resets all important variables to 0
        @return void
     */
    private void clr() {
        display.setText("");
        display.setHint("0");
        start = 0;
        history = "";
        result = "0";
        number1 = 0;
        number2 = 0;
        position = -1;
        current = '\0';
        hDisplay.setText("");//resets history display
        resetError();//resets error display
    }

    /**********
     * Determines if calculation has started
     * @return boolean
     */
    private boolean start() {
        resetError();//reset error message display
        start = 1; //start=1 means calculation has started
        updateHistory("");//reset history display when new calculation starts
        calculating = true;
        return true;
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
