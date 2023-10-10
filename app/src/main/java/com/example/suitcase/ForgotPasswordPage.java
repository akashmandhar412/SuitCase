package com.example.suitcase;

import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.Toast;

import com.example.suitcase.databinding.ActivityForgotPasswordPageBinding;

public class ForgotPasswordPage extends AppCompatActivity {
    ActivityForgotPasswordPageBinding binding;
    DatabaseHelper databaseHelper;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        binding=ActivityForgotPasswordPageBinding.inflate(getLayoutInflater());
        setContentView(binding.getRoot());

        databaseHelper=new DatabaseHelper(this);
        binding.getEmailBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                String email=binding.forgotEmail.getText().toString().trim();
                Boolean checkUsers=databaseHelper.checkEmail(email);
                if (checkUsers==true){
                    Intent intent=new Intent(getApplicationContext(),resetPassword.class);
                    intent.putExtra("email",email);
                    startActivity(intent);
                }else {
                    Toast.makeText(ForgotPasswordPage.this, "Invalid Email", Toast.LENGTH_SHORT).show();
                }
            }
        });

    }
}