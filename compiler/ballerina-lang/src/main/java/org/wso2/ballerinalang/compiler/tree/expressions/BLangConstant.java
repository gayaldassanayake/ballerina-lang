/*
 *  Copyright (c) 2018, WSO2 Inc. (http://www.wso2.org) All Rights Reserved.
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
package org.wso2.ballerinalang.compiler.tree.expressions;

import org.ballerinalang.model.elements.Flag;
import org.ballerinalang.model.tree.AnnotatableNode;
import org.ballerinalang.model.tree.AnnotationAttachmentNode;
import org.ballerinalang.model.tree.DeprecatedNode;
import org.ballerinalang.model.tree.DocumentableNode;
import org.ballerinalang.model.tree.IdentifierNode;
import org.ballerinalang.model.tree.MarkdownDocumentationNode;
import org.ballerinalang.model.tree.NodeKind;
import org.ballerinalang.model.tree.TopLevelNode;
import org.ballerinalang.model.tree.types.TypeNode;
import org.wso2.ballerinalang.compiler.semantics.model.symbols.BConstantSymbol;
import org.wso2.ballerinalang.compiler.tree.BLangDeprecatedNode;
import org.wso2.ballerinalang.compiler.tree.BLangIdentifier;
import org.wso2.ballerinalang.compiler.tree.BLangMarkdownDocumentation;
import org.wso2.ballerinalang.compiler.tree.BLangNodeVisitor;
import org.wso2.ballerinalang.compiler.tree.BLangTypeDefinition;
import org.wso2.ballerinalang.compiler.tree.types.BLangType;

import java.util.ArrayList;
import java.util.EnumSet;
import java.util.List;
import java.util.Set;

/**
 * @since 0.985.0
 */
public class BLangConstant extends BLangLiteral implements AnnotatableNode, DocumentableNode, TopLevelNode {


    // Type node defined in the constant definition.
    public BLangType constantTypeNode;
    public BLangTypeDefinition associatedTypeDefinition;


    public BLangIdentifier name;
    public Set<Flag> flagSet;
    public BLangMarkdownDocumentation markdownDocumentationAttachment;
    public List<BLangDeprecatedNode> deprecatedAttachments;
    public BConstantSymbol symbol;

    public BLangConstant() {
        this.flagSet = EnumSet.noneOf(Flag.class);
        this.deprecatedAttachments = new ArrayList<>();
    }



    public void setConstantTypeNode(TypeNode constantTypeNode) {
        this.constantTypeNode  = (BLangType)constantTypeNode;
    }

    public IdentifierNode getName() {
        return name;
    }

    public void setName(IdentifierNode name) {
        this.name = (BLangIdentifier) name;
    }

    @Override
    public NodeKind getKind() {
        return NodeKind.CONSTANT;
    }

    @Override
    public Set<? extends Flag> getFlags() {
        return null;
    }

    @Override
    public void addFlag(Flag flag) {
        flagSet.add(flag);
    }

    @Override
    public List<? extends AnnotationAttachmentNode> getAnnotationAttachments() {
        // Todo
        return null;
    }

    @Override
    public void addAnnotationAttachment(AnnotationAttachmentNode annAttachment) {
        // Todo
    }

    @Override
    public BLangMarkdownDocumentation getMarkdownDocumentationAttachment() {
        return null;
    }

    @Override
    public void setMarkdownDocumentationAttachment(MarkdownDocumentationNode documentationNode) {
        this.markdownDocumentationAttachment = (BLangMarkdownDocumentation) documentationNode;
    }

    @Override
    public List<? extends DeprecatedNode> getDeprecatedAttachments() {
        return deprecatedAttachments;
    }

    @Override
    public void addDeprecatedAttachment(DeprecatedNode deprecatedAttachment) {
        this.deprecatedAttachments.add((BLangDeprecatedNode) deprecatedAttachment);
    }

    @Override
    public void accept(BLangNodeVisitor visitor) {
        visitor.visit(this);
    }
}
