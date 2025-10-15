package com.yimiyunlian.service;

import com.yimiyunlian.entity.Doctor;
import com.yimiyunlian.entity.MedicalHistory;
import com.yimiyunlian.entity.Patient;
import com.yimiyunlian.repository.MedicalHistoryRepository;
import com.yimiyunlian.repository.UserRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.Date;
import java.util.List;
import java.util.Optional;

@Service
public class MedicalHistoryService {

    @Autowired
    private MedicalHistoryRepository medicalHistoryRepository;

    @Autowired
    private UserRepository userRepository;
    
    @Autowired
    private PermissionService permissionService;

    /**
     * 提交患者病史
     */
    @Transactional
    public MedicalHistory submitMedicalHistory(Long patientId, Long doctorId, MedicalHistoryRequest request) {
        // 验证患者是否存在
        Patient patient = (Patient) userRepository.findById(patientId)
                .filter(user -> user instanceof Patient)
                .orElseThrow(() -> new RuntimeException("患者不存在"));

        // 验证医生是否存在
        Doctor doctor = (Doctor) userRepository.findById(doctorId)
                .filter(user -> user instanceof Doctor)
                .orElseThrow(() -> new RuntimeException("医生不存在"));
        
        // 检查医生是否有权限提交该患者的病史
        if (!permissionService.hasPermission(doctor, patient, "SUBMIT")) {
            throw new RuntimeException("无权限提交该患者的病史记录");
        }

        // 创建病史记录
        MedicalHistory medicalHistory = new MedicalHistory();
        medicalHistory.setPatient(patient);
        medicalHistory.setDoctor(doctor);
        medicalHistory.setDiagnosisDate(request.getDiagnosisDate() != null ? request.getDiagnosisDate() : new Date());
        medicalHistory.setDiagnosis(request.getDiagnosis());
        medicalHistory.setSymptoms(request.getSymptoms());
        medicalHistory.setTreatment(request.getTreatment());
        medicalHistory.setNotes(request.getNotes());
        medicalHistory.setAttachments(request.getAttachments());

        // 保存病史记录
        MedicalHistory savedHistory = medicalHistoryRepository.save(medicalHistory);

        // 更新患者的既往病史摘要
        updatePatientMedicalHistorySummary(patient);

        return savedHistory;
    }

    /**
     * 修改患者病史
     */
    @Transactional
    public MedicalHistory updateMedicalHistory(Long historyId, Long doctorId, MedicalHistoryRequest request) {
        // 查找病史记录
        MedicalHistory medicalHistory = medicalHistoryRepository.findById(historyId)
                .orElseThrow(() -> new RuntimeException("病史记录不存在"));
        
        // 验证医生是否存在
        Doctor doctor = (Doctor) userRepository.findById(doctorId)
                .filter(user -> user instanceof Doctor)
                .orElseThrow(() -> new RuntimeException("医生不存在"));
        
        // 检查医生是否有权限修改该患者的病史
        if (!permissionService.hasPermission(doctor, medicalHistory.getPatient(), "UPDATE")) {
            throw new RuntimeException("无权限修改该患者的病史记录");
        }

        // 更新病史记录
        if (request.getDiagnosisDate() != null) {
            medicalHistory.setDiagnosisDate(request.getDiagnosisDate());
        }
        if (request.getDiagnosis() != null) {
            medicalHistory.setDiagnosis(request.getDiagnosis());
        }
        if (request.getSymptoms() != null) {
            medicalHistory.setSymptoms(request.getSymptoms());
        }
        if (request.getTreatment() != null) {
            medicalHistory.setTreatment(request.getTreatment());
        }
        if (request.getNotes() != null) {
            medicalHistory.setNotes(request.getNotes());
        }
        if (request.getAttachments() != null) {
            medicalHistory.setAttachments(request.getAttachments());
        }
        medicalHistory.setUpdatedAt(new Date());

        // 保存更新
        MedicalHistory updatedHistory = medicalHistoryRepository.save(medicalHistory);

        // 更新患者的既往病史摘要
        updatePatientMedicalHistorySummary(medicalHistory.getPatient());

        return updatedHistory;
    }

    /**
     * 删除患者病史
     */
    @Transactional
    public void deleteMedicalHistory(Long historyId, Long doctorId) {
        // 查找病史记录
        MedicalHistory medicalHistory = medicalHistoryRepository.findById(historyId)
                .orElseThrow(() -> new RuntimeException("病史记录不存在"));
        
        // 验证医生是否存在
        Doctor doctor = (Doctor) userRepository.findById(doctorId)
                .filter(user -> user instanceof Doctor)
                .orElseThrow(() -> new RuntimeException("医生不存在"));
        
        // 检查医生是否有权限删除该患者的病史
        if (!permissionService.hasPermission(doctor, medicalHistory.getPatient(), "DELETE")) {
            throw new RuntimeException("无权限删除该患者的病史记录");
        }

        Patient patient = medicalHistory.getPatient();

        // 删除病史记录
        medicalHistoryRepository.delete(medicalHistory);

        // 更新患者的既往病史摘要
        updatePatientMedicalHistorySummary(patient);
    }

