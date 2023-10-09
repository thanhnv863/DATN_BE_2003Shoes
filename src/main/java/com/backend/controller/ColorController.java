package com.backend.controller;

import com.backend.dto.request.CategoryRequest;
import com.backend.dto.request.ColorRequest;
import com.backend.service.ICategoryService;
import com.backend.service.IColorService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/api/v1/color")
public class ColorController {

    @Autowired
    private IColorService iColorService;

    @GetMapping("/getAllColor")
    public ResponseEntity<?> getCategory() {
        return ResponseEntity.ok(iColorService.getAll());
    }

    @PostMapping("/addNewColor")
    public ResponseEntity<?> addNewCategory(@RequestBody ColorRequest colorRequest){
        return ResponseEntity.ok(iColorService.addNewColor(colorRequest));
    }
}
