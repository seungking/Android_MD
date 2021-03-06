package kiman.androidmd;

import android.annotation.SuppressLint;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.pm.ApplicationInfo;
import android.content.pm.PackageManager;
import android.graphics.Bitmap;
import android.graphics.Canvas;
import android.graphics.drawable.BitmapDrawable;
import android.graphics.drawable.Drawable;
import android.os.AsyncTask;
import android.os.Build;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.WindowManager;
import android.widget.AdapterView;
import android.widget.BaseAdapter;
import android.widget.ImageView;
import android.widget.ListView;
import android.widget.TextView;
import android.widget.Toast;
import android.widget.Toolbar;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.content.ContextCompat;
import androidx.core.graphics.drawable.DrawableCompat;

import java.io.ByteArrayOutputStream;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

import kiman.androidmd.model.AppInfo;
import kiman.androidmd.service.ManagePref;


public class AppInfoActivity extends AppCompatActivity {
    private static final String TAG = AppInfoActivity.class.getSimpleName();
    // 메뉴 KEY
    private final int MENU_DOWNLOAD = 0;
    private final int MENU_ALL = 1;
    private int MENU_MODE = MENU_DOWNLOAD;

    private PackageManager pm;

    private View mLoadingContainer;
    private ListView mListView1 = null;
    private IAAdapter mAdapter1 = null;

    private ListView mListView2 = null;
    private IFAdapter mAdapter2 = null;

    String select = null;

    Toolbar toolbar;

    ManagePref managePref = new ManagePref();
    private int modify = -1;

    ArrayList<String> appname = new ArrayList<String>();
    ArrayList<String> packagename = new ArrayList<String>();
    ArrayList<String> appicon = new ArrayList<String>();

