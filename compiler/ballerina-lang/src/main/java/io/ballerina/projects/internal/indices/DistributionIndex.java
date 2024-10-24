/*
 * Copyright (c) 2024, WSO2 LLC. (https://www.wso2.com).
 *
 * WSO2 LLC. licenses this file to you under the Apache License,
 * Version 2.0 (the "License"); you may not use this file except
 * in compliance with the License.
 * You may obtain a copy of the License at
 *
 *     http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing,
 * software distributed under the License is distributed on an
 * "AS IS" BASIS, WITHOUT WARRANTIES OR CONDITIONS OF ANY
 * KIND, either express or implied. See the License for the
 * specific language governing permissions and limitations
 * under the License.
 */

package io.ballerina.projects.internal.indices;

import io.ballerina.projects.environment.PackageIndex;
import org.ballerinalang.central.client.model.PackageResolutionRequest;
import org.ballerinalang.central.client.model.PackageResolutionResponse;
import org.ballerinalang.central.client.model.ToolResolutionCentralResponse;

public class DistributionIndex implements PackageIndex {

    @Override
    public PackageResolutionResponse resolveDependencies(PackageResolutionRequest request, String supportedPlatform, String ballerinaVersion) {
        return null;
    }

    @Override
    public ToolResolutionCentralResponse resolveToolDependencies(PackageResolutionRequest request, String supportedPlatform, String ballerinaVersion) {
        return null;
    }
}
