package kiman.androidmd.fragment

import android.content.Intent
import android.net.Uri
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.LinearLayout
import android.widget.TextView
import androidx.fragment.app.Fragment
import kiman.androidmd.BuildConfig
import kiman.androidmd.R


class SettingsFragmentInfor : Fragment() {
    override fun onCreateView( inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View? {
        val view = inflater.inflate(R.layout.fragment_settings_infor, container, false)
        val versionName = BuildConfig.VERSION_NAME
        val appName = getString(R.string.app_name)
        val appVersionText = view.findViewById<TextView>(R.id.app_version_text)
        appVersionText.setText("Version: " + versionName)
        val appNameText = view.findViewById<TextView>(R.id.app_name_text)
        appNameText.setText(appName)
        val githubButton = view.findViewById<LinearLayout>(R.id.github_button)
        githubButton.setOnClickListener { view->
            val uri: Uri = Uri.parse("https://github.com/seungking/Android_MD")
            val intent = Intent(Intent.ACTION_VIEW, uri)
            startActivity(intent)
        }

        return view
    }

}