package com.example.logintest.message;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.graphics.Typeface;
import android.os.Bundle;
import android.text.Editable;
import android.text.TextWatcher;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.ScrollView;
import android.widget.TextView;

import com.example.logintest.data.model.GlideApp;
import com.example.logintest.profile.ProfileActivity;
import com.example.logintest.R;
import com.example.logintest.data.model.TextMessage;
import com.example.logintest.data.model.User;
import com.example.logintest.databinding.ActivityMessageBinding;
import com.google.firebase.FirebaseApp;
import com.google.firebase.database.ChildEventListener;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;

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

    StorageReference storageRef;

    private Calendar calendar;
    private SimpleDateFormat dateFormat;
    private String date;

    final public String TAG = "IVANNIA DEBUGGING";

    String current_user;

    private Menu menu;

    String user_message;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        scale = MessageActivity.this.getResources().getDisplayMetrics().density;
        super.onCreate(savedInstanceState);
        binding = ActivityMessageBinding.inflate(getLayoutInflater());
        setContentView(binding.getRoot());

        Intent intent = getIntent();

        current_user = "VD0zJUxccHfkHmoGnLb606t0G2G3";

        // MESSAGES ADDED WITH FIREBASE START

        initFirebase();

        // MESSAGES ADDED WITH FIREBASE END

        binding.inputEditText.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {

            }

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {
                if(s.toString().trim().length()==0){ // Make sure button is enabled only when
                                                     // there is text as input
                    binding.sendButton.setEnabled(false);
                } else {
                    binding.sendButton.setEnabled(true);
                }
            }

            @Override
            public void afterTextChanged(Editable s) {

            }
        });

        binding.sendButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

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
            }
        });
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu){
        getMenuInflater().inflate(R.menu.menu, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(@NonNull MenuItem item) {

        int id = item.getItemId();

        //menu item clicked handling

        if(id == R.id.profile){
            Intent intent = new Intent(MessageActivity.this, ProfileActivity.class);
            intent.putExtra("userName", current_user);
            startActivity(intent);
        }

        return super.onOptionsItemSelected(item);
    }

    private void initFirebase(){
        FirebaseApp.initializeApp(MessageActivity.this);
        firebaseDatabase = firebaseDatabase.getInstance();
        databaseReference = firebaseDatabase.getReference();
        databaseReference.child("messages").addChildEventListener(new ChildEventListener() {
            @Override
            public void onChildAdded(@NonNull DataSnapshot snapshot, @Nullable String previousChildName) {
                Map<String,Object> map = (Map<String,Object>) snapshot.getValue();
                TextMessage newMessage = new TextMessage(String.valueOf(map.get("userId")),
                        String.valueOf(map.get("text")), String.valueOf(map.get("date")));

                binding.linearLayoutFull.addView(createNewMessageDisplay(
                        newMessage.getUserId(),
                        newMessage.getText()));

                ScrollView scrollview = ((ScrollView) binding.scrollView); // Go to bottom for
                                                                           // every new message
                scrollview.post(new Runnable() {
                    @Override
                    public void run() {
                        scrollview.fullScroll(ScrollView.FOCUS_DOWN);
                    }
                });
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

        FirebaseStorage storage = FirebaseStorage.getInstance();
        storageRef = storage.getReference();
    }

    public LinearLayout createNewMessageDisplay(String user_name, String message){

        DatabaseReference user = databaseReference.child("users").child(user_name).child("username");

        ValueEventListener postListener = new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {
                user_message = dataSnapshot.child("users").child(user_name).child("username").getValue(String.class);
            }

            @Override
            public void onCancelled(DatabaseError databaseError) {
                // Getting Post failed, log a message
            }
        };
        databaseReference.addValueEventListener(postListener);

        LinearLayout box = new LinearLayout(MessageActivity.this);
        box.setOrientation(LinearLayout.HORIZONTAL);
        box.setLayoutParams(new LinearLayout.LayoutParams(LinearLayout.LayoutParams.FILL_PARENT, LinearLayout.LayoutParams.FILL_PARENT));

        ImageView userImage = new ImageView(MessageActivity.this);
        userImage.setLayoutParams(new LinearLayout.LayoutParams(LinearLayout.LayoutParams.FILL_PARENT, LinearLayout.LayoutParams.FILL_PARENT));
        userImage.getLayoutParams().height = (int) (50 * scale + 0.5f);
        userImage.getLayoutParams().width = (int) (50 * scale + 0.5f);
        StorageReference userImageStorage = storageRef.child("usersImages").child(user_message);

        GlideApp.with(this /* context */)
                .load(userImageStorage)
                .into(userImage);
        box.addView(userImage);

        LinearLayout text = new LinearLayout(MessageActivity.this);
        text.setOrientation(LinearLayout.VERTICAL);
        text.setLayoutParams(new LinearLayout.LayoutParams(LinearLayout.LayoutParams.FILL_PARENT, LinearLayout.LayoutParams.WRAP_CONTENT));

        TextView userName = new TextView(MessageActivity.this);

        userName.setText(user_message);
        userName.setTypeface(Typeface.DEFAULT_BOLD);
        text.addView(userName);
        TextView textMessage = new TextView(MessageActivity.this);
        textMessage.setText(message);
        text.addView(textMessage);

        box.addView(text);

        return box;
    }
}