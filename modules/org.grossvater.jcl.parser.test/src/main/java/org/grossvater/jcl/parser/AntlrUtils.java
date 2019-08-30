package org.grossvater.jcl.parser;

import static org.grossvater.jcl.parser.TestUtils.assertEquals;

import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.Reader;
import java.util.Arrays;
import java.util.Collection;
import java.util.List;

import org.antlr.v4.runtime.BaseErrorListener;
import org.antlr.v4.runtime.CharStream;
import org.antlr.v4.runtime.CharStreams;
import org.antlr.v4.runtime.CommonTokenStream;
import org.antlr.v4.runtime.Parser;
import org.antlr.v4.runtime.RecognitionException;
import org.antlr.v4.runtime.Recognizer;
import org.antlr.v4.runtime.RuleContext;
import org.antlr.v4.runtime.Token;
import org.antlr.v4.runtime.Vocabulary;
import org.antlr.v4.runtime.atn.ATN;
import org.antlr.v4.runtime.misc.Utils;
import org.antlr.v4.runtime.tree.ErrorNode;
import org.antlr.v4.runtime.tree.ParseTree;
import org.antlr.v4.runtime.tree.TerminalNode;
import org.antlr.v4.runtime.tree.Tree;
import org.antlr.v4.runtime.tree.pattern.ParseTreeMatch;
import org.antlr.v4.runtime.tree.pattern.ParseTreePattern;
import org.antlr.v4.runtime.tree.xpath.XPath;
import org.junit.Assert;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class AntlrUtils {
    private static Logger L = LoggerFactory.getLogger(AntlrUtils.class);
    
    static class ErrorListener extends BaseErrorListener {
        int errors;
        
        @Override
        public void syntaxError(Recognizer<?, ?> recognizer,
                                Object offendingSymbol,
                                int line, int charPositionInLine,
                                String msg,
                                RecognitionException e) {
            this.errors++;
        }
    }
    
    public static AntlrResult parse(Reader sr, JclParserOpts opts) {        
        ParseTree tree;        
        CharStream fs;
        JclLexer lexer;
        CommonTokenStream tokens;
        JclParser parser;
        ErrorListener eh = new ErrorListener();
        
        try {
            fs = CharStreams.fromReader(sr);
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
        
        lexer = new JclLexer(fs, opts);        
        tokens = new CommonTokenStream(lexer);        
        parser = new JclParser(tokens, opts);
    
        lexer.addErrorListener(eh);        
        parser.addErrorListener(eh);
        
        tree = parser.unit();
        L.debug("Tokens: {}", ParseUtils.toString(tokens.getTokens()));
        L.debug("Parse tree: {}", toStringTree(tree, parser));

        return new AntlrResult(tree, eh.errors, parser);
    }
    
    @SuppressWarnings("unchecked")
    public static void match(String content, ExToken[] expected) {
        JclLexer l = new JclLexer(CharStreams.fromString(content));
        List<Token> tokens;
        ErrorListener el = new ErrorListener();
        
        l.addErrorListener(el);
        
        tokens = (List<Token>)l.getAllTokens();
        L.debug("Tokens: {}", ParseUtils.toString(tokens));
        
        Assert.assertEquals(0, el.errors);
        assertEquals(tokens, expected);
    }

    public static void match(String content, int[] expected) {
        match(content, expected, null);
    }

    @SuppressWarnings("unchecked")
    public static void match(String content, int[] expected, JclParserOpts opts) {
        JclLexer l = new JclLexer(CharStreams.fromString(content), opts);
        List<Token> tokens;
        ErrorListener el = new ErrorListener();
        
        l.addErrorListener(el);
        
        tokens = (List<Token>)l.getAllTokens();
        L.debug("Tokens: {}", ParseUtils.toString(tokens));
        
        Assert.assertEquals(0, el.errors);
        assertEquals(tokens, expected);
    }    

    public static void match(String content, ExToken expected) {
        match(content, new ExToken[] { expected });
    }    

    public static void match(String filePath, String xpath, String[] expr, int rule) {
        match(filePath, xpath, expr, rule, null);
    }
    
    public static void match(String filePath, String xpath, String[] expr, int rule, JclParserOpts opts) {
        try {
            match(new InputStreamReader(new FileInputStream(filePath)), xpath, expr, rule, opts);
        } catch (FileNotFoundException e) {
            throw new RuntimeException(e);
        } 
    }
    
    public static void match(Reader sr, String xpath, String[] expr, int rule, JclParserOpts opts) {
        AntlrResult r;
        ParseTree tree;
        int errors;
        Parser parser;
        
        r = parse(sr, opts);
        
        Assert.assertNotNull(r);
        
        tree = r.tree;
        errors = r.errors;
        parser = r.parser;
        
        Assert.assertEquals(0, errors);
        
        L.debug("Find subtrees by xpath: {}", xpath);
        Collection<ParseTree> ptrees = XPath.findAll(tree, xpath, parser);
                
        L.debug("Found {} subtree(s).", ptrees.size());
        for (ParseTree pt : ptrees) {
            L.debug("Subtree: {}", toStringTree(pt, parser));
        }
        
        if (expr == null) {
            Assert.assertEquals(1, ptrees.size());
        } else {
            Assert.assertEquals(expr.length, ptrees.size());
        }
        
        if (expr != null) {
            int i = 0;
            
            for (ParseTree pt : ptrees) {
                String expr1 = expr[i++];
                
                L.debug("Match subtree by expr {}: {}", expr1, toStringTree(pt, parser));
                
                ParseTreePattern p = parser.compileParseTreePattern(expr1, rule);
                ParseTreeMatch m = p.match(pt);
                
                Assert.assertTrue(m.succeeded());
                L.debug("Success.");
            }
        }        
    }
    
    /**
     * Based on
     * {@link org.antlr.v4.runtime.tree.Trees#toStringTree(Tree, Parser)
     */
    public static String toStringTree(Tree t, Parser recog) {
        String[] ruleNames = recog != null ? recog.getRuleNames() : null;
        List<String> ruleNamesList = ruleNames != null ? Arrays.asList(ruleNames) : null;

        return toStringTree(t, ruleNamesList, recog.getVocabulary());
    }

    /**
     * Based on {@link org.antlr.v4.runtime.tree.Trees#toStringTree(Tree, List)
     */
    public static String toStringTree(final Tree t, final List<String> ruleNames, Vocabulary v) {
        String s = Utils.escapeWhitespace(getNodeText(t, ruleNames, v), false);
        if (t.getChildCount() == 0)
            return s;
        StringBuilder buf = new StringBuilder();
        buf.append("(");
        s = Utils.escapeWhitespace(getNodeText(t, ruleNames, v), false);
        buf.append(s);
        buf.append(' ');
        for (int i = 0; i < t.getChildCount(); i++) {
            if (i > 0)
                buf.append(' ');
            buf.append(toStringTree(t.getChild(i), ruleNames, v));
        }
        buf.append(")");
        return buf.toString();
    }

    /**
     * Based on {@link org.antlr.v4.runtime.tree.Trees#getNodeText(Tree, Parser)
     */
    public static String getNodeText_(Tree t, Parser recog, Vocabulary v) {
        String[] ruleNames = recog != null ? recog.getRuleNames() : null;
        List<String> ruleNamesList = ruleNames != null ? Arrays.asList(ruleNames) : null;

        return getNodeText(t, ruleNamesList, v);
    }

    /**
     * Based on {@link org.antlr.v4.runtime.tree.Trees#getNodeText(Tree, List)
     */
    public static String getNodeText(Tree t, List<String> ruleNames, Vocabulary v) {
        if (ruleNames != null) {
            if (t instanceof RuleContext) {
                int ruleIndex = ((RuleContext) t).getRuleContext().getRuleIndex();
                String ruleName = ruleNames.get(ruleIndex);
                int altNumber = ((RuleContext) t).getAltNumber();
                if (altNumber != ATN.INVALID_ALT_NUMBER) {
                    return ruleName + ":" + altNumber;
                }
                return ruleName;
            } else if (t instanceof ErrorNode) {
                return t.toString();
            } else if (t instanceof TerminalNode) {
                Token symbol = ((TerminalNode) t).getSymbol();
                if (symbol != null) {
                    return v.getSymbolicName(symbol.getType());
                }
            }
        }
        // no recog for rule names
        Object payload = t.getPayload();
        if (payload instanceof Token) {
            return ((Token) payload).getText();
        }
        return t.getPayload().toString();
    }
}
