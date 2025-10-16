package com.yimiyunlian.repository;

import com.yimiyunlian.entity.Doctor;
import com.yimiyunlian.entity.DoctorPermission;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public interface DoctorPermissionRepository extends JpaRepository<DoctorPermission, Long> {

    /**
     * 根据医生查找所有权限设置
     */
    List<DoctorPermission> findByDoctor(Doctor doctor);

    /**
     * 根据医生和目标科室查找权限
     */
    List<DoctorPermission> findByDoctorAndTargetDepartment(Doctor doctor, String targetDepartment);

    /**
     * 根据医生和目标医生查找权限
     */
    Optional<DoctorPermission> findByDoctorAndTargetDoctor(Doctor doctor, Doctor targetDoctor);

    /**
     * 根据医生和目标科室及目标医生查找特定权限
     */
    Optional<DoctorPermission> findByDoctorAndTargetDepartmentAndTargetDoctor(
            Doctor doctor, String targetDepartment, Doctor targetDoctor);

    /**
     * 删除医生的所有权限设置
     */
    void deleteByDoctor(Doctor doctor);

    /**
     * 删除针对特定目标医生的权限设置
     */
    void deleteByDoctorAndTargetDoctor(Doctor doctor, Doctor targetDoctor);

    /**
     * 删除针对特定科室的权限设置
     */
    void deleteByDoctorAndTargetDepartment(Doctor doctor, String targetDepartment);
}