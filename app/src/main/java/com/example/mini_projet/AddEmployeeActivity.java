package com.example.mini_projet;

import android.annotation.SuppressLint;
import android.content.Intent;
import android.graphics.Bitmap;
import android.net.Uri;
import android.os.Bundle;
import android.provider.MediaStore;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.Toast;
import androidx.appcompat.app.AppCompatActivity;
public class AddEmployeeActivity extends AppCompatActivity {

    private static final int PICK_IMAGE = 100; // رقم التعريف لاختيار الصورة
    private EditText firstNameEditText, lastNameEditText, phoneEditText, emailEditText;
    private ImageView employeeImageView;
    private Button saveButton, chooseImageButton;

    private EmployeeDatabaseHelper dbHelper;
    private Uri imageUri;  // لتخزين رابط الصورة التي تم اختيارها

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_add_employee);

        dbHelper = new EmployeeDatabaseHelper(this);

        firstNameEditText = findViewById(R.id.firstNameEditText);
        lastNameEditText = findViewById(R.id.lastNameEditText);
        phoneEditText = findViewById(R.id.phoneEditText);
        emailEditText = findViewById(R.id.emailEditText);
        employeeImageView = findViewById(R.id.employeeImageView);
        saveButton = findViewById(R.id.saveButton);
        chooseImageButton = findViewById(R.id.chooseImageButton);

        // زر لاختيار الصورة
        chooseImageButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                // افتح المعرض لاختيار صورة
                Intent intent = new Intent(Intent.ACTION_PICK, MediaStore.Images.Media.EXTERNAL_CONTENT_URI);
                startActivityForResult(intent, PICK_IMAGE);
            }
        });

        saveButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                String firstName = firstNameEditText.getText().toString();
                String lastName = lastNameEditText.getText().toString();
                String phone = phoneEditText.getText().toString();
                String email = emailEditText.getText().toString();

                if (firstName.isEmpty() || lastName.isEmpty() || phone.isEmpty() || email.isEmpty()) {
                    Toast.makeText(AddEmployeeActivity.this, "All fields are required", Toast.LENGTH_SHORT).show();
                    return;
                }

                if (imageUri == null) {
                    Toast.makeText(AddEmployeeActivity.this, "Please choose an image", Toast.LENGTH_SHORT).show();
                    return;
                }

                // إضافة الموظف مع الصورة
                Employee newEmployee = new Employee(0, firstName, lastName, phone, email, imageUri.toString());
                dbHelper.addEmployee(newEmployee);

                Toast.makeText(AddEmployeeActivity.this, "Employee added successfully", Toast.LENGTH_SHORT).show();
                finish(); // العودة إلى النشاط الرئيسي
            }
        });
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (requestCode == PICK_IMAGE && resultCode == RESULT_OK && data != null) {
            imageUri = data.getData();
            employeeImageView.setImageURI(imageUri);  // عرض الصورة المختارة في ImageView
        }
    }
}
