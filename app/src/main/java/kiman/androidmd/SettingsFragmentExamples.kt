package kiman.androidmd

import android.app.ActivityManager
import android.bluetooth.BluetoothAdapter
import android.content.Context
import android.content.Context.AUDIO_SERVICE
import android.content.pm.ApplicationInfo
import android.content.pm.PackageManager
import android.hardware.camera2.CameraManager
import android.media.AudioManager
import android.net.wifi.WifiManager
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Button
import androidx.fragment.app.Fragment

class SettingsFragmentExamples : Fragment() {
    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View? {
        val view = inflater.inflate(R.layout.fragment_settings_examples, container, false)

        val btButton: Button = view.findViewById(R.id.btButton)
        btButton.setOnClickListener{
            val btAdapter = BluetoothAdapter.getDefaultAdapter()
            if(btAdapter.isEnabled()) {
                btAdapter.disable()
            }
            else {
                btAdapter.enable()
            }
        }

        val wfButton: Button = view.findViewById(R.id.wifiButton)
        wfButton.setOnClickListener{
            var wfManager: WifiManager = context?.getSystemService(Context.WIFI_SERVICE) as WifiManager
            if (wfManager.isWifiEnabled) {
                wfManager.isWifiEnabled = false
            }
            else {
                wfManager.isWifiEnabled = true
            }
        }

        val flButton : Button = view.findViewById(R.id.flashButton)
        var isFlashOn:Boolean = false
        flButton.setOnClickListener{
            var camManager:CameraManager = context?.getSystemService(Context.CAMERA_SERVICE) as CameraManager
            val camID = camManager.cameraIdList[0]
            if (isFlashOn) {
                camManager.setTorchMode(camID, false)
                isFlashOn = false
            }
            else {
                camManager.setTorchMode(camID, true)
                isFlashOn = true
            }
        }
        val killButton : Button = view.findViewById(R.id.killSwitchButton)
        killButton.setOnClickListener {
            val packages: List<ApplicationInfo>
            val pm: PackageManager?
            pm = context?.packageManager
            //get a list of installed apps.
            //get a list of installed apps.
            packages = pm?.getInstalledApplications(0) as List<ApplicationInfo>
            val activityManager = context?.getSystemService(Context.ACTIVITY_SERVICE) as ActivityManager
            for (p in packages) {
                // 안드로이드의 기본앱일 경우 종료시키지 않는 조건문
//                if((p.flags and ApplicationInfo.FLAG_SYSTEM) == 1 ) {
//                    continue
//                }
                if (p.packageName.equals("kiman.androidmd")) {
                    continue
                }
                activityManager.killBackgroundProcesses(p.packageName)
            }
        }
        val silentButton : Button = view.findViewById(R.id.silentButton)
        silentButton.setOnClickListener {
            var audioManager = context?.getSystemService(AUDIO_SERVICE) as AudioManager
            if (audioManager.ringerMode == AudioManager.RINGER_MODE_NORMAL) {
                audioManager.setRingerMode(AudioManager.RINGER_MODE_VIBRATE)
            }
//            else if (audioManager.ringerMode == AudioManager.RINGER_MODE_SILENT){
//                audioManager.setRingerMode(AudioManager.RINGER_MODE_VIBRATE)
//            }
            else {
                audioManager.setRingerMode(AudioManager.RINGER_MODE_NORMAL)
            }
        }


        return view
    }
}

