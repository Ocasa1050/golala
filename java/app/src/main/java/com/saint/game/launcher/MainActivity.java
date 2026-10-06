package com.saint.game.launcher;

import android.Manifest;
import android.annotation.SuppressLint;
import android.app.Dialog;
import android.content.Context;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.os.Build;
import android.os.Bundle;
import android.os.Environment;
import android.content.pm.PackageManager;

import android.os.Handler;
import android.text.Editable;
import android.text.TextWatcher;
import android.view.KeyEvent;
import android.view.inputmethod.EditorInfo;
import android.widget.*;

import androidx.appcompat.widget.AppCompatButton;
import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.RecyclerView;

import android.content.Intent;
import android.net.Uri;

import android.view.View.OnClickListener;
import android.view.View;
import android.view.animation.AnimationUtils;
import android.view.animation.Animation;

import com.saint.game.launcher.Preferences;
import com.saint.game.R;
import com.saint.game.gui.util.MaskedEditText;


import org.ini4j.Wini;

import java.io.File;

import java.io.IOException;
import java.util.*;
import java.util.logging.Logger;


public class MainActivity extends AppCompatActivity {

    public Button settings;
    public Button play;
    EditText nickname;
    ImageButton ib_info;
    ImageView imageButton2;
    ImageView buttonews;
    ImageView imageView13;
    ImageView imageView9;
    ImageView imageView5;
    ImageView imageView33;

    RecyclerView recyclerView;
    TextView textView12;
    TextView textView13;
    TextView textView14;
    TextView textView15;
    TextView textView17;


    @SuppressLint({"MissingInflatedId", "WrongViewCast"})
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        // liberty studio //

        settings = (Button) findViewById(R.id.brp_launcher_settings_btn);
        play = (Button) findViewById(R.id.brp_launcher_play);

        // liberty studio //

