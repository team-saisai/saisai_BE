package com.saisai.domain.course.controller;

import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/api/users/{userId}/saved-courses")
@RequiredArgsConstructor
public class CourseLikeController {

}
