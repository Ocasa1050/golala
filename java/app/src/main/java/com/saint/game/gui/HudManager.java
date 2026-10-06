package com.saint.game.gui;

import android.app.Activity;
import android.view.View;
import android.view.animation.Animation;
import android.view.animation.AnimationUtils;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.ProgressBar;
import android.widget.TextView;

import androidx.constraintlayout.widget.ConstraintLayout;

import com.nvidia.devtech.NvEventQueueActivity;
import com.saint.game.R;
import com.saint.game.gui.util.Utils;

import java.io.UnsupportedEncodingException;
import java.text.DecimalFormat;
import java.text.SimpleDateFormat;
import java.text.DecimalFormatSymbols;
import java.util.ArrayList;
import java.util.Formatter;
import java.util.Calendar;
import java.util.Timer;
import java.util.TimerTask;

public class HudManager {
    public Activity activity;

    public ConstraintLayout hud_layout;

    public ProgressBar hud_health;
    public ProgressBar hud_armour;

    public TextView hud_money;

    public ImageView hud_weapon;

    public ImageView hud_menu;

    public ImageView btn_server_donate;

    public ArrayList<ImageView> hud_wanted;

    public HudManager(Activity aactivity) {
        activity = aactivity;

        hud_layout = aactivity.findViewById(R.id.hud_main);
        hud_layout.setVisibility(View.GONE);

        hud_health = aactivity.findViewById(R.id.hud_health_pb);
        hud_armour = aactivity.findViewById(R.id.hud_armour_pb);

        hud_money = aactivity.findViewById(R.id.hud_balance_text);
        hud_weapon = aactivity.findViewById(R.id.hud_weapon);
        hud_menu = aactivity.findViewById(R.id.hud_menuyzbek);
        btn_server_donate = aactivity.findViewById(R.id.btn_server_donate);

        hud_wanted = new ArrayList<>();
        hud_wanted.add(activity.findViewById(R.id.hud_star_1));
        hud_wanted.add(activity.findViewById(R.id.hud_star_2));
        hud_wanted.add(activity.findViewById(R.id.hud_star_3));
        hud_wanted.add(activity.findViewById(R.id.hud_star_4));
        hud_wanted.add(activity.findViewById(R.id.hud_star_5));
        hud_menu.setOnClickListener( view -> {
            openMenu();
            NvEventQueueActivity.getInstance().togglePlayer(1);
        });
        btn_server_donate.setOnClickListener( view -> {
            openMagazine();
            NvEventQueueActivity.getInstance().togglePlayer(1);
        });
    }

    public void UpdateHudInfo(int health, int armour, int hunger, int weaponid, int ammo, int playerid, int money, int wanted)
    {
        hud_health.setProgress(health);
        hud_armour.setProgress(armour);

        DecimalFormat formatter = new DecimalFormat();
        DecimalFormatSymbols symbols = DecimalFormatSymbols.getInstance();
        symbols.setGroupingSeparator(' ');
        formatter.setDecimalFormatSymbols(symbols);
        String s = formatter.format(money).toString();
        hud_money.setText(String.valueOf(s));

        int id = activity.getResources().getIdentifier(new Formatter().format("weapon_%d", Integer.valueOf(weaponid)).toString(), "drawable", activity.getPackageName());
        hud_weapon.setImageResource(id);

        hud_weapon.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                NvEventQueueActivity.getInstance().onWeaponChanged();
            }
        });
        if(wanted > 5) wanted = 5;
        for (int i2 = 0; i2 < wanted; i2++) {
            hud_wanted.get(i2).setBackgroundResource(R.drawable.ic_y_star);
        }
    }
    //пизда пакажи
    public void ShowHud() {
        Utils.ShowLayout(hud_layout, false);
    }

    public void HideHud() {
        Utils.HideLayout(hud_layout, false);
    }

    private void openMagazine()
    {
        Timer t = new Timer();
        t.schedule(new TimerTask(){
            @Override
            public void run() {
                NvEventQueueActivity.getInstance().openMagazine();
            }
        }, 300L);
    }
    private void openMenu()
    {
        Timer t = new Timer();
        t.schedule(new TimerTask(){
            @Override
            public void run() {
                NvEventQueueActivity.getInstance().openMenu();
            }
        }, 300L);
    }
}
