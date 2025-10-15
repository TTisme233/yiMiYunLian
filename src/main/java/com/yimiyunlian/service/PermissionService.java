package com.yimiyunlian.service;

import com.yimiyunlian.entity.Administrator;
import com.yimiyunlian.entity.Doctor;
import com.yimiyunlian.entity.DoctorPermission;
import com.yimiyunlian.entity.Patient;
import com.yimiyunlian.repository.DoctorPermissionRepository;
import com.yimiyunlian.repository.UserRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.Optional;

@Service
public class PermissionService {

    @Autowired
    private DoctorPermissionRepository permissionRepository;

    @Autowired
    private UserRepository userRepository;

    /**
     * 检查医生是否有权限对特定患者执行操作
     */
    public boolean hasPermission(Doctor doctor, Patient patient, String operation) {
        // 获取医生的所有权限设置
        List<DoctorPermission> permissions = permissionRepository.findByDoctor(doctor);

        for (DoctorPermission permission : permissions) {
            // 检查科室权限（如果设置了目标科室）
            if (permission.getTargetDepartment() != null && 
                !permission.getTargetDepartment().equals(patient.getDepartment())) {
                continue;
            }

            // 检查目标医生权限（如果设置了目标医生）
            if (permission.getTargetDoctor() != null && 
                (patient.getAttendingDoctor() == null || 
                !permission.getTargetDoctor().getId().equals(patient.getAttendingDoctor().getId()))) {
                continue;
            }

            // 根据操作类型检查具体权限
            switch (operation) {
                case "VIEW":
                    if (permission.isCanView()) return true;
                    break;
                case "SUBMIT":
                    if (permission.isCanSubmit()) return true;
                    break;
                case "UPDATE":
                    if (permission.isCanUpdate()) return true;
                    break;
                case "DELETE":
                    if (permission.isCanDelete()) return true;
                    break;
            }
        }

        // 如果没有找到匹配的权限设置，默认不允许
        return false;
    }

    /**
     * 为医生创建或更新科室权限
     */
    @Transactional
    public DoctorPermission setDepartmentPermission(
            Long doctorId, String targetDepartment,
            boolean canView, boolean canSubmit, boolean canUpdate, boolean canDelete,
            Administrator admin) {
        
        Doctor doctor = (Doctor) userRepository.findById(doctorId)
                .orElseThrow(() -> new RuntimeException("医生不存在"));

        // 查找是否已存在相同的权限设置
        Optional<DoctorPermission> existingPermissionOpt = permissionRepository.findByDoctorAndTargetDepartmentAndTargetDoctor(
                doctor, targetDepartment, null);

        DoctorPermission permission;
        if (existingPermissionOpt.isPresent()) {
            // 更新现有权限
            permission = existingPermissionOpt.get();
        } else {
            // 创建新权限
            permission = new DoctorPermission();
            permission.setDoctor(doctor);
            permission.setTargetDepartment(targetDepartment);
            permission.setCreatedBy(admin);
        }

        // 设置权限值
        permission.setCanView(canView);
        permission.setCanSubmit(canSubmit);
        permission.setCanUpdate(canUpdate);
        permission.setCanDelete(canDelete);

        return permissionRepository.save(permission);
    }

