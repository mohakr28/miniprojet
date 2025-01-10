package com.example.mini_projet;

import android.content.ContentResolver;
import android.content.Context;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.net.Uri;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.recyclerview.widget.GridLayoutManager;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import java.io.InputStream;
import java.util.List;

public class EmployeeAdapter extends RecyclerView.Adapter<EmployeeAdapter.EmployeeViewHolder> {

    private List<Employee> employeeList;
    private OnEmployeeDeleteListener deleteListener;
    private boolean isGridView; // New variable to check if GridView or ListView

    // Listener interface for delete action
    public interface OnEmployeeDeleteListener {
        void onDelete(Employee employee);
    }

    public EmployeeAdapter(List<Employee> employeeList, OnEmployeeDeleteListener deleteListener, boolean isGridView) {
        this.employeeList = employeeList;
        this.deleteListener = deleteListener;
        this.isGridView = isGridView; // Set the view type
    }

    // Method to update the employee list in the adapter
    public void updateEmployeeList(List<Employee> newEmployeeList) {
        this.employeeList = newEmployeeList;
        notifyDataSetChanged();
    }

    // Method to update view type (Grid or List)
    public void updateViewType(boolean isGridView) {
        this.isGridView = isGridView;
        notifyDataSetChanged();  // Refresh the RecyclerView with the new view type
    }

    @Override
    public EmployeeViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        // Select layout based on view type (Grid or List)
        int layoutRes = isGridView ? R.layout.item_employee_grid : R.layout.item_employee_list;
        View view = LayoutInflater.from(parent.getContext()).inflate(layoutRes, parent, false);
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
        loadImage(holder.itemView.getContext(), imageUri, holder.employeeImage);

        // Handle the layout differently based on the view type (Grid or List)
        if (isGridView) {
            holder.employeeImage.setVisibility(View.VISIBLE);
            holder.firstName.setVisibility(View.VISIBLE);
            holder.lastName.setVisibility(View.VISIBLE);
            holder.phone.setVisibility(View.VISIBLE);
            holder.email.setVisibility(View.VISIBLE);
            holder.contactPhoneButton.setVisibility(View.VISIBLE);
            holder.contactSmsButton.setVisibility(View.VISIBLE);
            holder.contactEmailButton.setVisibility(View.VISIBLE);
        } else {
            holder.employeeImage.setVisibility(View.VISIBLE);
            holder.firstName.setVisibility(View.VISIBLE);
            holder.lastName.setVisibility(View.VISIBLE);
            holder.phone.setVisibility(View.VISIBLE);
            holder.email.setVisibility(View.VISIBLE);
            holder.contactPhoneButton.setVisibility(View.VISIBLE);
            holder.contactSmsButton.setVisibility(View.VISIBLE);
            holder.contactEmailButton.setVisibility(View.VISIBLE);
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
        if (isNotEmpty(employee.getPhone())) {
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
        if (isNotEmpty(employee.getPhone())) {
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
        if (isNotEmpty(employee.getEmail())) {
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

    // Method to check if a string is not empty or null
    private boolean isNotEmpty(String str) {
        return str != null && !str.isEmpty();
    }

    public static class EmployeeViewHolder extends RecyclerView.ViewHolder {
        TextView firstName, lastName, phone, email;
        ImageView employeeImage;
        ImageButton deleteButton, contactPhoneButton, contactSmsButton, contactEmailButton;

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

    private static final String TAG = "EmployeeAdapter";

    // Optimized method for loading images
    public static void loadImage(Context context, String imageUri, ImageView imageView) {
        try {
            if (imageUri != null && !imageUri.isEmpty()) {
                Uri uri = Uri.parse(imageUri);
                ContentResolver resolver = context.getContentResolver();

                try (InputStream inputStream = resolver.openInputStream(uri)) {
                    if (inputStream != null) {
                        Bitmap bitmap = BitmapFactory.decodeStream(inputStream);
                        imageView.setImageBitmap(bitmap);
                    } else {
                        imageView.setImageResource(R.drawable.ic_person);
                    }
                }
            } else {
                imageView.setImageResource(R.drawable.ic_person); // Default image
            }
        } catch (Exception e) {
            Log.e(TAG, "Error loading image: " + e.getMessage(), e);
            imageView.setImageResource(R.drawable.ic_person); // Default image in case of error
        }
    }
}
