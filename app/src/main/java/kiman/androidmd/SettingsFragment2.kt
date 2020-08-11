package kiman.androidmd

import android.os.Bundle
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.LinearLayout
import androidx.fragment.app.Fragment
import androidx.navigation.findNavController

class SettingsFragment2: Fragment() {
    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View? {
        val view = inflater.inflate(R.layout.fragment_settings, container, false)

        val cat_motionLimit = view.findViewById<LinearLayout>(R.id.cat_motionLimit)
        val cat_appInfor = view.findViewById<LinearLayout>(R.id.cat_appInfor)
        cat_motionLimit?.setOnClickListener {view->
            Log.d("cat_motionLimit", "Activating")
            view.findNavController().navigate(R.id.limit_settings_dest)
        }
        cat_appInfor?.setOnClickListener {view->
            Log.d("cat_appInfor", "Activating")
        }
        return view
    }

}