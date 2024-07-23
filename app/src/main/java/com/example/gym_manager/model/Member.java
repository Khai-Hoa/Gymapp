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
    private String firstDay;
    private String lastDay;
    private Boolean isActive;
    private String role;
    private Double payment;


    public Member() {
    }

    public Member(String avatar, String name, String address, String phone, String birthday, String sex, String height, String weight, String firstDay, String lastDay, Boolean isActive, String role, Double payment) {
        this.avatar = avatar;
        this.name = name;
        this.address = address;
        this.phone = phone;
        this.birthday = birthday;
        this.sex = sex;
        this.height = height;
        this.weight = weight;
        this.firstDay = firstDay;
        this.lastDay = lastDay;
        this.isActive = isActive;
        this.role = role;
        this.payment = payment;
    }

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

    public String getFirstDay() {
        return firstDay;
    }

    public void setFirstDay(String firstDay) {
        this.firstDay = firstDay;
    }

    public String getLastDay() {
        return lastDay;
    }

    public void setLastDay(String lastDay) {
        this.lastDay = lastDay;
    }

    public Boolean getActive() {
        return isActive;
    }

    public void setActive(Boolean active) {
        isActive = active;
    }

    public String getRole() {
        return role;
    }

    public void setRole(String role) {
        this.role = role;
    }

    public Double getPayment() {
        return payment;
    }

    public void setPayment(Double payment) {
        this.payment = payment;
    }
}