    /** Called when the activity is first created. */
    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_app_info);

        SharedPreferences test = getSharedPreferences("save", MODE_PRIVATE);
        final SharedPreferences.Editor editor = test.edit();

        Intent get_intent = getIntent();
        select = get_intent.getStringExtra("select");
        modify = get_intent.getIntExtra("modify",-1);

        editor.putString("packageName","");

        mLoadingContainer = findViewById(R.id.loading_container);
        mListView1 = (ListView) findViewById(R.id.listView1);
        mListView2 = (ListView) findViewById(R.id.listView2);

        mAdapter1 = new IAAdapter(this);
        mListView1.setAdapter(mAdapter1);

        mAdapter2 = new IFAdapter(this);
        mListView2.setAdapter(mAdapter2);

        appname = managePref.getStringArrayPref(AppInfoActivity.this,"appname");
        packagename = managePref.getStringArrayPref(AppInfoActivity.this,"packagename");
        appicon = managePref.getStringArrayPref(AppInfoActivity.this,"appicon");

        mListView1.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> av, View view, int position,
                                    long id) {
                // TODO Auto-generated method stub
                String app_name = ((TextView) view.findViewById(R.id.app_name)).getText().toString();
                String package_name = ((TextView) view.findViewById(R.id.app_package)).getText().toString();

                ImageView imageView = (ImageView)view.findViewById(R.id.app_icon);
                ByteArrayOutputStream stream = new ByteArrayOutputStream();
                Bitmap bitmap = ((BitmapDrawable)imageView.getDrawable()).getBitmap();


                if(modify==-1) {
                    Intent intent = new Intent(AppInfoActivity.this, Gyro_Acc.class);
                    intent.putExtra("appname", app_name);
                    intent.putExtra("packagename", package_name);
                    intent.putExtra("appicon", managePref.BitmapToString(bitmap));

                    startActivity(intent);
                    finish();
                }
                else{

                    appname.set(modify,app_name);
                    packagename.set(modify,package_name);
                    appicon.set(modify,managePref.BitmapToString(bitmap));

                    managePref.setStringArrayPref(AppInfoActivity.this,"appname",appname);
                    managePref.setStringArrayPref(AppInfoActivity.this,"packagename",packagename);
                    managePref.setStringArrayPref(AppInfoActivity.this,"appicon",appicon);

                    startActivity(new Intent(AppInfoActivity.this, MainActivity.class));
                    finish();
                }
            }
        });

        mListView2.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> av, View view, int position,
                                    long id) {
                // TODO Auto-generated method stub
                String app_name = ((TextView) view.findViewById(R.id.app_name)).getText().toString();
                String package_name = ((TextView) view.findViewById(R.id.app_package)).getText().toString();

                ImageView imageView = (ImageView)view.findViewById(R.id.app_icon);
                ByteArrayOutputStream stream = new ByteArrayOutputStream();
                Bitmap bitmap = ((BitmapDrawable)imageView.getDrawable()).getBitmap();

                if(modify==-1) {
                    Intent intent = new Intent(AppInfoActivity.this, Gyro_Acc.class);
                    intent.putExtra("appname", app_name);
                    intent.putExtra("packagename", package_name);
                    intent.putExtra("appicon", managePref.BitmapToString(bitmap));

                    startActivity(intent);
                    finish();
                }
                else{

                    appname.set(modify,app_name);
                    packagename.set(modify,package_name);
                    appicon.set(modify,managePref.BitmapToString(bitmap));

                    managePref.setStringArrayPref(AppInfoActivity.this,"appname",appname);
                    managePref.setStringArrayPref(AppInfoActivity.this,"packagename",packagename);
                    managePref.setStringArrayPref(AppInfoActivity.this,"appicon",appicon);

                    startActivity(new Intent(AppInfoActivity.this, MainActivity.class));
                    finish();
                }
            }
        });

        findViewById(R.id.appInfoBack).setOnClickListener(v->{
            finish();
        });
    }

    @Override
    protected void onResume() {
        super.onResume();

        // 작업 시작
        startTask();
    }

    @Override
    protected void onStop() {
        super.onStop();
    }

    /**
     * 작업 시작
     */
    private void startTask() {
        new AppTask().execute();
    }

    /**
     * 로딩뷰 표시 설정
     *
     * @param isView
     *            표시 유무
     */
    private void setLoadingView(boolean isView) {
        if (isView) {
            // 화면 로딩뷰 표시
            mLoadingContainer.setVisibility(View.VISIBLE);
            mListView1.setVisibility(View.GONE);
        } else {
            // 화면 어플 리스트 표시
            mListView1.setVisibility(View.VISIBLE);
            mLoadingContainer.setVisibility(View.GONE);
        }
    }

    /**
     * List Fast Holder
     *
     * @author nohhs
     */
    private class ViewHolder {
        // App Icon
        public ImageView mIcon;
        // App Name
        public TextView mName;
        // App Package Name
        public TextView mPacakge;
    }

    /**
     * List Adapter
     *
     * @author nohhs
     */
    private class IAAdapter extends BaseAdapter {
        private Context mContext = null;

        private List<ApplicationInfo> mAppList = null;
        private ArrayList<AppInfo> mListData = new ArrayList<AppInfo>();

        public IAAdapter(Context mContext) {
            super();
            this.mContext = mContext;
        }

        public int getCount() {
            return mListData.size();
        }

        public Object getItem(int arg0) {
            return mListData.get(arg0);
        }

        public long getItemId(int arg0) {
            return 0;
        }

        public View getView(int position, View convertView, ViewGroup parent) {
            ViewHolder holder;

            if (convertView == null) {
                holder = new ViewHolder();

                LayoutInflater inflater = (LayoutInflater) mContext
                        .getSystemService(Context.LAYOUT_INFLATER_SERVICE);
                convertView = inflater.inflate(R.layout.list_item_layout, null);

                holder.mIcon = (ImageView) convertView
                        .findViewById(R.id.app_icon);
                holder.mName = (TextView) convertView
                        .findViewById(R.id.app_name);
                holder.mPacakge = (TextView) convertView
                        .findViewById(R.id.app_package);

                convertView.setTag(holder);
            } else {
                holder = (ViewHolder) convertView.getTag();
            }

            AppInfo data = mListData.get(position);

            if (data.mIcon != null) {
                holder.mIcon.setImageBitmap(data.mIcon);
            }

            holder.mName.setText(data.mAppNaem);
            holder.mPacakge.setText(data.mAppPackge);

            return convertView;
        }

        /**
         * 어플리케이션 리스트 작성
         */
        @SuppressLint("WrongConstant")
        public void rebuild() {
            if (mAppList == null) {

                Log.d(TAG, "Is Empty Application List");
                // 패키지 매니저 취득
                pm = AppInfoActivity.this.getPackageManager();

                // 설치된 어플리케이션 취득
                mAppList = pm
                        .getInstalledApplications(PackageManager.GET_UNINSTALLED_PACKAGES
                                | PackageManager.GET_DISABLED_COMPONENTS);
            }

            AppInfo.AppFilter filter;
            switch (MENU_MODE) {
                case MENU_DOWNLOAD:
                    filter = AppInfo.THIRD_PARTY_FILTER;
                    break;
                default:
                    filter = null;
                    break;
            }

            if (filter != null) {
                filter.init();
            }

            // 기존 데이터 초기화
            mListData.clear();

            AppInfo addInfo = null;
            ApplicationInfo info = null;
            for (ApplicationInfo app : mAppList) {
                info = app;

                if (filter == null || filter.filterApp(info)) {
                    // 필터된 데이터
                    if(app.packageName.contains("daemon")) continue;
                    addInfo = new AppInfo();
                    // App Icon
//                    addInfo.mIcon = app.loadIcon(pm);
                    addInfo.mIcon = getBitmapFromDrawable(app.loadIcon(pm));
                    // App Name
                    addInfo.mAppNaem = app.loadLabel(pm).toString();
                    // App Package Name
                    addInfo.mAppPackge = app.packageName;
                    mListData.add(addInfo);
                }
            }

            // 알파벳 이름으로 소트(한글, 영어)
            Collections.sort(mListData, AppInfo.ALPHA_COMPARATOR);
        }
    }

    private class IFAdapter extends BaseAdapter {
        private Context mContext = null;

        private List<ApplicationInfo> mAppList = null;
        private ArrayList<AppInfo> mListData = new ArrayList<AppInfo>();

        public IFAdapter(Context mContext) {
            super();
            this.mContext = mContext;
        }

        public int getCount() {
            return mListData.size();
        }

        public Object getItem(int arg0) {
            return mListData.get(arg0);
        }

        public long getItemId(int arg0) {
            return 0;
        }

        public View getView(int position, View convertView, ViewGroup parent) {
            ViewHolder holder;

            if (convertView == null) {
                holder = new ViewHolder();

                LayoutInflater inflater = (LayoutInflater) mContext
                        .getSystemService(Context.LAYOUT_INFLATER_SERVICE);
                convertView = inflater.inflate(R.layout.list_item_layout, null);

                holder.mIcon = (ImageView) convertView
                        .findViewById(R.id.app_icon);
                holder.mName = (TextView) convertView
                        .findViewById(R.id.app_name);
                holder.mPacakge = (TextView) convertView
                        .findViewById(R.id.app_package);

                convertView.setTag(holder);
            } else {
                holder = (ViewHolder) convertView.getTag();
            }

            AppInfo data = mListData.get(position);

            if (data.mIcon != null) {
                holder.mIcon.setImageBitmap(data.mIcon);
            }

            holder.mName.setText(data.mAppNaem);
            holder.mPacakge.setText(data.mAppPackge);

            return convertView;
        }

        /**
         * 어플리케이션 리스트 작성
         */
        @SuppressLint("WrongConstant")
        public void rebuild() {

            Bitmap bitmap = getBitmapFromVectorDrawable(AppInfoActivity.this ,R.drawable.ic_bluetooth_black_24dp);
            Bitmap bitmap1 = getBitmapFromVectorDrawable(AppInfoActivity.this ,R.drawable.ic_wifi_black_24dp);
            Bitmap bitmap2 = getBitmapFromVectorDrawable(AppInfoActivity.this ,R.drawable.ic_flash_on_black_24dp);
            Bitmap bitmap3 = getBitmapFromVectorDrawable(AppInfoActivity.this ,R.drawable.ic_bluetooth_black_24dp);
            Bitmap bitmap4 = getBitmapFromVectorDrawable(AppInfoActivity.this ,R.drawable.ic_volume_mute_black_24dp);

            Bitmap[] icons = {bitmap,bitmap1,bitmap2,bitmap3,bitmap4};
            String[] appnames = {"블루투스 ON/OFF", "WIFI ON/OFF", "라이트 ON/OFF", "프로세스 종료 ON/OFF", "음소거모드 ON/OFF"};
            String[] packageNames = {"bluetooth", "wifi", "light", "killp", "silent"};

            // 기존 데이터 초기화
            mListData.clear();

            AppInfo addInfo = null;
            for (int i=0; i<appnames.length; i++) {

                addInfo = new AppInfo();
                addInfo.mIcon = icons[i];
                addInfo.mAppNaem = appnames[i];
                addInfo.mAppPackge = packageNames[i];

                mListData.add(addInfo);
            }

            // 알파벳 이름으로 소트(한글, 영어)
            Collections.sort(mListData, AppInfo.ALPHA_COMPARATOR);
        }
    }

    @NonNull
    static private Bitmap getBitmapFromDrawable(@NonNull Drawable drawable) {
        final Bitmap bmp = Bitmap.createBitmap(drawable.getIntrinsicWidth(), drawable.getIntrinsicHeight(), Bitmap.Config.ARGB_8888);
        final Canvas canvas = new Canvas(bmp);
        drawable.setBounds(0, 0, canvas.getWidth(), canvas.getHeight());
        drawable.draw(canvas);
        return bmp;
    }
    /**
     * 작업 태스크
     * @author nohhs
     */
    private class AppTask extends AsyncTask<Void, Void, Void> {

        @Override
        protected void onPreExecute() {
            // 로딩뷰 시작
            setLoadingView(true);
        }

        @Override
        protected Void doInBackground(Void... params) {
            // 어플리스트 작업시작
            mAdapter1.rebuild();
            mAdapter2.rebuild();

            return null;
        }

        @Override
        protected void onPostExecute(Void result) {
            // 어댑터 갱신
            mAdapter1.notifyDataSetChanged();
            mAdapter2.notifyDataSetChanged();

            // 로딩뷰 정지
            setLoadingView(false);
        }

    };

    public static Bitmap getBitmapFromVectorDrawable(Context context, int drawableId) {
        Drawable drawable = ContextCompat.getDrawable(context, drawableId);
        if (Build.VERSION.SDK_INT < Build.VERSION_CODES.LOLLIPOP) {
            drawable = (DrawableCompat.wrap(drawable)).mutate();
        }

        Bitmap bitmap = Bitmap.createBitmap(drawable.getIntrinsicWidth(),
                drawable.getIntrinsicHeight(), Bitmap.Config.ARGB_8888);
        Canvas canvas = new Canvas(bitmap);
        drawable.setBounds(0, 0, canvas.getWidth(), canvas.getHeight());
        drawable.draw(canvas);

        return bitmap;
    }


}