package jp.juggler.screenshotbutton;


import static jp.juggler.Constants.COLOR_DATA;
import static jp.juggler.Constants.DEFULT_CHECK_SCRREN;
import static jp.juggler.Constants.INFORMATIONDIALOG;
import static jp.juggler.Constants.MY_SHARED_PREFERENCE;
import static jp.juggler.Constants.RECOVERYCLICK;
import static jp.juggler.Constants.SECURITY_CHECK_CLICK;

import android.content.Context;
import android.content.SharedPreferences;


public class MySharedPreference {
    private static MySharedPreference myPreferences;
    private static SharedPreferences sharedPreferences;
    private static SharedPreferences.Editor editor;

    private MySharedPreference(Context context) {
        sharedPreferences = context.getSharedPreferences(MY_SHARED_PREFERENCE, Context.MODE_PRIVATE);
        editor = sharedPreferences.edit();
        editor.apply();
    }

    public static MySharedPreference getPreferences(Context context) {
        if (myPreferences == null) myPreferences = new MySharedPreference(context);
        return myPreferences;
    }


    public boolean getSecurityCheckClick() {
        return sharedPreferences.getBoolean(SECURITY_CHECK_CLICK, false);
    }


    public void setSecurityCheckClick(boolean checkClick) {
        editor.putBoolean(SECURITY_CHECK_CLICK, checkClick);
        editor.commit();
    }


    public boolean getDefuActitivtyCheckClick() {
        return sharedPreferences.getBoolean(DEFULT_CHECK_SCRREN, false);
    }


    public void setDefuActitivtyCheckClick(boolean checkClick) {
        editor.putBoolean(DEFULT_CHECK_SCRREN, checkClick);
        editor.commit();
    }


    public boolean getInformationDialogCheckClick() {
        return sharedPreferences.getBoolean(INFORMATIONDIALOG, false);
    }


    public void setInformationDialogCheckClick(boolean check_information_dailog) {
        editor.putBoolean(INFORMATIONDIALOG, check_information_dailog);
        editor.commit();
    }


    public String getSecurityQuestionData() {
        return sharedPreferences.getString(RECOVERYCLICK, "");
    }


    public boolean get_active_time_onoff() {
        return sharedPreferences.getBoolean("on_off", false);
    }


    public void set_active_time_onoff(boolean z) {
        editor.putBoolean("on_off", z);
        editor.commit();
    }


    public boolean get_Pattern_active_onoff() {
        return sharedPreferences.getBoolean("pattern_on_off", false);
    }

    public void set_Pattern_active_onoff(boolean z) {
        editor.putBoolean("pattern_on_off", z);
        editor.commit();
    }


    public boolean get_active_Pin_onoff() {
        return sharedPreferences.getBoolean("PinCheckCondition", false);
    }

    public void set_active_Pin_onoff(boolean z) {
        editor.putBoolean("PinCheckCondition", z);
        editor.commit();
    }


    public void setColorData(String colorName) {
        editor.putString(COLOR_DATA, colorName);
        editor.apply();
    }

    public String getColorData() {
        return sharedPreferences.getString(COLOR_DATA, "#0066FF");
    }

}
