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
import io.ballerina.projects.internal.repositories.FileSystemRepository;
import org.ballerinalang.central.client.model.PackageResolutionRequest;
import org.ballerinalang.central.client.model.PackageResolutionResponse;
import org.ballerinalang.central.client.model.ToolResolutionCentralResponse;
import org.eclipse.jgit.api.CreateBranchCommand;
import org.eclipse.jgit.api.Git;
import org.eclipse.jgit.api.errors.GitAPIException;
import org.eclipse.jgit.transport.UsernamePasswordCredentialsProvider;

import java.io.File;
import java.io.IOException;
import java.io.PrintStream;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.Arrays;

import static org.wso2.ballerinalang.util.RepoUtils.SET_BALLERINA_DEV_CENTRAL;
import static org.wso2.ballerinalang.util.RepoUtils.SET_BALLERINA_STAGE_CENTRAL;

public class FileSystemRepositoryIndex implements PackageIndex {
    private static final String INDEX_DIR = "index"; // TODO: move somewhere better
    private static final String INDEX_GIT_REPO = "ballerina-index-test";
    private static final String INDEX_GIT_REPO_URI = "https://github.com/gayaldassanayake/ballerina-index-test.git";

    FileSystemRepository fileSystemRepository;
    PrintStream outStream;
    Path gitRepo;

    public FileSystemRepositoryIndex(FileSystemRepository fileSystemRepository) {
        outStream = System.out;
        this.fileSystemRepository = fileSystemRepository;
        gitRepo = fileSystemRepository.cacheDirectory().resolve(INDEX_DIR).resolve(INDEX_GIT_REPO);
    }

    @Override
    public PackageResolutionResponse resolveDependencies(PackageResolutionRequest request, String supportedPlatform, String ballerinaVersion) {
        return null;
    }

    @Override
    public ToolResolutionCentralResponse resolveToolDependencies(PackageResolutionRequest request, String supportedPlatform, String ballerinaVersion) {
        return null;
    }

    public void getPackage() {
        // TODO: update only if not sticky
        update();
        getPackageData();
    }

    public void update() { // TODO: should be private
        // TODO: fetch and update the index from the central iff !offline && !sticky
        if (!indexFetched()) { // TODO: make sure this is not executed in compile time
            fetchIndex();
        } else {
            outStream.println("Package index is already fetched.");
        }
        // TODO: checkout to the correct branch (main/ dev/ stage)
        checkoutToBranch();
        fetchIndexHead();
    }


    private void getPackageData(String org, String name, Strin) {
        gitRepo.resolve()
    }

    //    private <Optional> findPackageVersion(PackageResolutionRequest.Package package) {
//
//    }
//
//    private void find
    private boolean indexFetched() {
        outStream.println("File system git repo path: " + fileSystemRepository.cacheDirectory().resolve(INDEX_DIR).resolve(INDEX_GIT_REPO));
        if (!gitRepo.toFile().isDirectory()) {
            return false;
        }
        File gitDir = new File(gitRepo.toFile(), ".git");
        return gitDir.isDirectory();
    }

    private void fetchIndex() {
        try {
            Files.createDirectories(fileSystemRepository.cacheDirectory().resolve(INDEX_DIR));
        } catch (IOException e) {
            throw new RuntimeException("Error while creating the package index directory: " + e.getMessage());
        }

        outStream.println("Cloning the package index repository..."); // TODO: make it a progress bar if possible

        try (Git git = Git.cloneRepository()
                .setURI(INDEX_GIT_REPO_URI)
                .setDirectory(fileSystemRepository.cacheDirectory().resolve(INDEX_DIR).resolve(INDEX_GIT_REPO).toFile())
                .setBranchesToClone(Arrays.asList("refs/heads/prod", "refs/heads/dev", "refs/heads/stage"))
                .setCredentialsProvider(
                        new UsernamePasswordCredentialsProvider(
                                System.getenv("packageUser"),
                                System.getenv("packagePAT"))) // TODO: remove once the index git repo is public
                .call()) {
            git.checkout().setCreateBranch(true).setUpstreamMode(CreateBranchCommand.SetupUpstreamMode.TRACK).setStartPoint("origin/prod").setName("prod").call();
            git.checkout().setCreateBranch(true).setUpstreamMode(CreateBranchCommand.SetupUpstreamMode.TRACK).setStartPoint("origin/dev").setName("dev").call();
            git.checkout().setCreateBranch(true).setUpstreamMode(CreateBranchCommand.SetupUpstreamMode.TRACK).setStartPoint("origin/stage").setName("stage").call();
        } catch (GitAPIException e) {
            throw new RuntimeException("Error while cloning the package index repository: " + e.getMessage(), e);
        }
    }

    private void checkoutToBranch() {
        Branch branch = Branch.PROD;
        if (SET_BALLERINA_STAGE_CENTRAL) {
            branch = Branch.STAGE;
        } else if (SET_BALLERINA_DEV_CENTRAL) {
            branch = Branch.DEV;
        }
        outStream.println("Checking out to the " + branch.branchName() + " branch...");
        try (Git git = Git.open(gitRepo.toFile())) {
            git.checkout().setName(branch.branchName()).call();
        } catch (IOException | GitAPIException e) {
            throw new RuntimeException("Error while checking out to the main branch: " + e.getMessage(), e);
        }
    }

    private void fetchIndexHead() {
        outStream.println("Checking out to the head");
        try (Git git = Git.open(gitRepo.toFile())) {
            git.pull().setCredentialsProvider(
                    new UsernamePasswordCredentialsProvider(
                            System.getenv("packageUser"),
                            System.getenv("packagePAT"))).call(); // TODO: remove once the index git repo is public
        } catch (IOException | GitAPIException e) {
            throw new RuntimeException("Error while pulling the latest changes from the upstream: " + e.getMessage(), e);
        }
    }

    enum Branch {
        PROD("prod"), // TODO: deside on the name. prod/ master/ main
        DEV("dev"),
        STAGE("stage");

        private final String branchName;

        Branch(String branchName) {
            this.branchName = branchName;
        }

        public String branchName() {
            return branchName;
        }
    }
}
