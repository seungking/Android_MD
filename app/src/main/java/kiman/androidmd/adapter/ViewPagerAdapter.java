package kiman.androidmd.adapter;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.constraintlayout.widget.ConstraintLayout;
import androidx.viewpager.widget.PagerAdapter;

import kiman.androidmd.R;

public class ViewPagerAdapter extends PagerAdapter {

    private Context context;
    private LayoutInflater inflater;

    public ViewPagerAdapter(Context context) {
        this.context = context;
    }

    //설정 값들 선언
    private int images[] ={
            R.drawable.p1,
            R.drawable.p2,
            R.drawable.p3,
            R.drawable.p4
    };

    private String titles[] ={
            "권한 설정",
            "앱 선택",
            "모션 설정",
            "설정 완료!"
    };

    private String descs[] ={
            "EXEMO를 사용하기 위해 권한 허용해 주세요.",
            "모션을 통해 실행할 동작을 선택해 주세요.",
            "나만의 멋진 모션을 등록해 주세요!",
            "설정이 완료되었습니다! 자유롭게 EXEMO를 이용해보세요!"
    };

    @Override
    public int getCount() {
        return titles.length;
    }

    @Override
    public boolean isViewFromObject(@NonNull View view, @NonNull Object object) {
        return view == (ConstraintLayout)object;
    }

    @NonNull
    @Override
    public Object instantiateItem(@NonNull ViewGroup container, int position) {
        inflater = (LayoutInflater)context.getSystemService(context.LAYOUT_INFLATER_SERVICE);
        View v = inflater.inflate(R.layout.view_pager,container,false);

        ImageView imageView = v.findViewById(R.id.imgViewPager);
        TextView txtTitle = v.findViewById(R.id.txtTitleViewPager);
        TextView txtDesc = v.findViewById(R.id.txtDescViewPager);

        imageView.setImageResource(images[position]);
        txtTitle.setText(titles[position]);
        txtDesc.setText(descs[position]);

        container.addView(v);
        return v;
    }

    @Override
    public void destroyItem(@NonNull ViewGroup container, int position, @NonNull Object object) {
        container.removeView((ConstraintLayout)object);
    }
}
