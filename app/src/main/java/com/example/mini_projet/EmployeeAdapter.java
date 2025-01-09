package com.example.mini_projet;

import android.content.Intent;
import android.net.Uri;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.recyclerview.widget.RecyclerView;

import java.util.List;

public class EmployeeAdapter extends RecyclerView.Adapter<EmployeeAdapter.EmployeeViewHolder> {

    private List<Employee> employeeList;
    private OnEmployeeDeleteListener deleteListener;

    // Listener interface for delete action
    public interface OnEmployeeDeleteListener {
        void onDelete(Employee employee);
    }

    public EmployeeAdapter(List<Employee> employeeList, OnEmployeeDeleteListener deleteListener) {
        this.employeeList = employeeList;
        this.deleteListener = deleteListener;
    }

    @Override
    public EmployeeViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.item_employee, parent, false);
        return new EmployeeViewHolder(view);
    }

    @Override
    public void onBindViewHolder(EmployeeViewHolder holder, int position) {
        Employee employee = employeeList.get(position);

        // Set employee details in the UI
        holder.firstName.setText(employee.getFirstName());
        holder.lastName.setText(employee.getLastName());
        holder.phone.setText(employee.getPhone());
        holder.email.setText(employee.getEmail());

        // Load image if URI exists
        String imageUri = employee.getImageUri();
        if (imageUri != null && !imageUri.isEmpty()) {
            Uri uri = Uri.parse(imageUri);
            holder.employeeImage.setImageURI(uri);
        }

        // Edit functionality on item click
        holder.itemView.setOnClickListener(v -> {
            Intent intent = new Intent(holder.itemView.getContext(), EditEmployeeActivity.class);
            intent.putExtra("employee_id", employee.getId());
            holder.itemView.getContext().startActivity(intent);
        });

        // Delete functionality on delete button click
        holder.deleteButton.setOnClickListener(v -> {
            if (deleteListener != null) {
                deleteListener.onDelete(employee);
            }
        });

        // Call functionality on phone button click
        if (employee.getPhone() != null && !employee.getPhone().isEmpty()) {
            holder.contactPhoneButton.setVisibility(View.VISIBLE);
            holder.contactPhoneButton.setOnClickListener(v -> {
                Intent callIntent = new Intent(Intent.ACTION_DIAL);
                callIntent.setData(Uri.parse("tel:" + employee.getPhone()));
                holder.itemView.getContext().startActivity(callIntent);
            });
        } else {
            holder.contactPhoneButton.setVisibility(View.GONE);
        }

        // SMS functionality on SMS button click
        if (employee.getPhone() != null && !employee.getPhone().isEmpty()) {
            holder.contactSmsButton.setVisibility(View.VISIBLE);
            holder.contactSmsButton.setOnClickListener(v -> {
                Intent smsIntent = new Intent(Intent.ACTION_VIEW);
                smsIntent.setData(Uri.parse("smsto:" + employee.getPhone()));
                holder.itemView.getContext().startActivity(smsIntent);
            });
        } else {
            holder.contactSmsButton.setVisibility(View.GONE);
        }

        // Email functionality on email button click
        if (employee.getEmail() != null && !employee.getEmail().isEmpty()) {
            holder.contactEmailButton.setVisibility(View.VISIBLE);
            holder.contactEmailButton.setOnClickListener(v -> {
                Intent emailIntent = new Intent(Intent.ACTION_SENDTO);
                emailIntent.setData(Uri.parse("mailto:" + employee.getEmail()));
                holder.itemView.getContext().startActivity(emailIntent);
            });
        } else {
            holder.contactEmailButton.setVisibility(View.GONE);
        }
    }

    @Override
    public int getItemCount() {
        return employeeList.size();
    }

    public static class EmployeeViewHolder extends RecyclerView.ViewHolder {
        TextView firstName, lastName, phone, email;
        ImageView employeeImage;
        Button deleteButton, contactPhoneButton, contactSmsButton, contactEmailButton;

        public EmployeeViewHolder(View itemView) {
            super(itemView);
            firstName = itemView.findViewById(R.id.firstName);
            lastName = itemView.findViewById(R.id.lastName);
            phone = itemView.findViewById(R.id.phone);
            email = itemView.findViewById(R.id.email);
            employeeImage = itemView.findViewById(R.id.employeeImage);
            deleteButton = itemView.findViewById(R.id.deleteButton);
            contactPhoneButton = itemView.findViewById(R.id.contactPhoneButton);
            contactSmsButton = itemView.findViewById(R.id.contactSmsButton);
            contactEmailButton = itemView.findViewById(R.id.contactEmailButton);
        }
    }

    // Method to update the employee list in the adapter
    public void updateEmployeeList(List<Employee> newEmployeeList) {
        this.employeeList = newEmployeeList;
        notifyDataSetChanged();
    }
}
