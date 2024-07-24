package com.example.gym_manager.model;

public class Member {
    private String memberId;
    private String avatar;
    private String name;
    private String address;
    private String phone;
    private String birthday;
    private String sex;
    private String height;
    private String weight;
    private String startDay;
    private int monthsRegistered; // Số tháng đăng ký
    private boolean isActive;
    private String role;
    private double amountPaid; // Số tiền đã trả
    private String endDay;

    // Constructor không tham số
    public Member() {
    }

    // Constructor với tham số
    public Member(String avatar, String name, String address, String phone, String birthday, String sex, String height, String weight, String startDay, int monthsRegistered, boolean isActive, String role, double amountPaid, String endDay) {
        this.avatar = avatar;
        this.name = name;
        this.address = address;
        this.phone = phone;
        this.birthday = birthday;
        this.sex = sex;
        this.height = height;
        this.weight = weight;
        this.startDay = startDay;
        this.monthsRegistered = monthsRegistered;
        this.isActive = isActive;
        this.role = role;
        this.amountPaid = amountPaid;
        this.endDay = endDay;
    }

    // Các phương thức getter và setter
    public String getMemberId() {
        return memberId;
    }

    public void setMemberId(String memberId) {
        this.memberId = memberId;
    }

    public String getAvatar() {
        return avatar;
    }

    public void setAvatar(String avatar) {
        this.avatar = avatar;
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

    public String getPhone() {
        return phone;
    }

    public void setPhone(String phone) {
        this.phone = phone;
    }

    public String getBirthday() {
        return birthday;
    }

    public void setBirthday(String birthday) {
        this.birthday = birthday;
    }

    public String getSex() {
        return sex;
    }

    public void setSex(String sex) {
        this.sex = sex;
    }

    public String getHeight() {
        return height;
    }

    public void setHeight(String height) {
        this.height = height;
    }

    public String getWeight() {
        return weight;
    }

    public void setWeight(String weight) {
        this.weight = weight;
    }

    public String getStartDay() {
        return startDay;
    }

    public void setStartDay(String startDay) {
        this.startDay = startDay;
    }

    public int getMonthsRegistered() {
        return monthsRegistered;
    }

    public void setMonthsRegistered(int monthsRegistered) {
        this.monthsRegistered = monthsRegistered;
    }

    public boolean isActive() {
        return isActive;
    }

    public void setActive(boolean active) {
        isActive = active;
    }

    public String getRole() {
        return role;
    }

    public void setRole(String role) {
        this.role = role;
    }

    public double getAmountPaid() {
        return amountPaid;
    }

    public void setAmountPaid(double amountPaid) {
        this.amountPaid = amountPaid;
    }

    public String getEndDay() {
        return endDay;
    }

    public void setEndDay(String endDay) {
        this.endDay = endDay;
    }
}
