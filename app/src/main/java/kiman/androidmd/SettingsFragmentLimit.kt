package kiman.androidmd

import android.app.AlertDialog
import android.content.Context
import android.content.SharedPreferences
import android.os.Bundle
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import androidx.preference.PreferenceManager
import com.suke.widget.SwitchButton

class SettingsFragmentLimit : Fragment() {


    override fun onCreateView( inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View? {
        val view = inflater.inflate(R.layout.fragment_settings_limit, container, false)

        val switch_background : SwitchButton = view.findViewById(R.id.switch_background)

//        val sharedPreferences: SharedPreferences = view.context.getSharedPreferences(
//            "switch_start_check",
//            Context.MODE_PRIVATE
//        )
//        //맨처음 시작했는지 확인
//        val check_switch_value: SharedPreferences = PreferenceManager
//            .getDefaultSharedPreferences(view.context)
//
//        val check_switch = check_switch_value.getBoolean("start_background", true)
//        Log.d("log1", "check switch  : "+ check_switch)
//        switch_background.setChecked(true);

        switch_background.setOnCheckedChangeListener { CompoundButton, onSwitch ->
            if(onSwitch){
                val pref: SharedPreferences = PreferenceManager
                    .getDefaultSharedPreferences(view.context)
                var editor  = pref.edit()
                editor.putBoolean("switch_background",true)
                editor.commit()
                (activity as MainActivity).startMotionCatch()
                val string_back: String = "백그라운드에서의 실행을 허용합니다."
                showAlertPopup(string_back)
            }
            else{
                val pref: SharedPreferences = PreferenceManager
                    .getDefaultSharedPreferences(view.context)
                var editor  = pref.edit()
                editor.putBoolean("switch_background",false)
                editor.commit()
                (activity as MainActivity).stopMotionCatch()
            }
        }

        return view
    }

    private fun showAlertPopup(string: String) {
        val alertDialog = AlertDialog.Builder(this.activity)
            .setTitle(string)
            .setPositiveButton("OK", null)
            .create()

        alertDialog.show()
    }

}