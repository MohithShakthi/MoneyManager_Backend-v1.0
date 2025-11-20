package com.mohith.moneymanager.controller;

import com.mohith.moneymanager.service.DashBoardService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.Map;

@RestController
@RequiredArgsConstructor
@RequestMapping("/dashboard")
public class DashBoardController {
    private final DashBoardService dashBoardService;

    @GetMapping
    public ResponseEntity<Map<String,Object>> getDashboardData(){
        Map<String,Object> data = dashBoardService.getDashBoardData();
        return ResponseEntity.ok(data);
    }
}