    /**
     * 为医生创建或更新针对特定医生患者的权限
     */
    @Transactional
    public DoctorPermission setDoctorSpecificPermission(
            Long doctorId, Long targetDoctorId,
            boolean canView, boolean canSubmit, boolean canUpdate, boolean canDelete,
            Administrator admin) {
        
        Doctor doctor = (Doctor) userRepository.findById(doctorId)
                .orElseThrow(() -> new RuntimeException("医生不存在"));

        Doctor targetDoctor = (Doctor) userRepository.findById(targetDoctorId)
                .orElseThrow(() -> new RuntimeException("目标医生不存在"));

        // 查找是否已存在相同的权限设置
        Optional<DoctorPermission> existingPermissionOpt = permissionRepository.findByDoctorAndTargetDoctor(
                doctor, targetDoctor);

        DoctorPermission permission;
        if (existingPermissionOpt.isPresent()) {
            // 更新现有权限
            permission = existingPermissionOpt.get();
        } else {
            // 创建新权限
            permission = new DoctorPermission();
            permission.setDoctor(doctor);
            permission.setTargetDoctor(targetDoctor);
            permission.setCreatedBy(admin);
        }

        // 设置权限值
        permission.setCanView(canView);
        permission.setCanSubmit(canSubmit);
        permission.setCanUpdate(canUpdate);
        permission.setCanDelete(canDelete);

        return permissionRepository.save(permission);
    }

    /**
     * 获取医生的所有权限设置
     */
    public List<DoctorPermission> getDoctorPermissions(Long doctorId) {
        Doctor doctor = (Doctor) userRepository.findById(doctorId)
                .orElseThrow(() -> new RuntimeException("医生不存在"));

        return permissionRepository.findByDoctor(doctor);
    }

    /**
     * 删除医生的权限设置
     */
    @Transactional
    public void deletePermission(Long permissionId) {
        permissionRepository.deleteById(permissionId);
    }

    /**
     * 更新患者的所属医生和科室
     */
    @Transactional
    public Patient updatePatientAssignment(Long patientId, Long doctorId, String department) {
        Patient patient = (Patient) userRepository.findById(patientId)
                .orElseThrow(() -> new RuntimeException("患者不存在"));

        if (doctorId != null) {
            Doctor doctor = (Doctor) userRepository.findById(doctorId)
                    .orElseThrow(() -> new RuntimeException("医生不存在"));
            patient.setAttendingDoctor(doctor);
        }

        if (department != null) {
            patient.setDepartment(department);
        }

        return (Patient) userRepository.save(patient);
    }

    /**
     * 权限设置请求DTO
     */
    public static class PermissionRequest {
        private Long doctorId;
        private String targetDepartment;
        private Long targetDoctorId;
        private boolean canView;
        private boolean canSubmit;
        private boolean canUpdate;
        private boolean canDelete;

        // Getters and Setters
        public Long getDoctorId() {
            return doctorId;
        }

        public void setDoctorId(Long doctorId) {
            this.doctorId = doctorId;
        }

        public String getTargetDepartment() {
            return targetDepartment;
        }

        public void setTargetDepartment(String targetDepartment) {
            this.targetDepartment = targetDepartment;
        }

        public Long getTargetDoctorId() {
            return targetDoctorId;
        }

        public void setTargetDoctorId(Long targetDoctorId) {
            this.targetDoctorId = targetDoctorId;
        }

        public boolean isCanView() {
            return canView;
        }

        public void setCanView(boolean canView) {
            this.canView = canView;
        }

        public boolean isCanSubmit() {
            return canSubmit;
        }

        public void setCanSubmit(boolean canSubmit) {
            this.canSubmit = canSubmit;
        }

        public boolean isCanUpdate() {
            return canUpdate;
        }

        public void setCanUpdate(boolean canUpdate) {
            this.canUpdate = canUpdate;
        }

        public boolean isCanDelete() {
            return canDelete;
        }

        public void setCanDelete(boolean canDelete) {
            this.canDelete = canDelete;
        }
    }

    /**
     * 患者分配请求DTO
     */
    public static class PatientAssignmentRequest {
        private Long patientId;
        private Long doctorId;
        private String department;

        // Getters and Setters
        public Long getPatientId() {
            return patientId;
        }

        public void setPatientId(Long patientId) {
            this.patientId = patientId;
        }

        public Long getDoctorId() {
            return doctorId;
        }

        public void setDoctorId(Long doctorId) {
            this.doctorId = doctorId;
        }

        public String getDepartment() {
            return department;
        }

        public void setDepartment(String department) {
            this.department = department;
        }
    }
}