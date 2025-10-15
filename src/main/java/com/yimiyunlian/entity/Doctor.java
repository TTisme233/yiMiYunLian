package com.yimiyunlian.entity;

import javax.persistence.DiscriminatorValue;
import javax.persistence.Entity;

@Entity
@DiscriminatorValue("DOCTOR")
public class Doctor extends User {

    private String doctorId; // 医生工号
    private String department; // 所属科室
    private String specialty; // 专业特长
    private String qualification; // 职称
    private String education; // 学历
    private String experience; // 工作经验
    private String availableTime; // 出诊时间
    private String introduction; // 医生简介
    private double rating; // 评分
    
    // Getters and Setters
    public String getDoctorId() {
        return doctorId;
    }
    
    public void setDoctorId(String doctorId) {
        this.doctorId = doctorId;
    }
    
    public String getDepartment() {
        return department;
    }
    
    public void setDepartment(String department) {
        this.department = department;
    }
    
    public String getSpecialty() {
        return specialty;
    }
    
    public void setSpecialty(String specialty) {
        this.specialty = specialty;
    }
    
    public String getQualification() {
        return qualification;
    }
    
    public void setQualification(String qualification) {
        this.qualification = qualification;
    }
    
    public String getEducation() {
        return education;
    }
    
    public void setEducation(String education) {
        this.education = education;
    }
    
    public String getExperience() {
        return experience;
    }
    
    public void setExperience(String experience) {
        this.experience = experience;
    }
    
    public String getAvailableTime() {
        return availableTime;
    }
    
    public void setAvailableTime(String availableTime) {
        this.availableTime = availableTime;
    }
    
    public String getIntroduction() {
        return introduction;
    }
    
    public void setIntroduction(String introduction) {
        this.introduction = introduction;
    }
    
    public double getRating() {
        return rating;
    }
    
    public void setRating(double rating) {
        this.rating = rating;
    }
}