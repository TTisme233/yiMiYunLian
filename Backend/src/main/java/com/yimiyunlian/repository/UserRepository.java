package com.yimiyunlian.repository;

import com.yimiyunlian.entity.Administrator;
import com.yimiyunlian.entity.Doctor;
import com.yimiyunlian.entity.Patient;
import com.yimiyunlian.entity.User;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public interface UserRepository extends JpaRepository<User, Long> {

    /**
     * 根据用户名查找用户
     */
    Optional<User> findByUsername(String username);

    /**
     * 检查用户名是否已存在
     */
    boolean existsByUsername(String username);

    /**
     * 检查邮箱是否已存在
     */
    boolean existsByEmail(String email);
    
    /**
     * 查找所有患者
     */
    List<Patient> findByClass(Class<Patient> patientClass);
    
    /**
     * 查找所有医生
     */
    List<Doctor> findByClass(Class<Doctor> doctorClass);
    
    /**
     * 查找所有管理员
     */
    List<Administrator> findByClass(Class<Administrator> adminClass);
    
    /**
     * 根据病历号查找患者
     */
    Optional<Patient> findPatientByMedicalRecordNumber(String medicalRecordNumber);
    
    /**
     * 根据医生工号查找医生
     */
    Optional<Doctor> findDoctorByDoctorId(String doctorId);
    
    /**
     * 根据管理员工号查找管理员
     */
    Optional<Administrator> findAdministratorByAdminId(String adminId);
}