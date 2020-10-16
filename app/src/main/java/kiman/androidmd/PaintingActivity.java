package kiman.androidmd;

import android.content.DialogInterface;
import android.content.Intent;
import android.content.pm.ActivityInfo;
import android.graphics.Bitmap;
import android.graphics.Color;
import android.graphics.Point;
import android.os.Bundle;
import android.util.Log;
import android.view.Display;
import android.view.Menu;
import android.view.MenuItem;
import android.view.MotionEvent;
import android.view.View;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.content.ContextCompat;

import com.google.android.material.snackbar.Snackbar;

import java.util.ArrayList;

import kiman.androidmd.service.ManagePref;
import kiman.androidmd.ui.DrawingView;
import top.defaults.colorpicker.ColorPickerPopup;

public class PaintingActivity extends AppCompatActivity implements View.OnClickListener
{
    static DrawingView mDrawingView;
	static ImageView mFillBackgroundImageView;
	static ImageView mUndoImageView;
	static ImageView mRedoImageView;
	static ImageView back;
	static ImageView next;
	static TextView clear;

    private static int mCurrentColor;

    @Override
    protected void onCreate(Bundle savedInstanceState)
    {
        super.onCreate(savedInstanceState);
		setRequestedOrientation(ActivityInfo.SCREEN_ORIENTATION_PORTRAIT);
        setContentView(R.layout.activity_painting);

        mDrawingView = (DrawingView) findViewById(R.id.main_drawing_view);
        mFillBackgroundImageView = (ImageView)findViewById(R.id.main_fill_iv);
        mUndoImageView = (ImageView)findViewById(R.id.main_undo_iv);
        mRedoImageView = (ImageView)findViewById(R.id.main_redo_iv);
		back = (ImageView)findViewById(R.id.painting_back);
		next = (ImageView)findViewById(R.id.painting_next);
		clear = (TextView)findViewById(R.id.painting_clear);

        mDrawingView.setOnClickListener(this);
        mFillBackgroundImageView.setOnClickListener(this);
        mUndoImageView.setOnClickListener(this);
        mRedoImageView.setOnClickListener(this);
        back.setOnClickListener(this);
        next.setOnClickListener(this);
        clear.setOnClickListener(this);
    }


	@Override
	public boolean onCreateOptionsMenu(Menu menu) {
		getMenuInflater().inflate(R.menu.menu_paint, menu);
		return true;
	}

	@Override
	public boolean onOptionsItemSelected(MenuItem item)
	{

		return super.onOptionsItemSelected(item);
	}

	@Override
	public void onClick(View v) {
		switch (v.getId()) {
			case R.id.painting_back:
				finish();

				break;
			case R.id.painting_next:
				mDrawingView.setDrawingCacheEnabled(true);
				mDrawingView.buildDrawingCache();
				Bitmap drawingCache = mDrawingView.getDrawingCache();
				drawingCache = Bitmap.createBitmap(drawingCache,0,0,(int)mDrawingView.getWidth(),(int)mDrawingView.getHeight());
				Log.d("LOG1", String.valueOf(mDrawingView.getWidth()) + " " + String.valueOf(mDrawingView.getHeight()));
				ManagePref managePref = new ManagePref();
				ArrayList<String> appname = new ArrayList<String>();
				ArrayList<String> packagename = new ArrayList<String>();
				ArrayList<String> appicon = new ArrayList<String>();
				ArrayList<String> date = new ArrayList<String>();
				ArrayList<String> switch_motion = new ArrayList<String>();
				ArrayList<String> patterns = new ArrayList<String>();
				ArrayList<String> images = new ArrayList<String>();

				appname = managePref.getStringArrayPref(PaintingActivity.this,"appname");
				packagename = managePref.getStringArrayPref(PaintingActivity.this,"packagename");
				appicon = managePref.getStringArrayPref(PaintingActivity.this,"appicon");
				date = managePref.getStringArrayPref(PaintingActivity.this,"date");
				switch_motion = managePref.getStringArrayPref(PaintingActivity.this,"switch");
				patterns = managePref.getStringArrayPref(PaintingActivity.this,"patterns");
				images = managePref.getStringArrayPref(PaintingActivity.this,"images");


				appname.add(getIntent().getStringExtra("appname"));
				packagename.add(getIntent().getStringExtra("packagename"));
				appicon.add(getIntent().getStringExtra("appicon"));
				date.add(getIntent().getStringExtra("date"));
				switch_motion.add("off");
				patterns.add(getIntent().getStringExtra("patterns"));
				images.add(managePref.BitmapToString(drawingCache));

				managePref.setStringArrayPref(PaintingActivity.this,"appname",appname);
				managePref.setStringArrayPref(PaintingActivity.this,"packagename",packagename);
				managePref.setStringArrayPref(PaintingActivity.this,"appicon",appicon);
				managePref.setStringArrayPref(PaintingActivity.this,"date",date);
				managePref.setStringArrayPref(PaintingActivity.this,"switch",switch_motion);
				managePref.setStringArrayPref(PaintingActivity.this,"patterns",patterns);
				managePref.setStringArrayPref(PaintingActivity.this,"images",images);

				finish();

				break;
			case R.id.main_fill_iv:
				new ColorPickerPopup.Builder(this)
						.initialColor(Color.parseColor("#007DD6")) // Set initial color
						.enableBrightness(true) // Enable brightness slider or not
						.enableAlpha(false) // Enable alpha slider or not
						.okTitle("Choose")
						.cancelTitle("Cancel")
						.showIndicator(true)
						.showIndicator(true)
						.showValue(false)
						.build()
						.show(v, new ColorPickerPopup.ColorPickerObserver() {
							@Override
							public void onColorPicked(int color) {
								mCurrentColor = color;
								mDrawingView.setPaintColor(mCurrentColor);
								mFillBackgroundImageView.setBackgroundColor(color);
							}
						});
				break;
			case R.id.main_undo_iv:
				mDrawingView.undo();
				break;
			case R.id.main_redo_iv:
				mDrawingView.redo();
				break;
			case R.id.painting_clear:
				mDrawingView.clear();
			default:
				break;
		}
	}

	@Override
	public boolean onTouchEvent(MotionEvent event) {
//		LinePath linePath;
		//Log.d("LOG1", "extractingcolor1");
		int index;
		int id;
		int eventMasked = event.getActionMasked();
		switch (eventMasked) {
			case MotionEvent.ACTION_DOWN:
			case MotionEvent.ACTION_POINTER_DOWN: {
				index = event.getActionIndex();
				id = event.getPointerId(index);

				break;
			}
		}
		return true;
	}



}
