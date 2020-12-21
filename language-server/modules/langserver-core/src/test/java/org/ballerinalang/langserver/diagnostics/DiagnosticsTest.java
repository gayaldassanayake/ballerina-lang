/*
 *  Copyright (c) 2017, WSO2 Inc. (http://www.wso2.org) All Rights Reserved.
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
package org.ballerinalang.langserver.diagnostics;

import com.google.gson.Gson;
import com.google.gson.JsonObject;
import com.google.gson.JsonParser;
import org.ballerinalang.langserver.LSContextOperation;
import org.ballerinalang.langserver.common.utils.CommonUtil;
import org.ballerinalang.langserver.commons.DocumentServiceContext;
import org.ballerinalang.langserver.contexts.ContextBuilder;
import org.ballerinalang.langserver.diagnostic.DiagnosticsHelper;
import org.ballerinalang.langserver.util.FileUtils;
import org.ballerinalang.langserver.util.TestUtil;
import org.ballerinalang.langserver.workspace.BallerinaWorkspaceManager;
import org.eclipse.lsp4j.Diagnostic;
import org.eclipse.lsp4j.jsonrpc.Endpoint;
import org.testng.Assert;
import org.testng.annotations.AfterClass;
import org.testng.annotations.BeforeClass;
import org.testng.annotations.DataProvider;
import org.testng.annotations.Test;

import java.io.File;
import java.io.IOException;
import java.nio.file.InvalidPathException;
import java.nio.file.Path;
import java.util.List;
import java.util.Map;
import java.util.Optional;

/**
 * Diagnostics tests are covered here.
 *
 * @since 2.0.0
 */
public class DiagnosticsTest {

    private Endpoint serviceEndpoint;

    private final Path testRoot = FileUtils.RES_DIR.resolve("diagnostics");

    private final JsonParser parser = new JsonParser();

    private final Gson gson = new Gson();

    private final BallerinaWorkspaceManager workspaceManager = new BallerinaWorkspaceManager();

    @BeforeClass
    public void init() {
        this.serviceEndpoint = TestUtil.initializeLanguageSever();
    }

    @Test(dataProvider = "completion-data-provider")
    public void test(String config) throws IOException {
        String configDir = "configs";
        String configJsonPath = "diagnostics" + File.separator + configDir + File.separator + config + ".json";
        JsonObject configJsonObject = FileUtils.fileContentAsObject(configJsonPath);

        String response = this.getResponse(configJsonObject);
        JsonObject responseJson = parser.parse(response).getAsJsonObject();
        JsonObject responseDiags = unifyResponse(responseJson);
        JsonObject expectedDiags = configJsonObject.get("items").getAsJsonObject();

        boolean result = responseDiags.equals(expectedDiags);
        if (!result) {
            // Fix test cases replacing expected using responses
//            JsonObject obj = new JsonObject();
//            obj.add("source", configJsonObject.get("source"));
//            obj.add("items", responseDiags);
//            java.nio.file.Files.write(FileUtils.RES_DIR.resolve(configJsonPath),
//                                      obj.toString().getBytes(java.nio.charset.StandardCharsets.UTF_8));
////
////             //This will print nice comparable text in IDE
//            Assert.assertEquals(responseDiags.toString(), expectedDiags.toString(),
//                    "Failed Test for: " + configJsonPath);
            Assert.fail("Failed Test for: " + configJsonPath);
        }
    }

    String getResponse(JsonObject configJsonObject) {
        Path sourcePath = testRoot.resolve(configJsonObject.get("source").getAsString());
        DocumentServiceContext serviceContext = ContextBuilder.buildBaseContext(sourcePath.toUri().toString(),
                this.workspaceManager, LSContextOperation.TXT_DID_OPEN);
        workspaceManager.didOpen(sourcePath, null);
        Map<String, List<Diagnostic>> diagnostics = DiagnosticsHelper.getInstance()
                .getBallerinaDiagnostics(serviceContext);

        return gson.toJson(diagnostics).replace("\r\n", "\n").replace("\\r\\n", "\\n");
    }

    JsonObject unifyResponse(JsonObject response) {
        JsonObject unifiedJson = new JsonObject();
        for (String key : response.keySet()) {
            Optional<Path> path = CommonUtil.getPathFromURI(key);
            if (path.isEmpty()) {
                throw new InvalidPathException("Invalid path found", key);
            }
            Path relativeKey = testRoot.resolve("sources").relativize(path.get());
            unifiedJson.add(relativeKey.toString(), response.get(key));
        }

        return unifiedJson;
    }

    @DataProvider(name = "completion-data-provider")
    public Object[] dataProvider() {
        return new Object[]{/*"project_diagnostics1",*/ "single_file_diagnostics1"};
    }

    @AfterClass
    public void cleanupLanguageServer() {
        TestUtil.shutdownLanguageServer(this.serviceEndpoint);
    }
}
