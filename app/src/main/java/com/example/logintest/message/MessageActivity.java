package com.example.logintest.message;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import android.content.res.Resources;
import android.graphics.Typeface;
import android.media.Image;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.ScrollView;
import android.widget.TextView;
import android.widget.Toast;

import com.example.logintest.data.model.LoggedInUser;
import com.example.logintest.data.model.User;
import com.example.logintest.databinding.ActivityMessageBinding;
import com.google.firebase.FirebaseApp;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import org.json.JSONException;
import org.json.JSONObject;

import java.util.Iterator;
import java.util.Set;

public class MessageActivity extends AppCompatActivity {

    ActivityMessageBinding binding;
    float scale;

    // creating a variable for
    // our Firebase Database.
    FirebaseDatabase firebaseDatabase;

    // creating a variable for our
    // Database Reference for Firebase.
    DatabaseReference databaseReference;

    LinearLayout newMessage;

    final public String TAG = "IVANNIA DEBUGGING";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        scale = MessageActivity.this.getResources().getDisplayMetrics().density;
        super.onCreate(savedInstanceState);
        binding = ActivityMessageBinding.inflate(getLayoutInflater());
        setContentView(binding.getRoot());

        Log.v(TAG, FirebaseApp.getInstance().getOptions().getProjectId());

        // MESSAGES ADDED WITH FIREBASE START

        // below line is used to get the instance
        // of our Firebase database.
        firebaseDatabase = FirebaseDatabase.getInstance();

        // below line is used to get
        // reference for our database.
        databaseReference = firebaseDatabase.getReference("messages");

        getdata();

        // MESSAGES ADDED WITH FIREBASE END

        ScrollView scrollview = ((ScrollView) binding.scrollView);
        scrollview.post(new Runnable() { // Start at the bottom
            @Override
            public void run() {
                scrollview.fullScroll(ScrollView.FOCUS_DOWN);
            }
        });

        binding.sendButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                ScrollView scrollview = ((ScrollView) binding.scrollView);
                scrollview.post(new Runnable() { // Start at the bottom
                    @Override
                    public void run() {
                        scrollview.fullScroll(ScrollView.FOCUS_DOWN);
                    }
                });

                binding.inputEditText.setText("");

                // TO DO: Send message to database and show message?
            }
        });
    }

    public LinearLayout createNewMessageDisplay(String user_name, String message){

        // TO DO: Change to make it dynamic

        User user = new User("1", user_name, getResources().getIdentifier("ic_user1",
                "drawable", getPackageName()));

        LinearLayout box = new LinearLayout(MessageActivity.this);
        box.setOrientation(LinearLayout.HORIZONTAL);
        box.setLayoutParams(new LinearLayout.LayoutParams(LinearLayout.LayoutParams.FILL_PARENT, LinearLayout.LayoutParams.FILL_PARENT));

        ImageView userImage = new ImageView(MessageActivity.this);
        userImage.setLayoutParams(new LinearLayout.LayoutParams(LinearLayout.LayoutParams.FILL_PARENT, LinearLayout.LayoutParams.FILL_PARENT));
        userImage.getLayoutParams().height = (int) (50 * scale + 0.5f);
        userImage.getLayoutParams().width = (int) (50 * scale + 0.5f);
        userImage.setImageResource(user.getUserIcon());
        box.addView(userImage);

        LinearLayout text = new LinearLayout(MessageActivity.this);
        text.setOrientation(LinearLayout.VERTICAL);
        text.setLayoutParams(new LinearLayout.LayoutParams(LinearLayout.LayoutParams.FILL_PARENT, LinearLayout.LayoutParams.WRAP_CONTENT));

        TextView userName = new TextView(MessageActivity.this);
        userName.setText(user.getDisplayName());
        userName.setTypeface(Typeface.DEFAULT_BOLD);
        text.addView(userName);
        TextView textMessage = new TextView(MessageActivity.this);
        textMessage.setText(message);
        text.addView(textMessage);

        box.addView(text);

        return box;
    }

    private void getdata() {

        // calling add value event listener method
        // for getting the values from database.
        databaseReference.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                // this method is call to get the realtime
                // updates in the data.
                // this method is called when the data is
                // changed in our Firebase console.
                // below line is for getting the data from
                // snapshot of our database.
                JSONObject messages = (JSONObject) snapshot.getValue(JSONObject.class);

                Iterator< ? > keys = messages.keys();

                while (keys.hasNext()) {
                    String key = (String) keys.next();
                    try {
                        if (messages.get(key) instanceof JSONObject) {
                            JSONObject message = (JSONObject) messages.get(key);
                            String user_name = message.getString("name");
                            String text = message.getString("text");

                            newMessage = createNewMessageDisplay(user_name, text);

                            binding.linearLayoutFull.addView(newMessage);
                        }
                    } catch (JSONException e) {
                        throw new RuntimeException(e);
                    }
                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {
                // calling on cancelled method when we receive
                // any error or we are not able to get the data.
                Toast.makeText(MessageActivity.this, "Fail to get data.", Toast.LENGTH_SHORT).show();
            }
        });
    }
}