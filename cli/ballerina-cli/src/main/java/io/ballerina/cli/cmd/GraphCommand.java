/*
 *  Copyright (c) 2019, WSO2 Inc. (http://www.wso2.org) All Rights Reserved.
 *
 *  WSO2 Inc. licenses this file to you under the Apache License,
 *  Version 2.0 (the "License"); you may not use this file except
 *  in compliance with the License.
 *  You may obtain a copy of the License at
 *
 *    http://www.apache.org/licenses/LICENSE-2.0
 *
 *  Unless required by applicable law or agreed to in writing,
 *  software distributed under the License is distributed on an
 *  "AS IS" BASIS, WITHOUT WARRANTIES OR CONDITIONS OF ANY
 *  KIND, either express or implied.  See the License for the
 *  specific language governing permissions and limitations
 *  under the License.
 */

package io.ballerina.cli.cmd;

import io.ballerina.cli.BLauncherCmd;
import io.ballerina.cli.utils.ProjectUtils;
import io.ballerina.projects.BuildOptions;
import io.ballerina.projects.Project;
import io.ballerina.projects.ProjectException;
import io.ballerina.projects.directory.BuildProject;
import io.ballerina.projects.directory.SingleFileProject;
import io.ballerina.projects.util.ProjectConstants;
import org.ballerinalang.toml.exceptions.SettingsTomlException;
import org.wso2.ballerinalang.util.RepoUtils;
import picocli.CommandLine;

import java.io.PrintStream;
import java.nio.file.Path;
import java.nio.file.Paths;

import static io.ballerina.cli.cmd.Constants.GRAPH_COMMAND;
import static org.wso2.ballerinalang.util.RepoUtils.isBallerinaStandaloneFile;

/**
 * This class represents the "bal graph" command.
 *
 * @since 2201.2.0
 */
// TODO: change the bal graph message in all places.
@CommandLine.Command(name = GRAPH_COMMAND, description = "bal graph - Print the dependency graph of a Ballerina "
        + "project in DOT graph description language")
public class GraphCommand {
    private final PrintStream outStream;
    private final PrintStream errStream;
    private final boolean exitWhenFinish;
    @CommandLine.Parameters(arity = "0..1")
    private final Path projectPath;
    @CommandLine.Option(names = "--dump-raw-graphs", description = "Print the raw dependency graphs",
            defaultValue = "false")
    private boolean dumpRawGraphs;
    @CommandLine.Option(names = {"--help", "-h"}, hidden = true, defaultValue = "false")
    private boolean helpFlag;
    @CommandLine.Option(names = {"--offline"}, description = "Print the dependency graph offline without downloading " +
            "dependencies.", defaultValue = "false")
    private boolean offline;
    @CommandLine.Option(names = "--sticky", description = "stick to exact versions locked (if exists)",
            defaultValue = "false")
    private boolean sticky;

    public GraphCommand() {
        this.projectPath = Paths.get(System.getProperty(ProjectConstants.USER_DIR));
        this.outStream = System.out;
        this.errStream = System.err;
        this.exitWhenFinish = true;
    }

    GraphCommand(Path projectPath, PrintStream outStream, PrintStream errStream, boolean exitWhenFinish) {
        this.projectPath = projectPath;
        this.outStream = outStream;
        this.errStream = errStream;
        this.exitWhenFinish = exitWhenFinish;
        this.offline = true;
    }

    public void execute() {
        if (this.helpFlag) {
            String commandUsageInfo = BLauncherCmd.getCommandUsageInfo(GRAPH_COMMAND);
            this.errStream.println(commandUsageInfo);
            return;
        }

        Project project;
        BuildOptions buildOptions = constructBuildOptions();

        boolean isSingleFileProject = isBallerinaStandaloneFile(this.projectPath);
        if (isSingleFileProject) {
            try {
                project = SingleFileProject.load(this.projectPath, buildOptions);
            } catch (ProjectException e) {
                CommandUtil.printError(this.errStream, e.getMessage(), null, false);
                CommandUtil.exitError(this.exitWhenFinish);
                return;
            }
        } else {
            try {
                project = BuildProject.load(this.projectPath, buildOptions);
            } catch (ProjectException e) {
                CommandUtil.printError(this.errStream, e.getMessage(), null, false);
                CommandUtil.exitError(this.exitWhenFinish);
                return;
            }
        }

        // If project is empty
        if (ProjectUtils.isProjectEmpty(project)) {
            CommandUtil.printError(this.errStream, "package is empty. Please add at least one .bal file.", null,
                    false);
            CommandUtil.exitError(this.exitWhenFinish);
            return;
        }

        // Validate Settings.toml file
        try {
            RepoUtils.readSettings();
        } catch (SettingsTomlException e) {
            this.outStream.println("warning: " + e.getMessage());
        }

        // TODO: taskbuilder


        if (this.exitWhenFinish) {
            Runtime.getRuntime().exit(0);
        }
    }

    private BuildOptions constructBuildOptions() {
        BuildOptions.BuildOptionsBuilder buildOptionsBuilder = BuildOptions.builder();

        buildOptionsBuilder
                .setDumpRawGraphs(this.dumpRawGraphs)
                .setOffline(this.offline)
                .setSticky(sticky);

        return buildOptionsBuilder.build();
    }
}
