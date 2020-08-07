package kiman.androidmd

import android.content.SharedPreferences
import android.os.Bundle
import android.util.Log
import androidx.preference.PreferenceFragmentCompat

class SettingsFragment : PreferenceFragmentCompat() {

    var run : Boolean = false;
    override fun onCreatePreferences(savedInstanceState: Bundle?, rootKey: String?) {
        addPreferencesFromResource(R.xml.preferences)
        preferenceManager.sharedPreferences.registerOnSharedPreferenceChangeListener(prefListener)
    }

    var prefListener: SharedPreferences.OnSharedPreferenceChangeListener =
        object : SharedPreferences.OnSharedPreferenceChangeListener {

            override fun onSharedPreferenceChanged(sharedPreferences: SharedPreferences, key: String) {
                if (key == "switch_background") {
                    Log.d("PrefLog", "switch_background is changed")
                    run = sharedPreferences.getBoolean("switch_background",false); // key에 저장된 값을 가져옴

                    if(run) (activity as MainActivity).startMotionCatch()
                    else (activity as MainActivity).stopMotionCatch()

                }

                else if (key == "switch_limit") {
                    Log.d("PrefLog", "switch_limit is changed")
                }
            }
        }
}
