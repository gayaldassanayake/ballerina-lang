/*
 * Copyright (c) 2017, WSO2 Inc. (http://www.wso2.org) All Rights Reserved.
 *
 * WSO2 Inc. licenses this file to you under the Apache License,
 * Version 2.0 (the "License"); you may not use this file except
 * in compliance with the License.
 * You may obtain a copy of the License at
 *
 *    http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing,
 * software distributed under the License is distributed on an
 * "AS IS" BASIS, WITHOUT WARRANTIES OR CONDITIONS OF ANY
 * KIND, either express or implied.  See the License for the
 * specific language governing permissions and limitations
 * under the License.
 */
package org.ballerinax.jdbc.actions;

import org.ballerinalang.bre.Context;
import org.ballerinalang.bre.bvm.BlockingNativeCallableUnit;
import org.ballerinalang.jvm.Strand;
import org.ballerinalang.jvm.values.ArrayValue;
import org.ballerinalang.jvm.values.ObjectValue;
import org.ballerinalang.model.types.TypeKind;
import org.ballerinalang.natives.annotations.Argument;
import org.ballerinalang.natives.annotations.BallerinaFunction;
import org.ballerinalang.natives.annotations.ReturnType;
import org.ballerinax.jdbc.Constants;
import org.ballerinax.jdbc.SQLDatasource;
import org.ballerinax.jdbc.statement.SQLStatement;
import org.ballerinax.jdbc.statement.UpdateStatement;

import static org.ballerinalang.util.BLangConstants.BALLERINA_BUILTIN_PKG;
import static org.ballerinax.jdbc.Constants.JDBC_PACKAGE_PATH;

/**
 * {@code Update} is the Update remote function implementation of the JDBC Connector.
 *
 * @since 0.8.0
 */
@BallerinaFunction(
        orgName = "ballerinax", packageName = "jdbc",
        functionName = "nativeUpdate",
        args = {
                @Argument(name = "sqlQuery", type = TypeKind.STRING),
                @Argument(name = "keyColumns", type = TypeKind.ARRAY, elementType = TypeKind.STRING),
                @Argument(name = "parameters", type = TypeKind.ARRAY, elementType = TypeKind.UNION,
                          structType = "Param")
        },
        returnType = {
                @ReturnType(type = TypeKind.RECORD, structType = "Result", structPackage = JDBC_PACKAGE_PATH),
                @ReturnType(type = TypeKind.RECORD, structType = "JdbcClientError",
                        structPackage = BALLERINA_BUILTIN_PKG)
        }
)
public class Update extends BlockingNativeCallableUnit {

    @Override
    public void execute(Context context) {
        //TODO: #16033
    }

    public static Object nativeUpdate(Strand strand, ObjectValue client, String query, Object keyColumns,
            ArrayValue parameters) {
        SQLDatasource sqlDatasource = (SQLDatasource) client.getNativeData(Constants.JDBC_CLIENT);
        SQLStatement updateStatement = new UpdateStatement(client, sqlDatasource, query, (ArrayValue) keyColumns,
                parameters);
        return updateStatement.execute();
    }
}
