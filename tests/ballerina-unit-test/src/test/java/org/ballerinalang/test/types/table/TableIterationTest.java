/*
 *  Copyright (c) 2018, WSO2 Inc. (http://www.wso2.org) All Rights Reserved.
 *
 *  WSO2 Inc. licenses this file to you under the Apache License,
 *  Version 2.0 (the "License"); you may not use this file except
 *  in compliance with the License.
 *  You may obtain a copy of the License at
 *
 *  http://www.apache.org/licenses/LICENSE-2.0
 *
 *  Unless required by applicable law or agreed to in writing, software
 *  distributed under the License is distributed on an "AS IS" BASIS,
 *  WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 *  See the License for the specific language governing permissions and
 *  limitations under the License.
 */
package org.ballerinalang.test.types.table;

import org.ballerinalang.launcher.util.BAssertUtil;
import org.ballerinalang.launcher.util.BCompileUtil;
import org.ballerinalang.launcher.util.BRunUtil;
import org.ballerinalang.launcher.util.CompileResult;
import org.ballerinalang.model.values.BFloat;
import org.ballerinalang.model.values.BInteger;
import org.ballerinalang.model.values.BStringArray;
import org.ballerinalang.model.values.BValue;
import org.ballerinalang.test.utils.SQLDBUtils.TestDatabase;
import org.testng.Assert;
import org.testng.annotations.AfterSuite;
import org.testng.annotations.BeforeClass;
import org.testng.annotations.Test;

import static org.ballerinalang.test.utils.SQLDBUtils.DBType;
import static org.ballerinalang.test.utils.SQLDBUtils.DB_DIRECTORY;
import static org.ballerinalang.test.utils.SQLDBUtils.FileBasedTestDatabase;

/**
 * Class to test table iteration functionality.
 */
public class TableIterationTest {

    private CompileResult result;
    private CompileResult resultNegative;
    private static final String DB_NAME = "TEST_DATA_TABLE__ITR_DB";
    private TestDatabase testDatabase;

    @BeforeClass
    public void setup() {
        result = BCompileUtil.compile("test-src/types/table/table_iteration.bal");
        resultNegative = BCompileUtil.compile("test-src/types/table/table_iteration_negative.bal");
        testDatabase = new FileBasedTestDatabase(DBType.H2,
                "datafiles/sql/TableIterationTestData.sql", DB_DIRECTORY, DB_NAME);
    }

    @Test(groups = "TableIterationTest", description = "Negative tests for select operation")
    public void testNegative() {
        BAssertUtil.validateError(resultNegative, 0, "incompatible types: expected 'int', found 'float'", 40, 30);
        BAssertUtil.validateError(resultNegative, 1,
                                  "incompatible types: expected 'float', found 'int'", 45, 42);
        BAssertUtil.validateError(resultNegative, 2,
                                  "incompatible lambda function types: expected 'EmployeeIncompatible', found " +
                                          "'Employee'", 71, 41);
        BAssertUtil.validateError(resultNegative, 3,
                                  "incompatible types: expected 'EmployeeSalary', found '" +
                                          "(EmployeeSalaryIncompatible) collection'", 78, 41);
        BAssertUtil.validateError(resultNegative, 4,
                                  "incompatible lambda function types: expected 'EmployeeIncompatible', found " +
                                          "'Employee'", 85, 41);
    }

    @Test(groups = "TableIterationTest", description = "Check accessing data using foreach iteration")
    public void testForEachInTableWithStmt() {
        BValue[] returns = BRunUtil.invoke(result, "testForEachInTableWithStmt");
        Assert.assertEquals(returns.length, 4);
        Assert.assertEquals(((BInteger) returns[0]).intValue(), 1);
        Assert.assertEquals(((BInteger) returns[1]).intValue(), 25);
        Assert.assertEquals(((BFloat) returns[2]).floatValue(), 400.25);
        Assert.assertEquals(returns[3].stringValue(), "John");
    }

