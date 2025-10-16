package com.yimiyunlian.repository;

import com.yimiyunlian.entity.MedicalHistory;
import com.yimiyunlian.entity.Patient;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public interface MedicalHistoryRepository extends JpaRepository<MedicalHistory, Long> {

    /**
     * 根据患者ID查找所有病史记录
     */
    List<MedicalHistory> findByPatientId(Long patientId);

    /**
     * 根据患者和医生ID查找病史记录
     */
    List<MedicalHistory> findByPatientIdAndDoctorId(Long patientId, Long doctorId);

    /**
     * 根据ID和医生ID查找病史记录（确保只有记录的医生可以修改/删除）
     */
    Optional<MedicalHistory> findByIdAndDoctorId(Long id, Long doctorId);

    /**
     * 根据患者ID和病史ID查找病史记录
     */
    Optional<MedicalHistory> findByIdAndPatientId(Long id, Long patientId);

    /**
     * 删除患者的所有病史记录
     */
    void deleteByPatientId(Long patientId);
}