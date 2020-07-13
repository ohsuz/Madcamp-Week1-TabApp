package com.example.tabproject;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import java.util.ArrayList;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

public class WordAdapter extends RecyclerView.Adapter<WordAdapter.WordViewHolder> {

    private ArrayList<Word> ws;

    public WordAdapter(ArrayList<Word> ws){
        this.ws = ws;
    }

    // 레이아웃 파일에 있는 UI 컴포넌트를 WordListViewHolder 클래스의 멤버 변수와 연결
    public class WordViewHolder extends RecyclerView.ViewHolder{
        TextView k_word;
        TextView e_word;

        public WordViewHolder(View view){
            super(view);
            k_word = (TextView)view.findViewById(R.id.k_word);
            e_word = (TextView)view.findViewById(R.id.e_word);
        }
    }

    @NonNull
    @Override
    public WordViewHolder onCreateViewHolder(@NonNull ViewGroup viewGroup, int viewType) {
        View view = LayoutInflater.from(viewGroup.getContext()).inflate(R.layout.word_item,viewGroup,false);
        return new WordAdapter.WordViewHolder(view);
    }

    // onBindViewHolder가 호출될 때 WordListViewHolder에 데이터를 추가함함
    @Override
    public void onBindViewHolder(@NonNull WordViewHolder viewholder, int position) {
        viewholder.k_word.setText(ws.get(position).getK_word());
        viewholder.e_word.setText(ws.get(position).getE_word());
    }

    @Override
    public int getItemCount() {
        return (null != ws ? ws.size() : 0);
    }
}