    @Test(groups = "TableIterationTest", description = "Check accessing data using foreach iteration")
    public void testForEachInTableWithIndex() {
        BValue[] returns = BRunUtil.invoke(result, "testForEachInTableWithIndex");
        Assert.assertEquals(returns.length, 2);
        Assert.assertEquals(returns[0].stringValue(), ",1,2,3");
        Assert.assertEquals(returns[1].stringValue(), ",0,1,2");
    }

    @Test(groups = "TableIterationTest", description = "Check accessing data using foreach iteration")
    public void testForEachInTable() {
        BValue[] returns = BRunUtil.invoke(result, "testForEachInTable");
        Assert.assertEquals(returns.length, 4);
        Assert.assertEquals(((BInteger) returns[0]).intValue(), 1);
        Assert.assertEquals(((BInteger) returns[1]).intValue(), 25);
        Assert.assertEquals(((BFloat) returns[2]).floatValue(), 400.25);
        Assert.assertEquals(returns[3].stringValue(), "John");
    }

    @Test(groups = "TableIterationTest", description = "Check count operation function on table")
    public void testCountInTable() {
        BValue[] returns = BRunUtil.invoke(result, "testCountInTable");
        Assert.assertEquals(returns.length, 1);
        Assert.assertEquals(((BInteger) returns[0]).intValue(), 3);
    }

    @Test(groups = "TableIterationTest", description = "Check filter operation")
    public void testFilterTable() {
        BValue[] returns = BRunUtil.invoke(result, "testFilterTable");
        Assert.assertEquals(returns.length, 3);
        Assert.assertEquals(((BInteger) returns[0]).intValue(), 2);
        Assert.assertEquals(((BInteger) returns[1]).intValue(), 1);
        Assert.assertEquals(((BInteger) returns[2]).intValue(), 10);
    }

    @Test(groups = "TableIterationTest", description = "Check filter operation")
    public void testFilterWithAnonymousFuncOnTable() {
        BValue[] returns = BRunUtil.invoke(result, "testFilterWithAnonymousFuncOnTable");
        Assert.assertEquals(returns.length, 3);
        Assert.assertEquals(((BInteger) returns[0]).intValue(), 2);
        Assert.assertEquals(((BInteger) returns[1]).intValue(), 1);
        Assert.assertEquals(((BInteger) returns[2]).intValue(), 10);
    }

    @Test(groups = "TableIterationTest", description = "Check filter and count operation")
    public void testFilterTableWithCount() {
        BValue[] returns = BRunUtil.invoke(result, "testFilterTableWithCount");
        Assert.assertEquals(returns.length, 1);
        Assert.assertEquals(((BInteger) returns[0]).intValue(), 2);
    }

    @Test(groups = "TableIterationTest", description = "Check accessing data using foreach iteration")
    public void testMapTable() {
        BValue[] returns = BRunUtil.invoke(result, "testMapTable");
        Assert.assertEquals(returns.length, 1);
        Assert.assertEquals(((BStringArray) returns[0]).get(0), "John");
        Assert.assertEquals(((BStringArray) returns[0]).get(1), "Anne");
        Assert.assertEquals(((BStringArray) returns[0]).get(2), "Mary");
        Assert.assertEquals(((BStringArray) returns[0]).get(3), "Peter");
    }

    @Test(groups = "TableIterationTest", description = "Check map with filter operation")
    public void testMapWithFilterTable() {
        BValue[] returns = BRunUtil.invoke(result, "testMapWithFilterTable");
        Assert.assertEquals(returns.length, 1);
        Assert.assertEquals(((BStringArray) returns[0]).get(0), "Peter");
    }

    @Test(groups = "TableIterationTest", description = "Check filter with map operation")
    public void testFilterWithMapTable() {
        BValue[] returns = BRunUtil.invoke(result, "testFilterWithMapTable");
        Assert.assertEquals(returns.length, 1);
        Assert.assertEquals(((BStringArray) returns[0]).get(0), "Peter");
    }

