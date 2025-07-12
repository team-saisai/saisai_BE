package com.saisai.domain.course.controller;

import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@Tag(name = "코스 저장 API")
@RestController
@RequestMapping("/api/saved-courses")
@RequiredArgsConstructor
public class CourseSaveController {

}
