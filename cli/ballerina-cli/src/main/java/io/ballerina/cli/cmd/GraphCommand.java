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

import io.ballerina.projects.util.ProjectConstants;
import picocli.CommandLine;

import java.io.PrintStream;
import java.nio.file.Path;
import java.nio.file.Paths;

import static io.ballerina.cli.cmd.Constants.GRAPH_COMMAND;

/**
 * This class represents the "bal graph" command.
 *
 * @since
 */
@CommandLine.Command(name = GRAPH_COMMAND, description = "bal graph - Print the dependency graph of a Ballerina "
        + "project in text form")
public class GraphCommand {
    private final Path projectPath;
    private final PrintStream outStream;
    private final PrintStream errStream;
    private final boolean exitWhenFinish;

    @CommandLine.Option(names = "--dump-raw-graphs", description = "Print the raw dependency graphs in text form.",
            defaultValue = "false")
    private boolean dumpRawGraphs;

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

    }
}