    /**
     * 查询患者的所有病史记录
     */
    public List<MedicalHistory> getPatientMedicalHistories(Long patientId, Long doctorId) {
        // 验证患者是否存在
        Patient patient = (Patient) userRepository.findById(patientId)
                .filter(user -> user instanceof Patient)
                .orElseThrow(() -> new RuntimeException("患者不存在"));
        
        // 验证医生是否存在
        Doctor doctor = (Doctor) userRepository.findById(doctorId)
                .filter(user -> user instanceof Doctor)
                .orElseThrow(() -> new RuntimeException("医生不存在"));
        
        // 检查医生是否有权限查看该患者的病史
        if (!permissionService.hasPermission(doctor, patient, "VIEW")) {
            throw new RuntimeException("无权限查看该患者的病史记录");
        }

        // 查询病史记录
        return medicalHistoryRepository.findByPatientId(patientId);
    }

    /**
     * 查询医生为患者创建的病史记录
     */
    public List<MedicalHistory> getDoctorPatientMedicalHistories(Long patientId, Long doctorId) {
        // 验证患者是否存在
        Patient patient = (Patient) userRepository.findById(patientId)
                .filter(user -> user instanceof Patient)
                .orElseThrow(() -> new RuntimeException("患者不存在"));
        
        // 验证医生是否存在
        Doctor doctor = (Doctor) userRepository.findById(doctorId)
                .filter(user -> user instanceof Doctor)
                .orElseThrow(() -> new RuntimeException("医生不存在"));
        
        // 检查医生是否有权限查看该患者的病史
        if (!permissionService.hasPermission(doctor, patient, "VIEW")) {
            throw new RuntimeException("无权限查看该患者的病史记录");
        }

        // 查询病史记录
        return medicalHistoryRepository.findByPatientIdAndDoctorId(patientId, doctorId);
    }

    /**
     * 根据ID查询病史记录
     */
    public MedicalHistory getMedicalHistoryById(Long historyId, Long doctorId) {
        // 查找病史记录
        MedicalHistory medicalHistory = medicalHistoryRepository.findById(historyId)
                .orElseThrow(() -> new RuntimeException("病史记录不存在"));
        
        // 验证医生是否存在
        Doctor doctor = (Doctor) userRepository.findById(doctorId)
                .filter(user -> user instanceof Doctor)
                .orElseThrow(() -> new RuntimeException("医生不存在"));
        
        // 检查医生是否有权限查看该患者的病史
        if (!permissionService.hasPermission(doctor, medicalHistory.getPatient(), "VIEW")) {
            throw new RuntimeException("无权限查看该病史记录");
        }
        
        return medicalHistory;
        return medicalHistoryRepository.findById(historyId)
                .orElseThrow(() -> new RuntimeException("病史记录不存在"));
    }

    /**
     * 更新患者的既往病史摘要
     */
    private void updatePatientMedicalHistorySummary(Patient patient) {
        List<MedicalHistory> histories = medicalHistoryRepository.findByPatientId(patient.getId());
        StringBuilder summary = new StringBuilder();

        // 生成简洁的病史摘要
        for (MedicalHistory history : histories) {
            if (history.getDiagnosis() != null && !history.getDiagnosis().isEmpty()) {
                if (summary.length() > 0) {
                    summary.append("; ");
                }
                summary.append(history.getDiagnosis());
            }
        }

        // 更新患者的既往病史字段
        patient.setMedicalHistory(summary.length() > 0 ? summary.toString() : null);
        userRepository.save(patient);
    }

    /**
     * 病史请求DTO
     */
    public static class MedicalHistoryRequest {
        private Date diagnosisDate;
        private String diagnosis;
        private String symptoms;
        private String treatment;
        private String notes;
        private String attachments;

        // Getters and Setters
        public Date getDiagnosisDate() {
            return diagnosisDate;
        }

        public void setDiagnosisDate(Date diagnosisDate) {
            this.diagnosisDate = diagnosisDate;
        }

        public String getDiagnosis() {
            return diagnosis;
        }

        public void setDiagnosis(String diagnosis) {
            this.diagnosis = diagnosis;
        }

        public String getSymptoms() {
            return symptoms;
        }

        public void setSymptoms(String symptoms) {
            this.symptoms = symptoms;
        }

        public String getTreatment() {
            return treatment;
        }

        public void setTreatment(String treatment) {
            this.treatment = treatment;
        }

        public String getNotes() {
            return notes;
        }

        public void setNotes(String notes) {
            this.notes = notes;
        }

        public String getAttachments() {
            return attachments;
        }

        public void setAttachments(String attachments) {
            this.attachments = attachments;
        }
    }
}