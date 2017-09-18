/**
 * Copyright 2017 grosvater
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

import java.io.File;

import org.junit.Assert;
import org.junit.Test;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class JclParserTest {
	private static String RES = "/parser";
	
	private static Logger L = LoggerFactory.getLogger(JclParserTest.class);
	
	@Test
	public void testEmpty() {
		parse(RES, "empty.jcl", "/*", null, JclParser.RULE_unit);
	}
	
	private void parse(String base, String fileName, String xpath, String expr, int rule) {
		parse(base, fileName, xpath, expr, rule, null);
	}
	
	private void parse(String base, String fileName, String xpath, String expr, int rule,
					   JclParserOpts opts) {
		String filePath;
		
		filePath = TestUtils.makeFile(base, fileName);
		
		Assert.assertTrue(new File(filePath).exists());		
		
		L.info("Test {}/{}", base, fileName);
		AntlrUtils.match(filePath, xpath, expr != null ? new String[] { expr } : null, rule, opts);
	}
}