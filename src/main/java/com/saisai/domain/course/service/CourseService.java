package com.saisai.domain.course.service;

import com.saisai.domain.course.dto.response.GetCourseListRes;
import com.saisai.domain.course.entity.CourseApi;
import com.saisai.domain.course.repository.CourseRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.json.simple.JSONObject;
import org.springframework.data.domain.Page;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Slf4j
@Service
@RequiredArgsConstructor
@Transactional(readOnly = true)
public class CourseService {

    private final CourseRepository courseRepository;
    private final CourseApi courseApi;

    public Page<GetCourseListRes> getCoursess(int page) throws Exception {
        JSONObject courseBody = courseApi.callCourseAPI(page-1);
        return GetCourseListRes.createCourseList(courseBody);
    }
}
