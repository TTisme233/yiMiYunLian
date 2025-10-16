package com.yimiyunlian.controller;

import com.yimiyunlian.entity.Doctor;
import com.yimiyunlian.entity.Patient;
import com.yimiyunlian.entity.User;
import com.yimiyunlian.repository.UserRepository;
import com.yimiyunlian.service.ProfileService;
import com.yimiyunlian.service.UserDetailsServiceImpl;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.web.bind.annotation.*;

import java.util.HashMap;
import java.util.Map;

@RestController
@RequestMapping("/api/profile")
public class ProfileController {

    @Autowired
    private ProfileService profileService;

    @Autowired
    private UserDetailsServiceImpl userDetailsService;
    
    @Autowired
    private UserRepository userRepository;

    /**
     * 获取当前登录用户的个人信息
     */
    @GetMapping("/me")
    public ResponseEntity<?> getCurrentUserProfile() {
        try {
            // 获取当前认证用户
            User currentUser = getCurrentAuthenticatedUser();

            // 根据用户类型返回相应的个人信息
            if (currentUser instanceof Doctor) {
                Doctor doctor = profileService.getDoctorProfile(currentUser.getId());
                return ResponseEntity.ok(doctor);
            } else if (currentUser instanceof Patient) {
                Patient patient = profileService.getPatientProfile(currentUser.getId());
                return ResponseEntity.ok(patient);
            } else {
                return ResponseEntity.ok(currentUser);
            }
        } catch (RuntimeException e) {
            Map<String, String> error = new HashMap<>();
            error.put("error", e.getMessage());
            return new ResponseEntity<>(error, HttpStatus.BAD_REQUEST);
        }
    }

    /**
     * 更新医生个人信息
     */
    @PutMapping("/doctor")
    public ResponseEntity<?> updateDoctorProfile(@RequestBody ProfileService.DoctorProfileRequest request) {
        try {
            // 获取当前认证的医生
            User currentUser = getCurrentAuthenticatedUser();
            if (!(currentUser instanceof Doctor)) {
                throw new RuntimeException("只有医生可以更新医生信息");
            }

            // 更新个人信息
            Doctor updatedDoctor = profileService.updateDoctorProfile(currentUser.getId(), request);
            return ResponseEntity.ok(updatedDoctor);
        } catch (RuntimeException e) {
            Map<String, String> error = new HashMap<>();
            error.put("error", e.getMessage());
            return new ResponseEntity<>(error, HttpStatus.BAD_REQUEST);
        }
    }

    /**
     * 更新患者个人信息
     */
    @PutMapping("/patient")
    public ResponseEntity<?> updatePatientProfile(@RequestBody ProfileService.PatientProfileRequest request) {
        try {
            // 获取当前认证的患者
            User currentUser = getCurrentAuthenticatedUser();
            if (!(currentUser instanceof Patient)) {
                throw new RuntimeException("只有患者可以更新患者信息");
            }

            // 更新个人信息
            Patient updatedPatient = profileService.updatePatientProfile(currentUser.getId(), request);
            return ResponseEntity.ok(updatedPatient);
        } catch (RuntimeException e) {
            Map<String, String> error = new HashMap<>();
            error.put("error", e.getMessage());
            return new ResponseEntity<>(error, HttpStatus.BAD_REQUEST);
        }
    }

    /**
     * 获取其他用户的公开信息
     * 注意：这里只返回可以公开的信息，不包含敏感信息
     */
    @GetMapping("/user/{userId}")
    public ResponseEntity<?> getUserPublicInfo(@PathVariable Long userId) {
        try {
            // 获取当前认证用户（确保用户已登录）
            User currentUser = getCurrentAuthenticatedUser();

            // 根据ID获取目标用户
            User targetUser = userRepository.findById(userId)
                    .orElseThrow(() -> new RuntimeException("用户不存在"));

            // 根据用户类型返回公开信息
            if (targetUser instanceof Doctor) {
                Doctor doctor = (Doctor) targetUser;
                // 构建公开信息DTO，不包含敏感信息
                Map<String, Object> publicInfo = new HashMap<>();
                publicInfo.put("id", doctor.getId());
                publicInfo.put("username", doctor.getUsername());
                publicInfo.put("fullName", doctor.getFullName());
                publicInfo.put("department", doctor.getDepartment());
                publicInfo.put("specialty", doctor.getSpecialty());
                publicInfo.put("qualification", doctor.getQualification());
                publicInfo.put("introduction", doctor.getIntroduction());
                publicInfo.put("rating", doctor.getRating());
                return ResponseEntity.ok(publicInfo);
            } else if (targetUser instanceof Patient) {
                Patient patient = (Patient) targetUser;
                // 构建公开信息DTO，不包含敏感信息
                Map<String, Object> publicInfo = new HashMap<>();
                publicInfo.put("id", patient.getId());
                publicInfo.put("fullName", patient.getFullName());
                // 注意：患者信息更加敏感，只返回基本信息
                return ResponseEntity.ok(publicInfo);
            }

            // 对于管理员或其他用户类型，返回基本信息
            Map<String, Object> publicInfo = new HashMap<>();
            publicInfo.put("id", targetUser.getId());
            publicInfo.put("username", targetUser.getUsername());
            publicInfo.put("fullName", targetUser.getFullName());
            return ResponseEntity.ok(publicInfo);
        } catch (RuntimeException e) {
            Map<String, String> error = new HashMap<>();
            error.put("error", e.getMessage());
            return new ResponseEntity<>(error, HttpStatus.BAD_REQUEST);
        }
    }

    /**
     * 获取当前认证的用户对象
     */
    private User getCurrentAuthenticatedUser() {
        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
        if (authentication == null) {
            throw new RuntimeException("未认证的请求");
        }

        String username = authentication.getName();
        Object user = userDetailsService.loadFullUserByUsername(username);

        if (!(user instanceof User)) {
            throw new RuntimeException("无效的用户类型");
        }

        return (User) user;
    }
}