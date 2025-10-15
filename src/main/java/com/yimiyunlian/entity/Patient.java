package com.yimiyunlian.entity;

import javax.persistence.DiscriminatorValue;
import javax.persistence.Entity;
import javax.persistence.ManyToOne;
import java.util.Date;

@Entity
@DiscriminatorValue("PATIENT")
public class Patient extends User {

    private String medicalRecordNumber; // 病历号
    private Date dateOfBirth; // 出生日期
    private String gender; // 性别
    private String address; // 地址
    private String bloodType; // 血型
    private String emergencyContact; // 紧急联系人
    private String emergencyContactPhone; // 紧急联系人电话
    private String medicalHistory; // 既往病史
    private String department; // 所属科室
    
    @ManyToOne
    private Doctor attendingDoctor; // 所属医生
    
    // Getters and Setters
    public String getMedicalRecordNumber() {
        return medicalRecordNumber;
    }
    
    public void setMedicalRecordNumber(String medicalRecordNumber) {
        this.medicalRecordNumber = medicalRecordNumber;
    }
    
    public Date getDateOfBirth() {
        return dateOfBirth;
    }
    
    public void setDateOfBirth(Date dateOfBirth) {
        this.dateOfBirth = dateOfBirth;
    }
    
    public String getGender() {
        return gender;
    }
    
    public void setGender(String gender) {
        this.gender = gender;
    }
    
    public String getAddress() {
        return address;
    }
    
    public void setAddress(String address) {
        this.address = address;
    }
    
    public String getBloodType() {
        return bloodType;
    }
    
    public void setBloodType(String bloodType) {
        this.bloodType = bloodType;
    }
    
    public String getEmergencyContact() {
        return emergencyContact;
    }
    
    public void setEmergencyContact(String emergencyContact) {
        this.emergencyContact = emergencyContact;
    }
    
    public String getEmergencyContactPhone() {
        return emergencyContactPhone;
    }
    
    public void setEmergencyContactPhone(String emergencyContactPhone) {
        this.emergencyContactPhone = emergencyContactPhone;
    }
    
    public String getMedicalHistory() {
        return medicalHistory;
    }
    
    public void setMedicalHistory(String medicalHistory) {
        this.medicalHistory = medicalHistory;
    }
    
    public String getDepartment() {
        return department;
    }
    
    public void setDepartment(String department) {
        this.department = department;
    }
    
    public Doctor getAttendingDoctor() {
        return attendingDoctor;
    }
    
    public void setAttendingDoctor(Doctor attendingDoctor) {
        this.attendingDoctor = attendingDoctor;
    }
}