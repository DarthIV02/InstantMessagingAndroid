package com.example.logintest.message;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;

import android.graphics.Typeface;
import android.os.Bundle;
import android.view.View;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.ScrollView;
import android.widget.TextView;

import com.example.logintest.data.model.TextMessage;
import com.example.logintest.data.model.User;
import com.example.logintest.databinding.ActivityMessageBinding;
import com.google.firebase.FirebaseApp;
import com.google.firebase.database.ChildEventListener;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;

import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Map;

public class MessageActivity extends AppCompatActivity {

    ActivityMessageBinding binding;
    float scale;

    // creating a variable for
    // our Firebase Database.
    FirebaseDatabase firebaseDatabase;

    // creating a variable for our
    // Database Reference for Firebase.
    DatabaseReference databaseReference;

    private Calendar calendar;
    private SimpleDateFormat dateFormat;
    private String date;

    final public String TAG = "IVANNIA DEBUGGING";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        scale = MessageActivity.this.getResources().getDisplayMetrics().density;
        super.onCreate(savedInstanceState);
        binding = ActivityMessageBinding.inflate(getLayoutInflater());
        setContentView(binding.getRoot());

        // MESSAGES ADDED WITH FIREBASE START

        initFirebase();

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

                String current_user = "Ivannia";

                //String messageId = UUID.randomUUID().toString();

                if (android.os.Build.VERSION.SDK_INT >= android.os.Build.VERSION_CODES.O) {
                    calendar = Calendar.getInstance();
                    dateFormat = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
                    date = dateFormat.format(calendar.getTime());
                    TextMessage message = new TextMessage(current_user,
                            String.valueOf(binding.inputEditText.getText()),
                            String.valueOf(date));
                    String messageId = "m" + date;
                    databaseReference.child("messages").child(messageId).setValue(message);
                }

                binding.inputEditText.setText(""); // Set input to nothing
                scrollview.post(new Runnable() { // Start at the bottom
                    @Override
                    public void run() {
                        scrollview.fullScroll(ScrollView.FOCUS_DOWN);
                    }
                });

                // TO DO: Bring down keyboard once the message is sent
            }
        });
    }

    private void initFirebase(){
        FirebaseApp.initializeApp(MessageActivity.this);
        firebaseDatabase = firebaseDatabase.getInstance();
        databaseReference = firebaseDatabase.getReference();
        databaseReference.child("messages").addChildEventListener(new ChildEventListener() {
            @Override
            public void onChildAdded(@NonNull DataSnapshot snapshot, @Nullable String previousChildName) {
                Map<String,Object> map = (Map<String,Object>) snapshot.getValue();
                TextMessage newMessage = new TextMessage(String.valueOf(map.get("userName")),
                        String.valueOf(map.get("text")), String.valueOf(map.get("date")));
                binding.linearLayoutFull.addView(createNewMessageDisplay(
                        newMessage.getUserName(),
                        newMessage.getText()));
            }

            @Override
            public void onChildChanged(@NonNull DataSnapshot snapshot, @Nullable String previousChildName) {

            }

            @Override
            public void onChildRemoved(@NonNull DataSnapshot snapshot) {

            }

            @Override
            public void onChildMoved(@NonNull DataSnapshot snapshot, @Nullable String previousChildName) {

            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {

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
}