package kiman.androidmd;

import android.content.Context;
import android.content.SharedPreferences;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.drawable.BitmapDrawable;
import android.graphics.drawable.Drawable;
import android.preference.PreferenceManager;
import android.util.Base64;

import androidx.appcompat.app.AppCompatActivity;

import org.json.JSONArray;
import org.json.JSONException;

import java.io.ByteArrayOutputStream;
import java.util.ArrayList;

//로커 DB 접근 클래스
public class ManagePref extends AppCompatActivity {

//    static private ArrayList<String> appname = new ArrayList<String>();
//    static private ArrayList<String> packagename = new ArrayList<String>();
//    static private ArrayList<String> appicon = new ArrayList<String>();
//    static private ArrayList<String> date = new ArrayList<String>();
//
//    public ManagePref(){
//        appname = getStringArrayPref(this,"appname");
//        packagename = getStringArrayPref(this,"packagename");
//        appicon = getStringArrayPref(this,"appicon");
//        date = getStringArrayPref(this,"date");
//    }
//
//    public void setPref(String name, String packagen, String icon, String date_){
//
//        appname.add(name);
//        packagename.add(packagen);
//        appicon.add(icon);
//        date.add(date_);
//
//        setStringArrayPref(this,"appname",appname);
//        setStringArrayPref(this,"packagename",packagename);
//        setStringArrayPref(this,"appicon",appicon);
//        setStringArrayPref(this,"date",date);
//    }


//    public ArrayList<String> getAppname(){
//        return appname;
//    }
//    public ArrayList<String> getPackagename(){
//        return packagename;
//    }
//    public ArrayList<String> getAppicon(){
//        return appicon;
//    }
//    public ArrayList<String> getDate(){
//        return date;
//    }

    public ArrayList<String> getStringArrayPref(Context context, String str) {
        String string = PreferenceManager.getDefaultSharedPreferences(context).getString(str, null);
        ArrayList arrayList = new ArrayList();
        if (string != null) {
            try {
                JSONArray jSONArray = new JSONArray(string);
                for (int i = 0; i < jSONArray.length(); i++) {
                    arrayList.add(jSONArray.optString(i));
                }
            } catch (JSONException e) {
                e.printStackTrace();
            }
        }
        return arrayList;
    }

    public void setStringArrayPref(Context context, String str, ArrayList<String> arrayList) {
        SharedPreferences.Editor edit = PreferenceManager.getDefaultSharedPreferences(context).edit();
        JSONArray jSONArray = new JSONArray();
        for (int i = 0; i < arrayList.size(); i++) {
            jSONArray.put(arrayList.get(i));
        }
        if (arrayList.isEmpty()) {
            edit.putString(str, null);
        } else {
            edit.putString(str, jSONArray.toString());
        }
        edit.apply();
    }

    public Bitmap StringToBitmap(String str) {
        try {
            byte[] decode = Base64.decode(str, 0);
            return BitmapFactory.decodeByteArray(decode, 0, decode.length);
        } catch (Exception e) {
            e.getMessage();
            return null;
        }
    }

    public String BitmapToString(Bitmap bitmap) {
        ByteArrayOutputStream byteArrayOutputStream = new ByteArrayOutputStream();
        bitmap.compress(Bitmap.CompressFormat.PNG, 70, byteArrayOutputStream);
        return Base64.encodeToString(byteArrayOutputStream.toByteArray(), 0);
    }

    public Drawable BitmapToDrawable(Bitmap bitmap){
        Drawable drawable = new BitmapDrawable(bitmap);
        return drawable;
    }
}