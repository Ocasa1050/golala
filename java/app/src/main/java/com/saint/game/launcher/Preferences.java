package com.saint.game.launcher;


public class Preferences {
    public static String nickname = "";

    public static String getNick(){ return nickname;}
    public static String setNick(String string){
        return nickname = string;
    }
}
