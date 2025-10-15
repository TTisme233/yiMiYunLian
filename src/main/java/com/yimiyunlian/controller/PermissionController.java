package com.yimiyunlian.controller;

import com.yimiyunlian.entity.Administrator;
import com.yimiyunlian.entity.DoctorPermission;
import com.yimiyunlian.entity.Patient;
import com.yimiyunlian.entity.User;
import com.yimiyunlian.service.PermissionService;
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
@RequestMapping("/api/admin/permissions")
public class PermissionController {

    @Autowired
    private PermissionService permissionService;

    @Autowired
    private UserDetailsServiceImpl userDetailsService;

    /**
     * 获取当前登录的管理员
     */
    private Administrator getCurrentAdministrator() {
        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
        if (authentication == null) {
            throw new RuntimeException("未认证的请求");
        }

        String username = authentication.getName();
        Object user = userDetailsService.loadFullUserByUsername(username);

        if (!(user instanceof Administrator)) {
            throw new RuntimeException("只有管理员可以操作权限设置");
        }

        return (Administrator) user;
    }

    /**
     * 设置医生对特定科室的权限
     */
    @PostMapping("/department")
    public ResponseEntity<?> setDepartmentPermission(@RequestBody PermissionService.PermissionRequest request) {
        try {
            // 确保当前用户是管理员
            Administrator admin = getCurrentAdministrator();

            // 验证请求参数
            if (request.getDoctorId() == null) {
                throw new RuntimeException("医生ID不能为空");
            }
            if (request.getTargetDepartment() == null || request.getTargetDepartment().isEmpty()) {
                throw new RuntimeException("目标科室不能为空");
            }

            // 设置权限
            DoctorPermission permission = permissionService.setDepartmentPermission(
                    request.getDoctorId(),
                    request.getTargetDepartment(),
                    request.isCanView(),
                    request.isCanSubmit(),
                    request.isCanUpdate(),
                    request.isCanDelete(),
                    admin
            );

            return ResponseEntity.ok(permission);
        } catch (RuntimeException e) {
            Map<String, String> error = new HashMap<>();
            error.put("error", e.getMessage());
            return new ResponseEntity<>(error, HttpStatus.BAD_REQUEST);
        }
    }

    /**
     * 设置医生对特定医生患者的权限
     */
    @PostMapping("/doctor-specific")
    public ResponseEntity<?> setDoctorSpecificPermission(@RequestBody PermissionService.PermissionRequest request) {
        try {
            // 确保当前用户是管理员
            Administrator admin = getCurrentAdministrator();

            // 验证请求参数
            if (request.getDoctorId() == null) {
                throw new RuntimeException("医生ID不能为空");
            }
            if (request.getTargetDoctorId() == null) {
                throw new RuntimeException("目标医生ID不能为空");
            }

            // 设置权限
            DoctorPermission permission = permissionService.setDoctorSpecificPermission(
                    request.getDoctorId(),
                    request.getTargetDoctorId(),
                    request.isCanView(),
                    request.isCanSubmit(),
                    request.isCanUpdate(),
                    request.isCanDelete(),
                    admin
            );

            return ResponseEntity.ok(permission);
        } catch (RuntimeException e) {
            Map<String, String> error = new HashMap<>();
            error.put("error", e.getMessage());
            return new ResponseEntity<>(error, HttpStatus.BAD_REQUEST);
        }
    }

    /**
     * 获取医生的所有权限设置
     */
    @GetMapping("/doctor/{doctorId}")
    public ResponseEntity<?> getDoctorPermissions(@PathVariable Long doctorId) {
        try {
            // 确保当前用户是管理员
            Administrator admin = getCurrentAdministrator();

            // 获取权限设置
            List<DoctorPermission> permissions = permissionService.getDoctorPermissions(doctorId);

            return ResponseEntity.ok(permissions);
        } catch (RuntimeException e) {
            Map<String, String> error = new HashMap<>();
            error.put("error", e.getMessage());
            return new ResponseEntity<>(error, HttpStatus.BAD_REQUEST);
        }
    }

    /**
     * 删除权限设置
     */
    @DeleteMapping("/{permissionId}")
    public ResponseEntity<?> deletePermission(@PathVariable Long permissionId) {
        try {
            // 确保当前用户是管理员
            Administrator admin = getCurrentAdministrator();

            // 删除权限
            permissionService.deletePermission(permissionId);

            Map<String, String> response = new HashMap<>();
            response.put("message", "权限设置已删除");
            return ResponseEntity.ok(response);
        } catch (RuntimeException e) {
            Map<String, String> error = new HashMap<>();
            error.put("error", e.getMessage());
            return new ResponseEntity<>(error, HttpStatus.BAD_REQUEST);
        }
    }

    /**
     * 分配患者到医生和科室
     */
    @PutMapping("/patient-assignment")
    public ResponseEntity<?> updatePatientAssignment(@RequestBody PermissionService.PatientAssignmentRequest request) {
        try {
            // 确保当前用户是管理员
            Administrator admin = getCurrentAdministrator();

            // 验证请求参数
            if (request.getPatientId() == null) {
                throw new RuntimeException("患者ID不能为空");
            }
            if (request.getDoctorId() == null && request.getDepartment() == null) {
                throw new RuntimeException("医生ID或科室至少需要提供一个");
            }

            // 更新患者分配
            Patient updatedPatient = permissionService.updatePatientAssignment(
                    request.getPatientId(),
                    request.getDoctorId(),
                    request.getDepartment()
            );

            return ResponseEntity.ok(updatedPatient);
        } catch (RuntimeException e) {
            Map<String, String> error = new HashMap<>();
            error.put("error", e.getMessage());
            return new ResponseEntity<>(error, HttpStatus.BAD_REQUEST);
        }
    }
}