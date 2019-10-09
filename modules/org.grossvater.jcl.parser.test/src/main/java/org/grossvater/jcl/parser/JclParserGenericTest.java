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

import static org.grossvater.jcl.parser.LineUtils.lines;

public class JclParserGenericTest {
    @Test
    public void testEmpty() {
        parse("/*", null, JclParser.RULE_unit,
              "");
    }
    
    @Test
    public void testOp() {
        parse("//records", "<FIELD_ID><FIELD_NAME><FIELD_OP>", JclParser.RULE_records,
              "//test proc");
    }
    
    @Test
    public void testNoNameOp() {
        parse("//records", "<FIELD_ID><FIELD_OP>", JclParser.RULE_records,
              "// proc");
    }

    @Test
    public void testPosTokenParam() {
        parse("/unit/records/record/operation/params", "<token>", JclParser.RULE_params,
                "//test proc a");
    }

    @Test
    public void testPosStringParam() {
        parse("/unit/records/record/operation/params", "<string>", JclParser.RULE_params,
                "//test proc 'a'");
    }

    @Test
    public void testPosMultilineStringParam() {
        parse("/unit/records/record/operation/params", "<multilineString>", JclParser.RULE_params,
              lines("//test proc 'a",
                    "// b'"
              ));
    }

    @Test
    public void testPosMultilineMiddleStringParam() {
        parse("/unit/records/record/operation/params", "<multilineString>", JclParser.RULE_params,
                lines("//test proc 'a",
                      "// b",
                      "// c'"
                ));
    }

    @Test
    public void testInstreamOperation() {
        parse("/unit/records/record/instreamOperation", "<instreamOperation>", JclParser.RULE_instreamOperation,
                lines("// DD *",
                        "text",
                        "/*"
                ));
    }

    private void parse(String xpath, String expr, int rule, String...lines) {
        try (Reader r = TestUtils.makeReader(lines)) {        
            AntlrUtils.match(r, xpath, expr != null ? new String[] { expr } : null, rule, null);
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
    }
}