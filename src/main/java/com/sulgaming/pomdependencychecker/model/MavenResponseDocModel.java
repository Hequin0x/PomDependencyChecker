package com.sulgaming.pomdependencychecker.model;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;

@JsonIgnoreProperties(ignoreUnknown = true)
public class MavenResponseDocModel {
    public String latestVersion;
}
