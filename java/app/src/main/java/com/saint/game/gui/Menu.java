package com.saint.game.gui;

import android.app.Activity;
import android.view.View;
import android.view.animation.Animation;
import android.view.animation.AnimationUtils;
import android.widget.*;
import com.nvidia.devtech.NvEventQueueActivity;
import androidx.constraintlayout.widget.ConstraintLayout;

import com.saint.game.R;
import com.saint.game.gui.util.Utils;

import java.util.Timer;
import java.util.TimerTask;

public class Menu {
    public Activity activity;

    public Animation animation;

    public ConstraintLayout constraintLayout;

    public ImageView menu_clossed; // закрыть меню

    public ImageView btn_promo; // промокод

    public ImageView btn_menu_cars; // машина

    public ImageView btn_menu_gps; // навигатор

    public ImageView btn_family; // семья

    public ImageView btn_menu_zaloba; // жалобы

    public ImageView btn_anim; // анимация

    public ImageView btn_menu_donate; // донат

    public ImageView btn_menu_inv; // инвентарь

    public ImageView btn_menu_personal; // статистика

    public ImageView btn_menu_settings; // настройки

    public Menu(Activity aactivity){

        menu_clossed = aactivity.findViewById(R.id.menu_clossed); // закрытие меню меню

        constraintLayout = aactivity.findViewById(R.id.constraintlayoutMenu);

        animation = AnimationUtils.loadAnimation(aactivity, R.anim.button_click);

        btn_promo = aactivity.findViewById(R.id.btn_menu_promo); // промо

        btn_menu_cars = aactivity.findViewById(R.id.btn_menu_cars); // машина

        btn_menu_gps = aactivity.findViewById(R.id.btn_menu_gps); // навигатор

        btn_family = aactivity.findViewById(R.id.btn_family); // навигатор

        btn_menu_zaloba = aactivity.findViewById(R.id.btn_menu_zaloba); // репорт

        btn_anim = aactivity.findViewById(R.id.btn_anim); // анимации

        btn_menu_donate = aactivity.findViewById(R.id.btn_menu_donate); // донат

        btn_menu_inv = aactivity.findViewById(R.id.btn_menu_inv); // инвентарь

        btn_menu_personal = aactivity.findViewById(R.id.btn_menu_personal); // статистика

        btn_menu_settings = aactivity.findViewById(R.id.btn_menu_settings); // настройки

        aactivity.findViewById(R.id.menu_clossed).setOnClickListener(view -> { // значение кнопки выйти
            close();
        });
        btn_promo.setOnClickListener( view -> { // значение промокода
            openPromo();
            close();
        });
        btn_menu_cars.setOnClickListener( view -> { // значение машины
            openCar();
            close();
        });
        btn_menu_gps.setOnClickListener( view -> { // значение навигатора
            openNavigator();
            close();
        });
        btn_family.setOnClickListener( view -> { // значение семьи
            openFamily();
            close();
        });
        btn_menu_zaloba.setOnClickListener( view -> { // значение репорта
            openReport();
            close();
        });
        btn_anim.setOnClickListener( view -> { // значение анимации
            openAnim();
            close();
        });
        btn_menu_donate.setOnClickListener( view -> { // значение доната
            openDonate();
            close();
        });
        btn_menu_inv.setOnClickListener( view -> { // значение инвентаря
            openInv();
            close();
        });
        btn_menu_personal.setOnClickListener( view -> { // значение статистики
            openStatistik();
            close();
        });
        btn_menu_settings.setOnClickListener( view -> { // значение настроек
            openSettings();
            close();
        });

        Utils.HideLayout(constraintLayout, false);

    }
    public void ShowMenu(boolean isRegister) { // показать меню
        Utils.ShowLayout(constraintLayout, true);
    }

    public void close() { // закрыть меню
        Utils.HideLayout(constraintLayout, true);
        NvEventQueueActivity.getInstance().togglePlayer(0);
    }
    private void openPromo() //промокод
    {
        Timer t = new Timer();
        t.schedule(new TimerTask(){
            @Override
            public void run() {
                NvEventQueueActivity.getInstance().openPromo();
            }
        }, 300L);
    }
    private void openCar() //машина
    {
        Timer t = new Timer();
        t.schedule(new TimerTask(){
            @Override
            public void run() {
                NvEventQueueActivity.getInstance().openCar();
            }
        }, 300L);
    }
    private void openNavigator() //навигатор
    {
        Timer t = new Timer();
        t.schedule(new TimerTask(){
            @Override
            public void run() {
                NvEventQueueActivity.getInstance().openNavigator();
            }
        }, 300L);
    }
    private void openFamily() //семья
    {
        Timer t = new Timer();
        t.schedule(new TimerTask(){
            @Override
            public void run() {
                NvEventQueueActivity.getInstance().openFamily();
            }
        }, 300L);
    }
    private void openReport() //репорт
    {
        Timer t = new Timer();
        t.schedule(new TimerTask(){
            @Override
            public void run() {
                NvEventQueueActivity.getInstance().openReport();
            }
        }, 300L);
    }
    private void openAnim() //анимации
    {
        Timer t = new Timer();
        t.schedule(new TimerTask(){
            @Override
            public void run() {
                NvEventQueueActivity.getInstance().openAnim();
            }
        }, 300L);
    }
    private void openDonate() //донат
    {
        Timer t = new Timer();
        t.schedule(new TimerTask(){
            @Override
            public void run() {
                NvEventQueueActivity.getInstance().openDonate();
            }
        }, 300L);
    }
    private void openInv() //инвентарь
    {
        Timer t = new Timer();
        t.schedule(new TimerTask(){
            @Override
            public void run() {
                NvEventQueueActivity.getInstance().openInv();
            }
        }, 300L);
    }
    private void openStatistik() //статистика
    {
        Timer t = new Timer();
        t.schedule(new TimerTask(){
            @Override
            public void run() {
                NvEventQueueActivity.getInstance().openStatistik();
            }
        }, 300L);
    }
    private void openSettings() //настройки
    {
        Timer t = new Timer();
        t.schedule(new TimerTask(){
            @Override
            public void run() {
                NvEventQueueActivity.getInstance().openSettings();
            }
        }, 300L);
    }
}