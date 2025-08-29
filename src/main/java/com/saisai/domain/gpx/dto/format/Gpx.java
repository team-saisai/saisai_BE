package com.saisai.domain.gpx.dto.format;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.fasterxml.jackson.dataformat.xml.annotation.JacksonXmlElementWrapper;
import com.fasterxml.jackson.dataformat.xml.annotation.JacksonXmlProperty;
import com.fasterxml.jackson.dataformat.xml.annotation.JacksonXmlRootElement;
import java.util.List;

@JacksonXmlRootElement(localName = "gpx")
@JsonIgnoreProperties(ignoreUnknown = true)
public record Gpx(
    @JacksonXmlProperty(isAttribute = true)
    String creator,

    @JacksonXmlElementWrapper(useWrapping = false)
    @JacksonXmlProperty(localName = "trk")
    List<Track> tracks
) {

}
