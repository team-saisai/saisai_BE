package com.saisai.domain.course.dto.response;

import java.util.ArrayList;
import java.util.List;
import lombok.extern.slf4j.Slf4j;
import org.json.simple.JSONArray;
import org.json.simple.JSONObject;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.PageRequest;

@Slf4j
public record GetCourseListRes (
    String courseId,
    String courseName,
    String summary,
    Integer level,
    Double distance,
    Double estimatedTime,
    String sigun
) {

    public static GetCourseListRes createGetCourseListRes (
        String courseId,
        String courseName,
        String summary,
        Integer level,
        Double distance,
        Double estimatedTime,
        String sigun
    ) {
        return new GetCourseListRes(
            courseId,
            courseName,
            summary,
            level,
            distance,
            estimatedTime,
            sigun
        );
    }

    public static Page<GetCourseListRes> createCourseList(JSONObject courseBody) throws Exception {
        try {
            JSONObject items = (JSONObject) courseBody.get("items");
            JSONArray courseArr = (JSONArray) items.get("item");

            ArrayList<String> courseIdList = new ArrayList<>(); // 코스 Id 중복 확안용
            List<GetCourseListRes> getCourseLis = new ArrayList<>();

            for (int i = 0; i < courseArr.size(); i++) {
                JSONObject course = (JSONObject) courseArr.get(i);
                if(courseIdList.contains((String)course.get("crsIdx"))) {
                    continue;
                } else {
                    courseIdList.add((String)course.get("crsIdx"));
                }

                String courseId = (String)course.get("crsIdx");
                String courseName = (String)course.get("crsKorNm");
                String summary = (String)course.get("crsSummary");
                String levelStr = (String)course.get("crsLevel");
                Integer level = Integer.parseInt(levelStr);
                String distanceStr = (String)course.get("crsDstnc");
                Double distance = Double.parseDouble(distanceStr);
                String estimatedTimeStr = (String)course.get("crsTotlRqrmHour");
                Double estimatedTime = Double.parseDouble(estimatedTimeStr);
                String sigun = (String)course.get("sigun");

                GetCourseListRes getCourseListRes = GetCourseListRes.createGetCourseListRes(
                    courseId,
                    courseName,
                    summary,
                    level,
                    distance,
                    estimatedTime,
                    sigun
                );
                getCourseLis.add(getCourseListRes);
            }

            Long totalCount = (Long) courseBody.get("totalCount");

            Long pageLong = (Long) courseBody.get("pageNo");
            int page = pageLong.intValue();
            Long sizeLong = (Long) courseBody.get("numOfRows");
            int size = sizeLong.intValue();

            return new PageImpl<>(getCourseLis,
                PageRequest.of(page - 1, size), totalCount);
        }
        catch (Exception exception) {
            throw new Exception(exception);
        }
    }
}
