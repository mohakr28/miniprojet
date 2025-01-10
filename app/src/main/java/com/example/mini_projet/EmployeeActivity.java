package com.example.mini_projet;

import android.os.Bundle;
import android.view.View;
import android.widget.Button;

import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.GridLayoutManager;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import java.util.ArrayList;
import java.util.List;

public class EmployeeActivity extends AppCompatActivity {

    private RecyclerView recyclerView;
    private EmployeeAdapter employeeAdapter;
    private Button toggleViewButton;
    private boolean isGridView = false;
    private List<Employee> employeeList;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_employee);

        recyclerView = findViewById(R.id.recyclerView);
        toggleViewButton = findViewById(R.id.toggleViewButton);

        // Sample employee data
        employeeList = new ArrayList<>();
        employeeList.add(new Employee("John", "Doe", "555-1234", "john.doe@example.com", ""));
        employeeList.add(new Employee("Jane", "Doe", "555-5678", "jane.doe@example.com", ""));

        employeeAdapter = new EmployeeAdapter(employeeList, new EmployeeAdapter.OnEmployeeDeleteListener() {
            @Override
            public void onDelete(Employee employee) {
                // Handle delete
            }
        }, isGridView);

        recyclerView.setAdapter(employeeAdapter);

        // Set default layout manager (grid view or list view)
        setRecyclerViewLayoutManager();

        toggleViewButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                isGridView = !isGridView;
                setRecyclerViewLayoutManager();
                employeeAdapter.updateViewType(isGridView);
            }
        });
    }

    private void setRecyclerViewLayoutManager() {
        if (isGridView) {
            recyclerView.setLayoutManager(new GridLayoutManager(this, 2));
        } else {
            recyclerView.setLayoutManager(new LinearLayoutManager(this));
        }
    }
}
