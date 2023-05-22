package com.example.logintest.ui.login;

import android.app.Activity;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.text.Editable;
import android.text.TextWatcher;
import android.util.Log;
import android.view.KeyEvent;
import android.view.View;
import android.view.inputmethod.EditorInfo;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.ProgressBar;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.annotation.StringRes;
import androidx.appcompat.app.AppCompatActivity;
import androidx.lifecycle.Observer;
import androidx.lifecycle.ViewModelProvider;

import com.example.logintest.R;
import com.example.logintest.data.model.User;
import com.example.logintest.databinding.ActivityLoginBinding;
import com.example.logintest.message.MessageActivity;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.AuthCredential;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.EmailAuthProvider;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseAuthUserCollisionException;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.auth.UserProfileChangeRequest;
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

public class LoginActivity extends AppCompatActivity {

    private LoginViewModel loginViewModel;
    private ActivityLoginBinding binding;

    final public String TAG = "IVANNIA DEBUGGING";

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        binding = ActivityLoginBinding.inflate(getLayoutInflater());
        setContentView(binding.getRoot());

        loginViewModel = new ViewModelProvider(this, new LoginViewModelFactory())
                .get(LoginViewModel.class);

        final EditText usernameEditText = binding.username;
        final EditText passwordEditText = binding.password;
        final Button loginButton = binding.login;
        final ProgressBar loadingProgressBar = binding.loading;
        final TextView registerEditTeXT = binding.registerText;
        TextView registerText = findViewById(R.id.register_text);


        loginViewModel.getLoginFormState().observe(this, new Observer<LoginFormState>() {
            @Override
            public void onChanged(@Nullable LoginFormState loginFormState) {
                if (loginFormState == null) {
                    return;
                }
                loginButton.setEnabled(loginFormState.isDataValid());
                if (loginFormState.getUsernameError() != null) {
                    usernameEditText.setError(getString(loginFormState.getUsernameError()));
                }
                if (loginFormState.getPasswordError() != null) {
                    passwordEditText.setError(getString(loginFormState.getPasswordError()));
                }
            }
        });

        loginViewModel.getLoginResult().observe(this, new Observer<LoginResult>() {
            @Override
            public void onChanged(@Nullable LoginResult loginResult) {
                if (loginResult == null) {
                    return;
                }
                loadingProgressBar.setVisibility(View.GONE);
                if (loginResult.getError() != null) {
                    showLoginFailed(loginResult.getError());
                }
                if (loginResult.getSuccess() != null) {
                    updateUiWithUser(loginResult.getSuccess());
                }
                setResult(Activity.RESULT_OK);

                //Complete and destroy login activity once successful
                finish();
            }
        });

        TextWatcher afterTextChangedListener = new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {
                // ignore
            }

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {
                // ignore
            }

