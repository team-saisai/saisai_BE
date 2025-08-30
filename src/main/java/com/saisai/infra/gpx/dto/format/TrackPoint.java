package com.saisai.infra.gpx.dto.format;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.fasterxml.jackson.dataformat.xml.annotation.JacksonXmlProperty;

@JsonIgnoreProperties(ignoreUnknown = true)
public record TrackPoint(
    @JacksonXmlProperty(isAttribute = true)
    Double lat,

    @JacksonXmlProperty(isAttribute = true)
    Double lon,

    @JacksonXmlProperty(isAttribute = true)
    Double ele
) {

}
