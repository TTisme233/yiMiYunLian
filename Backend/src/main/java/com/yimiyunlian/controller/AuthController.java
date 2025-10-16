package com.yimiyunlian.controller;

import com.yimiyunlian.entity.Administrator;
import com.yimiyunlian.entity.Doctor;
import com.yimiyunlian.entity.Patient;
import com.yimiyunlian.entity.User;
import com.yimiyunlian.repository.UserRepository;
import com.yimiyunlian.util.JwtUtils;
import com.yimiyunlian.util.PasswordUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.Date;
import java.util.HashMap;
import java.util.Map;
import java.util.Optional;

@RestController
@RequestMapping("/api/auth")
public class AuthController {

    @Autowired
    private UserRepository userRepository;

    @Autowired
    private JwtUtils jwtUtils;

    @Autowired
    private PasswordUtils passwordUtils;

    @PostMapping("/login")
    public ResponseEntity<?> authenticateUser(@RequestBody LoginRequest loginRequest) {
        // 查找用户
        Optional<User> userOptional = userRepository.findByUsername(loginRequest.getUsername());
        
        if (!userOptional.isPresent()) {
            return ResponseEntity.status(HttpStatus.UNAUTHORIZED).body("用户名或密码错误");
        }
        
        User user = userOptional.get();

        if (!passwordUtils.checkPassword(loginRequest.getPassword(), user.getPassword())) {
            return ResponseEntity.status(HttpStatus.UNAUTHORIZED).body("用户名或密码错误");
        }
        

        String userType = "USER";
        
        if (user instanceof Patient) {
            userType = "PATIENT";
        } else if (user instanceof Doctor) {
            userType = "DOCTOR";
        } else if (user instanceof Administrator) {
            userType = "ADMINISTRATOR";
        }
        

        String jwtToken = jwtUtils.generateJwtToken(user.getUsername(), userType);
        

        Map<String, Object> response = new HashMap<>();
        response.put("token", jwtToken);
        response.put("username", user.getUsername());
        response.put("email", user.getEmail());
        response.put("userType", userType);
        
        return ResponseEntity.ok(response);
    }


    @PostMapping("/register")
    public ResponseEntity<?> registerUser(@RequestBody RegisterRequest registerRequest) {

        if (userRepository.existsByUsername(registerRequest.getUsername())) {
            return ResponseEntity.status(HttpStatus.BAD_REQUEST).body("用户名已存在");
        }
        

        if (userRepository.existsByEmail(registerRequest.getEmail())) {
            return ResponseEntity.status(HttpStatus.BAD_REQUEST).body("邮箱已被注册");
        }
        
        User user;
        

        switch (registerRequest.getUserType()) {
            case "PATIENT":
                Patient patient = new Patient();
                patient.setMedicalRecordNumber(registerRequest.getMedicalRecordNumber());
                patient.setDateOfBirth(registerRequest.getDateOfBirth());
                patient.setGender(registerRequest.getGender());
                user = patient;
                break;
                
            case "DOCTOR":
                Doctor doctor = new Doctor();
                doctor.setDoctorId(registerRequest.getDoctorId());
                doctor.setDepartment(registerRequest.getDepartment());
                doctor.setSpecialty(registerRequest.getSpecialty());
                user = doctor;
                break;
                
            case "ADMINISTRATOR":
                Administrator admin = new Administrator();
                admin.setAdminId(registerRequest.getAdminId());
                admin.setRole(registerRequest.getRole());
                user = admin;
                break;
                
            default:
                return ResponseEntity.status(HttpStatus.BAD_REQUEST).body("不支持的用户类型");
        }
        

        user.setUsername(registerRequest.getUsername());
        user.setEmail(registerRequest.getEmail());
        user.setPassword(passwordUtils.encryptPassword(registerRequest.getPassword()));
        user.setFullName(registerRequest.getFullName());
        user.setPhoneNumber(registerRequest.getPhoneNumber());
        

        userRepository.save(user);
        
        return ResponseEntity.ok("注册成功");
    }


    public static class LoginRequest {
        private String username;
        private String password;
        
        public String getUsername() {
            return username;
        }
        
        public void setUsername(String username) {
            this.username = username;
        }
        
        public String getPassword() {
            return password;
        }
        
        public void setPassword(String password) {
            this.password = password;
        }
    }


    public static class RegisterRequest {
        private String username;
        private String email;
        private String password;
        private String userType;
        private String fullName;
        private String phoneNumber;
        

        private String medicalRecordNumber;
        private Date dateOfBirth;
        private String gender;
        

        private String doctorId;
        private String department;
        private String specialty;
        

        private String adminId;
        private String role;
        

        public String getUsername() {
            return username;
        }
        
        public void setUsername(String username) {
            this.username = username;
        }
        
        public String getEmail() {
            return email;
        }
        
        public void setEmail(String email) {
            this.email = email;
        }
        
        public String getPassword() {
            return password;
        }
        
        public void setPassword(String password) {
            this.password = password;
        }
        
        public String getUserType() {
            return userType;
        }
        
        public void setUserType(String userType) {
            this.userType = userType;
        }
        
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

        public String getAdminId() {
            return adminId;
        }
        
        public void setAdminId(String adminId) {
            this.adminId = adminId;
        }
        
        public String getRole() {
            return role;
        }
        
        public void setRole(String role) {
            this.role = role;
        }
    }
}