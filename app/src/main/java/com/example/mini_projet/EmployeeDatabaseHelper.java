package com.example.mini_projet;

import android.annotation.SuppressLint;
import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;

import java.util.ArrayList;
import java.util.List;

public class EmployeeDatabaseHelper extends SQLiteOpenHelper {

    private static final String DATABASE_NAME = "employee_db";
    private static final int DATABASE_VERSION = 1;
    private static final String TABLE_EMPLOYEES = "employees";
    private static final String COLUMN_ID = "id";
    private static final String COLUMN_FIRST_NAME = "first_name";
    private static final String COLUMN_LAST_NAME = "last_name";
    private static final String COLUMN_PHONE = "phone";
    private static final String COLUMN_EMAIL = "email";
    private static final String COLUMN_IMAGE_RES_ID = "image_res_id";

    public EmployeeDatabaseHelper(Context context) {
        super(context, DATABASE_NAME, null, DATABASE_VERSION);
    }

    @Override
    public void onCreate(SQLiteDatabase db) {
        String CREATE_TABLE = "CREATE TABLE " + TABLE_EMPLOYEES + "("
                + COLUMN_ID + " INTEGER PRIMARY KEY AUTOINCREMENT, "
                + COLUMN_FIRST_NAME + " TEXT, "
                + COLUMN_LAST_NAME + " TEXT, "
                + COLUMN_PHONE + " TEXT, "
                + COLUMN_EMAIL + " TEXT, "
                + COLUMN_IMAGE_RES_ID + " TEXT)";
        db.execSQL(CREATE_TABLE);
    }

    @Override
    public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {
        db.execSQL("DROP TABLE IF EXISTS " + TABLE_EMPLOYEES);
        onCreate(db);
    }

    // Add employee to database
    public void addEmployee(Employee employee) {
        SQLiteDatabase db = this.getWritableDatabase();
        ContentValues values = new ContentValues();
        values.put(COLUMN_FIRST_NAME, employee.getFirstName());
        values.put(COLUMN_LAST_NAME, employee.getLastName());
        values.put(COLUMN_PHONE, employee.getPhone());
        values.put(COLUMN_EMAIL, employee.getEmail());
        values.put(COLUMN_IMAGE_RES_ID, employee.getImageUri());

        db.insert(TABLE_EMPLOYEES, null, values);
        db.close();
    }

    // Get employee by ID
    public Employee getEmployeeById(int id) {
        SQLiteDatabase db = this.getReadableDatabase();
        Cursor cursor = db.query(TABLE_EMPLOYEES, null, COLUMN_ID + " = ?", new String[]{String.valueOf(id)}, null, null, null);

        if (cursor != null) {
            cursor.moveToFirst();
            @SuppressLint("Range") Employee employee = new Employee(
                    cursor.getInt(cursor.getColumnIndex(COLUMN_ID)),
                    cursor.getString(cursor.getColumnIndex(COLUMN_FIRST_NAME)),
                    cursor.getString(cursor.getColumnIndex(COLUMN_LAST_NAME)),
                    cursor.getString(cursor.getColumnIndex(COLUMN_PHONE)),
                    cursor.getString(cursor.getColumnIndex(COLUMN_EMAIL)),
                    cursor.getString(cursor.getColumnIndex(COLUMN_IMAGE_RES_ID))
            );
            cursor.close();
            return employee;
        }
        return null;
    }

    // Update employee
    public boolean updateEmployee(int id, String firstName, String lastName, String phone, String email) {
        SQLiteDatabase db = this.getWritableDatabase();
        ContentValues values = new ContentValues();
        values.put(COLUMN_FIRST_NAME, firstName);
        values.put(COLUMN_LAST_NAME, lastName);
        values.put(COLUMN_PHONE, phone);
        values.put(COLUMN_EMAIL, email);

        int rowsUpdated = db.update(TABLE_EMPLOYEES, values, COLUMN_ID + " = ?", new String[]{String.valueOf(id)});
        return rowsUpdated > 0;
    }

    // Get all employees
    public List<Employee> getAllEmployees() {
        List<Employee> employeeList = new ArrayList<>();
        SQLiteDatabase db = this.getReadableDatabase();
        Cursor cursor = db.query(TABLE_EMPLOYEES, null, null, null, null, null, null);

        if (cursor != null) {
            while (cursor.moveToNext()) {
                @SuppressLint("Range") Employee employee = new Employee(
                        cursor.getInt(cursor.getColumnIndex(COLUMN_ID)),
                        cursor.getString(cursor.getColumnIndex(COLUMN_FIRST_NAME)),
                        cursor.getString(cursor.getColumnIndex(COLUMN_LAST_NAME)),
                        cursor.getString(cursor.getColumnIndex(COLUMN_PHONE)),
                        cursor.getString(cursor.getColumnIndex(COLUMN_EMAIL)),
                        cursor.getString(cursor.getColumnIndex(COLUMN_IMAGE_RES_ID))
                );
                employeeList.add(employee);
            }
            cursor.close();
        }
        return employeeList;
    }

    // Function to delete an employee by ID
    public void deleteEmployee(int id) {
        SQLiteDatabase db = this.getWritableDatabase();
        db.delete("employees", "id = ?", new String[]{String.valueOf(id)});
        db.close();
    }

    // Function to search employees by name or role
    public List<Employee> searchEmployees(String query) {
        List<Employee> employees = new ArrayList<>();
        SQLiteDatabase db = this.getReadableDatabase();
        Cursor cursor = db.rawQuery(
                "SELECT * FROM employees WHERE first_name LIKE ? OR last_name LIKE ?",
                new String[]{"%" + query + "%", "%" + query + "%"}
        );

        if (cursor.moveToFirst()) {
            do {
                @SuppressLint("Range") Employee employee = new Employee(
                        cursor.getInt(cursor.getColumnIndex(COLUMN_ID)),
                        cursor.getString(cursor.getColumnIndex(COLUMN_FIRST_NAME)),
                        cursor.getString(cursor.getColumnIndex(COLUMN_LAST_NAME)),
                        cursor.getString(cursor.getColumnIndex(COLUMN_PHONE)),
                        cursor.getString(cursor.getColumnIndex(COLUMN_EMAIL)),
                        cursor.getString(cursor.getColumnIndex(COLUMN_IMAGE_RES_ID))
                );
                employees.add(employee);
            } while (cursor.moveToNext());
        }
        cursor.close();
        db.close();
        return employees;
    }

}
