package com.lodong.spring.golfreservation.controller;

import com.lodong.spring.golfreservation.dto.admin.AddTimeOfInstructorDto;
import com.lodong.spring.golfreservation.dto.admin.DeleteTimeOfInstructorDto;
import com.lodong.spring.golfreservation.responseentity.service.AdminService;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@Slf4j
@RestController
@RequestMapping("rest/v1/admin")
public class AdminController {
    private final String key = "DGNsFNscvnOWQNRKLNdfaBOHN231FJNBFNANLKWQJKL421";
    private final AdminService adminService;

    public AdminController(AdminService adminService) {
        this.adminService = adminService;
    }

    //강사 시간 추가
    @PostMapping("/add/instructor/time")
    public ResponseEntity<?> addInstructorTime(@RequestBody AddTimeOfInstructorDto addTimeOfInstructorDto, String key) {
        if (key.equals(this.key)) {
            adminService.addInstructorTime(addTimeOfInstructorDto);
        }
        return ResponseEntity.ok(null);
    }
   /* //강사 시간 삭제
    @DeleteMapping("/delete/instructor/time")
    public ResponseEntity<?> deleteInstructorTime(@RequestBody DeleteTimeOfInstructorDto DeleteTimeOfInstructorDto, String key) {
        if (key.equals(this.key)) {
            adminService.deleteInstructorTime(DeleteTimeOfInstructorDto);
        }
        return ResponseEntity.ok(null);
    }*/
}
