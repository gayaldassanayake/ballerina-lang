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

package io.ballerina.projects.internal;

import io.ballerina.projects.SemanticVersion;
import io.ballerina.projects.internal.indices.FileSystemRepositoryIndex;
import org.ballerinalang.central.client.model.PackageResolutionRequest;
import org.ballerinalang.central.client.model.PackageResolutionResponse;

import java.util.ArrayList;
import java.util.List;

public class DependencyResolver {
    FileSystemRepositoryIndex fileSystemRepositoryIndex;

    public DependencyResolver(FileSystemRepositoryIndex fileSystemRepositoryIndex) {
        this.fileSystemRepositoryIndex = fileSystemRepositoryIndex;
    }

    public void resolveDependencies(PackageResolutionRequest packageResolutionRequest, String supportedPlatform, String ballerinaVersion) {
        // TODO:
        //  1. Need to check the organization info when implementing the private packages feature
        //  2. central uses a redis cache to improve the package resolution speed. Need to look into the possibility
        //  of keeping things in memory to speed things up.
        //  3. Telemetry?
        List<PackageResolutionResponse.Package> resolvedPackages = new ArrayList<>();
        List<PackageResolutionResponse.Package> unresolvedPackages = new ArrayList<>();
        for (PackageResolutionRequest.Package pkg: packageResolutionRequest.getPackages()) {
            // Resolve the dependencies
            resolvedPackages.add(resolvePackage(pkg, supportedPlatform, ballerinaVersion));
            // TODO: if unresolved, add to the unresolvedPackages list
        }
        PackageResolutionResponse response = PackageResolutionResponse.from(resolvedPackages, unresolvedPackages);
    }

    private PackageResolutionResponse.Package resolvePackage(PackageResolutionRequest.Package pkg, String supportedPlatform, String ballerinaVersion) {
        VersionRange versionRange = getVersionRange(pkg.getMode(), pkg.getVersion());
        PackageResolutionResponse.Package pkgResponse = getPackage(pkg.org(), pkg.getName(), versionRange, supportedPlatform, ballerinaVersion);
        // TODO: how is multiple versions of same package get resolved.
    }

    private VersionRange getVersionRange(PackageResolutionRequest.Mode mode, String minVersion) {
        if (minVersion == null || minVersion.isEmpty()) {
            int largestInt = Integer.MAX_VALUE;
            SemanticVersion smallestVersion = SemanticVersion.from("0.0.0");
            SemanticVersion largestVersion = SemanticVersion.from(largestInt + "." + largestInt + "." + largestInt);
            return new VersionRange(smallestVersion, largestVersion);
        }
        SemanticVersion minVersionSemvar = SemanticVersion.from(minVersion);
        if (mode == null) {
            mode = PackageResolutionRequest.Mode.MEDIUM;
        }
        if (mode == PackageResolutionRequest.Mode.HARD) {
            return new VersionRange(minVersionSemvar, minVersionSemvar);
        }
        if (mode == PackageResolutionRequest.Mode.SOFT) {
            SemanticVersion nextMajorVersion = SemanticVersion.from(minVersionSemvar.major() + 1 + ".0.0");
            return new VersionRange(minVersionSemvar, nextMajorVersion);
        }
        return new VersionRange(minVersionSemvar, SemanticVersion.from(minVersionSemvar.major() + "." + (minVersionSemvar.minor() + 1) + ".0"));
    }

    private PackageResolutionResponse.Package getPackage(String org, String name, VersionRange versionRange, String supportedPlatform, String ballerinaVersion) {

    }
}

record VersionRange(SemanticVersion lowerBound, SemanticVersion upperBound) {

}
//enum VersionRange {
//    ALL_RANGE,
//    EXACT_VERSION,
//    MIN_VERSION,
//}

