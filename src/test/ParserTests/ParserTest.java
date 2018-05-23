package test.ParserTests;

import org.junit.Assert;
import org.junit.Before;
import org.junit.Test;
import org.mockito.Mock;
import org.w3c.dom.Document;
import org.w3c.dom.Element;
import org.w3c.dom.Node;
import org.xml.sax.SAXException;

import java.io.IOException;
import java.util.ArrayList;
import javax.xml.parsers.ParserConfigurationException;
import javax.xml.transform.TransformerException;

import javafx.util.Pair;

import main.*;
import main.Players.*;
import main.Parser.ParserUtils;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertTrue;
import static test.ParserTests.ParserTestUtils.*;

/**
 * Created by William on 5/16/2018.
 * <p>
 * This class tests XML representations of the Parsable objects in our project.
 */

public class ParserTest {

    @Before
    public void setUp() {
        ParserUtils.setDebug(false);
    }


    // A couple of quick tests to show that the above testing code works
    @Test
    public void compareXmlSame() throws IOException, SAXException, ParserConfigurationException {
        Assert.assertTrue(ParserUtils.nodesAreEquivalent(
                ParserUtils.nodeFromString("<div><span1></span1><span2></span2></div>").getParentNode(),
                ParserUtils.nodeFromString("<div><span1></span1><span2></span2></div>").getParentNode())
        );
    }

    @Test
    public void compareXmlChildrenDifferentOrder() throws IOException, SAXException, ParserConfigurationException {
        Assert.assertTrue(ParserUtils.nodesAreEquivalent(
                ParserUtils.nodeFromString("<set><span1></span1><span2></span2></set>").getParentNode(),
                ParserUtils.nodeFromString("<set><span2></span2><span1></span1></set>").getParentNode()));
    }

    @Test
    public void compareXmlChildMissing() throws IOException, SAXException, ParserConfigurationException {
        Assert.assertFalse(ParserUtils.nodesAreEquivalent(
                ParserUtils.nodeFromString("<div><span1></span1><span2></span2></div>").getParentNode(),
                ParserUtils.nodeFromString("<div><span1></span1></div>").getParentNode()));

        Assert.assertFalse(ParserUtils.nodesAreEquivalent(
                ParserUtils.nodeFromString("<div><span1></span1></div>").getParentNode(),
                ParserUtils.nodeFromString("<div><span1></span1><span2></span2></div>").getParentNode()));

    }

    @Test
    public void compareXmlChildText() throws IOException, SAXException, ParserConfigurationException {

        Assert.assertFalse(ParserUtils.nodesAreEquivalent(
                ParserUtils.nodeFromString("<div><span1>text</span1><span2></span2></div>").getParentNode(),
                ParserUtils.nodeFromString("<div><span1></span1><span2></span2></div>").getParentNode()));
        Assert.assertTrue(ParserUtils.nodesAreEquivalent(
                ParserUtils.nodeFromString("<div><span1>text</span1><span2></span2></div>").getParentNode(),
                ParserUtils.nodeFromString("<div><span1>text</span1><span2></span2></div>").getParentNode()));


    }
}
