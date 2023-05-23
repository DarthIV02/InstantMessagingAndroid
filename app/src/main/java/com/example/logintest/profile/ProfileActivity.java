package com.example.logintest.profile;

import android.app.AlertDialog;
import android.app.ProgressDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.graphics.Bitmap;
import android.net.Uri;
import android.os.Bundle;
import android.provider.MediaStore;
import android.view.View;
import android.widget.ImageView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import com.bumptech.glide.load.resource.bitmap.CenterCrop;
import com.bumptech.glide.load.resource.bitmap.RoundedCorners;
import com.bumptech.glide.request.RequestOptions;
import com.example.logintest.data.model.GlideApp;
import com.example.logintest.databinding.ActivityProfileBinding;
import com.example.logintest.message.MessageActivity;
import com.example.logintest.ui.login.LoadingActivity;
import com.example.logintest.ui.login.LoginActivity;
import com.example.logintest.ui.login.RegisterActivity;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.OnProgressListener;
import com.google.firebase.storage.StorageReference;
import com.google.firebase.storage.UploadTask;

import java.io.IOException;
import java.util.UUID;

public class ProfileActivity extends AppCompatActivity {

    ActivityProfileBinding binding;
    ImageView userImageView;

    String current_id;

    private final int PICK_IMAGE_REQUEST = 22;

    private Uri filePath;

    StorageReference storageRef;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        binding = ActivityProfileBinding.inflate(getLayoutInflater());
        setContentView(binding.getRoot());

        userImageView = binding.userImage;

        FirebaseStorage storage = FirebaseStorage.getInstance();
        storageRef = storage.getReference();

        Intent intent = getIntent();
        current_id = intent.getStringExtra("USERID");

        StorageReference conic = storageRef.child("usersImages").child(current_id);

        GlideApp.with(this /* context */)
                .load(conic)
                .apply(new RequestOptions().transforms(new CenterCrop(), new RoundedCorners(70)))
                .into(userImageView);

        binding.deleteButton.setOnClickListener(new View.OnClickListener() {
            public void onClick(View v) {
                showDeleteConfirmationDialog();
            }


// Método para mostrar el diálogo de confirmación de eliminación
        private void showDeleteConfirmationDialog() {
            AlertDialog.Builder builder = new AlertDialog.Builder(ProfileActivity.this);
            builder.setTitle("Confirmar eliminación");
            builder.setMessage("¿Estás seguro de que deseas eliminar tu cuenta? Esta acción no se puede deshacer.");
            builder.setPositiveButton("Eliminar", new DialogInterface.OnClickListener() {
                @Override
                public void onClick(DialogInterface dialog, int which) {
                    // Usuario confirmó la eliminación, proceder con el borrado de la cuenta
                    deleteCurrentUser();
                }
            });
            builder.setNegativeButton("Cancelar", null);
            AlertDialog dialog = builder.create();
            dialog.show();
        }

        private void deleteCurrentUser() {
                FirebaseUser currentUser = FirebaseAuth.getInstance().getCurrentUser();
                if (currentUser != null) {
                    //DatabaseReference usersRef = FirebaseDatabase.getInstance().getReference().child("users");
                    //usersRef.child(currentUser.getUid()).removeValue();
                    //storageRef.child("usersImages").child(currentUser.getUid()).delete();
                    currentUser.delete()
                    .addOnCompleteListener(new OnCompleteListener<Void>() {
                                @Override
                                public void onComplete(@NonNull Task<Void> task) {
                                    if (task.isSuccessful()) {
                                        // El usuario se eliminó exitosamente
                                        Toast.makeText(ProfileActivity.this, "Usuario eliminado", Toast.LENGTH_SHORT).show();
                                        // Realiza cualquier acción adicional que necesites después de eliminar el usuario

                                        Intent intent = new Intent(ProfileActivity.this, LoginActivity.class);
                                        startActivity(intent);
                                    } else {
                                        // Ocurrió un error al intentar eliminar el usuario
                                        Toast.makeText(ProfileActivity.this, "Error al eliminar el usuario", Toast.LENGTH_SHORT).show();
                                    }
                                }
                            });
                } else {
                    // No se encontró un usuario autenticado actualmente
                    Toast.makeText(ProfileActivity.this, "No se encontró un usuario autenticado", Toast.LENGTH_SHORT).show();
                }
            }
        });

        binding.logOutButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(ProfileActivity.this, LoginActivity.class);
                startActivity(intent);
            }
        });
//        binding.changePasswordButton.setOnClickListener(new View.OnClickListener() {
//            @Override
//            public void onClick(View v) {
//                showChangePasswordDialog();
//            }
//
//        });
   }
}