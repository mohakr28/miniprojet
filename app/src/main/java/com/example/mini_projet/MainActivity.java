package com.example.mini_projet;

import android.content.Intent;
import android.content.pm.PackageManager;
import android.os.Build;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.RadioButton;
import android.widget.RadioGroup;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;
import androidx.core.app.ActivityCompat;
import androidx.core.content.ContextCompat;
import androidx.recyclerview.widget.GridLayoutManager;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import java.util.ArrayList;
import java.util.List;

public class MainActivity extends AppCompatActivity {

    private static final int REQUEST_MEDIA_PERMISSION = 1; // Request code for media permission
    private RecyclerView recyclerView;
    private EmployeeAdapter adapter;
    private EmployeeDatabaseHelper dbHelper;
    private Button addEmployeeButton;
    private EditText searchEditText;
    private RadioGroup viewModeRadioGroup;
    private RadioButton radioListView;
    private RadioButton radioGridView;
    private static final int REQUEST_CALL_PERMISSION = 1;
    private static final int REQUEST_SMS_PERMISSION = 2;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        // Initialize views
        recyclerView = findViewById(R.id.recyclerView);
        addEmployeeButton = findViewById(R.id.addEmployeeButton);
        searchEditText = findViewById(R.id.searchEditText);
        viewModeRadioGroup = findViewById(R.id.viewModeRadioGroup);
        radioListView = findViewById(R.id.radioListView);
        radioGridView = findViewById(R.id.radioGridView);

        // Initialize database helper
        dbHelper = new EmployeeDatabaseHelper(this);

        // Set default layout manager (ListView mode)
        recyclerView.setLayoutManager(new LinearLayoutManager(this));

        // Add Employee button click listener
        addEmployeeButton.setOnClickListener(v -> {
            // Start AddEmployeeActivity when button is clicked
            Intent intent = new Intent(MainActivity.this, AddEmployeeActivity.class);
            startActivity(intent);
        });

        // Check and request media permissions
        checkStoragePermissions();

        // Load employees from the database
        loadEmployees();

        // Search functionality
        searchEditText.addTextChangedListener(new android.text.TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence charSequence, int start, int count, int after) {}

            @Override
            public void onTextChanged(CharSequence charSequence, int start, int before, int count) {
                // Filter the employee list based on search input
                filterEmployees(charSequence.toString());
            }

            @Override
            public void afterTextChanged(android.text.Editable editable) {}
        });

        // Set up the RadioGroup listener to switch between ListView and GridView
        viewModeRadioGroup.setOnCheckedChangeListener((group, checkedId) -> {
            if (checkedId == R.id.radioListView) {
                // Set RecyclerView to ListView layout
                recyclerView.setLayoutManager(new LinearLayoutManager(this));
            } else if (checkedId == R.id.radioGridView) {
                // Set RecyclerView to GridView layout (2 columns in this example)
                recyclerView.setLayoutManager(new GridLayoutManager(this, 2));
            }
        });
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
                ActivityCompat.requestPermissions(this, new String[]{ android.Manifest.permission.READ_MEDIA_IMAGES }, REQUEST_MEDIA_PERMISSION);
            }
        } else {
            // For older versions, request READ_EXTERNAL_STORAGE
            if (ContextCompat.checkSelfPermission(this, android.Manifest.permission.READ_EXTERNAL_STORAGE) != PackageManager.PERMISSION_GRANTED) {
                ActivityCompat.requestPermissions(this, new String[]{ android.Manifest.permission.READ_EXTERNAL_STORAGE }, REQUEST_MEDIA_PERMISSION);
            }
        }
        // Check and request call permission
        if (ContextCompat.checkSelfPermission(this, android.Manifest.permission.CALL_PHONE) != PackageManager.PERMISSION_GRANTED) {
            ActivityCompat.requestPermissions(this, new String[]{android.Manifest.permission.CALL_PHONE}, REQUEST_CALL_PERMISSION);
        }

        // Check and request SMS permission
        if (ContextCompat.checkSelfPermission(this, android.Manifest.permission.SEND_SMS) != PackageManager.PERMISSION_GRANTED) {
            ActivityCompat.requestPermissions(this, new String[]{android.Manifest.permission.SEND_SMS}, REQUEST_SMS_PERMISSION);
        }
    }

    // Function to handle the result of permission requests
    @Override
    public void onRequestPermissionsResult(int requestCode, String[] permissions, int[] grantResults) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults);

        if (requestCode == REQUEST_MEDIA_PERMISSION) {
            if (grantResults.length > 0 && grantResults[0] == PackageManager.PERMISSION_GRANTED) {
                // Permission granted, continue with file operations
                Toast.makeText(this, "Media access permission granted", Toast.LENGTH_SHORT).show();
            } else {
                // Permission denied, handle accordingly
                Toast.makeText(this, "Media access permission denied", Toast.LENGTH_SHORT).show();
            }
        }
        if(requestCode== REQUEST_CALL_PERMISSION) {
            if (grantResults.length > 0 && grantResults[0] == PackageManager.PERMISSION_GRANTED) {
                // إذن الاتصال الهاتفي تم منحه
                Toast.makeText(this, "Call permission granted", Toast.LENGTH_SHORT).show();
            } else {
                // إذن الاتصال الهاتفي تم رفضه
                Toast.makeText(this, "Call permission denied", Toast.LENGTH_SHORT).show();
            }
        }
        if(requestCode== REQUEST_SMS_PERMISSION) {
            if (grantResults.length > 0 && grantResults[0] == PackageManager.PERMISSION_GRANTED) {
                // إذن الرسائل النصية تم منحه
                Toast.makeText(this, "SMS permission granted", Toast.LENGTH_SHORT).show();
            } else {
                // إذن الرسائل النصية تم رفضه
                Toast.makeText(this, "SMS permission denied", Toast.LENGTH_SHORT).show();
            }
        }
    }

    // Function to load employees from the database and update the RecyclerView
    private void loadEmployees() {
        List<Employee> employees = dbHelper.getAllEmployees();

        // If the adapter is not yet initialized, initialize it
        if (adapter == null) {
            adapter = new EmployeeAdapter(employees, new EmployeeAdapter.OnEmployeeDeleteListener() {
                @Override
                public void onDelete(Employee employee) {
                    // Delete the employee from the database
                    dbHelper.deleteEmployee(employee.getId());
                    Toast.makeText(MainActivity.this, "Employee deleted", Toast.LENGTH_SHORT).show();
                    loadEmployees(); // Refresh the list after deletion
                }
            });
            recyclerView.setAdapter(adapter);
        } else {
            // If the adapter is already initialized, just update the list
            adapter.updateEmployeeList(employees);
        }
    }

    // Function to filter employees based on the search query
    private void filterEmployees(String query) {
        List<Employee> filteredList = new ArrayList<>();
        for (Employee employee : dbHelper.getAllEmployees()) {
            if (employee.getFirstName().toLowerCase().contains(query.toLowerCase()) ||
                    employee.getLastName().toLowerCase().contains(query.toLowerCase())) {
                filteredList.add(employee);
            }
        }

        // Update the adapter with the filtered list
        adapter.updateEmployeeList(filteredList);
    }
}
