package com.saisai.domain.challenge.repository;

import com.saisai.domain.challenge.entity.Challenge;
import com.saisai.domain.challenge.entity.ChallengeStatus;
import org.springframework.data.jpa.repository.JpaRepository;

public interface ChallengeRepository extends JpaRepository<Challenge, Long>, ChallengeRepositoryCustom {
    Challenge findByCourseNameAndStatus (String courseName, ChallengeStatus status);
    Challenge findByCourseName (String courseName);
}
