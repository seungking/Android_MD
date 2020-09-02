package kiman.androidmd

import android.app.ActivityManager
import android.bluetooth.BluetoothAdapter
import android.content.Context
import android.content.Intent
import android.content.SharedPreferences
import android.content.pm.ApplicationInfo
import android.content.pm.PackageManager
import android.hardware.Sensor
import android.hardware.SensorEvent
import android.hardware.SensorEventListener
import android.hardware.SensorManager
import android.hardware.camera2.CameraManager
import android.media.AudioManager
import android.net.wifi.WifiManager
import android.os.Bundle
import android.util.Log
import android.view.MenuItem
import androidx.appcompat.app.AppCompatActivity
import androidx.fragment.app.Fragment
import androidx.fragment.app.FragmentPagerAdapter
import androidx.preference.PreferenceManager
import androidx.viewpager.widget.ViewPager
import com.google.android.material.bottomnavigation.BottomNavigationView
import kiman.androidmd.adapter.ThreadsAdapter
import kiman.androidmd.fragment.BaseFragment
import kiman.androidmd.model.Email
import kiman.androidmd.service.BackPressCloseHandler
import kiman.androidmd.service.ManagePref
import kiman.androidmd.service.MyService
import kotlinx.android.synthetic.main.activity_inbox.*
import kotlinx.android.synthetic.main.activity_main_2.*
import java.util.*
import kotlin.collections.ArrayList

