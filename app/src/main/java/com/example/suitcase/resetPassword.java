package com.example.suitcase;

import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.Toast;
import com.example.suitcase.databinding.ActivityResetPasswordBinding;

public class resetPassword extends AppCompatActivity {
    ActivityResetPasswordBinding binding;

    DatabaseHelper databaseHelper;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        binding=ActivityResetPasswordBinding.inflate(getLayoutInflater());
        setContentView(binding.getRoot());

        databaseHelper = new DatabaseHelper(this);

        binding.resetBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                // Validate email address
                String email = binding.email.getText().toString().trim();
                if (databaseHelper.checkEmail(email)) {
                    // Email is valid, show the new password fields
                    binding.rePassword.setVisibility(View.VISIBLE);
                    binding.ConfirmRePassword.setVisibility(View.VISIBLE);

                    // Handle the logic for updating the password in the database
                    binding.resetBtn.setOnClickListener(new View.OnClickListener() {
                        @Override
                        public void onClick(View v) {
                            String newPassword = binding.rePassword.getText().toString().trim();
                            String confirmPassword = binding.ConfirmRePassword.getText().toString().trim();

                            if (newPassword.isEmpty() || confirmPassword.isEmpty()) {
                                // Check if newPassword or confirmPassword is empty
                                Toast.makeText(resetPassword.this, "Please enter both password fields", Toast.LENGTH_SHORT).show();
                            } else if (newPassword.equals(confirmPassword)) {
                                // Passwords match, update the password in the database
                                if (databaseHelper.updatePassword(email, newPassword)) {
                                    // Password updated successfully
                                    Intent intent = new Intent(getApplicationContext(), Login_Page.class);
                                    startActivity(intent);
                                    Toast.makeText(resetPassword.this, "Password reset successfully!", Toast.LENGTH_SHORT).show();
                                } else {
                                    // Password update failed
                                    Toast.makeText(resetPassword.this, "Password reset failed!", Toast.LENGTH_SHORT).show();
                                }
                            } else {
                                // Passwords do not match, show an error message
                                Toast.makeText(resetPassword.this, "confirm passwords do not match!", Toast.LENGTH_SHORT).show();
                            }
                        }
                    });
                } else {
                    Toast.makeText(resetPassword.this, "Invalid Email Address !", Toast.LENGTH_SHORT).show();
                }
            }
        });
    }
}