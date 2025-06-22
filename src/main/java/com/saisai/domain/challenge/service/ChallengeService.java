package com.saisai.domain.challenge.service;

import com.saisai.domain.challenge.dto.response.ChallengeInfoProjection;
import com.saisai.domain.challenge.dto.response.PopularChallengeListItemRes;
import com.saisai.domain.challenge.repository.ChallengeParticipantRepository;
import com.saisai.domain.course.dto.response.CourseSummaryInfo;
import com.saisai.domain.course.service.CourseService;
import java.util.ArrayList;
import java.util.List;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@RequiredArgsConstructor
@Transactional(readOnly = true)
public class ChallengeService {

    private final ChallengeParticipantRepository challengeParticipantRepository;
    private final CourseService courseService;

    public List<PopularChallengeListItemRes> getPopularChallenges() {
        List<ChallengeInfoProjection> challengeInfoList = challengeParticipantRepository.findTop10PopularChallenges();
        List<PopularChallengeListItemRes> resultList = new ArrayList<>();

        for(ChallengeInfoProjection challengeInfo : challengeInfoList) {
            CourseSummaryInfo courseSummaryInfo = courseService.getCourseSummaryInfoByCourseName(challengeInfo.courseName());
            resultList.add(PopularChallengeListItemRes.from(challengeInfo, courseSummaryInfo));
        }
        return resultList;
    }

}
