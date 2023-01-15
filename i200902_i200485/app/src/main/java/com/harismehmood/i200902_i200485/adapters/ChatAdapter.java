package com.harismehmood.i200902_i200485.adapters;



import android.app.AlertDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.util.Base64;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.constraintlayout.widget.ConstraintLayout;
import androidx.recyclerview.widget.RecyclerView;

import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.firebase.Timestamp;
import com.google.firebase.firestore.FirebaseFirestore;
import com.harismehmood.i200902_i200485.R;
import com.harismehmood.i200902_i200485.User.ChatModel;
import com.harismehmood.i200902_i200485.utilities.Constants;

import java.util.List;


public class ChatAdapter extends RecyclerView.Adapter<RecyclerView.ViewHolder>{
    private final Context context;
    public final List<ChatModel> arrayChats;
    private Bitmap receivedProfileImage;
    private final String senderId;


    public final static int VIEW_TYPE_MESSAGE_SENT = 1;
    public final static int VIEW_TYPE_MESSAGE_RECEIVED = 2;

    public void setReceivedProfileImage(Bitmap bitmap){
        receivedProfileImage = bitmap;
    }

    public ChatAdapter(Context context, List<ChatModel> arrayChats, Bitmap receivedProfileImage, String senderId) {
        this.context=context;
        this.arrayChats=arrayChats;
        this.receivedProfileImage=receivedProfileImage;
        this.senderId=senderId;
    }


    @NonNull
    @Override
    public RecyclerView.ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {

        if(viewType==VIEW_TYPE_MESSAGE_SENT)
            return new sendMessageViewholder(
                    LayoutInflater.from(parent.getContext()).inflate(R.layout.user_send_message_recycler_layout, parent, false)
                   //set long click listener on singleChatLayout
            );

        else
            return new receiveMessageViewHolder(
                    LayoutInflater.from(parent.getContext()).inflate(R.layout.user_receive_message_recycler_layout, parent, false)
            );

    }

    @Override
    public void onBindViewHolder(@NonNull RecyclerView.ViewHolder holder, int position) {
        if(getItemViewType(position) == VIEW_TYPE_MESSAGE_SENT) {
            ((sendMessageViewholder) holder).setData(arrayChats.get(position));
        }
        else if(getItemViewType(position) == VIEW_TYPE_MESSAGE_RECEIVED) {
            ((receiveMessageViewHolder) holder).setData(arrayChats.get(position), receivedProfileImage);
        }
    }
public List<ChatModel> getArrayChats() {
    return arrayChats;
}
    @Override
    public int getItemCount() {
        return arrayChats.size();
    }

    public int getItemViewType(int position) {
        return arrayChats.get(position).senderId.equals(senderId) ? VIEW_TYPE_MESSAGE_SENT : VIEW_TYPE_MESSAGE_RECEIVED;
    }

    static class sendMessageViewholder extends RecyclerView.ViewHolder{

    public sendMessageViewholder(@NonNull View itemView) {
        super(itemView);
    }
        public  void setData(ChatModel chatModel){
         //   ImageView profileImage = itemView.findViewById(R.id.receiveMessageImage);
            TextView message = itemView.findViewById(R.id.textMessage);
            TextView time = itemView.findViewById(R.id.textTime);
            ImageView imageMessage = itemView.findViewById(R.id.messageImageView);
            ConstraintLayout singleMessageLayout = itemView.findViewById(R.id.singleMessageLayout);
         //   profileImage.setImageBitmap(recievedProfileImage);
            message.setText(chatModel.message);
            time.setText(chatModel.messageTime);
            if(chatModel.imageMessage!=null){
                imageMessage.setVisibility(View.VISIBLE);
                byte[] decodedString = Base64.decode(chatModel.imageMessage, Base64.DEFAULT);
                Bitmap decodedByte = BitmapFactory.decodeByteArray(decodedString, 0, decodedString.length);
                imageMessage.setImageBitmap(decodedByte);
            }
            else
                imageMessage.setVisibility(View.GONE);
            if(message.getText().toString().equals("")){
                message.setVisibility(View.GONE);
            }
            else
                message.setVisibility(View.VISIBLE);

            //set long click listener on singleChatLayout
            singleMessageLayout.setOnLongClickListener(new View.OnLongClickListener() {
                @Override
                public boolean onLongClick(View v) {

                       FirebaseFirestore database;
                        database = FirebaseFirestore.getInstance();
                        //delete message from database

               AlertDialog.Builder builder = new AlertDialog.Builder(itemView.getContext());
                builder.setTitle("Delete Message");
                builder.setIcon(R.drawable.ic_baseline_delete_24);
                builder.setMessage("Are you sure you want to delete this message?");
                builder.setPositiveButton("Yes", (dialog, which) -> {
                    database.collection(Constants.KEY_CHAT_ROOMS).
                            whereEqualTo(Constants.KEY_CHAT_ROOM_MESSAGE, chatModel.message)
                            .get()
                            .addOnSuccessListener(queryDocumentSnapshots -> {
                                if (!queryDocumentSnapshots.isEmpty()) {
                                    for (int i = 0; i < queryDocumentSnapshots.size(); i++) {
                                        database.collection(Constants.KEY_CHAT_ROOMS)
                                                .document(queryDocumentSnapshots.getDocuments().get(i).getId())
                                                .delete()
                                                .addOnSuccessListener(new OnSuccessListener<Void>() {
                                                    @Override
                                                    public void onSuccess(Void aVoid) {
                                                        //remove element from adapter and notify adapter

                                                        dialog.dismiss();
                                                    }
                                                })
                                                .addOnFailureListener(new OnFailureListener() {
                                                    @Override
                                                    public void onFailure(@NonNull Exception e) {
                                                        Toast.makeText(itemView.getContext(), "Error: " + e.getMessage(), Toast.LENGTH_SHORT).show();
                                                    }
                                                });
                                    }
                                }
                            });
                });
                builder.setNegativeButton("No", (dialog, which) -> {
                    dialog.dismiss();
                }).show();


                    return true;
                }
           });
        }

    }
    static class receiveMessageViewHolder extends RecyclerView.ViewHolder {

    public receiveMessageViewHolder(@NonNull View itemView) {
        super(itemView);
    }
        public  void setData(ChatModel chatModel, Bitmap receivedProfileImage){
            ImageView profileImage = itemView.findViewById(R.id.receiveMessageImage);
            TextView message = itemView.findViewById(R.id.textMessage);
            TextView time = itemView.findViewById(R.id.textTime);
            ImageView imageMessage = itemView.findViewById(R.id.messageImageView);
    //        profileImage.setImageBitmap(recievedProfileImage);
            message.setText(chatModel.message);
            time.setText(chatModel.messageTime);
            if(chatModel.imageMessage!=null){
                imageMessage.setVisibility(View.VISIBLE);
                byte[] decodedString = Base64.decode(chatModel.imageMessage, Base64.DEFAULT);
                Bitmap decodedByte = BitmapFactory.decodeByteArray(decodedString, 0, decodedString.length);
                imageMessage.setImageBitmap(decodedByte);
            }
            else
                imageMessage.setVisibility(View.GONE);
            if(message.getText().toString().equals("")){
                message.setVisibility(View.GONE);
            }
            else
                message.setVisibility(View.VISIBLE);
            if(receivedProfileImage != null)
                profileImage.setImageBitmap(receivedProfileImage);
        }

    }

}