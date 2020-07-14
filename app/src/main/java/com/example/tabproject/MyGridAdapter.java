package com.example.tabproject;

import android.app.AlertDialog;
import android.content.Context;
import android.content.Intent;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.FrameLayout;
import android.widget.GridView;
import android.widget.ImageView;
import android.widget.Toast;

class MyGridAdapter extends BaseAdapter {
    Context context;
    LayoutInflater inf;

    Integer[] posterID = {
            R.drawable.p1, R.drawable.p2, R.drawable.p3, R.drawable.p4, R.drawable.p5,
            R.drawable.p6, R.drawable.p7, R.drawable.p8, R.drawable.p9, R.drawable.p10,
            R.drawable.p11, R.drawable.p12, R.drawable.p13, R.drawable.p14, R.drawable.p15,
            R.drawable.p16, R.drawable.p17, R.drawable.p18, R.drawable.p19, R.drawable.p20
    };

    public MyGridAdapter(Context c){
        context = c;
        inf = (LayoutInflater) context.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
    }
    @Override
    public int getCount() {
        return posterID.length;
    }

    @Override
    public Object getItem(int i) {
        return null;
    }

    @Override
    public long getItemId(int i) {
        return 0;
    }

    @Override
    public View getView(final int position, View view, ViewGroup viewGroup) {
        View v= (View) View.inflate(context,  R.layout.fragment_2, null);
        ImageView imageview = (ImageView) v.findViewById(R.id.ivPoster);
        imageview.setImageResource(posterID[position]);
        final int pos = position;

        imageview.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(context, Img.class);
                intent.putExtra("id", position);

                context.startActivity(intent);
            }
        });


        /*
        imageview.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                View dialogView = (View) View.inflate(context,
                        R.layout.dialog, null);
                AlertDialog.Builder dlg = new AlertDialog.Builder(context);
                ImageView ivPoster = (ImageView) dialogView.findViewById(R.id.ivPoster);
                ivPoster.setImageResource(posterID[pos]);
                dlg.setIcon(R.drawable.ic_launcher_background);
                dlg.setView(dialogView);
                dlg.setNegativeButton("닫기", null);
                dlg.show();
            }
        });

         */




        return imageview;
    }
}
