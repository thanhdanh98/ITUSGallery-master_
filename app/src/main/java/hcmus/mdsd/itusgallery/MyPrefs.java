package hcmus.mdsd.itusgallery;

import android.content.Context;
import android.content.SharedPreferences;

public class MyPrefs {
    SharedPreferences myPrefs;

    public MyPrefs(Context context){
        myPrefs = context.getSharedPreferences("data",Context.MODE_PRIVATE);
    }

    void setNightModeState(int state){
        SharedPreferences.Editor editor = myPrefs.edit();
        editor.putInt("NightMode",state);
        editor.apply();
    }

    int loadNightModeState(){
        return myPrefs.getInt("NightMode", 0);
    }

    public void setPassword(String password){
        SharedPreferences.Editor editor = myPrefs.edit();
        editor.putString("Password",password);
        editor.apply();
    }

    public String getPassword(){
        return myPrefs.getString("Password","");
    }

    void setPassMode(Integer passMode){
        SharedPreferences.Editor editor = myPrefs.edit();
        editor.putInt("PassMode",passMode);
        editor.apply();
    }

    Integer getPassMode(){
        //passMode = 0 là chế độ login, passMode = 1 là chế độ sửa đổi password, passMode = 2 là chế độ xoá password
        return myPrefs.getInt("PassMode",0);
    }

    void SetNumberOfColumns(Integer[] columns) {
        SharedPreferences.Editor editor = myPrefs.edit();
        editor.putInt("columnVertical",columns[0]);
        editor.putInt("columnHorizontal",columns[1]);
        editor.apply();
    }

    Integer[] getNumberOfColumns() {
        Integer[] columns = new Integer[2];
        columns[0] = myPrefs.getInt("columnVertical", 4);
        columns[1] = myPrefs.getInt("columnHorizontal", 6);
        return columns;
    }

    void setStartHour(int hour){
        SharedPreferences.Editor editor = myPrefs.edit();
        editor.putInt("StartHour",hour);
        editor.apply();
    }

    int loadStartHour(){
        return myPrefs.getInt("StartHour", 0);
    }

    void setStartMinute(int minute){
        SharedPreferences.Editor editor = myPrefs.edit();
        editor.putInt("StartMinute",minute);
        editor.apply();
    }

    int loadStartMinute(){
        return myPrefs.getInt("StartMinute", 0);
    }

    void setEndHour(int hour){
        SharedPreferences.Editor editor = myPrefs.edit();
        editor.putInt("EndHour",hour);
        editor.apply();
    }

    int loadEndHour(){
        return myPrefs.getInt("EndHour", 0);
    }

    void setEndMinute(int minute){
        SharedPreferences.Editor editor = myPrefs.edit();
        editor.putInt("EndMinute",minute);
        editor.apply();
    }

    int loadEndMinute(){
        return myPrefs.getInt("EndMinute", 0);
    }

    public void isFirstLaunch(boolean isFirst){
        SharedPreferences.Editor editor = myPrefs.edit();
        editor.putBoolean("FirstLaunch",isFirst);
        editor.apply();
    }

    public boolean loadIsFirstLaunch(){
        return myPrefs.getBoolean("FirstLaunch", true);
    }
}