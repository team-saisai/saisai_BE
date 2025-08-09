package com.saisai.domain.checkpoint.client;

import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.service.annotation.GetExchange;
import org.springframework.web.service.annotation.HttpExchange;

@HttpExchange("https://www.durunubi.kr/api/poi/main")
public interface DurunubiCheckpointClient {

    @GetExchange
    String getCheckpoints(
        @RequestParam("min_latitude") Double minLatitude,
        @RequestParam("min_longitude") Double minLongitude,
        @RequestParam("max_latitude") Double maxLatitude,
        @RequestParam("max_longitude") Double maxLongitude,
        @RequestParam("course_id") String courseId
    );
}
