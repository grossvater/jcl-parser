/**
 * Copyright 2017 grossvater
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *   http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */
package org.grossvater.jcl.parser;

import java.io.IOException;
import java.io.Reader;

import org.junit.Test;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class JclParserTest {
    private static Logger L = LoggerFactory.getLogger(JclParserTest.class);
    
    @Test
    public void testEmpty() {
        parse("empty", "/*", null, JclParser.RULE_unit,
              "");
    }
    
    @Test
    public void testRecord1() {
        parse("record1", "//records", "<FIELD_ID><FIELD_NAME><FIELD_OP>", JclParser.RULE_records,
              "//ptest myproc");
    }
    
    @Test
    public void testRecord2() {
        parse("record2", "//records", "<FIELD_ID><FIELD_OP>", JclParser.RULE_records,
              "// myproc");
    }

    @Test
    public void testKwParamOdd() {
        parse("kwParam", "//kwParam", "<PARAM_TOKEN>", JclParser.RULE_kwParam,
              "//XXX YYY A=B=C");        
        parse("kwParam", "//kwParamValue", "<PARAM_TOKEN><EQ><PARAM_TOKEN>", JclParser.RULE_kwParamValue,
              "//XXX YYY A=B=C");
    }
    
    private void parse(String testName, String xpath, String expr, int rule, String...lines) {
        try (Reader r = TestUtils.makeReader(lines)) {
        
            L.info("Test {}", testName);
            AntlrUtils.match(r, xpath, expr != null ? new String[] { expr } : null, rule, null);
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
    }
}