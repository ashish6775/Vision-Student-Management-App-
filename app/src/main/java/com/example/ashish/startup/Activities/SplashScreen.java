package com.example.ashish.startup.Activities;

import android.content.Intent;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.view.Window;
import android.view.WindowManager;
import android.view.animation.Animation;
import android.view.animation.AnimationUtils;
import android.widget.TextView;

import com.example.ashish.startup.R;

public class SplashScreen extends AppCompatActivity {

    private TextView splash;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        requestWindowFeature(Window.FEATURE_NO_TITLE);
        this.getWindow().setFlags(WindowManager.LayoutParams.FLAG_FULLSCREEN,WindowManager.LayoutParams.FLAG_FULLSCREEN);
        setContentView(R.layout.activity_splash_screen);

        splash = (TextView)findViewById(R.id.splash);
        Animation myanim = AnimationUtils.loadAnimation(this,R.anim.mytransition);
        splash.startAnimation(myanim);
        final Intent i = new Intent(this,Welcome.class);
        Thread timer = new Thread(){
            public void run(){
                try {
                    sleep(500);
                }catch (InterruptedException e){
                    e.printStackTrace();
                }finally {
                    startActivity(i);
                    finish();
                }
            }
        };
                timer.start();
    }
}