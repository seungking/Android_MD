package kiman.androidmd

import android.app.AlertDialog
import android.content.DialogInterface
import android.content.SharedPreferences
import android.os.Bundle
import android.util.Log
import android.widget.Toast
import androidx.preference.PreferenceFragmentCompat



class SettingsFragment : PreferenceFragmentCompat() {
    override fun onCreatePreferences(savedInstanceState: Bundle?, rootKey: String?) {
        addPreferencesFromResource(R.xml.preferences)
        preferenceManager.sharedPreferences.registerOnSharedPreferenceChangeListener(prefListener)
    }

    var prefListener:SharedPreferences.OnSharedPreferenceChangeListener =
        object : SharedPreferences.OnSharedPreferenceChangeListener {

        override fun onSharedPreferenceChanged(sharedPreferences: SharedPreferences, key: String) {
            if (key == "switch_background") {
                Log.d("PrefLog", "switch_background is changed")
                Log.d("PrefLog", sharedPreferences.getBoolean("switch_background", true).toString())
                if (sharedPreferences.getBoolean("switch_background", true)) {
                    val string_back:String = "백그라운드에서의 실행을 허용합니다."
                    showAlertPopup(string_back)
                }
            }

            else if (key == "switch_limit") {
                Log.d("PrefLog", "switch_limit is changed")
                if (sharedPreferences.getBoolean("switch_limit", true)) {
                    val string_limit:String = "실행 가능한 동작의 수를 5개로 제한합니다." + "해제시 인식률이 떨어집니다."
                    showAlertPopup(string_limit)
                }
            }
        }
    }

    private fun showAlertPopup(string: String) {
        val alertDialog = AlertDialog.Builder(this.activity)
            .setTitle(string)
            .setPositiveButton("OK", null)
            .create()

        alertDialog.show()
    }
}
