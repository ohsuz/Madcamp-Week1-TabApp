package com.example.tabproject;

import android.Manifest;
import android.app.Activity;
import android.app.AlertDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.net.Uri;
import android.os.Build;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.squareup.picasso.Picasso;

import java.util.List;

import androidx.annotation.NonNull;
import androidx.core.app.ActivityCompat;
import androidx.core.content.ContextCompat;
import androidx.recyclerview.widget.RecyclerView;
import de.hdodenhof.circleimageview.CircleImageView;

import static androidx.core.app.ActivityCompat.requestPermissions;
import static androidx.core.app.ActivityCompat.shouldShowRequestPermissionRationale;
import static androidx.core.content.ContextCompat.*;

public class ContactAdapter extends RecyclerView.Adapter<ContactAdapter.ContactViewHolder>{
    Context mContext;
    List<Contact> contactList;

    public ContactAdapter(Context mContext, List<Contact> contactList){
        this.mContext = mContext;
        this.contactList = contactList;
    }

    // onCreateViewHolder: viewType 형태의 아이템 뷰를 위한 뷰홀더 객체 생성
    @NonNull
    @Override
    public ContactViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(mContext).inflate(R.layout.phone_item,parent,false);
        return new ContactViewHolder(view);
    }

    // onBindViewHolder: position에 해당하는 데이터를 뷰홀더의 아이템뷰에 표시
    @Override
    public void onBindViewHolder(@NonNull ContactViewHolder holder, int position) {
        Contact contact = contactList.get(position);
        holder.name.setText(contact.getName());
        holder.phone.setText(contact.getPhone());

        if(contact.getPhoto()!=null){
            Picasso.get().load(contact.getPhoto()).into(holder.img);
        }else{
            holder.img.setImageResource(R.drawable.ic_person);
        }
    }

    // getItemCount(): 전체 아이템 개수리턴
    @Override
    public int getItemCount() {
        return contactList.size();
    }

    // ViewHolder: 화면에 표시될 아이템 뷰를 저장하는 객체
    public class ContactViewHolder extends RecyclerView.ViewHolder{

        TextView name, phone;
        CircleImageView img;
        ImageView call, msg;

        public ContactViewHolder(final View itemView) {
            super(itemView);
            name = itemView.findViewById(R.id.name);
            phone = itemView.findViewById(R.id.phone);
            img = itemView.findViewById(R.id.img);
            call = itemView.findViewById(R.id.call);
            msg = itemView.findViewById(R.id.msg);
            //Toast.makeText(mContext, "tel:"+contactList.get(getAdapterPosition()).getPhone(), Toast.LENGTH_SHORT).show();

            call.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    ActivityCompat.requestPermissions((Activity)mContext, new String[]{Manifest.permission.CALL_PHONE},1000);

                    if(checkSelfPermission(mContext, Manifest.permission.CALL_PHONE)== PackageManager.PERMISSION_GRANTED) {
                        mContext.startActivity(new Intent("android.intent.action.CALL", Uri.parse("tel:"+contactList.get(getAdapterPosition()).getPhone())));
                    }else{
                        Toast.makeText(mContext, "Permission denied.", Toast.LENGTH_SHORT).show();
                    }
                }
            });

            msg.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    Intent intent = new Intent((Activity)mContext, MessagePopup.class);
                    intent.putExtra("phone", contactList.get(getAdapterPosition()).getPhone());
                    mContext.startActivity(intent);

                    /*
                    Uri uri = Uri.parse("smsto:"+contactList.get(getAdapterPosition()).getPhone());
                    Intent it = new Intent(Intent.ACTION_SENDTO, uri);
                    it.putExtra("sms_body","hi");
                    mContext.startActivity(it);

                     */
                }
            });


        }
    }
}
