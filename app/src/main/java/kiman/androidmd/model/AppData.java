package kiman.androidmd.model;
 
import android.app.Application;
import android.content.pm.PackageInfo;

public class AppData extends Application {

    //어플 하나 변수로 지정
    PackageInfo packageInfo;
 
    public PackageInfo getPackageInfo() {
        return packageInfo;
    }
 
    public void setPackageInfo(PackageInfo packageInfo) {
        this.packageInfo = packageInfo;
    }
}