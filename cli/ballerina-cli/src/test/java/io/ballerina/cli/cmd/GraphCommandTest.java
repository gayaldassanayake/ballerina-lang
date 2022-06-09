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

import org.testng.Assert;
import org.testng.annotations.BeforeClass;

import java.io.IOException;
import java.net.URI;
import java.net.URISyntaxException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.Objects;

public class GraphCommandTest extends BaseCommandTest {
    private Path testResources;

    @BeforeClass
    public void setup() throws IOException {
        super.setup();
        try {
            this.testResources = super.tmpDir.resolve("build-test-resources");
            URI testResourcesURI = Objects.requireNonNull(getClass().getClassLoader().getResource("test-resources"))
                    .toURI();
            Path originalTestResources = Paths.get(testResourcesURI);
            Files.walkFileTree(originalTestResources, new BuildCommandTest.Copy(originalTestResources,
                    this.testResources));
        } catch (URISyntaxException e) {
            Assert.fail("error loading resources");
        }
    }

    // @Test(description = "Get the dependency graph of a valid single ballerina file")
    // @Test(description = "Get the dependency graph of a valid ballerina project")
    // valid bala file
    // @Test(description = "Get the dependency graph of a non ballerina file")
    // non existing bal file
    // @Test(description = "Get the dependency graph of a bal file with no entry")
    // @Test(description = "Get the dependency graph of a ballerina file with syntax error")
    // @Test(description = "Get the dependency graph of a ballerina project with syntax error")
    // @Test(description = "Get the dependency graph of a valid ballerina file")
    // @Test(description = "Get the dependency graph with code generation of a single ballerina file")
    // @Test(description = "Get the dependency graph with code generation of a ballerina project")
    // sticky flag
    // offline flag
    // dump-raw-graphs
    // jar conflicts?
    // java11bal project ?
    // get graph from a different dir ?
    // with tests
    // multimodule
    // no write permission??
    // empty package with compiler plugin?
    // empty package with tests only ?
    // java imports??

}