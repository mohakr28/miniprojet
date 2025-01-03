package com.example.mini_projet;

import android.content.Intent;
import android.content.pm.PackageManager;
import android.os.Build;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;
import androidx.core.app.ActivityCompat;
import androidx.core.content.ContextCompat;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import java.util.List;

public class MainActivity extends AppCompatActivity {

    private static final int REQUEST_MEDIA_PERMISSION = 1; // Request code for media permission
    private RecyclerView recyclerView;
    private EmployeeAdapter adapter;
    private EmployeeDatabaseHelper dbHelper;
    private Button addEmployeeButton;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        // Initialize views
        recyclerView = findViewById(R.id.recyclerView);
        addEmployeeButton = findViewById(R.id.addEmployeeButton);

        // Initialize database helper
        dbHelper = new EmployeeDatabaseHelper(this);

        // Set up RecyclerView with a linear layout manager
        recyclerView.setLayoutManager(new LinearLayoutManager(this));

        // Add Employee button click listener
        addEmployeeButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                // Start AddEmployeeActivity when button is clicked
                Intent intent = new Intent(MainActivity.this, AddEmployeeActivity.class);
                startActivity(intent);
            }
        });

        // Check and request media permissions
        checkStoragePermissions();

        // Load employees from the database
        loadEmployees();
    }

    @Override
    protected void onResume() {
        super.onResume();
        // Refresh the employee list when coming back from AddEmployeeActivity
        loadEmployees();
    }

    // Function to check if media permissions are granted
    private void checkStoragePermissions() {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.TIRAMISU) {
            // For Android 13 and above, request READ_MEDIA_IMAGES
            if (ContextCompat.checkSelfPermission(this, android.Manifest.permission.READ_MEDIA_IMAGES) != PackageManager.PERMISSION_GRANTED) {
                ActivityCompat.requestPermissions(this, new String[]{
                        android.Manifest.permission.READ_MEDIA_IMAGES}, REQUEST_MEDIA_PERMISSION);
            }
        } else {
            // For older versions, request READ_EXTERNAL_STORAGE
            if (ContextCompat.checkSelfPermission(this, android.Manifest.permission.READ_EXTERNAL_STORAGE) != PackageManager.PERMISSION_GRANTED) {
                ActivityCompat.requestPermissions(this, new String[]{
                        android.Manifest.permission.READ_EXTERNAL_STORAGE}, REQUEST_MEDIA_PERMISSION);
            }
        }
    }

    // Function to handle the result of permission requests
    @Override
    public void onRequestPermissionsResult(int requestCode, String[] permissions, int[] grantResults) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults);

        // Check if the requested permission was for media access
        if (requestCode == REQUEST_MEDIA_PERMISSION) {
            if (grantResults.length > 0 && grantResults[0] == PackageManager.PERMISSION_GRANTED) {
                // Permission granted, continue with file operations
                Toast.makeText(this, "Media access permission granted", Toast.LENGTH_SHORT).show();
            } else {
                // Permission denied, handle accordingly
                Toast.makeText(this, "Media access permission denied", Toast.LENGTH_SHORT).show();
            }
        }
    }

    // Function to load employees from the database and update the RecyclerView
    private void loadEmployees() {
        List<Employee> employees = dbHelper.getAllEmployees();

        // If the adapter is not yet initialized, initialize it
        if (adapter == null) {
            adapter = new EmployeeAdapter(employees);
            recyclerView.setAdapter(adapter);
        } else {
            // If the adapter is already initialized, just update the list
            adapter.updateEmployeeList(employees);
        }
    }
}
