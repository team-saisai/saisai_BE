package com.saisai.domain.course.api;

import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.service.annotation.GetExchange;
import org.springframework.web.service.annotation.HttpExchange;

@HttpExchange("http://apis.data.go.kr/B551011/Durunubi")
public interface CourseApiInterface {

    @GetExchange("/courseList")
    String callCourseApi(
        @RequestParam("MobileOS") String mobileOS,
        @RequestParam("MobileApp") String mobileApp,
        @RequestParam("serviceKey") String serviceKey,
        @RequestParam("brdDiv") String brdDiv,
        @RequestParam("pageNo") int pageNo,
        @RequestParam("numOfRows") int numOfRows,
        @RequestParam("_type") String type
    );
}
