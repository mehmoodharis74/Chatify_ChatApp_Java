package com.harismehmood.i200902_i200485.utilities;

import java.util.HashMap;

public class Constants {
    public static final String USERS_KEY_COLLECTIONS = "users";
    public static final String USER_NAME = "userName";
    public static final String USER_IMG = "userIMG";
    public static final String USER_ID = "userId";
    public static final String USER_PHONE = "userPhone";
    public static final String KEY_UserPREFERENCE = "chatAppPreferences";
    public static final String KEY_IS_SIGNED_IN = "isSignedIn";
    public static final String KEY_FCM_TOKEN = "fcmToken";
    public static final String KEY_USER = "users";
    public static final String KEY_CHAT_ROOMS = "chatRooms";
    public static final String KEY_SENDER_ID = "senderId";
    public static final String KEY_RECEIVER_ID = "receiverId";
    public static final String KEY_CHAT_ROOM_MESSAGE = "message";
    public static final String KEY_CHAT_ROOM_MESSAGE_IMAGE = "ImageMessage";
    public static final String KEY_TIMESTAMP = "timestamp";
    public static final String KEY_COLLECTION_CONVERSATIONS = "conversations";
    public static final String KEY_SENDER_NAME = "senderName";
    public static final String KEY_RECEIVER_NAME = "receiverName";
    public static final String KEY_SENDER_IMAGE = "senderImage";
    public static final String KEY_RECEIVER_IMAGE = "receiverImage";
    public static final String KEY_LAST_MESSAGE = "lastMessage";
    public static final String KEY_AVAILABILITY = "availability";
    public static final String KEY_REMOTE_MSG_AUTHORIZATION = "Authorization";
    public static final String KEY_REMOTE_MSG_CONTENT_TYPE = "Content-Type";
    public static final String KEY_REMOTE_MSG_DATA = "data";
    public static final String KEY_REMOTE_MSG_REGISTRATION_IDS = "registration_ids";

    public static HashMap<String, String> remoteMsgHeaders = null;
    public static HashMap<String, String> getRemoteMsgHeaders(){
        if(remoteMsgHeaders == null ){
            remoteMsgHeaders = new HashMap<>();
            remoteMsgHeaders.put(
                    KEY_REMOTE_MSG_AUTHORIZATION,
                    "key=AAAAuOwwYCU:APA91bHO4EtBJ4wE_qc6LZCLo6vwPK4mhdzsKugANSkHM1Ns3udifEAezszTI5eFuAt6OTW2NjIVtIxMtgMDnLH1-HqU5Xa2ZhA5zGLHBX_1HIQCBltB3HZWFhjkhX1O-UBp625nMNlO"
            );
            remoteMsgHeaders.put(
                    KEY_REMOTE_MSG_CONTENT_TYPE,
                    "application/json"
            );

        }
        return remoteMsgHeaders;
    }

}
