package com.library.controller;

import com.library.model.Statistics;
import com.library.service.StatisticsService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/api/statistics")
public class StatisticsRestController {
    
    @Autowired
    private StatisticsService statisticsService;
    
    @GetMapping
    public ResponseEntity<Statistics> getLibraryStatistics() {
        return ResponseEntity.ok(statisticsService.getLibraryStatistics());
    }
}
