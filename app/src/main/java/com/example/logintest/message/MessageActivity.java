package com.example.logintest.message;

import static com.google.android.material.internal.ContextUtils.getActivity;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.widget.NestedScrollView;

import android.annotation.SuppressLint;
import android.app.Activity;
import android.app.ProgressDialog;
import android.content.Intent;
import android.content.SharedPreferences;
import android.graphics.Bitmap;
import android.graphics.Typeface;
import android.net.Uri;
import android.os.Bundle;
import android.provider.MediaStore;
import android.text.Editable;
import android.text.TextWatcher;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.inputmethod.InputMethodManager;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.ScrollView;
import android.widget.TextView;
import android.widget.Toast;

import com.bumptech.glide.load.resource.bitmap.CenterCrop;
import com.bumptech.glide.load.resource.bitmap.RoundedCorners;
import com.bumptech.glide.request.RequestOptions;
import com.example.logintest.data.model.GlideApp;
import com.example.logintest.data.model.ImageMessage;
import com.example.logintest.data.model.Message;
import com.example.logintest.profile.ProfileActivity;
import com.example.logintest.R;
import com.example.logintest.data.model.TextMessage;
import com.example.logintest.data.model.User;
import com.example.logintest.databinding.ActivityMessageBinding;
import com.example.logintest.ui.login.RegisterActivity;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.gms.tasks.Task;
import com.google.android.material.imageview.ShapeableImageView;
import com.google.firebase.FirebaseApp;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.ChildEventListener;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.Query;
import com.google.firebase.database.ValueEventListener;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.OnProgressListener;
import com.google.firebase.storage.StorageReference;
import com.google.firebase.storage.UploadTask;

import java.io.IOException;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Map;
import java.util.UUID;

public class MessageActivity extends AppCompatActivity {

    ActivityMessageBinding binding;
    float scale;

    // creating a variable for
    // our Firebase Database.
    FirebaseDatabase firebaseDatabase;

    // creating a variable for our
    // Database Reference for Firebase.
    DatabaseReference databaseReference;

    FirebaseStorage storage;

    StorageReference storageRef;

    private Calendar calendar;
    private SimpleDateFormat dateFormat;
    private String date;

    final public String TAG = "IVANNIA DEBUGGING";

    String current_user;
    String current_id;

    private Menu menu;

    String user_message;

    private final int PICK_IMAGE_REQUEST = 22;

    private Uri filePath;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        scale = MessageActivity.this.getResources().getDisplayMetrics().density;
        super.onCreate(savedInstanceState);
        binding = ActivityMessageBinding.inflate(getLayoutInflater());
        setContentView(binding.getRoot());

        Intent intent = getIntent();

        current_id = intent.getStringExtra("uid");
        current_user = intent.getStringExtra("username");

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

        androidx.core.widget.NestedScrollView scrollview = ((androidx.core.widget.NestedScrollView) binding.scrollView); // Go to bottom for
        // every new message
        scrollview.post(new Runnable() {
            @Override
            public void run() {
                scrollview.fullScroll(ScrollView.FOCUS_DOWN);
            }
        });

        binding.sendButton.setOnClickListener(new View.OnClickListener() {
            @SuppressLint("RestrictedApi")
            @Override
            public void onClick(View v) {

                if (android.os.Build.VERSION.SDK_INT >= android.os.Build.VERSION_CODES.O) {
                    calendar = Calendar.getInstance();
                    dateFormat = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
                    date = dateFormat.format(calendar.getTime());
                    TextMessage message = new TextMessage(current_user, current_id,
                            String.valueOf(binding.inputEditText.getText()),
                            String.valueOf(date));
                    String messageId = "m" + date;
                    databaseReference.child("messages").child(messageId).setValue(message);
                }

                binding.inputEditText.setText(""); // Set input to nothing
                hideKeyboard(MessageActivity.this);
            }
        });

