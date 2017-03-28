package com.pontech.tawking;

import android.content.Intent;
import android.graphics.Color;
import android.media.AudioManager;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;

import android.util.Log;
import android.view.KeyEvent;
import android.view.View;
import android.view.ViewGroup;

import android.content.Context;
import android.widget.LinearLayout;
import android.widget.Toast;
import android.widget.EditText;
import android.widget.Button;
import android.speech.tts.TextToSpeech;

import java.util.Locale;
import java.util.Timer;
import java.util.TimerTask;

public class speak extends AppCompatActivity {
    TextToSpeech t1;
    Context context;
    AudioManager audioManager;

    ViewGroup activity_main;
    LinearLayout buttonBoard;
    EditText editText;

    Timer timer;
    MyTimerTask myTimerTask;

    Button activeButton;
    LinearLayout activeButtonBoard;
    int activeButtonPosition = -1;
    int activeButtonBoardPosition = 0;

    private void nextButton() {
        activeButtonPosition++;
        if (activeButtonBoardPosition >= buttonBoard.getChildCount()) {
            activeButtonBoardPosition = 0;
            activeButtonPosition = 0;
        }
        activeButtonBoard = (LinearLayout)(buttonBoard.getChildAt(activeButtonBoardPosition));

        if (activeButtonPosition >= activeButtonBoard.getChildCount()) {
            activeButtonBoardPosition++;
            activeButtonPosition = -1;
            nextButton();
            return;
        }

        Log.d("nextButton", Integer.toString(activeButtonBoardPosition) + "-" + Integer.toString(activeButtonPosition));

        if(activeButton != null) activeButton.setBackgroundColor(Color.LTGRAY);
        activeButton = (Button)(activeButtonBoard.getChildAt(activeButtonPosition));
        if( activeButton.isShown() == false ) {
            nextButton();
            return;
        }
        activeButton.setBackgroundColor(Color.YELLOW);
    }

    private void loopViews(ViewGroup view) {
        for (int i = 0; i < view.getChildCount(); i++) {
            View v = view.getChildAt(i);

            if (v instanceof Button) {
                // Do something

                Button b = (Button) v; //findViewById(R.id.Button1);

                b.setOnLongClickListener(new View.OnLongClickListener() {
                    @Override
                    public boolean onLongClick(View view) {
                        speak(((Button) view).getText().toString());
                        return true;
                    }
                });

            } else if (v instanceof ViewGroup) {
                this.loopViews((ViewGroup) v);
            }
        }
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_speak);
        context = getApplicationContext();

        activity_main = (ViewGroup)findViewById(R.id.activity_main);
        buttonBoard = (LinearLayout)findViewById(R.id.ButtonBoard);
        editText = (EditText) findViewById(R.id.edit_message);

        t1=new TextToSpeech(context, new TextToSpeech.OnInitListener() {
            @Override
            public void onInit(int status) {
                if (status != TextToSpeech.ERROR) {
                    t1.setLanguage(Locale.UK);
                }
            }
        }, "Speech");

        loopViews(activity_main);

        timer = new Timer();
        myTimerTask = new MyTimerTask();
        timer.schedule(myTimerTask, 1000, 1000); // task to call, start up ms, repeat ms (optional)
        // todo: 3 Shut the timer down when we leave this activity

        audioManager = (AudioManager) getSystemService(Context.AUDIO_SERVICE);
        audioManager.setMode(AudioManager.MODE_IN_CALL);
        audioManager.setSpeakerphoneOn(true);

        //audioManager.getDevices(AudioManager.GET_DEVICES_OUTPUTS);

        //audioManager.setMode(AudioManager.MODE_NORMAL);
        //audioManager.setWiredHeadsetOn(false);
        //audioManager.setSpeakerphoneOn(true);


    }

    public void speak(String message) {
        CharSequence text = "Toast: " + message;
        int duration = Toast.LENGTH_SHORT;
        Toast toast = Toast.makeText(context, text, duration);
        toast.show();
        t1.speak(message, TextToSpeech.QUEUE_ADD, null);
    }

    /** Called when the user clicks the Send button */
    public void sendMessage(View view) {
        // Do something in response to button
        String message = editText.getText().toString();
        speak(message);
    }
    public void clearMessage(View view) {
        editText.setText("");
    }

    /** Called when the user clicks the a button */
    public void sendButton(View view) {
        Button button = (Button) view;
        String message = editText.getText().toString();
        ((EditText) findViewById(R.id.edit_message)).setText(message + " " + button.getText());
    }

    @Override
    public boolean onKeyDown(int keyCode, KeyEvent event) {
        if(keyCode == KeyEvent.KEYCODE_MEDIA_PLAY) {
            Toast.makeText(context, "KEYCODE_MEDIA_PLAY", Toast.LENGTH_SHORT).show();
            return true;
        }
        else if(keyCode == KeyEvent.KEYCODE_MEDIA_PLAY_PAUSE){
            Toast.makeText(context, "KEYCODE_MEDIA_PLAY_PAUSE", Toast.LENGTH_SHORT).show();
            return true;
        }
        else if(keyCode == KeyEvent.KEYCODE_AVR_INPUT){
            Toast.makeText(context, "KEYCODE_AVR_INPUT", Toast.LENGTH_SHORT).show();
            return true;
        }
        else if(keyCode == KeyEvent.KEYCODE_BACK){
            Toast.makeText(context, "KEYCODE_BACK", Toast.LENGTH_SHORT).show();
            //return true;
        }
        else if(keyCode == KeyEvent.KEYCODE_HEADSETHOOK){
            //String message = editText.getText().toString();
            //speak(message);

            activeButtonBoard = (LinearLayout)(buttonBoard.getChildAt(activeButtonBoardPosition));
            activeButton = (Button)(activeButtonBoard.getChildAt(activeButtonPosition));
            String button_text = activeButton.getText().toString();
            if( button_text.equals("*Send") ) {
                String message = editText.getText().toString();
                speak(message);
                editText.setText("");
            }
            else if( button_text.equals("*Clear") ) {
                editText.setText("");
            }
            else {
                editText.setText( editText.getText().toString() + " " + button_text );
            }

            //Toast.makeText(context, "KEYCODE_HEADSETHOOK", Toast.LENGTH_SHORT).show();
            return true;
        }
        else {
            Toast.makeText(context, "onKeyDown", Toast.LENGTH_SHORT).show();
        }
        return super.onKeyDown(keyCode, event);
    }

    private class MyTimerTask extends TimerTask {
        @Override
        public void run() {
            runOnUiThread(new Runnable(){
                @Override
                public void run() {
                    //editText.setText(editText.getText().toString() + "*");
                    nextButton();
                }});
        }
    }

}
