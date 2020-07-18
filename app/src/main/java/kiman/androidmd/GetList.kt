package kiman.androidmd

import android.app.Activity
import android.app.Application
import android.content.Context
import android.util.Log
import androidx.appcompat.app.AppCompatActivity
import java.util.*
import kotlin.collections.ArrayList


class GetList : Application(){

    lateinit var context: Context

    init{
        instance = this
    }

    companion object {
        private var instance: GetList? = null
        fun applicationContext() : Context {
            return instance!!.applicationContext
        }
    }
    override fun onCreate() {
        super.onCreate()
    }

    public fun getlist(): MutableList<Email.EmailThread> {
        Log.d("Log1","setupthreadlist1");
        val managepref : ManagePref =  ManagePref()

        val appname : ArrayList<String> = managepref.getStringArrayPref(applicationContext(),"appname");
        val packagename : ArrayList<String> = managepref.getStringArrayPref(applicationContext(), "packagename")
        val appicon : ArrayList<String> = managepref.getStringArrayPref(applicationContext(), "appicon")
        val date : ArrayList<String> = managepref.getStringArrayPref(applicationContext(), "date")
        Log.d("Log1","setupthreadlist2");
//        appname = managepref.getStringArrayPref(context, "appname")
//        packagename = managepref.getStringArrayPref(context, "packagename")
//        appicon = managepref.getStringArrayPref(context, "appicon")
//        date = managepref.getStringArrayPref(context, "date")
        Log.d("Log1","setupthreadlist3 appname size : " + appname.size + packagename.size + appicon.size + date.size);
        val list = mutableListOf<Email.EmailThread>();

        Log.d("Log1","insert start")
        for( i in 0 until (appname.size) ){
            Log.d("LOG1",i.toString())
            Log.d("Log1",appname.get(i) + "  " +  packagename.get(i));
            var temp : Email.EmailThread = Email.EmailThread(
                id = 0,
                sender = Email.Person("MOTION 1", managepref.StringToBitmap(appicon.get(i))),
                subject = appname.get(i),
                emails = listOf(
                    Email(
                        excerpt = date.get(i),
                        body = "MOTION 1",
                        timestamp = packagename.get(i))))
            list.add(temp)
        }
        Log.d("Log1","list size : " + list.size)

        return list
    }

}