        binding.addImageButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                upload_Image();
            }
        });
    }

    public static void hideKeyboard(Activity activity) {
        InputMethodManager imm = (InputMethodManager) activity.getSystemService(Activity.INPUT_METHOD_SERVICE);
        //Find the currently focused view, so we can grab the correct window token from it.
        View view = activity.getCurrentFocus();
        //If no view currently has focus, create a new one, just so we can grab a window token from it
        if (view == null) {
            view = new View(activity);
        }
        imm.hideSoftInputFromWindow(view.getWindowToken(), 0);
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
            intent.putExtra("USERID", current_id);
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

                if(map.get("text") == null){
                     ImageMessage newMessage = new ImageMessage(String.valueOf(map.get("userName")),
                             String.valueOf(map.get("userId")), String.valueOf(map.get("imageUID")),
                             String.valueOf(map.get("date")));

                    binding.linearLayoutFull.addView(createNewImageDisplay(
                            newMessage.getUserName(),
                            newMessage.getUserId(),
                            newMessage.getImageUID())
                    );

                } else {
                     TextMessage newMessage = new TextMessage(String.valueOf(map.get("userName")),
                             String.valueOf(map.get("userId")), String.valueOf(map.get("text")),
                             String.valueOf(map.get("date")));

                    binding.linearLayoutFull.addView(createNewMessageDisplay(
                            newMessage.getUserName(),
                            newMessage.getUserId(),
                            newMessage.getText())
                    );
                }

                androidx.core.widget.NestedScrollView scrollview = ((androidx.core.widget.NestedScrollView) binding.scrollView); // Go to bottom for
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

        storage = FirebaseStorage.getInstance();
        storageRef = storage.getReference();
    }

    public LinearLayout createNewMessageDisplay(String user_name, String uid, String message){

        LinearLayout box = new LinearLayout(MessageActivity.this);
        box.setOrientation(LinearLayout.HORIZONTAL);
        box.setLayoutParams(new LinearLayout.LayoutParams(LinearLayout.LayoutParams.FILL_PARENT, LinearLayout.LayoutParams.FILL_PARENT));

        //ImageView userImage = new ImageView(MessageActivity.this);
        ImageView userImage = new ShapeableImageView(MessageActivity.this);
        userImage.setLayoutParams(new LinearLayout.LayoutParams(LinearLayout.LayoutParams.FILL_PARENT, LinearLayout.LayoutParams.FILL_PARENT));
        userImage.getLayoutParams().height = (int) (60 * scale + 0.5f);
        userImage.getLayoutParams().width = (int) (60 * scale + 0.5f);
        userImage.setPadding(0,0,0,(int) (3 * scale + 0.5f));

        StorageReference userImageStorage = storageRef.child("usersImages").child(uid);

        GlideApp.with(getApplicationContext() /* context */)
                .load(userImageStorage)
                .apply(new RequestOptions().transforms(new CenterCrop(), new RoundedCorners(90)))
                .into(userImage);
        box.addView(userImage);

        LinearLayout text = new LinearLayout(MessageActivity.this);
        text.setOrientation(LinearLayout.VERTICAL);
        text.setLayoutParams(new LinearLayout.LayoutParams(LinearLayout.LayoutParams.FILL_PARENT, LinearLayout.LayoutParams.WRAP_CONTENT));
        text.setPadding((int) (10 * scale + 0.5f), 0, 0, 0);

        TextView userName = new TextView(MessageActivity.this);

        userName.setText(user_name);
        userName.setTypeface(Typeface.DEFAULT_BOLD);
        text.addView(userName);
        TextView textMessage = new TextView(MessageActivity.this);
        textMessage.setText(message);
        text.addView(textMessage);

        box.addView(text);

        return box;
    }

    public LinearLayout createNewImageDisplay(String user_name, String uid, String imageUID){

        LinearLayout box = new LinearLayout(MessageActivity.this);
        box.setOrientation(LinearLayout.HORIZONTAL);
        box.setLayoutParams(new LinearLayout.LayoutParams(LinearLayout.LayoutParams.FILL_PARENT, LinearLayout.LayoutParams.FILL_PARENT));


        ImageView userImage = new ShapeableImageView(MessageActivity.this);
        userImage.setLayoutParams(new LinearLayout.LayoutParams(LinearLayout.LayoutParams.FILL_PARENT, LinearLayout.LayoutParams.FILL_PARENT));
        userImage.getLayoutParams().height = (int) (60 * scale + 0.5f);
        userImage.getLayoutParams().width = (int) (60 * scale + 0.5f);
        userImage.setPadding(0,0,0,(int) (3 * scale + 0.5f));

        StorageReference userImageStorage = storageRef.child("usersImages").child(uid);

        GlideApp.with(getApplicationContext() /* context */)
                .load(userImageStorage)
                .apply(new RequestOptions().transforms(new CenterCrop(), new RoundedCorners(90)))
                .into(userImage);
        box.addView(userImage);

        LinearLayout text = new LinearLayout(MessageActivity.this);
        text.setOrientation(LinearLayout.VERTICAL);
        text.setLayoutParams(new LinearLayout.LayoutParams(LinearLayout.LayoutParams.FILL_PARENT, LinearLayout.LayoutParams.WRAP_CONTENT));
        text.setPadding((int) (10 * scale + 0.5f), 0, 0, 0);

        TextView userName = new TextView(MessageActivity.this);

        userName.setText(user_name);
        userName.setTypeface(Typeface.DEFAULT_BOLD);
        text.addView(userName);

        ImageView image = new ImageView(MessageActivity.this);
        StorageReference imageStorage = storageRef.child("images").child(imageUID);
        imageStorage.getDownloadUrl().addOnSuccessListener(new OnSuccessListener<Uri>() {
            @Override
            public void onSuccess(Uri uri) {
                GlideApp.with(getApplicationContext() /* context */)
                        .load(imageStorage)
                        .into(image);
                text.addView(image);
            }
        }).addOnFailureListener(new OnFailureListener() {
            @Override
            public void onFailure(@NonNull Exception e) {
                Log.v(TAG, e.getMessage());
            }
        });

        box.addView(text);

        return box;
    }

    public void upload_Image(){
        // Defining Implicit Intent to mobile gallery
        Intent intent = new Intent();
        intent.setType("image/*");
        intent.setAction(Intent.ACTION_GET_CONTENT);
        startActivityForResult(
                Intent.createChooser(
                        intent,
                        "Select Image from here..."),
                PICK_IMAGE_REQUEST);
    }

    @Override
    protected void onActivityResult(int requestCode,
                                    int resultCode,
                                    Intent data)
    {

        super.onActivityResult(requestCode,
                resultCode,
                data);

        // checking request code and result code
        // if request code is PICK_IMAGE_REQUEST and
        // resultCode is RESULT_OK
        // then set image in the image view
        if (requestCode == PICK_IMAGE_REQUEST
                && resultCode == RESULT_OK
                && data != null
                && data.getData() != null) {

            // Get the Uri of data
            filePath = data.getData();
            try {

                // Code for showing progressDialog while uploading
                ProgressDialog progressDialog
                        = new ProgressDialog(this);
                progressDialog.setTitle("Uploading...");
                progressDialog.show();

                // Defining the child of storageReference

                String uid = UUID.randomUUID().toString();

                StorageReference ref
                        = storageRef
                        .child(
                                "images/"
                                        + uid);

                // adding listeners on upload
                // or failure of image
                ref.putFile(filePath)
                        .addOnSuccessListener(
                                new OnSuccessListener<UploadTask.TaskSnapshot>() {

                                    @Override
                                    public void onSuccess(
                                            UploadTask.TaskSnapshot taskSnapshot)
                                    {

                                        // Image uploaded successfully
                                        // Dismiss dialog
                                        progressDialog.dismiss();
                                        Toast
                                                .makeText(MessageActivity.this,
                                                        "Image Uploaded!!",
                                                        Toast.LENGTH_SHORT)
                                                .show();

                                        if (android.os.Build.VERSION.SDK_INT >= android.os.Build.VERSION_CODES.O) {
                                            calendar = Calendar.getInstance();
                                            dateFormat = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
                                            date = dateFormat.format(calendar.getTime());
                                            ImageMessage message = new ImageMessage(current_user, current_id,
                                                    uid,
                                                    String.valueOf(date));
                                            String messageId = "m" + date;
                                            databaseReference.child("messages").child(messageId).setValue(message);
                                        }
                                    }
                                })

                        .addOnFailureListener(new OnFailureListener() {
                            @Override
                            public void onFailure(@NonNull Exception e)
                            {

                                // Error, Image not uploaded
                                progressDialog.dismiss();
                                Toast
                                        .makeText(MessageActivity.this,
                                                "Failed " + e.getMessage(),
                                                Toast.LENGTH_SHORT)
                                        .show();
                            }
                        })
                        .addOnProgressListener(
                                new OnProgressListener<UploadTask.TaskSnapshot>() {

                                    // Progress Listener for loading
                                    // percentage on the dialog box
                                    @Override
                                    public void onProgress(
                                            UploadTask.TaskSnapshot taskSnapshot)
                                    {
                                        double progress
                                                = (100.0
                                                * taskSnapshot.getBytesTransferred()
                                                / taskSnapshot.getTotalByteCount());
                                        progressDialog.setMessage(
                                                "Uploaded "
                                                        + (int)progress + "%");
                                    }
                                });
            }

            catch (Exception e) {
                // Log the exception
                e.printStackTrace();
            }
        }
    }
}