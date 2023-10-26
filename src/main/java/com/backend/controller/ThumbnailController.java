package com.backend.controller;

import com.backend.service.IThumbnailService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/api/v1/thumbnail")
public class ThumbnailController {

    @Autowired
    private IThumbnailService iThumbnailService;

    @GetMapping("/getAllThumbnail")
    public ResponseEntity<?> getAllThumbnail() {
        return ResponseEntity.ok(iThumbnailService.getAll());
    }
}
