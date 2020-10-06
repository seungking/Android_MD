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

import com.airbnb.lottie.LottieAnimationView;

import kiman.androidmd.R;

public class ViewPagerAdapter extends PagerAdapter {

    private Context context;
    private LayoutInflater inflater;
    private String[] titles;
    private String[] descs;
    public ViewPagerAdapter(Context context) {
        this.context = context;

        titles = new String[]{
                context.getString(R.string.VPA_titles_1),
                context.getString(R.string.VPA_titles_2),
                context.getString(R.string.VPA_titles_3),
                context.getString(R.string.VPA_titles_4)
        };

        descs = new String[]{
                context.getString(R.string.VPA_descs_1),
                context.getString(R.string.VPA_descs_2),
                context.getString(R.string.VPA_descs_3),
                context.getString(R.string.VPA_descs_4)
        };
    }

    //설정 값들 선언
    private int images[] ={
            R.drawable.p1,
            R.drawable.p2,
            R.drawable.p3,
            R.drawable.p4
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


        LottieAnimationView lottieAnimationView;
        lottieAnimationView = v.findViewById(R.id.imgViewPager_lottie);
        lottieAnimationView.loop(true);
        lottieAnimationView.playAnimation();

        ImageView imageView = v.findViewById(R.id.imgViewPager);
        TextView txtTitle = v.findViewById(R.id.txtTitleViewPager);
        TextView txtDesc = v.findViewById(R.id.txtDescViewPager);

        if(position==0){
            lottieAnimationView.setAnimation("onboard1.json");
        }
        else if(position==1){
            lottieAnimationView.setAnimation("onboard2.json");
        }
        else if(position==2){
            lottieAnimationView.setAnimation("onboard3.json");
        }
        else if(position==3){
            lottieAnimationView.setAnimation("onboard4.json");
        }

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
