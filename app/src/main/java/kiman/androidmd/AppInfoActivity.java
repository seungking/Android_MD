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

import java.io.ByteArrayOutputStream;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

public class AppInfoActivity extends AppCompatActivity {
    private static final String TAG = AppInfoActivity.class.getSimpleName();
    // 메뉴 KEY
    private final int MENU_DOWNLOAD = 0;
    private final int MENU_ALL = 1;
    private int MENU_MODE = MENU_DOWNLOAD;

    private PackageManager pm;

    private View mLoadingContainer;
    private ListView mListView = null;
    private IAAdapter mAdapter = null;

    String select = null;

    Toolbar toolbar;

    ManagePref managePref = new ManagePref();

    /** Called when the activity is first created. */
    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_app_info);
        getWindow().setFlags(WindowManager.LayoutParams.FLAG_FULLSCREEN,
                WindowManager.LayoutParams.FLAG_FULLSCREEN);

//        toolbar = (Toolbar)findViewById(R.id.app_list_toolbar);

        SharedPreferences test = getSharedPreferences("save", MODE_PRIVATE);
        final SharedPreferences.Editor editor = test.edit();

        Intent get_intent = getIntent();
        select = get_intent.getStringExtra("select");
        Log.e("package11",select + "\n###\n");

        editor.putString("packageName","");

        mLoadingContainer = findViewById(R.id.loading_container);
        mListView = (ListView) findViewById(R.id.listView1);

        mAdapter = new IAAdapter(this);
        mListView.setAdapter(mAdapter);

        mListView.setOnItemClickListener(new AdapterView.OnItemClickListener() {

            @Override
            public void onItemClick(AdapterView<?> av, View view, int position,
                                    long id) {
                // TODO Auto-generated method stub
                String app_name = ((TextView) view.findViewById(R.id.app_name)).getText().toString();
                String package_name = ((TextView) view.findViewById(R.id.app_package)).getText().toString();

                ImageView imageView = (ImageView)view.findViewById(R.id.app_icon);
                ByteArrayOutputStream stream = new ByteArrayOutputStream();
                Bitmap bitmap = ((BitmapDrawable)imageView.getDrawable()).getBitmap();

                Toast.makeText(AppInfoActivity.this, package_name, Toast.LENGTH_SHORT).show();

                Log.d("Log1",package_name);
                    editor.putString("packageName",package_name);
                    editor.commit();

                Intent intent = new Intent(AppInfoActivity.this,Gyro_Acc.class);
                intent.putExtra("appname",app_name);
                intent.putExtra("packagename",package_name);
                intent.putExtra("appicon",managePref.BitmapToString(bitmap));

                startActivity(intent);
                finish();
            }
        });
    }

    @Override
    protected void onResume() {
        super.onResume();

        // 작업 시작
        startTask();
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
            mListView.setVisibility(View.GONE);
        } else {
            // 화면 어플 리스트 표시
            mListView.setVisibility(View.VISIBLE);
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
            mAdapter.rebuild();

            return null;
        }

        @Override
        protected void onPostExecute(Void result) {
            // 어댑터 갱신
            mAdapter.notifyDataSetChanged();

            // 로딩뷰 정지
            setLoadingView(false);
        }

    };

//    @Override
//    public boolean onPrepareOptionsMenu(Menu menu) {
//        if (MENU_MODE == MENU_DOWNLOAD) {
//            menu.findItem(MENU_DOWNLOAD).setVisible(false);
//            menu.findItem(MENU_ALL).setVisible(true);
//        } else {
//            menu.findItem(MENU_DOWNLOAD).setVisible(true);
//            menu.findItem(MENU_ALL).setVisible(false);
//        }
//
//        return true;
//    }

//    @Override
//    public boolean onOptionsItemSelected(MenuItem item) {
//        int menuId = item.getItemId();
//
//        if (menuId == MENU_DOWNLOAD) {
//            MENU_MODE = MENU_DOWNLOAD;
//        } else {
//            MENU_MODE = MENU_ALL;
//        }
//
//        startTask();
//
//        return true;
////    }
}