package com.sulgaming.pomdependencychecker;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.sulgaming.pomdependencychecker.model.DependencyCheck;
import com.sulgaming.pomdependencychecker.model.MavenResponseModel;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.concurrent.Service;
import javafx.concurrent.Task;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.scene.control.*;
import javafx.stage.FileChooser;
import org.apache.maven.artifact.versioning.DefaultArtifactVersion;
import org.apache.maven.model.Dependency;
import org.apache.maven.model.Model;
import org.apache.maven.model.io.xpp3.MavenXpp3Reader;
import org.codehaus.plexus.util.xml.pull.XmlPullParserException;

import java.io.File;
import java.io.FileReader;
import java.io.IOException;
import java.net.URL;
import java.util.List;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

public class Controller {

    private ObservableList<DependencyCheck> dependencyChecks = FXCollections.observableArrayList();

    @FXML
    public ProgressBar progressIndicator;
    @FXML
    public TableView<DependencyCheck> dependenciesTable;
    @FXML
    public TableColumn<DependencyCheck, String> groupIdColumn;
    @FXML
    public TableColumn<DependencyCheck, String> artifactIdColumn;
    @FXML
    public TableColumn<DependencyCheck, String> versionColumn;
    @FXML
    public TableColumn<DependencyCheck, String> repoVersionColumn;

    @FXML
    private void initialize() {
        this.dependenciesTable.setItems(dependencyChecks);

        this.groupIdColumn.setCellValueFactory(cellData -> cellData.getValue().groupIdProperty());
        this.artifactIdColumn.setCellValueFactory(cellData -> cellData.getValue().artifactIdProperty());
        this.versionColumn.setCellValueFactory(cellData -> cellData.getValue().versionProperty());
        this.repoVersionColumn.setCellValueFactory(cellData -> cellData.getValue().mavenVersionProperty());

        this.repoVersionColumn.setCellFactory(column -> new TableCell<DependencyCheck, String>() {
            @Override
            protected void updateItem(String item, boolean empty) {
                super.updateItem(item, empty);

                setText(empty ? "" : getItem());
                setGraphic(null);

                TableRow<DependencyCheck> currentRow = getTableRow();

                try {
                    if (isNewVersion(currentRow.getItem().getVersion(), item)) {
                        currentRow.setStyle("-fx-background-color:lightcoral");
                    } else {
                        currentRow.setStyle("-fx-background-color:lightgreen");
                    }
                } catch (Exception ex) { }
            }
        });
    }

    @FXML
    private void handleButtonAction(ActionEvent event) throws IOException, XmlPullParserException {
        this.dependencyChecks.clear();

        FileChooser fileChooser = new FileChooser();
        fileChooser.setTitle("Open Pom File");
        fileChooser.getExtensionFilters().add(new FileChooser.ExtensionFilter("XML Files(*.xml)", "*.xml"));

        File pomFile = fileChooser.showOpenDialog(null);

        if(pomFile != null) {
            final Service<Void> parseService = new Service<Void>() {

                @Override
                protected Task<Void> createTask() {
                    return new Task<Void>() {

                        @Override
                        protected Void call() throws Exception {
                            parsePomFile(pomFile);
                            return null;
                        }
                    };
                }
            };

            parseService.start();
        }
    }

    private void parsePomFile(File pomFile) throws IOException, XmlPullParserException {
        MavenXpp3Reader reader = new MavenXpp3Reader();
        Model model = reader.read(new FileReader(pomFile));

        List<Dependency> dependencies = model.getDependencies();

        int i = 0;

        for(Dependency dependency : dependencies) {
            i++;

            String version = null;
            String mavenVersion = null;

            // local dependency
            if(dependency.getVersion().contains("$")) {
                try {
                    Pattern p = Pattern.compile("\\{(.*?)}");
                    Matcher m = p.matcher(dependency.getVersion());
                    if(m.find()) {
                        version = model.getProperties().getProperty(m.group(1));
                    }
                } catch (Exception ex) {
                    version = "Unknown";
                }
            } else {
                version = dependency.getVersion();
            }

            // maven dependency
            try {
                MavenResponseModel mavenResponseModel = this.getMavenDependency(dependency.getGroupId(), dependency.getArtifactId());
                mavenVersion = mavenResponseModel.content.doc.get(0).latestVersion;
            } catch (Exception ex) {
                mavenVersion = "Unknown";
            }

            this.dependencyChecks.add(new DependencyCheck(dependency.getGroupId(), dependency.getArtifactId(), version, mavenVersion));

            this.progressIndicator.setProgress(((i * 100) / dependencies.size()) / (10 / 0.1));
        }
    }

    private MavenResponseModel getMavenDependency(String groupId, String artifactId) throws IOException {
        ObjectMapper mapper = new ObjectMapper();
        URL mavenUrl = new URL(String.format("http://search.maven.org/solrsearch/select?q=g:%s%%20AND%%20a:%s&rows=1&wt=json", groupId, artifactId));

        return mapper.readValue(mavenUrl, MavenResponseModel.class);
    }

    private boolean isNewVersion(String version1, String version2) {
        DefaultArtifactVersion current = new DefaultArtifactVersion(version1);
        DefaultArtifactVersion testVersion = new DefaultArtifactVersion(version2);

        return current.compareTo(testVersion) < 0;
    }
}
