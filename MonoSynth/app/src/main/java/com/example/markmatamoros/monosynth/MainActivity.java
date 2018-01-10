//Mono Synth - Mark Matamoros Fall 2014

package com.example.markmatamoros.monosynth;

import android.app.Activity;
import android.content.Context;
import android.hardware.SensorEventListener;
import android.hardware.Sensor;
import android.hardware.SensorEvent;
import android.hardware.SensorManager;
import android.media.AudioManager;
import android.media.AudioTrack;
import android.media.AudioFormat;
import android.os.Bundle;
import android.view.MotionEvent;
import android.view.View;
import android.view.View.OnTouchListener;
import android.view.Menu;
import android.view.MenuItem;


public class MainActivity extends Activity implements OnTouchListener, SensorEventListener
{
    //referenced frequency for playback
    int fundamental = 440;

    //Harmonic series for square wave
    AudioTrack tone1;
    AudioTrack tone2;
    AudioTrack tone3;
    AudioTrack tone4;
    AudioTrack tone5;

    //Touch location on screen
    float xValue;
    float yValue;

    //maximum range of accelerometer sensor
    float max;

    SensorManager sensorManager;
    Sensor accelerometer;

    View myView;

    @Override
    protected void onCreate(Bundle savedInstanceState)
    {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        setVolumeControlStream(AudioManager.STREAM_MUSIC);

        sensorManager = (SensorManager) getSystemService(Context.SENSOR_SERVICE);
        accelerometer = sensorManager.getDefaultSensor(Sensor.TYPE_ACCELEROMETER);
        sensorManager.registerListener(this, accelerometer, SensorManager.SENSOR_DELAY_NORMAL);

        max = accelerometer.getMaximumRange();

        //create harmonic series
        tone1 = tone(fundamental);
        tone2 = tone(fundamental*3);
        tone3 = tone(fundamental*5);
        tone4 = tone(fundamental*7);
        tone5 = tone(fundamental*9);

        //initialize volume
        tone1.setVolume(0);
        tone2.setVolume(0);
        tone3.setVolume(0);
        tone4.setVolume(0);
        tone5.setVolume(0);

        //activate (play) signal
        tone1.play();
        tone2.play();
        tone3.play();
        tone4.play();
        tone5.play();

        myView = findViewById(android.R.id.content);
        myView.setOnTouchListener(new View.OnTouchListener()
        {
            public boolean onTouch(View v, MotionEvent event)
            {
                int touchDown = event.getAction();

                //playback speed
                int speed = 44100;

                //variable (screen width / 2) used to create larger (audible) frequency range
                int octaveDiv = (getResources().getDisplayMetrics().widthPixels)/2;

                //activate playback upon touching screen
                if (touchDown == MotionEvent.ACTION_DOWN)
                {
                    //grab x and y values
                    xValue = event.getX();
                    yValue = event.getY();

                    //utilize sample playback rate to change audible frequency output
                    tone1.setPlaybackRate((int) (speed * (xValue / octaveDiv)));
                    tone2.setPlaybackRate((int) (speed * (xValue / octaveDiv)));
                    tone3.setPlaybackRate((int) (speed * (xValue / octaveDiv)));
                    tone4.setPlaybackRate((int) (speed * (xValue / octaveDiv)));
                    tone5.setPlaybackRate((int) (speed * (xValue / octaveDiv)));

                    //output sine wave amplitudes (playback) according to harmonic series values (w/ y-axis)
                    tone1.setVolume((yValue/720));
                    tone2.setVolume((yValue/720)/3);
                    tone3.setVolume((yValue/720)/5);
                    tone4.setVolume((yValue/720)/7);
                    tone5.setVolume((yValue/720)/9);

                    return true;
                }

                //continue playback upon moving around the screen
                if (touchDown == MotionEvent.ACTION_MOVE)
                {
                    //grab x and y values
                    xValue = event.getX();
                    yValue = event.getY();

                    //utilize sample playback rate to change audible frequency output
                    tone1.setPlaybackRate((int) (speed * (xValue / octaveDiv)));
                    tone2.setPlaybackRate((int) (speed * (xValue / octaveDiv)));
                    tone3.setPlaybackRate((int) (speed * (xValue / octaveDiv)));
                    tone4.setPlaybackRate((int) (speed * (xValue / octaveDiv)));
                    tone5.setPlaybackRate((int) (speed * (xValue / octaveDiv)));

                    //output sine wave amplitudes (playback) according to harmonic series values (w/ y-axis)
                    tone1.setVolume((yValue/720));
                    tone2.setVolume((yValue/720)/3);
                    tone3.setVolume((yValue/720)/5);
                    tone4.setVolume((yValue/720)/7);
                    tone5.setVolume((yValue/720)/9);

                    return true;
                }

                //set playback volume to 0 upon not touching the screen
                if (touchDown == MotionEvent.ACTION_UP)
                {
                    tone1.setVolume(0);
                    tone2.setVolume(0);
                    tone3.setVolume(0);
                    tone4.setVolume(0);
                    tone5.setVolume(0);

                    return true;
                }

                return false;
            }


        });
    }

