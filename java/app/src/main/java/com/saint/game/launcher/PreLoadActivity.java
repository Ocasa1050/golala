package com.saint.game.launcher;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.animation.Animation;
import android.view.animation.AnimationUtils;
import android.widget.Button;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;
import com.saint.game.R;



public class PreLoadActivity extends AppCompatActivity {
    public Button play;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_pre_load);
		
		Animation animation = AnimationUtils.loadAnimation(this, R.anim.button_click);
        play = (Button) findViewById(R.id.brp_launcher_pre_load_download);


        play.setOnClickListener(new OnClickListener() {
            public void onClick(View v) {
                v.startAnimation(animation);
                startActivity(new Intent(getApplicationContext(), LoaderActivity.class));
                finish();
            }
        });
    }
} 