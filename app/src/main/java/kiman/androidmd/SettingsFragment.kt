package kiman.androidmd

import android.content.SharedPreferences
import android.os.Bundle
import android.util.Log
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
            }

            else if (key == "switch_limit") {
                Log.d("PrefLog", "switch_limit is changed")
            }
        }
    }
}
