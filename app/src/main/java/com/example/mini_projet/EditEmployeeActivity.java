package com.example.mini_projet;

import android.Manifest;
import android.content.ContentResolver;
import android.content.Context;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.net.Uri;
import android.os.Bundle;
import android.util.Log;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;
import androidx.core.app.ActivityCompat;
import androidx.core.content.ContextCompat;

import com.bumptech.glide.Glide;

import java.io.IOException;
import java.io.InputStream;


public class EditEmployeeActivity extends AppCompatActivity {

    private EditText firstNameEditText, lastNameEditText, phoneEditText, emailEditText;
    private ImageView employeeImageView;
    private TextView employeeIdTextView;
    private Button saveButton;
    private EmployeeDatabaseHelper dbHelper; // Database helper for SQLite

    private static final int REQUEST_MEDIA_PERMISSION = 100;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_edit_employee);

        // Initialize views
        firstNameEditText = findViewById(R.id.firstNameEditText);
        lastNameEditText = findViewById(R.id.lastNameEditText);
        phoneEditText = findViewById(R.id.phoneEditText);
        emailEditText = findViewById(R.id.emailEditText);
        employeeIdTextView = findViewById(R.id.employeeIdTextView);
        saveButton = findViewById(R.id.saveButton);

        // Initialize database helper
        dbHelper = new EmployeeDatabaseHelper(this);

        // Get employee ID from Intent
        Intent intent = getIntent();
        int employeeId = intent.getIntExtra("employee_id", -1);

        if (employeeId != -1) {
            // Fetch employee data from database
            Employee employee = dbHelper.getEmployeeById(employeeId);

            if (employee != null) {
                // Set data to EditText fields
                firstNameEditText.setText(employee.getFirstName());
                lastNameEditText.setText(employee.getLastName());
                phoneEditText.setText(employee.getPhone());
                emailEditText.setText(employee.getEmail());


                // Set employee ID to TextView
                employeeIdTextView.setText(String.valueOf(employee.getId()));
            }
        }

        // Handle save button click
        saveButton.setOnClickListener(v -> {
            String firstName = firstNameEditText.getText().toString().trim();
            String lastName = lastNameEditText.getText().toString().trim();
            String phone = phoneEditText.getText().toString().trim();
            String email = emailEditText.getText().toString().trim();

            if (firstName.isEmpty() || lastName.isEmpty() || phone.isEmpty() || email.isEmpty()) {
                Toast.makeText(this, "Please fill in all fields", Toast.LENGTH_SHORT).show();
            } else {
                // Update employee data in database
                int id = Integer.parseInt(employeeIdTextView.getText().toString());
                boolean isUpdated = dbHelper.updateEmployee(id, firstName, lastName, phone, email);

                if (isUpdated) {
                    Toast.makeText(this, "Employee updated successfully", Toast.LENGTH_SHORT).show();
                    finish(); // Close activity and return to previous screen
                } else {
                    Toast.makeText(this, "Failed to update employee", Toast.LENGTH_SHORT).show();
                }
            }
        });

        // Check permissions for accessing external storage
        if (ContextCompat.checkSelfPermission(this, Manifest.permission.READ_EXTERNAL_STORAGE) != PackageManager.PERMISSION_GRANTED) {
            ActivityCompat.requestPermissions(this, new String[]{Manifest.permission.READ_EXTERNAL_STORAGE}, REQUEST_MEDIA_PERMISSION);
        }
    }


}
