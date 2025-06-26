package com.saisai.domain.gpx.dto.format;

import com.fasterxml.jackson.dataformat.xml.annotation.JacksonXmlProperty;

public record TrackPoint(
    @JacksonXmlProperty(isAttribute = true)
    Double lat,

    @JacksonXmlProperty(isAttribute = true)
    Double lon,

    @JacksonXmlProperty(isAttribute = true)
    Double ele
) {

}
