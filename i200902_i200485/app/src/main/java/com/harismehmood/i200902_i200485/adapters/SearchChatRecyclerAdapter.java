package com.harismehmood.i200902_i200485.adapters;


import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.util.Base64;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.constraintlayout.widget.ConstraintLayout;
import androidx.recyclerview.widget.RecyclerView;

import com.harismehmood.i200902_i200485.R;
import com.harismehmood.i200902_i200485.User.UserModel;
import com.harismehmood.i200902_i200485.listeners.UserListener;

import java.util.List;

public class SearchChatRecyclerAdapter extends RecyclerView.Adapter<SearchChatRecyclerAdapter.viewholder> {

    Context context;
    List<UserModel> arrayContacts;
    UserListener userListener;

    public SearchChatRecyclerAdapter(Context context, List<UserModel> array_contact) {
        this.context = context;
        this.arrayContacts = array_contact;
        this.userListener = (UserListener) context;
    }

    @NonNull
    @Override
    public viewholder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View item = LayoutInflater.from(context).inflate(R.layout.search_chat_row_design, parent, false);
        viewholder view_holder = new viewholder(item);
        return view_holder;
    }

    @NonNull
    @Override
    public void onBindViewHolder(@NonNull SearchChatRecyclerAdapter.viewholder holder, int position) {
        holder.txtUserName.setText(arrayContacts.get(position).userName);
      // holder.txtLastMessage.setText(arrayContacts.get(position).lastMessage);
      // holder.txtLastMessageTime.setText(arrayContacts.get(position).lastMessageTime);
       holder.imgUserImage.setImageBitmap(getUserBitmapImage(arrayContacts.get(position).userImage));
       holder.singleChatRow.setOnClickListener(v -> userListener.onUserClicked(arrayContacts.get(position)));


    }


    @Override
    public int getItemCount() {
        return arrayContacts.size();
    }

    public class viewholder extends RecyclerView.ViewHolder {
        TextView txtUserName, txtLastMessage, txtLastMessageTime;
        ImageView imgUserImage,imgSeenUnseen;
        ConstraintLayout singleChatRow;
        //declare elements here like textview, imageview etc.

        public viewholder(@NonNull View itemView) {
            super(itemView);

            // create elements and store data in it by getting element id by item.
            // and store data in those elements get the id of element from where they belongs to
            txtUserName = itemView.findViewById(R.id.searchChatRowUserName);
           // txtLastMessage = itemView.findViewById(R.id.mainChatRowLastMessage);
           // txtLastMessageTime = itemView.findViewById(R.id.mainChatRowLastMessageTime);
            imgUserImage = itemView.findViewById(R.id.searchChatRowProfileImage);
            singleChatRow = itemView.findViewById(R.id.searchChatSingleRow);
        }

    }
    public Bitmap getUserBitmapImage(String encodedImage){
        byte [] encodeByte=Base64.decode(encodedImage, Base64.DEFAULT);
        return BitmapFactory.decodeByteArray(encodeByte, 0, encodeByte.length);
    }
}
