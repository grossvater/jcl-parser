package org.grossvater.jcl.parser;

import org.antlr.v4.runtime.Parser;
import org.antlr.v4.runtime.tree.ParseTree;

public class AntlrResult {
	ParseTree tree;
	int errors;
	Parser parser;
	
	public AntlrResult(ParseTree tree, int errors, Parser parser) {
		if (tree == null) {
			throw new IllegalArgumentException("tree");
		}
		if (parser == null) {
			throw new IllegalArgumentException("parser");
		}
		
		this.tree = tree;
		this.errors = errors;
		this.parser = parser;
	}
	
	public ParseTree getTree() {
		return tree;
	}

	public int getErrors() {
		return errors;
	}

	public Parser getParser() {
		return parser;
	}
}
