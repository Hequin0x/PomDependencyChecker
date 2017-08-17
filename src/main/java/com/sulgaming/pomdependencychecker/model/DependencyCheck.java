package com.sulgaming.pomdependencychecker.model;

import javafx.beans.property.SimpleStringProperty;
import javafx.beans.property.StringProperty;

public class DependencyCheck {

    private final StringProperty groupId;
    private final StringProperty artifactId;
    private final StringProperty version;
    private final StringProperty mavenVersion;

    public DependencyCheck(String groupId, String artifactId, String version, String mavenVersion) {
        this.groupId = new SimpleStringProperty(groupId);
        this.artifactId = new SimpleStringProperty(artifactId);
        this.version = new SimpleStringProperty(version);
        this.mavenVersion = new SimpleStringProperty(mavenVersion);
    }

    public String getGroupId() {
        return groupId.get();
    }

    public StringProperty groupIdProperty() {
        return groupId;
    }

    public String getArtifactId() {
        return artifactId.get();
    }

    public StringProperty artifactIdProperty() {
        return artifactId;
    }

    public String getVersion() {
        return version.get();
    }

    public StringProperty versionProperty() {
        return version;
    }

    public String getMavenVersion() {
        return mavenVersion.get();
    }

    public StringProperty mavenVersionProperty() {
        return mavenVersion;
    }
}
