package com.sulgaming.pomdependencychecker.model;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.fasterxml.jackson.annotation.JsonProperty;

import java.util.List;

@JsonIgnoreProperties(ignoreUnknown = true)
public class MavenResponseContentModel {
    @JsonProperty("docs")
    public List<MavenResponseDocModel> doc;
}
