package com.example.mini_projet;

import android.content.Intent;
import android.content.SharedPreferences;
import android.content.pm.PackageManager;
import android.os.Build;
import android.os.Bundle;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Spinner;
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

    private static final int REQUEST_MEDIA_PERMISSION = 1;
    private RecyclerView recyclerView;
    private EmployeeAdapter adapter;
    private EmployeeDatabaseHelper dbHelper;
    private Button addEmployeeButton;
    private EditText searchEditText;
    private Spinner viewModeSpinner;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        // Initialize views
        recyclerView = findViewById(R.id.recyclerView);
        addEmployeeButton = findViewById(R.id.addEmployeeButton);
        searchEditText = findViewById(R.id.searchEditText);
        viewModeSpinner = findViewById(R.id.viewModeSpinner);

        // Initialize database helper
        dbHelper = new EmployeeDatabaseHelper(this);

        // Load employees from the database
        loadEmployees();

        // Add Employee button click listener
        addEmployeeButton.setOnClickListener(v -> {
            Intent intent = new Intent(MainActivity.this, AddEmployeeActivity.class);
            startActivity(intent);
        });

        // Setup Spinner for view mode selection
        setupViewModeSpinner();

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
    }

    @Override
    protected void onResume() {
        super.onResume();
        // Refresh the employee list when returning to the activity
        loadEmployees();
    }

    private void setupViewModeSpinner() {
        // Array of options for view modes
        String[] viewModes = {"List View", "Grid View"};

        // Create an ArrayAdapter for the Spinner
        ArrayAdapter<String> adapter = new ArrayAdapter<>(this, android.R.layout.simple_spinner_item, viewModes);
        adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        viewModeSpinner.setAdapter(adapter);

        // Retrieve the saved view mode from SharedPreferences
        SharedPreferences preferences = getSharedPreferences("ViewModePrefs", MODE_PRIVATE);
        String savedViewMode = preferences.getString("view_mode", "list");

        // Set the default layout manager and spinner selection based on saved mode
        if ("list".equals(savedViewMode)) {
            recyclerView.setLayoutManager(new LinearLayoutManager(this));
            viewModeSpinner.setSelection(0);
        } else if ("grid".equals(savedViewMode)) {
            recyclerView.setLayoutManager(new GridLayoutManager(this, 2));
            viewModeSpinner.setSelection(1);
        }

        // Set a listener for Spinner item selection
        viewModeSpinner.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
                SharedPreferences.Editor editor = preferences.edit();

                if (position == 0) {
                    recyclerView.setLayoutManager(new LinearLayoutManager(MainActivity.this));
                    editor.putString("view_mode", "list");
                } else if (position == 1) {
                    recyclerView.setLayoutManager(new GridLayoutManager(MainActivity.this, 2));
                    editor.putString("view_mode", "grid");
                }

                editor.apply(); // Save the selected mode

                // Update the adapter to reflect the new view mode
                updateAdapterViewType(position == 1); // Pass true for grid view, false for list view
            }

            @Override
            public void onNothingSelected(AdapterView<?> parent) {
                // Default to List View if nothing is selected
                recyclerView.setLayoutManager(new LinearLayoutManager(MainActivity.this));
            }
        });
    }

    // Function to load employees from the database and update the RecyclerView
    private void loadEmployees() {
        List<Employee> employees = dbHelper.getAllEmployees();
        SharedPreferences preferences = getSharedPreferences("ViewModePrefs", MODE_PRIVATE);
        String savedViewMode = preferences.getString("view_mode", "list");
        boolean isGridView = "grid".equals(savedViewMode);

        if (adapter == null) {
            adapter = new EmployeeAdapter(employees, employee -> {
                dbHelper.deleteEmployee(employee.getId());
                Toast.makeText(MainActivity.this, "Employee deleted", Toast.LENGTH_SHORT).show();
                loadEmployees(); // Refresh the list after deletion
            }, isGridView);
            recyclerView.setAdapter(adapter);
        } else {
            adapter.updateEmployeeList(employees);
        }
    }

    // Function to update the adapter view type (list or grid)
    private void updateAdapterViewType(boolean isGridView) {
        if (adapter != null) {
            adapter.updateViewType(isGridView); // Update the adapter to switch between list/grid
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
        adapter.updateEmployeeList(filteredList);
    }
}
