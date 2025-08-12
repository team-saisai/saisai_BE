package com.saisai.domain.gpx.service;

import com.saisai.domain.checkpoint.dto.response.Checkpoint;
import com.saisai.domain.gpx.dto.GpxPoint;
import java.util.List;
import lombok.RequiredArgsConstructor;
import org.springframework.cache.annotation.Cacheable;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
public class GpxCacheService {

    private final MergeGpxSevice mergeGpxSevice;

    @Cacheable(value = "gpxPoints", key = "#courseId", unless = "#result == null")
    public List<GpxPoint> getMergedGpxPoints(Long courseId, List<Checkpoint> checkpoints) {

        return mergeGpxSevice.mergedGpxPoints(courseId, checkpoints);
    }

}
