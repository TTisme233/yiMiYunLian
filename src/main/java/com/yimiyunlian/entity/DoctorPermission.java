package com.yimiyunlian.entity;

import javax.persistence.*;
import java.util.Date;

@Entity
@Table(name = "doctor_permissions")
public class DoctorPermission {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @ManyToOne
    @JoinColumn(name = "doctor_id", nullable = false)
    private Doctor doctor;

    @Column(name = "target_department")
    private String targetDepartment; // 目标科室，可以为null表示所有科室

    @ManyToOne
    @JoinColumn(name = "target_doctor_id")
    private Doctor targetDoctor; // 目标医生，可以为null表示所有医生的患者

    private boolean canView = false; // 是否可以查看病史
    private boolean canSubmit = false; // 是否可以提交病史
    private boolean canUpdate = false; // 是否可以修改病史
    private boolean canDelete = false; // 是否可以删除病史

    @ManyToOne
    @JoinColumn(name = "created_by", nullable = false)
    private Administrator createdBy; // 创建权限的管理员

    private Date createdAt;
    private Date updatedAt;

    // Getters and Setters
    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public Doctor getDoctor() {
        return doctor;
    }

    public void setDoctor(Doctor doctor) {
        this.doctor = doctor;
    }

    public String getTargetDepartment() {
        return targetDepartment;
    }

    public void setTargetDepartment(String targetDepartment) {
        this.targetDepartment = targetDepartment;
    }

    public Doctor getTargetDoctor() {
        return targetDoctor;
    }

    public void setTargetDoctor(Doctor targetDoctor) {
        this.targetDoctor = targetDoctor;
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

    public Administrator getCreatedBy() {
        return createdBy;
    }

    public void setCreatedBy(Administrator createdBy) {
        this.createdBy = createdBy;
    }

    public Date getCreatedAt() {
        return createdAt;
    }

    public void setCreatedAt(Date createdAt) {
        this.createdAt = createdAt;
    }

    public Date getUpdatedAt() {
        return updatedAt;
    }

    public void setUpdatedAt(Date updatedAt) {
        this.updatedAt = updatedAt;
    }

    @PrePersist
    protected void onCreate() {
        createdAt = new Date();
        updatedAt = new Date();
    }

    @PreUpdate
    protected void onUpdate() {
        updatedAt = new Date();
    }
}