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
            String.valueOf(R.string.VPA_titles_1),
            String.valueOf(R.string.VPA_titles_2),
            String.valueOf(R.string.VPA_titles_3),
            String.valueOf(R.string.VPA_titles_4)
    };

    private String descs[] ={
            String.valueOf(R.string.VPA_descs_1),
            String.valueOf(R.string.VPA_descs_2),
            String.valueOf(R.string.VPA_descs_3),
            String.valueOf(R.string.VPA_descs_4)
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
