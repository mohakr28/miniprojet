package com.example.mini_projet;

public class Employee {
    private int id;
    private String firstName, lastName, phone, email;
    private String imageUri;  // تخزين URI كـ String

    // Constructor مع imageUri من نوع String
    public Employee(int id, String firstName, String lastName, String phone, String email, String imageUri) {
        this.id = id;
        this.firstName = firstName;
        this.lastName = lastName;
        this.phone = phone;
        this.email = email;
        this.imageUri = imageUri;
    }

    public Employee(int anInt, String string, String string1) {
    }

    // Getters and Setters
    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }

    public String getFirstName() {
        return firstName;
    }

    public void setFirstName(String firstName) {
        this.firstName = firstName;
    }

    public String getLastName() {
        return lastName;
    }

    public void setLastName(String lastName) {
        this.lastName = lastName;
    }

    public String getPhone() {
        return phone;
    }

    public void setPhone(String phone) {
        this.phone = phone;
    }

    public String getEmail() {
        return email;
    }

    public void setEmail(String email) {
        this.email = email;
    }

    public String getImageUri() {
        return imageUri;
    }


}
