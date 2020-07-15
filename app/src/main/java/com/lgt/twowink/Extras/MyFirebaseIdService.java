package com.lgt.twowink.Extras;

import androidx.annotation.NonNull;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.iid.FirebaseInstanceId;
import com.google.firebase.messaging.FirebaseMessagingService;
import com.lgt.twowink.Model.Token;

public class MyFirebaseIdService extends FirebaseMessagingService {

    String userId;

    private SessionManager sessionManager;

    @Override
    public void onNewToken(@NonNull String s) {
        super.onNewToken(s);
        sessionManager=new SessionManager();
        userId=sessionManager.getUser(getApplicationContext()).getUser_id();
        String refreshToken= FirebaseInstanceId.getInstance().getToken();
        if (!userId.equals("")){
            updateToken(refreshToken);
        }
    }

    private void updateToken(String refreshToken) {

        DatabaseReference databaseReference= FirebaseDatabase.getInstance().getReference().child(Commn.Tokens).child(userId);
        Token token=new Token(refreshToken);
        databaseReference.child(Commn.user_token).setValue(token);
    }

}
