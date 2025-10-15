package com.yimiyunlian.controller;

import com.yimiyunlian.entity.Doctor;
import com.yimiyunlian.entity.MedicalHistory;
import com.yimiyunlian.service.MedicalHistoryService;
import com.yimiyunlian.service.UserDetailsServiceImpl;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.web.bind.annotation.*;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

@RestController
@RequestMapping("/api/medical-history")
public class MedicalHistoryController {

    @Autowired
    private MedicalHistoryService medicalHistoryService;

    @Autowired
    private UserDetailsServiceImpl userDetailsService;

    /**
     * 获取当前登录的医生信息
     */
    private Doctor getCurrentDoctor() {
        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
        if (authentication == null) {
            throw new RuntimeException("未认证的请求");
        }

        String username = authentication.getName();
        Object user = userDetailsService.loadFullUserByUsername(username);
        
        if (!(user instanceof Doctor)) {
            throw new RuntimeException("只有医生可以操作病史记录");
        }

        return (Doctor) user;
    }

    /**
     * 提交患者病史
     */
    @PostMapping("/patient/{patientId}")
    public ResponseEntity<?> submitMedicalHistory(
            @PathVariable Long patientId,
            @RequestBody MedicalHistoryService.MedicalHistoryRequest request) {
        try {
            Doctor doctor = getCurrentDoctor();
            MedicalHistory medicalHistory = medicalHistoryService.submitMedicalHistory(patientId, doctor.getId(), request);
            return new ResponseEntity<>(medicalHistory, HttpStatus.CREATED);
        } catch (RuntimeException e) {
            Map<String, String> error = new HashMap<>();
            error.put("error", e.getMessage());
            return new ResponseEntity<>(error, HttpStatus.BAD_REQUEST);
        }
    }

    /**
     * 修改患者病史
     */
    @PutMapping("/{historyId}")
    public ResponseEntity<?> updateMedicalHistory(
            @PathVariable Long historyId,
            @RequestBody MedicalHistoryService.MedicalHistoryRequest request) {
        try {
            Doctor doctor = getCurrentDoctor();
            MedicalHistory medicalHistory = medicalHistoryService.updateMedicalHistory(historyId, doctor.getId(), request);
            return new ResponseEntity<>(medicalHistory, HttpStatus.OK);
        } catch (RuntimeException e) {
            Map<String, String> error = new HashMap<>();
            error.put("error", e.getMessage());
            return new ResponseEntity<>(error, HttpStatus.BAD_REQUEST);
        }
    }

    /**
     * 删除患者病史
     */
    @DeleteMapping("/{historyId}")
    public ResponseEntity<?> deleteMedicalHistory(@PathVariable Long historyId) {
        try {
            Doctor doctor = getCurrentDoctor();
            medicalHistoryService.deleteMedicalHistory(historyId, doctor.getId());
            Map<String, String> response = new HashMap<>();
            response.put("message", "病史记录删除成功");
            return new ResponseEntity<>(response, HttpStatus.OK);
        } catch (RuntimeException e) {
            Map<String, String> error = new HashMap<>();
            error.put("error", e.getMessage());
            return new ResponseEntity<>(error, HttpStatus.BAD_REQUEST);
        }
    }

    /**
     * 查询患者的所有病史记录
     */
    @GetMapping("/patient/{patientId}")
    public ResponseEntity<?> getPatientMedicalHistories(@PathVariable Long patientId) {
        try {
            // 确保当前用户是医生或管理员
            Doctor doctor = getCurrentDoctor(); // 会验证是否是医生
            List<MedicalHistory> histories = medicalHistoryService.getPatientMedicalHistories(patientId, doctor.getId());
            return new ResponseEntity<>(histories, HttpStatus.OK);
        } catch (RuntimeException e) {
            Map<String, String> error = new HashMap<>();
            error.put("error", e.getMessage());
            return new ResponseEntity<>(error, HttpStatus.BAD_REQUEST);
        }
    }

    /**
     * 查询医生为患者创建的病史记录
     */
    @GetMapping("/patient/{patientId}/my")
    public ResponseEntity<?> getMyPatientMedicalHistories(@PathVariable Long patientId) {
        try {
            Doctor doctor = getCurrentDoctor();
            List<MedicalHistory> histories = medicalHistoryService.getDoctorPatientMedicalHistories(patientId, doctor.getId());
            return new ResponseEntity<>(histories, HttpStatus.OK);
        } catch (RuntimeException e) {
            Map<String, String> error = new HashMap<>();
            error.put("error", e.getMessage());
            return new ResponseEntity<>(error, HttpStatus.BAD_REQUEST);
        }
    }

    /**
     * 根据ID查询病史记录详情
     */
    @GetMapping("/{historyId}")
    public ResponseEntity<?> getMedicalHistoryById(@PathVariable Long historyId) {
        try {
            // 确保当前用户是医生或管理员
            Doctor doctor = getCurrentDoctor(); // 会验证是否是医生
            MedicalHistory history = medicalHistoryService.getMedicalHistoryById(historyId, doctor.getId());
            return new ResponseEntity<>(history, HttpStatus.OK);
        } catch (RuntimeException e) {
            Map<String, String> error = new HashMap<>();
            error.put("error", e.getMessage());
            return new ResponseEntity<>(error, HttpStatus.BAD_REQUEST);
        }
    }
}