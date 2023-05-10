package com.example.logintest.message;

import androidx.appcompat.app.AppCompatActivity;

import android.content.res.Resources;
import android.graphics.Typeface;
import android.media.Image;
import android.os.Bundle;
import android.view.View;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.ScrollView;
import android.widget.TextView;

import com.example.logintest.data.model.LoggedInUser;
import com.example.logintest.data.model.User;
import com.example.logintest.databinding.ActivityMessageBinding;

public class MessageActivity extends AppCompatActivity {

    ActivityMessageBinding binding;
    float scale;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        scale = MessageActivity.this.getResources().getDisplayMetrics().density;
        super.onCreate(savedInstanceState);
        binding = ActivityMessageBinding.inflate(getLayoutInflater());
        setContentView(binding.getRoot());

        // MESSAGES ADDED WITH FIREBASE START

        User user1 = new User("1", "Ivan", getResources().getIdentifier("ic_user1",
                "drawable", getPackageName()));

        User user2 = new User("2", "Ivannia", getResources().getIdentifier("ic_user2",
                "drawable", getPackageName()));

        LinearLayout newMessage1;
        LinearLayout newMessage;

        newMessage1 = createNewMessageDisplay(user1, "This is Ivan's my test message just like I was saying this is a really" +
                "long text message that should write at leat 2 sentences");

        binding.linearLayoutFull.addView(newMessage1);

        newMessage = createNewMessageDisplay(user2, "This is Ivannia's my test message just like I was saying this is a really" +
                "long text message that should write at leat 2 sentences");

        binding.linearLayoutFull.addView(newMessage);

        newMessage1 = createNewMessageDisplay(user1, "This is Ivan's my test message just like I was saying this is a really" +
                "long text message that should write at leat 2 sentences");

        binding.linearLayoutFull.addView(newMessage1);

        newMessage = createNewMessageDisplay(user2, "This is Ivannia's my test message just like I was saying this is a really" +
                "long text message that should write at leat 2 sentences");

        binding.linearLayoutFull.addView(newMessage);

        newMessage1 = createNewMessageDisplay(user1, "This is Ivan's my test message just like I was saying this is a really" +
                "long text message that should write at leat 2 sentences");

        binding.linearLayoutFull.addView(newMessage1);

        newMessage = createNewMessageDisplay(user2, "This is Ivannia's my test message just like I was saying this is a really" +
                "long text message that should write at leat 2 sentences");

        binding.linearLayoutFull.addView(newMessage);

        newMessage1 = createNewMessageDisplay(user1, "This is Ivan's my test message just like I was saying this is a really" +
                "long text message that should write at leat 2 sentences");

        binding.linearLayoutFull.addView(newMessage1);

        newMessage = createNewMessageDisplay(user2, "This is Ivannia's my test message just like I was saying this is a really" +
                "long text message that should write at leat 2 sentences");

        binding.linearLayoutFull.addView(newMessage);

        newMessage1 = createNewMessageDisplay(user1, "This is Ivan's my test message just like I was saying this is a really" +
                "long text message that should write at leat 2 sentences");

        binding.linearLayoutFull.addView(newMessage1);

        newMessage = createNewMessageDisplay(user2, "This is Ivannia's my test message just like I was saying this is a really" +
                "long text message that should write at leat 2 sentences");

        binding.linearLayoutFull.addView(newMessage);

        newMessage1 = createNewMessageDisplay(user1, "This is Ivan's my test message just like I was saying this is a really" +
                "long text message that should write at leat 2 sentences");

        binding.linearLayoutFull.addView(newMessage1);

        newMessage = createNewMessageDisplay(user2, "This is Ivannia's my test message just like I was saying this is a really" +
                "long text message that should write at leat 2 sentences");

        binding.linearLayoutFull.addView(newMessage);

        newMessage1 = createNewMessageDisplay(user1, "This is Ivan's my test message just like I was saying this is a really" +
                "long text message that should write at leat 2 sentences");

        binding.linearLayoutFull.addView(newMessage1);

        newMessage = createNewMessageDisplay(user2, "This is the last message from me");

        binding.linearLayoutFull.addView(newMessage);

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

    public LinearLayout createNewMessageDisplay(User user, String message){
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