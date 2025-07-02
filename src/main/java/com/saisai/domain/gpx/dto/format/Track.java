package com.saisai.domain.gpx.dto.format;

import com.fasterxml.jackson.annotation.JsonAlias;
import com.fasterxml.jackson.dataformat.xml.annotation.JacksonXmlElementWrapper;
import com.fasterxml.jackson.dataformat.xml.annotation.JacksonXmlProperty;
import java.util.List;

public record Track(
    @JacksonXmlProperty(localName = "name")
    String name,

    @JacksonXmlElementWrapper(useWrapping = false)
    @JacksonXmlProperty(localName = "trkseg")
    @JsonAlias({"trkseq", "trkseg"}) // 파일마다 태그명이 다른거 방지
    List<TrackSegment> trackSegments
) {

}