        Animation animation = AnimationUtils.loadAnimation(this, R.anim.button_click);


        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
            if (checkSelfPermission(Manifest.permission.READ_EXTERNAL_STORAGE) == PackageManager.PERMISSION_DENIED || checkSelfPermission(Manifest.permission.WRITE_EXTERNAL_STORAGE) == PackageManager.PERMISSION_DENIED || checkSelfPermission(Manifest.permission.RECORD_AUDIO) == PackageManager.PERMISSION_DENIED) {
                requestPermissions(new String[]{Manifest.permission.READ_EXTERNAL_STORAGE, Manifest.permission.WRITE_EXTERNAL_STORAGE, Manifest.permission.RECORD_AUDIO}, 1000);
            }
        }


        InitLogic();
        LoadNick();



        nickname = findViewById(R.id.edit_text_name);
        ib_info = findViewById(R.id.ib_info);



        ((Button) findViewById(R.id.brp_launcher_settings_btn)).setOnClickListener(new OnClickListener() {
            public void onClick(View v) {
                v.startAnimation(animation);
                Timer t = new Timer();
                t.schedule(new TimerTask(){
                    @Override
                    public void run() {
                        onClickPlay();
                    }
                }, 200L);

            }
        });


        ((AppCompatButton) findViewById(R.id.button_play)).setOnClickListener(new OnClickListener() {
            public void onClick(View v) {
                v.startAnimation(animation);
                Timer t = new Timer();
                t.schedule(new TimerTask(){
                    @Override
                    public void run() {
                        onClickPlay();
                    }
                }, 200L);
            }
        });

        ((ImageButton) ib_info).setOnClickListener(new OnClickListener() {
            public void onClick(View v) {
                v.startAnimation(animation);
                TextView info_nick = findViewById(R.id.text_view_info_about_nickname);
                if(info_nick.getVisibility() == View.INVISIBLE)
                    info_nick.setVisibility(View.VISIBLE);
                else
                    info_nick.setVisibility(View.INVISIBLE);
            }
        });


        settings.setOnClickListener(new OnClickListener() {
            public void onClick(View v) {
                onClickSettings();
            }
        });

        ((ImageButton) findViewById(R.id.button_vk)).setOnClickListener(new OnClickListener() {
            public void onClick(View v) {
                v.startAnimation(animation);
                Timer t = new Timer();
                t.schedule(new TimerTask(){
                    @Override
                    public void run() {
                        startActivity(new Intent("android.intent.action.VIEW", Uri.parse("https://vk.com/")));
                    }
                }, 200L);
            }
        });

        ((ImageView) findViewById(R.id.newsstor)).setOnClickListener(new OnClickListener() {
            public void onClick(View v) {
                v.startAnimation(animation);
                Timer t = new Timer();
                t.schedule(new TimerTask(){
                    @Override
                    public void run() {
                        startActivity(new Intent("android.intent.action.VIEW", Uri.parse("https://vk.com/")));
                    }
                }, 200L);
            }
        });
        ((ImageButton) findViewById(R.id.button_ytobe)).setOnClickListener(new OnClickListener() {
            public void onClick(View v) {
                v.startAnimation(animation);
                Timer t = new Timer();
                t.schedule(new TimerTask(){
                    @Override
                    public void run() {
                        startActivity(new Intent("android.intent.action.VIEW", Uri.parse("https://www.youtube.com/")));
                    }
                }, 200L);
            }
        });
        ((ImageButton) findViewById(R.id.button_donate)).setOnClickListener(new OnClickListener() {
            public void onClick(View v) {
                v.startAnimation(animation);
                Timer t = new Timer();
                t.schedule(new TimerTask(){
                    @Override
                    public void run() {
                        startActivity(new Intent("android.intent.action.VIEW", Uri.parse("https://forum.unity-mobile.ru/")));
                    }
                }, 200L);
            }

        });

        ((ImageButton) findViewById(R.id.button_discord)).setOnClickListener(new OnClickListener() {
            public void onClick(View v) {
                v.startAnimation(animation);
                Timer t = new Timer();
                t.schedule(new TimerTask(){
                    @Override
                    public void run() {
                        startActivity(new Intent("android.intent.action.VIEW", Uri.parse("https://discord.gg/")));
                    }
                }, 200L);
            }
        });

        ((ImageButton) findViewById(R.id.button_telegram)).setOnClickListener(new OnClickListener() {
            public void onClick(View v) {
                v.startAnimation(animation);
                Timer t = new Timer();
                t.schedule(new TimerTask(){
                    @Override
                    public void run() {
                        startActivity(new Intent("android.intent.action.VIEW", Uri.parse("https://t.me/")));
                    }
                }, 200L);
            }
        });

        ((AppCompatButton) findViewById(R.id.button_clean_game)).setOnClickListener(new OnClickListener() {
            public void onClick(View v) {
                v.startAnimation(animation);
                Timer t = new Timer();
                t.schedule(new TimerTask(){
                    @Override
                    public void run() {
                        ToLoad();
                    }
                }, 200L);
            }
        });



        ((EditText) nickname)
                .setOnEditorActionListener(
                        new EditText.OnEditorActionListener() {
                            @Override
                            public boolean onEditorAction(
                                    TextView v, int actionId, KeyEvent event) {
                                if (actionId == EditorInfo.IME_ACTION_SEARCH
                                        || actionId == EditorInfo.IME_ACTION_DONE
                                        || event.getAction() == KeyEvent.ACTION_DOWN
                                        && event.getKeyCode() == KeyEvent.KEYCODE_ENTER) {
                                    try {
                                        File f =
                                                new File(
                                                        Environment.getExternalStorageDirectory()
                                                                + "/Crmp/SAMP/settings.ini");
                                        if (!f.exists()) {
                                            f.createNewFile();
                                            f.mkdirs();
                                        }
                                        Wini w =
                                                new Wini(
                                                        new File(
                                                                Environment.getExternalStorageDirectory()
                                                                        + "/Crmp/SAMP/settings.ini"));
                                        if(checkValidNick()){
                                            w.put("client", "name", nickname.getText().toString());
                                            //Toast.makeText(this, "Ваш новый никнейм успешно сохранен!", Toast.LENGTH_SHORT).show();
                                            tost("Ваш новый никнейм успешно сохранен!");
                                            Preferences.setNick(String.valueOf(nickname.getText()));
                                        } else {
                                            checkValidNick();
                                        }
                                        w.store();
                                    } catch (IOException e) {
                                        e.printStackTrace();
                                        tost("Установите игру!");
                                    }
                                }
                                return false;
                            }
                        });

        nickname.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence charSequence, int i, int i1, int i2) {

            }

            @Override
            public void onTextChanged(CharSequence charSequence, int i, int i1, int i2) {
                try {
                    File f = new File(Environment.getExternalStorageDirectory() + "/Crmp/SAMP/settings.ini");
                    if (!f.exists()) {
                        f.createNewFile();
                        f.mkdirs();
                    }
                    Wini w = new Wini(new File(Environment.getExternalStorageDirectory() + "/Crmp/SAMP/settings.ini"));
                    if(checkValidNick()){
                        w.put("client", "name", nickname.getText().toString());
                    } else {
                        checkValidNick();
                    }
                    w.store();
                } catch (IOException e) {
                    e.printStackTrace();
                }
            }

            @Override
            public void afterTextChanged(Editable editable) {

            }
        });
    }

    @Override
    public void onRequestPermissionsResult(int requestCode, String[] permissions, int[] grantResults) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults);
        if (requestCode != 1000) {
            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
                requestPermissions(new String[]{Manifest.permission.READ_EXTERNAL_STORAGE, Manifest.permission.WRITE_EXTERNAL_STORAGE, Manifest.permission.RECORD_AUDIO}, 1000);
            }
        }
    }

    public void onClickPlay() {
        if(IsGameInstalled()) {
            startActivity(new Intent(getApplicationContext(), com.saint.game.core.GTASA.class));
        } else {
            startActivity(new Intent(getApplicationContext(), PreLoadActivity.class));
        }
    }

    private boolean IsGameInstalled()
    {
        String CheckFile = Environment.getExternalStorageDirectory() + "/Crmp/texdb/gta3 .img";
        File file = new File(CheckFile);
        return file.exists();
    }
    public void onClickSettings() {
        startActivity(new Intent(getApplicationContext(), SettingsActivity.class));
    }
    private void ToLoad()
    {
        startActivity(new Intent(this, LoaderActivity.class));
    }

    private void InitLogic() {
        try {
            Wini w = new Wini(new File(Environment.getExternalStorageDirectory() + "/Crmp/SAMP/settings.ini"));
            nickname = findViewById(R.id.edit_text_name);
            nickname.setText(w.get("client", "name"));
            w.store();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    public boolean checkValidNick(){
        EditText nick = (EditText) findViewById(R.id.edit_text_name);
        if(nick.getText().toString().isEmpty()) {
            tost("Введите ник");
            return false;
        }
        if(!(nick.getText().toString().contains("_"))){
            tost("Ник должен содержать символ \"_\"");
            return false;
        }
        if(nick.getText().toString().length() < 4){
            tost("Длина ника должна быть не менее 4 символов");
            return false;
        }
        return true;

    }

    private void LoadNick() {
        try {
            Wini w = new Wini(new File(Environment.getExternalStorageDirectory() + "/Crmp/SAMP/settings.ini"));
            Preferences.setNick(w.get("client", "name"));
            w.store();
        } catch (IOException e) {
            e.printStackTrace();

        }
    }
    private void startTimer() {
        Timer t = new Timer();
        t.schedule(new TimerTask(){

            @Override
            public void run() {
                onClickPlay();
            }
        }, 100L);
    }
    private void tost(String pon)
    {
        Toast.makeText(this, pon, Toast.LENGTH_SHORT).show();
    }

    public boolean BGCoreNative_isInternetConnected(Context context) {
        NetworkInfo netInfo = ((ConnectivityManager) context.getSystemService(Context.CONNECTIVITY_SERVICE)).getActiveNetworkInfo();
        if (netInfo == null || !netInfo.isConnectedOrConnecting()) {
            return false;
        }
        return true;
    }

    public /* synthetic */ void lambda$Online$0$MainActivity(String[] serverInfo) {
        TextView textView = this.textView14;

        this.textView15.setText("Сервер активен");
        this.imageView9.setVisibility(View.VISIBLE);
        this.imageView13.setVisibility(View.INVISIBLE);
    }

    public void lambda$Online$1$MainActivity() {
        this.textView15.setText("Сервер недоступен");
        this.imageView9.setVisibility(View.INVISIBLE);
        this.imageView13.setVisibility(View.VISIBLE);
    }

} 