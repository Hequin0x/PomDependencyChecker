package com.sulgaming.pomdependencychecker.model;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.fasterxml.jackson.annotation.JsonProperty;

@JsonIgnoreProperties(ignoreUnknown = true)
public class MavenResponseModel {
    @JsonProperty("response")
    public MavenResponseContentModel content;
}