class MainActivity : AppCompatActivity(),
    ViewPager.OnPageChangeListener,
    BottomNavigationView.OnNavigationItemReselectedListener,
    BottomNavigationView.OnNavigationItemSelectedListener {


    // overall back stack of containers
    val backStack = Stack<Int>()

    // list of base destination containers
    val fragments = listOf(
        BaseFragment.newInstance(R.layout.content_home_base, R.id.toolbar_home, R.id.nav_host_home),
        BaseFragment.newInstance(R.layout.content_settings_base, R.id.toolbar_settings, R.id.nav_host_settings))
    //BaseFragment.newInstance(R.layout.content_library_base, R.id.toolbar_library, R.id.nav_host_library))


    // map of navigation_id to container index
//    private val indexToPage = mapOf(0 to R.id.home, 1 to R.id.library, 2 to R.id.settings)
    val indexToPage = mapOf(0 to R.id.home, 1 to R.id.settings)
    var list = mutableListOf<Email.EmailThread>();
    val threadsAdapter = ThreadsAdapter()

    var packagename = ArrayList<String>()
    var mpackagename = ArrayList<String>()
    var patterns = ArrayList<String>()
    var mpatterns_list = ArrayList<ArrayList<ArrayList<String>>>()
    var patterns_list = ArrayList<ArrayList<ArrayList<String>>>()
    val managePref = ManagePref()
    var runningservice = false;

    //센서들
    var mSensorManager: SensorManager? = null
    var mGyroLis: SensorEventListener? = null
    var mGgyroSensor: Sensor? = null

    /*Used for Accelometer & Gyroscoper*/ //    private SensorManager mSensorManager = null;
    var userSensorListner: UserSensorListner? = null
    var mGyroscopeSensor: Sensor? = null
    var mAccelerometer: Sensor? = null

    /*Sensor variables*/
    var mGyroValues = FloatArray(3)
    var mAccValues = FloatArray(3)
    var mAccPitch = 0.0
    var mAccRoll:kotlin.Double = 0.0
    val mAccYaw = 0.0

    /*for unsing complementary fliter*/
    val a = 0.2f
    val NS2S = 1.0f / 1000000000.0f
    var pitch = 0.0
    var roll:kotlin.Double = 0.0
    var yaw:kotlin.Double = 0.0
    var prev_pitch = 0.0
    var prev_roll:kotlin.Double = 0.0
    var prev_yaw:kotlin.Double = 0.0
    var r = 0.0
    var p:kotlin.Double = 0.0
    var timestamp = 0.0
    var dt = 0.0
    var temp = 0.0
    val running = false
    var gyroRunning = false
    var accRunning = false

    //조건들
    var Arrsize = 5
    var threshold = 2
    var Arrsize_catch = 10
    var matcing_rate = 60
    var temp_a: String? = null

    //모션 저장할 공간
    var Store_a = java.util.ArrayList<String>()
    //LCS 계산 배열
    var NIL: Array<IntArray>? = null

    private var backPressCloseHandler: BackPressCloseHandler? = null

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main_2)

        // setup main view pager
        main_pager.addOnPageChangeListener(this)
        main_pager.adapter = ViewPagerAdapter()
        main_pager.post(this::checkDeepLink)
        main_pager.offscreenPageLimit = fragments.size

        // set bottom nav
        bottom_nav.setOnNavigationItemSelectedListener(this)
        bottom_nav.setOnNavigationItemReselectedListener(this)

        fab.setOnClickListener {
            stopMotionCatch()
            val nextIntent = Intent(this, AppInfoActivity::class.java)
            startActivity(nextIntent)
//            finish()
        }

        backPressCloseHandler =
            BackPressCloseHandler(this);

        // initialize backStack with elements
        if (backStack.empty()) backStack.push(0)

        backPressCloseHandler =
            BackPressCloseHandler(this);

        //센서 켬 (여긴 왜켯지)
        mSensorManager = getSystemService(Context.SENSOR_SERVICE) as SensorManager
        mGgyroSensor = mSensorManager!!.getDefaultSensor(Sensor.TYPE_LINEAR_ACCELERATION)
        mGyroLis = AccelerometerListener()

        userSensorListner = UserSensorListner()
        mGyroscopeSensor = mSensorManager!!.getDefaultSensor(Sensor.TYPE_GYROSCOPE)
        mAccelerometer = mSensorManager!!.getDefaultSensor(Sensor.TYPE_ACCELEROMETER)
    }

    /// BottomNavigationView ItemSelected Implementation
    override fun onNavigationItemSelected(item: MenuItem): Boolean {
        val position = indexToPage.values.indexOf(item.itemId)
        if (main_pager.currentItem != position) setItem(position)
        return true
    }

    override fun onNavigationItemReselected(item: MenuItem) {
        val position = indexToPage.values.indexOf(item.itemId)
        val fragment = fragments[position]
        fragment.popToRoot()
    }

    override fun onResume() {
        super.onResume()
        Log.d("log1","main resume!!")

//        // setup main view pager
//        main_pager.addOnPageChangeListener(this)
//        main_pager.adapter = ViewPagerAdapter()
//        main_pager.post(this::checkDeepLink)
//        main_pager.offscreenPageLimit = fragments.size

        val pref: SharedPreferences = PreferenceManager
            .getDefaultSharedPreferences(this)
        val switch_backbround : Boolean = pref.getBoolean("switch_background",false)
        if(switch_backbround) {
            startMotionCatch()
            runningservice = true;
        }
    }



    override fun onBackPressed() {
        if(inbox_recyclerview.expandedItem.viewIndex==0) inbox_recyclerview.collapse()
        else backPressCloseHandler!!.onBackPressed();
    }

    interface IOnBackPressed {
        fun onBackPressed(): Boolean
    }

    /// OnPageSelected Listener Implementation
    override fun onPageScrollStateChanged(state: Int) {}

    override fun onPageScrolled(p0: Int, p1: Float, p2: Int) {}

    override fun onPageSelected(page: Int) {
        val itemId = indexToPage[page] ?: R.id.home
        if (bottom_nav.selectedItemId != itemId) bottom_nav.selectedItemId = itemId
    }

    private fun setItem(position: Int) {
        main_pager.currentItem = position
        backStack.push(position)
    }

    private fun checkDeepLink() {
        fragments.forEachIndexed { index, fragment ->
            val hasDeepLink = fragment.handleDeepLink(intent)
            if (hasDeepLink) setItem(index)
        }
    }

    inner class ViewPagerAdapter : FragmentPagerAdapter(supportFragmentManager) {

        override fun getItem(position: Int): Fragment = fragments[position]

        override fun getCount(): Int = fragments.size

    }

    fun updatepattern(){
        mpackagename.clear();
        mpatterns_list.clear();
        Log.d("log1","updatepattern")
        var mpackage = managePref.getStringArrayPref(this, "packagename")
        patterns = managePref.getStringArrayPref(this, "patterns")

        var switch = managePref.getStringArrayPref(this, "switch")
//            for (i in 0..switch.size()-1) {
        Log.d("log1",packagename.toString())
        Log.d("log1",switch.toString())

        if(patterns.size>0) {
            patterns_list.clear()
            for (i in 0..patterns.size-1) {
                if(switch.get(i)=="on") {
                    var cur_pattern = ArrayList<ArrayList<String>>()
                    val tokens = patterns[i].split("]");

                    for (j in 0..tokens.size - 2) {
                        val temp = tokens[j].subSequence(1, tokens[j].length).trim().split(",");
                        val temp2 = ArrayList<String>()
                        for (k in 0..temp.size - 1) temp2.add(temp[k])

                        cur_pattern.add(temp2)
                    }
                    Log.d("log1","pattenrs list pacakge name : "+ i + "   " + mpackage.get(i))
                    mpackagename.add(mpackage.get(i))
                    mpatterns_list.add(cur_pattern)
                }
            }
            Log.d("log1","pattern list size : " + mpatterns_list.size)
            for(j in 0..mpatterns_list.size-1) Log.d("log1", "patterns "+j+" : "+mpatterns_list.get(j))
        }
    }

    fun startMotionCatch(){
        Log.d("log1","Start Motion Catch");
        startService(Intent(applicationContext,
            MyService::class.java))

        updatepattern()
        if(!runningservice) {
            startService(Intent(applicationContext, MyService::class.java))
            //센서켬

            //센서켬
            mSensorManager!!.registerListener(mGyroLis, mGgyroSensor, SensorManager.SENSOR_DELAY_UI)
            mSensorManager!!.registerListener(
                userSensorListner,
                mGyroscopeSensor,
                SensorManager.SENSOR_DELAY_UI
            )
            mSensorManager!!.registerListener(
                userSensorListner,
                mAccelerometer,
                SensorManager.SENSOR_DELAY_UI
            )

            runningservice=true
        }
    }

    fun stopMotionCatch(){
        Log.d("log1","Stop Motion Catch")

        if(runningservice) {
            stopService(Intent(applicationContext, MyService::class.java))
            //센서끄고 패턴에 저장
            mSensorManager!!.unregisterListener(mGyroLis)
            mSensorManager!!.unregisterListener(userSensorListner)
            runningservice = false
        }
    }

    inner class AccelerometerListener : SensorEventListener {
        override fun onSensorChanged(event: SensorEvent) {
            //센서 바뀔때마다
            var Check_sum = ""
            var check_sum = 0

            //임계값 하나라도 넘어가면 실행
            if ((Math.abs(event.values[0]) >= threshold) or
                (Math.abs(event.values[1]) >= threshold) or
                (Math.abs(event.values[2]) >= threshold)) {
//            if (r>40 | p>40) {

                //값들 저장하고 정렬(지금 은 필요없음)
                val eventvalues =
                    java.util.ArrayList<Float>()
                eventvalues.add(Math.abs(event.values[0]))
                eventvalues.add(Math.abs(event.values[1]))
                eventvalues.add(Math.abs(event.values[2]))
                Collections.sort(eventvalues)

                //값에 따라 라벨링
                if (eventvalues[2] - Math.abs(event.values[0]) < 0.01 || eventvalues[1] - Math.abs(event.values[0]) < 0.01) {
                    check_sum += if (event.values[0] > 0) 10000 else 20000
                }
                if (eventvalues[2] - Math.abs(event.values[1]) < 0.01 || eventvalues[1] - Math.abs(event.values[1]) < 0.01) {
                    check_sum += if (event.values[1] > 0) 1000 else 2000
                }
                if (eventvalues[2] - Math.abs(event.values[2]) < 0.01 || eventvalues[1] - Math.abs(event.values[2]) < 0.01
                ) {
                    check_sum += if (event.values[2] > 0) 100 else 200
                }

                check_sum += if (roll >= 0) 10 else 20
                check_sum += if (pitch >= 0) 1 else 2

                //라벨링 값 받아옴
                Check_sum = check_sum.toString()
                Log.e(
                    "LOG1",
                    " [X]:" + String.format(
                        "%.4f",
                        event.values[0]
                    ) + "    [Y]:" + String.format(
                        "%.4f",
                        event.values[1]
                    ) + "    [Z]:" + String.format(
                        "%.4f",
                        event.values[2]
                    ) + "   roll : " + roll + "  r : " + r + "   pitch : " + pitch + "   p : " + p + "   Check_sum : " + Check_sum
                )

                //전에 값과 같거나 아무것도 안들어오면 거른다
                if (Store_a.size > 0 && Store_a.get(Store_a.size - 1) != Check_sum) {

                    //사이즈는 사용자가 설정한 사이즈
                    var arrsize_temp: Int = Arrsize
                    //테스트 모드에서 사이즈는 다른 변수로 설정
                    arrsize_temp = Arrsize_catch
                    if (Store_a.size >= arrsize_temp) { //전에값 같으면 맨앞에꺼 삭제하고 삽입
                        Store_a.removeAt(0)
                        Store_a.add(Check_sum)
                    } else Store_a.add(Check_sum) //아니면 그냥삽입

                } else if (Store_a.size == 0) Store_a.add(Check_sum)

                temp_a = String() //스트링으로 값들 저장
                for (i in Store_a.indices) {
                    temp_a = temp_a + Store_a.get(i) + " "
                }
                Log.d("log1", temp_a)



                for (j in mpatterns_list.indices) {
                    val lcs = java.util.ArrayList<Int>()
                    var run_app = 0 //패턴들이랑 비교하는거
                    for (i in mpatterns_list.get(j).indices) {
                        val num1: Int = downtop(
                            mpatterns_list.get(j).get(i),
                            Store_a,
                            mpatterns_list.get(j).get(i).size,
                            Store_a.size,
                            NIL
                        )
                        val num2: Int = Math.min(Store_a.size, mpatterns_list.get(j).get(i).size)
                        var num =0;
                        if(num2!=0) num = ((num1 * 100) / num2).toInt()

                        if (num >= matcing_rate) run_app++ //일치하는 패턴 개수추가
                        lcs.add(num)
                    }

                    Log.e("lcs", " 번째 : lcs : " + lcs)

                    //패턴개수 3개 넘거나 사이즈 2개 이상이면 어플실행
                    if (run_app >= 3 && Store_a.size > 2) {
                        Store_a.clear()
                        Log.d("log1", "app run!!!!!!!!!!!!!!!!!           ")
                        if(mpackagename.get(j).startsWith("com.")) {
                            val intent = packageManager.getLaunchIntentForPackage(mpackagename.get(j))
                            val intent1 = getIntent()
                            finish()
                            startActivity(intent1)
                            startActivity(intent)

                        }
                        else{
                            startInnerFunction(mpackagename.get(j));
                        }
                    }
                }

            }
        }

        override fun onAccuracyChanged(
            sensor: Sensor,
            accuracy: Int
        ) {
        }
    }

    override fun onPause() {
        super.onPause()
        // setup main view pager
    }

    fun startInnerFunction(packagename : String){

        when(packagename){
            "bluetooth"->{
                val btAdapter = BluetoothAdapter.getDefaultAdapter()
                if(btAdapter.isEnabled()) {
                    btAdapter.disable()
                }
                else {
                    btAdapter.enable()
                }
            }
            "wifi" ->{
                var wfManager: WifiManager = this?.getSystemService(Context.WIFI_SERVICE) as WifiManager
                if (wfManager.isWifiEnabled) {
                    wfManager.setWifiEnabled(false)
                }
                else {
                    wfManager.setWifiEnabled(true)
                }
            }
            "light"->{
                var camManager: CameraManager = this?.getSystemService(Context.CAMERA_SERVICE) as CameraManager
                val camID = camManager.cameraIdList[0]
                var isFlashOn:Boolean = false
                if (isFlashOn) {
                    camManager.setTorchMode(camID, false)
                    isFlashOn = false
                }
                else {
                    camManager.setTorchMode(camID, true)
                    isFlashOn = true
                }
            }
            "killp"->{
                val packages: List<ApplicationInfo>
                val pm: PackageManager?
                pm = this?.packageManager
                //get a list of installed apps.
                //get a list of installed apps.
                packages = pm?.getInstalledApplications(0) as List<ApplicationInfo>
                val activityManager = this?.getSystemService(Context.ACTIVITY_SERVICE) as ActivityManager
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
            "silent"->{
                var audioManager = this?.getSystemService(Context.AUDIO_SERVICE) as AudioManager
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
        }
    }

    fun downtop(
        first: java.util.ArrayList<String>,
        second: java.util.ArrayList<String>,
        m: Int,
        n: Int,
        NIL: Array<IntArray>?
    ): Int {
        // allocate memory
        var NIL = NIL
        NIL = Array(m + 1) { IntArray(n + 1) }

//        Log.d("log1","downtop() : " + first + "   " + second)
        // calculate table
        for (i in 0..m)  // first
        {
            for (j in 0..n)  // second
            {
                if (i == 0 || j == 0) NIL[i][j] = 0 // initaliztion
                else if (first.get(i - 1).contains(second.get(j - 1))) {
//                    Log.d("log1","downtop() in first 1111111: " + first[i-1] + "   second : " + second[j-1])
                    NIL[i][j] = NIL[i - 1][j - 1] + 1
                }
                else { // insert max value into table
//                    Log.d("log1","downtop() in first 222222 : " + first[i-1] + "   second : " + second[j-1])
                    var temp = 0
                    temp = if (NIL[i - 1][j] > NIL[i][j - 1]) NIL[i - 1][j] else NIL[i][j - 1]
                    NIL[i][j] = temp
                }
            }
        }
//        Log.d("log1","NIL : " + NIL[m][n])
        return NIL[m][n] // return last value
    }

    /**
     * 1차 상보필터 적용 메서드  */
    private fun complementaty(new_ts: Double) {

        /* 자이로랑 가속 해제 */
        gyroRunning = false
        accRunning = false

        /*센서 값 첫 출력시 dt(=timestamp - event.timestamp)에 오차가 생기므로 처음엔 break */if (timestamp == 0.0) {
            timestamp = new_ts
            return
        }
        dt = (new_ts - timestamp) * NS2S // ns->s 변환
        timestamp = new_ts

        /* degree measure for accelerometer */mAccPitch = -Math.atan2(
            mAccValues[0].toDouble(),
            mAccValues[2].toDouble()
        ) * 180.0 / Math.PI // Y 축 기준
        mAccRoll = Math.atan2(
            mAccValues[1].toDouble(),
            mAccValues[2].toDouble()
        ) * 180.0 / Math.PI // X 축 기준
        /**
         * 1st complementary filter.
         * mGyroValuess : 각속도 성분.
         * mAccPitch : 가속도계를 통해 얻어낸 회전각.
         */
        temp = 1 / a * (mAccPitch - pitch) + mGyroValues[1]
        pitch = pitch + temp * dt
        temp = 1 / a * (mAccRoll - roll) + mGyroValues[0]
        roll = roll + temp * dt
        r = Math.abs(prev_roll - roll)
        p = Math.abs(prev_pitch - pitch)
        prev_roll = roll
        prev_pitch = pitch
//        Log.d("gyroacc22", roll.toString() + "    " + pitch)
    }

    inner class UserSensorListner : SensorEventListener {
        override fun onSensorChanged(event: SensorEvent) {
            when (event.sensor.type) {
                Sensor.TYPE_GYROSCOPE -> {

                    /*센서 값을 mGyroValues에 저장*/
                    mGyroValues = event.values
                    if (!gyroRunning) gyroRunning = true
                }
                Sensor.TYPE_ACCELEROMETER -> {

                    /*센서 값을 mAccValues에 저장*/
                    mAccValues = event.values
                    if (!accRunning) accRunning = true
                }
            }
            /**두 센서 새로운 값을 받으면 상보필터 적용 */
            if (gyroRunning && accRunning) {
                complementaty(event.timestamp.toDouble())
            }
        }

        override fun onAccuracyChanged(
            sensor: Sensor,
            accuracy: Int
        ) {
        }
    }

}
