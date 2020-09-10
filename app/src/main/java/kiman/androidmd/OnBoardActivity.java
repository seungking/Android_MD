package kiman.androidmd;


import androidx.appcompat.app.AppCompatActivity;
import androidx.viewpager.widget.ViewPager;

import android.content.Intent;
import android.os.Bundle;
import android.text.Html;
import android.view.View;
import android.widget.Button;
import android.widget.LinearLayout;
import android.widget.TextView;

import kiman.androidmd.adapter.ViewPagerAdapter;
import stream.custombutton.CustomButton;

public class OnBoardActivity extends AppCompatActivity {

    private ViewPager viewPager;
    private TextView btnLeft,btnRight;
    private ViewPagerAdapter adapter;
    private LinearLayout dotsLayout;
    private TextView[] dots;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_on_board);
        init();
    }

    private void init() {
        viewPager = findViewById(R.id.view_pager);
        btnLeft = findViewById(R.id.btnLeft);
        btnRight = findViewById(R.id.btnRight);
        dotsLayout = findViewById(R.id.dotsLayout);
        adapter = new ViewPagerAdapter(this);
        addDots(0);
        viewPager.addOnPageChangeListener(listener); // 리스너 연결
        viewPager.setAdapter(adapter);

        btnRight.setOnClickListener(v->{
            //next면 다음으로 넘어감
            if (btnRight.getText().toString().equals("Next")){
                viewPager.setCurrentItem(viewPager.getCurrentItem()+1);
            }
            else{
                // 다 끝나면 인증창으로
                startActivity(new Intent(OnBoardActivity.this,MainActivity.class));
                finish();
            }
        });

        btnLeft.setOnClickListener(v->{
            // 페이지 3으로 바로 이동
            viewPager.setCurrentItem(viewPager.getCurrentItem()+3);
        });

    }

    //뷰페이저 점 추가
    private void addDots(int position){
        dotsLayout.removeAllViews();
        dots = new TextView[4];
        for (int i = 0; i < dots.length; i++) {
            dots[i] = new TextView(this);
            //점 생성하는 html 코드
            dots[i].setText(Html.fromHtml("&#8226"));
            dots[i].setTextSize(35);
            dots[i].setTextColor(getResources().getColor(R.color.colorLightGrey));
            dotsLayout.addView(dots[i]);
        }

        // 선택하는 점 변경
        if(dots.length>0){
            dots[position].setTextColor(getResources().getColor(R.color.colorGrey));
        }
    }

    //뷰페이저 리스너
    private ViewPager.OnPageChangeListener listener = new ViewPager.OnPageChangeListener() {
        @Override
        public void onPageScrolled(int position, float positionOffset, int positionOffsetPixels) {

        }

        @Override
        public void onPageSelected(int position) {
            addDots(position);

            //움직일때마다 버튼설정
            //첫페이지에만 skip버튼 표시
            if(position==0){
                btnLeft.setVisibility(View.VISIBLE);
                btnLeft.setEnabled(true);
                btnRight.setText("Next");
            }
            else if(position==1){
                btnLeft.setVisibility(View.GONE);
                btnLeft.setEnabled(false);
                btnRight.setText("Next");
            }
            else if(position==2){
                btnLeft.setVisibility(View.GONE);
                btnLeft.setEnabled(false);
                btnRight.setText("Next");
            }
            else{
                btnLeft.setVisibility(View.GONE);
                btnLeft.setEnabled(false);
                btnRight.setText("Finish");
            }
        }

        @Override
        public void onPageScrollStateChanged(int state) {

        }
    };

}