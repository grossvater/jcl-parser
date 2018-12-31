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

public class JclParamsTest {
    @Test
    public void testPosParam() {
        parse("//posParam", new String[] { "<posParam>", "<posParam>" }, JclParser.RULE_posParam,
              "//XXX YYY A,B");        
    }

    @Test
    public void testEmptyPosParamNull() {
        parse("//posParam", new String[] { "<posParam>", "<posParam>", "<posParam>" }, JclParser.RULE_posParam,
              "//XXX YYY A,,B");        
    }

    @Test
    public void testEmptyPosParamFirstNull() {
        parse("//posParam", new String[] { "<posParam>", "<posParam>" }, JclParser.RULE_posParam,
              "//XXX YYY ,B");        
    }

    @Test
    public void testPosStringParam() {
        parse("//posParam", new String[] { "<posParam>", "<posParam>" }, JclParser.RULE_posParam,
              "//XXX YYY A,'B'");        
    }
    
    @Test
    public void testEmptyPosAndKw() {
        parse("//posParam", "<posParam>", JclParser.RULE_posParam,
              "//XXX YYY B,C=1");
        parse("//kwParamExpr", "<kwParamExpr>" , JclParser.RULE_kwParamExpr,
              "//XXX YYY B,C=1");                
    }

    @Test
    public void testKwParam() {
        parse("//kwParam", "<PARAM_TOKEN>", JclParser.RULE_kwParam,
              "//XXX YYY A=B");        
        parse("//kwParamValue", "<PARAM_TOKEN>", JclParser.RULE_kwParamValue,
              "//XXX YYY A=B");
    }
    
    @Test
    public void testKwParamOdd() {
        parse("//kwParam", "<PARAM_TOKEN>", JclParser.RULE_kwParam,
              "//XXX YYY A=B=C");        
        parse("//kwParamValue", "<PARAM_TOKEN><EQ><PARAM_TOKEN>", JclParser.RULE_kwParamValue,
              "//XXX YYY A=B=C");
    }
    
    private void parse(String xpath, String expr, int rule, String...lines) {
        try (Reader r = TestUtils.makeReader(lines)) {        
            AntlrUtils.match(r, xpath, expr != null ? new String[] { expr } : null, rule, null);
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
    }
    
    private void parse(String xpath, String[] expr, int rule, String...lines) {
        try (Reader r = TestUtils.makeReader(lines)) {        
            AntlrUtils.match(r, xpath, expr, rule, null);
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
    }    
}