            @Override
            public void afterTextChanged(Editable s) {
                loginViewModel.loginDataChanged(usernameEditText.getText().toString(),
                        passwordEditText.getText().toString());
            }
        };
        usernameEditText.addTextChangedListener(afterTextChangedListener);
        passwordEditText.addTextChangedListener(afterTextChangedListener);
        passwordEditText.setOnEditorActionListener(new TextView.OnEditorActionListener() {

            @Override
            public boolean onEditorAction(TextView v, int actionId, KeyEvent event) {
                if (actionId == EditorInfo.IME_ACTION_DONE) {
                    loginViewModel.login(usernameEditText.getText().toString(),
                            passwordEditText.getText().toString());
                }
                return false;
            }
        });

        loginButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                String loginInput = usernameEditText.getText().toString();
                String password = passwordEditText.getText().toString();

                if (loginInput.isEmpty() || password.isEmpty()) {
                    Toast.makeText(LoginActivity.this, "Please fill all the fields", Toast.LENGTH_SHORT).show();
                    return;
                }

                // Declare the credential variable as final
                final AuthCredential[] credential = new AuthCredential[1];

                // Check if the login input is an email or username
                if (loginInput.contains("@")) {
                    // Login input is an email
                    credential[0] = EmailAuthProvider.getCredential(loginInput, password);
                    signInWithCredential(credential[0]);
                } else {
                    // Login input is a username
                    // First, retrieve the user's email based on the provided username from the "users" node in the database
                    DatabaseReference usersRef = FirebaseDatabase.getInstance().getReference().child("users");
                    Query usernameQuery = usersRef.orderByChild("username").equalTo(loginInput);
                    usernameQuery.addListenerForSingleValueEvent(new ValueEventListener() {
                        @Override
                        public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                            if (dataSnapshot.exists()) {
                                String email = null;
                                for (DataSnapshot snapshot : dataSnapshot.getChildren()) {
                                    email = snapshot.child("email").getValue(String.class);
                                    String finalUid = dataSnapshot.getKey();
                                    break; // Only retrieve the first email
                                }
                                if (email != null) {
                                    credential[0] = EmailAuthProvider.getCredential(email, password);
                                    signInWithCredential(credential[0]);
                                } else {
                                    Toast.makeText(LoginActivity.this, "Invalid username", Toast.LENGTH_SHORT).show();
                                }
                            } else {
                                Toast.makeText(LoginActivity.this, "Invalid username", Toast.LENGTH_SHORT).show();
                            }
                        }

                        @Override
                        public void onCancelled(@NonNull DatabaseError databaseError) {
                            // Handle any errors that occur during the database query
                            Toast.makeText(LoginActivity.this, "Error retrieving user information", Toast.LENGTH_SHORT).show();
                        }
                    });
                }
            }
        });


        registerText.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(LoginActivity.this, RegisterActivity.class);
                startActivity(intent);
            }
        });
    }

    private void signInWithCredential(AuthCredential credential) {
        FirebaseAuth.getInstance().signInWithCredential(credential)
                .addOnCompleteListener(this, new OnCompleteListener<AuthResult>() {
                    @Override
                    public void onComplete(@NonNull Task<AuthResult> task) {
                        if (task.isSuccessful()) {
                            // User authentication is successful
                            Toast.makeText(LoginActivity.this, "Login successful", Toast.LENGTH_SHORT).show();

                            FirebaseUser user = FirebaseAuth.getInstance().getCurrentUser();
                            if (user != null) {
                                String uid = user.getUid();
                                String email = user.getEmail();

                                // Perform additional query to get the username based on the email
                                DatabaseReference usersRef = FirebaseDatabase.getInstance().getReference().child("users");
                                Query emailQuery = usersRef.orderByChild("email").equalTo(email);
                                emailQuery.addListenerForSingleValueEvent(new ValueEventListener() {
                                    @Override
                                    public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                                        if (dataSnapshot.exists()) {
                                            String username = null;
                                            for (DataSnapshot snapshot : dataSnapshot.getChildren()) {
                                                username = snapshot.child("username").getValue(String.class);
                                                break; // Only retrieve the first username
                                            }
                                            if (username != null) {
                                                // Create an Intent and pass the username and uid as extras
                                                Intent intent = new Intent(LoginActivity.this, MessageActivity.class);
                                                intent.putExtra("username", username);
                                                intent.putExtra("uid", uid);
                                                startActivity(intent);
                                                finish();
                                            } else {
                                                Toast.makeText(LoginActivity.this, "Failed to retrieve username", Toast.LENGTH_SHORT).show();
                                            }
                                        } else {
                                            Toast.makeText(LoginActivity.this, "Failed to retrieve username", Toast.LENGTH_SHORT).show();
                                        }
                                    }

                                    @Override
                                    public void onCancelled(@NonNull DatabaseError databaseError) {
                                        // Handle any errors that occur during the database query
                                        Toast.makeText(LoginActivity.this, "Error retrieving username", Toast.LENGTH_SHORT).show();
                                    }
                                });
                            }
                        } else {
                            // User authentication failed
                            Toast.makeText(LoginActivity.this, "Invalid email/username or password", Toast.LENGTH_SHORT).show();
                        }
                    }
                });
    }


    private void updateUiWithUser(LoggedInUserView model) {
        String welcome = getString(R.string.welcome) + model.getDisplayName();
        // TODO : initiate successful logged in experience
        Toast.makeText(getApplicationContext(), welcome, Toast.LENGTH_LONG).show();
    }

    private void showLoginFailed(@StringRes Integer errorString) {
        Toast.makeText(getApplicationContext(), errorString, Toast.LENGTH_SHORT).show();
    }

    @Override
    protected void onPause() {
        super.onPause();
        // Guardar el nombre o identificador de la actividad actual en las preferencias compartidas o en una variable global
        SharedPreferences preferences = getSharedPreferences("AppPreferences", MODE_PRIVATE);
        SharedPreferences.Editor editor = preferences.edit();
        editor.putString("LastActivity", getClass().getName());
        editor.apply();
    }

    @Override
    protected void onResume() {
        super.onResume();
        // Verificar si la aplicación estaba en la misma actividad antes de pasar al segundo plano
        SharedPreferences preferences = getSharedPreferences("AppPreferences", MODE_PRIVATE);
        String lastActivity = preferences.getString("LastActivity", "");

        if (!lastActivity.isEmpty() && lastActivity.equals(getClass().getName())) {
            // La aplicación estaba en la misma actividad, no es necesario hacer nada
        } else {
            // La aplicación estaba en una actividad diferente, realizar redirección o acción necesaria
            // Ejemplo: startActivity(new Intent(this, MainActivity.class));
        }
    }


}