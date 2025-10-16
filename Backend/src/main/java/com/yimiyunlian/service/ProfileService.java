package com.yimiyunlian.service;

import com.yimiyunlian.entity.Doctor;
import com.yimiyunlian.entity.Patient;
import com.yimiyunlian.entity.User;
import com.yimiyunlian.repository.UserRepository;
import com.yimiyunlian.util.PasswordUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.Date;
import java.util.Optional;

@Service
public class ProfileService {

    @Autowired
    private UserRepository userRepository;

    @Autowired
    private PasswordUtils passwordUtils;

    /**
     * 更新医生个人信息
     */
    @Transactional
    public Doctor updateDoctorProfile(Long doctorId, DoctorProfileRequest request) {
        // 查找医生
        User user = userRepository.findById(doctorId)
                .filter(u -> u instanceof Doctor)
                .orElseThrow(() -> new RuntimeException("医生不存在"));

        Doctor doctor = (Doctor) user;

        // 更新公共信息
        if (request.getFullName() != null) {
            doctor.setFullName(request.getFullName());
        }
        if (request.getPhoneNumber() != null) {
            doctor.setPhoneNumber(request.getPhoneNumber());
        }
        if (request.getEmail() != null) {
            // 检查邮箱是否被其他用户使用
            Optional<User> existingUser = userRepository.findByEmail(request.getEmail());
            if (existingUser.isPresent() && !existingUser.get().getId().equals(doctorId)) {
                throw new RuntimeException("邮箱已被使用");
            }
            doctor.setEmail(request.getEmail());
        }

        // 更新医生特有信息
        if (request.getDepartment() != null) {
            doctor.setDepartment(request.getDepartment());
        }
        if (request.getSpecialty() != null) {
            doctor.setSpecialty(request.getSpecialty());
        }
        if (request.getQualification() != null) {
            doctor.setQualification(request.getQualification());
        }
        if (request.getEducation() != null) {
            doctor.setEducation(request.getEducation());
        }
        if (request.getExperience() != null) {
            doctor.setExperience(request.getExperience());
        }
        if (request.getAvailableTime() != null) {
            doctor.setAvailableTime(request.getAvailableTime());
        }
        if (request.getIntroduction() != null) {
            doctor.setIntroduction(request.getIntroduction());
        }

        // 更新密码（如果提供）
        if (request.getNewPassword() != null && !request.getNewPassword().isEmpty()) {
            if (!passwordUtils.checkPassword(request.getCurrentPassword(), doctor.getPassword())) {
                throw new RuntimeException("当前密码错误");
            }
            doctor.setPassword(passwordUtils.encryptPassword(request.getNewPassword()));
        }

        doctor.setUpdatedAt(new Date());
        return userRepository.save(doctor);
    }

    /**
     * 更新患者个人信息
     */
    @Transactional
    public Patient updatePatientProfile(Long patientId, PatientProfileRequest request) {
        // 查找患者
        User user = userRepository.findById(patientId)
                .filter(u -> u instanceof Patient)
                .orElseThrow(() -> new RuntimeException("患者不存在"));

        Patient patient = (Patient) user;

        // 更新公共信息
        if (request.getFullName() != null) {
            patient.setFullName(request.getFullName());
        }
        if (request.getPhoneNumber() != null) {
            patient.setPhoneNumber(request.getPhoneNumber());
        }
        if (request.getEmail() != null) {
            // 检查邮箱是否被其他用户使用
            Optional<User> existingUser = userRepository.findByEmail(request.getEmail());
            if (existingUser.isPresent() && !existingUser.get().getId().equals(patientId)) {
                throw new RuntimeException("邮箱已被使用");
            }
            patient.setEmail(request.getEmail());
        }

        // 更新患者特有信息
        if (request.getDateOfBirth() != null) {
            patient.setDateOfBirth(request.getDateOfBirth());
        }
        if (request.getGender() != null) {
            patient.setGender(request.getGender());
        }
        if (request.getAddress() != null) {
            patient.setAddress(request.getAddress());
        }
        if (request.getBloodType() != null) {
            patient.setBloodType(request.getBloodType());
        }
        if (request.getEmergencyContact() != null) {
            patient.setEmergencyContact(request.getEmergencyContact());
        }
        if (request.getEmergencyContactPhone() != null) {
            patient.setEmergencyContactPhone(request.getEmergencyContactPhone());
        }

        // 更新密码（如果提供）
        if (request.getNewPassword() != null && !request.getNewPassword().isEmpty()) {
            if (!passwordUtils.checkPassword(request.getCurrentPassword(), patient.getPassword())) {
                throw new RuntimeException("当前密码错误");
            }
            patient.setPassword(passwordUtils.encryptPassword(request.getNewPassword()));
        }

        patient.setUpdatedAt(new Date());
        return userRepository.save(patient);
    }

    /**
     * 获取医生个人信息
     */
    public Doctor getDoctorProfile(Long doctorId) {
        return (Doctor) userRepository.findById(doctorId)
                .filter(u -> u instanceof Doctor)
                .orElseThrow(() -> new RuntimeException("医生不存在"));
    }

    /**
     * 获取患者个人信息
     */
    public Patient getPatientProfile(Long patientId) {
        return (Patient) userRepository.findById(patientId)
                .filter(u -> u instanceof Patient)
                .orElseThrow(() -> new RuntimeException("患者不存在"));
    }

    /**
     * 医生个人信息更新请求DTO
     */
    public static class DoctorProfileRequest {
        private String fullName;
        private String phoneNumber;
        private String email;
        private String department;
        private String specialty;
        private String qualification;
        private String education;
        private String experience;
        private String availableTime;
        private String introduction;
        private String currentPassword;
        private String newPassword;

        // Getters and Setters
        public String getFullName() {
            return fullName;
        }

        public void setFullName(String fullName) {
            this.fullName = fullName;
        }

        public String getPhoneNumber() {
            return phoneNumber;
        }

        public void setPhoneNumber(String phoneNumber) {
            this.phoneNumber = phoneNumber;
        }

        public String getEmail() {
            return email;
        }

        public void setEmail(String email) {
            this.email = email;
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

        public String getCurrentPassword() {
            return currentPassword;
        }

        public void setCurrentPassword(String currentPassword) {
            this.currentPassword = currentPassword;
        }

        public String getNewPassword() {
            return newPassword;
        }

        public void setNewPassword(String newPassword) {
            this.newPassword = newPassword;
        }
    }

    /**
     * 患者个人信息更新请求DTO
     */
    public static class PatientProfileRequest {
        private String fullName;
        private String phoneNumber;
        private String email;
        private Date dateOfBirth;
        private String gender;
        private String address;
        private String bloodType;
        private String emergencyContact;
        private String emergencyContactPhone;
        private String currentPassword;
        private String newPassword;

        // Getters and Setters
        public String getFullName() {
            return fullName;
        }

        public void setFullName(String fullName) {
            this.fullName = fullName;
        }

        public String getPhoneNumber() {
            return phoneNumber;
        }

        public void setPhoneNumber(String phoneNumber) {
            this.phoneNumber = phoneNumber;
        }

        public String getEmail() {
            return email;
        }

        public void setEmail(String email) {
            this.email = email;
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

        public String getCurrentPassword() {
            return currentPassword;
        }

        public void setCurrentPassword(String currentPassword) {
            this.currentPassword = currentPassword;
        }

        public String getNewPassword() {
            return newPassword;
        }

        public void setNewPassword(String newPassword) {
            this.newPassword = newPassword;
        }
    }
}