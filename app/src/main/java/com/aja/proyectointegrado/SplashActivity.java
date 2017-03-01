package com.aja.proyectointegrado;

import android.content.Intent;
import android.graphics.Color;
import android.graphics.PorterDuff;
import android.os.Bundle;
import android.os.CountDownTimer;
import android.support.v7.app.ActionBar;
import android.support.v7.app.AppCompatActivity;
import android.view.Window;
import android.widget.ImageButton;
import android.widget.ProgressBar;

public class SplashActivity extends AppCompatActivity {
    public static final int segundos = 10;
    public static final int milisegundos = segundos * 1000;
    public static final int delay = 2;
    private ProgressBar progressBar;
    private ImageButton car;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_splash);
        progressBar = (ProgressBar)findViewById(R.id.progressBar);
        progressBar.setMax(maximo_progreso());
        empezar_animacion();
        progressBar.getIndeterminateDrawable().setColorFilter(Color.WHITE, PorterDuff.Mode.SRC_IN);


        //car = (ImageButton) findViewById(R.id.car);
        //Animation pulse = AnimationUtils.loadAnimation(this, R.anim.pulse);
        //car.startAnimation(pulse);

    }
    private void empezar_animacion() {
        new CountDownTimer(milisegundos,1000){
            @Override
            public void onTick(long millisUntilFinished) {
                progressBar.setProgress(establecer_progreso(millisUntilFinished));
            }
            @Override
            public void onFinish() {
                Intent loading = new Intent(SplashActivity.this,LoginActivity.class);
                startActivity(loading);
                finish();
            }
        }.start();
    }
    private int maximo_progreso() {
        return segundos -delay;
    }
    private int establecer_progreso(long miliseconds){
        return (int)((miliseconds - miliseconds)/1000);
    }
}
