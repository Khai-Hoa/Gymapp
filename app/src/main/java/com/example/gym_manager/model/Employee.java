package com.example.gym_manager.model;

public class Employee {
    private String employeeId;
    private String name;
    private String address;
    private String url_avatar;
    private String phone;
    private String role;
    private String salary;


    public Employee() {
    }

    public Employee(String employeeId,String name, String address, String url_avatar, String phone, String role, String salary) {
        this.employeeId = employeeId;
        this.name = name;
        this.address = address;
        this.url_avatar = url_avatar;
        this.phone = phone;
        this.role = role;
        this.salary = salary;
    }

    public Employee(String name, String address, String url_avatar, String phone, String role, String salary) {
        this.name = name;
        this.address = address;
        this.url_avatar = url_avatar;
        this.phone = phone;
        this.role = role;
        this.salary = salary;
    }

    public String getEmployeeId() {
        return employeeId;
    }

    public void setEmployeeId(String employeeId) {
        this.employeeId = employeeId;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getAddress() {
        return address;
    }

    public void setAddress(String address) {
        this.address = address;
    }

    public String getUrl_avatar() {
        return url_avatar;
    }

    public void setUrl_avatar(String url_avatar) {
        this.url_avatar = url_avatar;
    }

    public String getPhone() {
        return phone;
    }

    public void setPhone(String phone) {
        this.phone = phone;
    }

    public String getRole() {
        return role;
    }

    public void setRole(String role) {
        this.role = role;
    }

    public String getSalary() {
        return salary;
    }

    public void setSalary(String salary) {
        this.salary = salary;
    }
}
