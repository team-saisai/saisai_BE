package com.saisai.domain.theme.repository;

import com.saisai.domain.theme.entity.Theme;
import java.util.Collections;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

public interface ThemeRepository extends JpaRepository<Theme, Long> {

    // 코스IDs에 해당하는 테마 조회
    @Query("SELECT tc.course.id, t.name " +
        "FROM ThemeCourse tc " +
        "JOIN tc.theme t " +
        "WHERE tc.course.id IN :courseIds"
    )
    List<Object[]> findThemeNamesByCourseIds(@Param("courseIds") List<Long> courseIds);

    default Map<Long, List<String>> findThemeNamesMapByCourseIds(List<Long> courseIds) {
        if (courseIds == null || courseIds.isEmpty()) {
            return Collections.emptyMap();
        }
        return findThemeNamesByCourseIds(courseIds).stream()
            .collect(Collectors.groupingBy(
                arr -> (Long) arr[0],
                Collectors.mapping(arr -> (String) arr[1], Collectors.toList())
            ));
    }

    //코스Id에 해당하는 테마 조회
    @Query("SELECT t.name " +
        "FROM ThemeCourse tc " +
        "JOIN tc.theme t " +
        "WHERE tc.course.id = :courseId"
    )
    List<String> findThemeNamesByCourseId(@Param("courseId") Long courseId);
}