    //write sine wave
    //credit: http://stackoverflow.com/questions/27056605/play-sound-in-left-or-right-speaker-using-android-audiotrack
    private AudioTrack tone(double freq)
    {
        int count = (int)(44100*2) & ~1;
        short[] samples = new short[count];

        for(int i=0; i<count; i+=2)
        {
            short sample = (short)(Math.sin(2*Math.PI*i/(44100/freq))*0x7FFF);
            samples[i+0] = sample;
            samples[i+1] = sample;
        }

        AudioTrack track = new AudioTrack(AudioManager.STREAM_MUSIC, 44100, AudioFormat.CHANNEL_OUT_STEREO,
                AudioFormat.ENCODING_PCM_16BIT, count*(Short.SIZE/8), AudioTrack.MODE_STATIC);

        track.write(samples, 0, count);
        track.setLoopPoints(0, count/4, -1);
        return track;
    }

    @Override
    public boolean onTouch(View v, MotionEvent event)
    {
        return false;
    }

    //utilization of accelerometer for vibrato-like effect
    @Override
    public void onSensorChanged(SensorEvent event)
    {
        //holds accelerometer outputted value (x-axis)
        float changeToP;

        //playback speed
        int speed = 44100;

        //variable (screen width / 2) used to create larger (audible) frequency range
        int octaveDiv = (getResources().getDisplayMetrics().widthPixels)/2;

        if (event.values[0] < 0)
        {
            //grab accelerometer value (x-axis)
            changeToP = (event.values[0]*(-1));

            //activate vibrato if condition is met
            if (changeToP/max > 0.5)
            {
                //adjust playback speed
                tone1.setPlaybackRate((int) (speed * (3+changeToP/max)));
                tone2.setPlaybackRate((int) (speed * (3.05+changeToP/max)));
                tone3.setPlaybackRate((int) (speed * (3.1+changeToP/max)));
                tone4.setPlaybackRate((int) (speed * (3.15+changeToP/max)));
                tone5.setPlaybackRate((int) (speed * (3.2+changeToP/max)));
            }
        }

        else
        {
            //utilize sample playback speed to change audible frequency output
            tone1.setPlaybackRate((int) (speed * (xValue / octaveDiv)));
            tone2.setPlaybackRate((int) (speed * (xValue / octaveDiv)));
            tone3.setPlaybackRate((int) (speed * (xValue / octaveDiv)));
            tone4.setPlaybackRate((int) (speed * (xValue / octaveDiv)));
            tone5.setPlaybackRate((int) (speed * (xValue / octaveDiv)));
        }
    }

    @Override
    public void onAccuracyChanged(Sensor sensor, int accuracy)
    {

    }

//    @Override
    /*public boolean onCreateOptionsMenu(Menu menu){
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
    }*/
}
