/*
 *  Copyright (c) 2020, WSO2 Inc. (http://www.wso2.org) All Rights Reserved.
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
package io.ballerinalang.compiler.syntax.tree;

import io.ballerinalang.compiler.internal.parser.tree.STNode;

/**
 * This is a generated syntax tree node.
 *
 * @since 1.3.0
 */
public class ExternalFunctionBodyNode extends FunctionBodyNode {

    public ExternalFunctionBodyNode(STNode internalNode, int position, NonTerminalNode parent) {
        super(internalNode, position, parent);
    }

    public Token equalsToken() {
        return childInBucket(0);
    }

    public NodeList<AnnotationNode> annotations() {
        return new NodeList<>(childInBucket(1));
    }

    public Token externalKeyword() {
        return childInBucket(2);
    }

    public Token semicolonToken() {
        return childInBucket(3);
    }

    @Override
    public void accept(NodeVisitor visitor) {
        visitor.visit(this);
    }

    @Override
    public <T> T apply(NodeTransformer<T> visitor) {
        return visitor.transform(this);
    }

    @Override
    protected String[] childNames() {
        return new String[]{
                "equalsToken",
                "annotations",
                "externalKeyword",
                "semicolonToken"};
    }

    public ExternalFunctionBodyNode modify(
            Token equalsToken,
            NodeList<AnnotationNode> annotations,
            Token externalKeyword,
            Token semicolonToken) {
        if (checkForReferenceEquality(
                equalsToken,
                annotations.underlyingListNode(),
                externalKeyword,
                semicolonToken)) {
            return this;
        }

        return NodeFactory.createExternalFunctionBodyNode(
                equalsToken,
                annotations,
                externalKeyword,
                semicolonToken);
    }
}