    @Test(groups = "TableIterationTest", description = "Check filter count and map operation")
    public void testFilterWithMapAndCountTable() {
        BValue[] returns = BRunUtil.invoke(result, "testFilterWithMapAndCountTable");
        Assert.assertEquals(returns.length, 1);
        Assert.assertEquals(((BInteger) returns[0]).intValue(), 1);
    }

    @Test(groups = "TableIterationTest", description = "Check min operation")
    public void testMinWithTable() {
        BValue[] returns = BRunUtil.invoke(result, "testMinWithTable");
        Assert.assertEquals(returns.length, 1);
        Assert.assertEquals(((BFloat) returns[0]).floatValue(), 100.25);
    }

    @Test(groups = "TableIterationTest", description = "Check max operation")
    public void testMaxWithTable() {
        BValue[] returns = BRunUtil.invoke(result, "testMaxWithTable");
        Assert.assertEquals(returns.length, 1);
        Assert.assertEquals(((BFloat) returns[0]).floatValue(), 600.25);
    }

    @Test(groups = "TableIterationTest", description = "Check sum operation")
    public void testSumWithTable() {
        BValue[] returns = BRunUtil.invoke(result, "testSumWithTable");
        Assert.assertEquals(returns.length, 1);
        Assert.assertEquals(((BFloat) returns[0]).floatValue(), 1701.0);
    }

    @Test(groups = "TableIterationTest", description = "Check average operation")
    public void testAverageWithTable() {
        BValue[] returns = BRunUtil.invoke(result, "testAverageWithTable");
        Assert.assertEquals(returns.length, 1);
        Assert.assertEquals(((BFloat) returns[0]).floatValue(), 425.25);
    }

    @Test(dependsOnGroups = "TableIterationTest")
    public void testCloseConnectionPool() {
        BValue[] returns = BRunUtil.invoke(result, "testCloseConnectionPool");
        BInteger retValue = (BInteger) returns[0];
        Assert.assertEquals(retValue.intValue(), 1);
    }

    @Test()
    public void testSelect() {
        BValue[] returns = BRunUtil.invokeFunction(result, "testSelect");
        Assert.assertNotNull(returns);
        Assert.assertEquals(returns.length, 1);
        Assert.assertEquals(returns[0].stringValue(),
                            "[{\"id\":1, \"salary\":100.0}, {\"id\":2, \"salary\":200.0}, {\"id\":3, " +
                                    "\"salary\":300.0}]");
    }

    @Test()
    public void testSelectCompatibleLambdaInput() {
        BValue[] returns = BRunUtil.invokeFunction(result, "testSelectCompatibleLambdaInput");
        Assert.assertNotNull(returns);
        Assert.assertEquals(returns.length, 1);
        Assert.assertEquals(returns[0].stringValue(),
                            "[{\"id\":1, \"salary\":100.0}, {\"id\":2, \"salary\":200.0}, {\"id\":3, " +
                                    "\"salary\":300.0}]");
    }

    @Test()
    public void testSelectCompatibleLambdaOutput() {
        BValue[] returns = BRunUtil.invokeFunction(result, "testSelectCompatibleLambdaOutput");
        Assert.assertNotNull(returns);
        Assert.assertEquals(returns.length, 1);
        Assert.assertEquals(returns[0].stringValue(),
                            "[{\"id\":1, \"salary\":100.0}, {\"id\":2, \"salary\":200.0}, {\"id\":3, " +
                                    "\"salary\":300.0}]");
    }

    @Test()
    public void testSelectCompatibleLambdaInputOutput() {
        BValue[] returns = BRunUtil.invokeFunction(result, "testSelectCompatibleLambdaInputOutput");
        Assert.assertNotNull(returns);
        Assert.assertEquals(returns.length, 1);
        Assert.assertEquals(returns[0].stringValue(),
                            "[{\"id\":1, \"salary\":100.0}, {\"id\":2, \"salary\":200.0}, {\"id\":3, " +
                                    "\"salary\":300.0}]");
    }

    @AfterSuite
    public void cleanup() {
        if (testDatabase != null) {
            testDatabase.stop();
        }
    }
}
