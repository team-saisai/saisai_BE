package com.saisai.domain.gpx.dto.format;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.fasterxml.jackson.dataformat.xml.annotation.JacksonXmlElementWrapper;
import com.fasterxml.jackson.dataformat.xml.annotation.JacksonXmlProperty;
import java.util.List;

@JsonIgnoreProperties(ignoreUnknown = true)
public record TrackSegment(
    @JacksonXmlProperty(localName = "trkpt")
    @JacksonXmlElementWrapper(useWrapping = false)
    List<TrackPoint> trackPoints
) {

}
