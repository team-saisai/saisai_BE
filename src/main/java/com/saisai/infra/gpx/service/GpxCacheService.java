package com.saisai.infra.gpx.service;

import com.saisai.infra.checkpoint.dto.response.Checkpoint;
import com.saisai.infra.gpx.client.GpxS3;
import com.saisai.infra.gpx.dto.GpxPoint;
import java.util.List;
import lombok.RequiredArgsConstructor;
import org.springframework.cache.annotation.Cacheable;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
public class GpxCacheService {

    private final MergeGpxSevice mergeGpxSevice;
    private final GpxS3 gpxS3;
    private final GpxParser gpxParser;

    @Cacheable(value = "gpxPoints", key = "#courseId", unless = "#result == null")
    public List<GpxPoint> getMergedGpxPoints(Long courseId, List<Checkpoint> checkpoints) {

        return mergeGpxSevice.mergedGpxPoints(courseId, checkpoints);
    }

    @Cacheable(value = "gpxPoints", key = "#courseId", unless = "#result == null")
    public List<GpxPoint> getGpxPoints(Long courseId, String gpxKey) {
        String gpxContent = gpxS3.getGpxContent(gpxKey);
        return gpxParser.parseCustomGpxFile(gpxContent);
    }

}
