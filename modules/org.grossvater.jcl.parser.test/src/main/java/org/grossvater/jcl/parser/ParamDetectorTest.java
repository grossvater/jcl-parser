package org.grossvater.jcl.parser;

import org.junit.Assert;
import org.junit.Before;
import org.junit.Test;

public class ParamDetectorTest {
    private final int PARAM_MODE = 1;
    private final int DEFAULT_MODE = 0;

    private final int TOKEN = 1;
    private final int EQ = 2;
    private final int DONTCARE = 3;
    private final String PARAM = "DLM";

    private ParamDetector de;

    @Before
    public void before() {
        this.de = new ParamDetector(PARAM_MODE, TOKEN, EQ, PARAM);
    }

    @Test
    public void test1() {
        Assert.assertNull(this.de.input(PARAM_MODE, TOKEN, PARAM));
        Assert.assertNull(this.de.input(PARAM_MODE, EQ));
        Assert.assertNull(this.de.input(PARAM_MODE, TOKEN, "value"));
        Assert.assertEquals("value", this.de.input(PARAM_MODE, DONTCARE));
    }

    @Test
    public void test2() {
        Assert.assertNull(this.de.input(PARAM_MODE, TOKEN, PARAM));
        Assert.assertNull(this.de.input(PARAM_MODE, EQ));
        Assert.assertNull(this.de.input(PARAM_MODE, TOKEN, "value"));
        Assert.assertEquals("value", this.de.input(DEFAULT_MODE, DONTCARE));
    }

    @Test
    public void test3() {
        Assert.assertNull(this.de.input(PARAM_MODE, TOKEN, PARAM));
        Assert.assertNull(this.de.input(PARAM_MODE, EQ));
        Assert.assertNull(this.de.input(PARAM_MODE, TOKEN, "A"));
        Assert.assertNull(this.de.input(PARAM_MODE, EQ, "="));
        Assert.assertNull(this.de.input(PARAM_MODE, TOKEN, "B"));
        Assert.assertEquals("A=B", this.de.input(PARAM_MODE, DONTCARE));
    }
}
