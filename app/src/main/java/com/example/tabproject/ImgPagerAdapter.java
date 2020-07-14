package com.example.tabproject;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.viewpager.widget.PagerAdapter;

public class ImgPagerAdapter extends PagerAdapter {
    private Context context = null;
    Integer[] posterID = {
            R.drawable.p1, R.drawable.p2, R.drawable.p3, R.drawable.p4, R.drawable.p5,
            R.drawable.p6, R.drawable.p7, R.drawable.p8, R.drawable.p9, R.drawable.p10,
            R.drawable.p11, R.drawable.p12, R.drawable.p13, R.drawable.p14, R.drawable.p15,
            R.drawable.p16, R.drawable.p17, R.drawable.p18, R.drawable.p19, R.drawable.p20
    };
    public ImgPagerAdapter(Context c){
        context = c;

    }
    @Override
    public Object instantiateItem(ViewGroup container, int position) {
        View view = null ;

        if (context != null) {
            // LayoutInflater를 통해 "/res/layout/page.xml"을 뷰로 생성.
            LayoutInflater inflater = (LayoutInflater) context.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
            view = inflater.inflate(R.layout.activity_img, container, false);
            ImageView imageView = (ImageView) view.findViewById(R.id.img) ;
            imageView.setImageResource(posterID[position]);
        }

        // 뷰페이저에 추가.
        container.addView(view) ;

        return view ;
    }
    @Override
    public void destroyItem(ViewGroup container, int position, Object object) {
        container.removeView((View) object);
    }

    @Override
    public int getCount() {
        return 20;
    }

    @Override
    public boolean isViewFromObject(@NonNull View view, @NonNull Object object) {
        return (view == (View)object);
    }
}
