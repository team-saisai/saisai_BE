package com.saisai.domain.course.entity;

import com.saisai.domain.common.exception.CustomException;
import com.saisai.domain.common.exception.ExceptionCode;
import java.io.BufferedReader;
import java.io.InputStreamReader;
import java.net.URL;
import lombok.extern.slf4j.Slf4j;
import org.json.simple.JSONObject;
import org.json.simple.parser.JSONParser;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;

@Slf4j
@Component
public class CourseApi {

    @Value("${durunubi.secret}")
    private String API_SECERET_KEY;

    public JSONObject callCourseAPI(int page) throws CustomException {
        try {
            String result = " ";

            URL url = new URL("\thttps://apis.data.go.kr/B551011/Durunubi/courseList" +
                "?MobileOS=ETC" +
                "&MobileApp=saisai" +
                "&ServiceKey=" + API_SECERET_KEY +
                "&brdDiv=DNBW" +
                "&pageNo=" + page +
                "&numOfRows=10" +
                "&_type=json"
            );

            BufferedReader bf = new BufferedReader(new InputStreamReader(url.openStream(), "UTF-8"));
            result = bf.readLine();

            JSONParser jsonParser = new JSONParser();
            JSONObject jsonObject = (JSONObject) jsonParser.parse(result);
            JSONObject response = (JSONObject) jsonObject.get("response");
            JSONObject courseBody = (JSONObject) response.get("body");
            return courseBody;
        } catch (Exception exception) {
            exception.printStackTrace();
            throw new CustomException(ExceptionCode.DATABASE_ERROR);
        }
    }
}
