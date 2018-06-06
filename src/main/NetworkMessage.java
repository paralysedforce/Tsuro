package main;

import org.w3c.dom.Document;
import org.w3c.dom.Element;
import org.w3c.dom.Node;
import org.xml.sax.SAXException;

import java.io.ByteArrayInputStream;
import java.io.IOException;
import java.io.StringWriter;

import javax.xml.parsers.DocumentBuilderFactory;
import javax.xml.parsers.ParserConfigurationException;
import javax.xml.transform.OutputKeys;
import javax.xml.transform.Transformer;
import javax.xml.transform.TransformerException;
import javax.xml.transform.TransformerFactory;
import javax.xml.transform.dom.DOMSource;
import javax.xml.transform.stream.StreamResult;

public enum NetworkMessage {
    GET_NAME("get-name"), PLAYER_NAME("player-name"), INITIALIZE("initialize"),
    VOID("void"), PLACE_PAWN("place-pawn"), PAWN_LOC("pawn-loc"), PLAY_TURN("play-turn"),
    TILE("tile"), END_GAME("end-game");

    final String str;

    NetworkMessage(String s) {
        str = s;
    }

    public String getTag() {
        return str;
    }

    public Element getMessageRootElement(Document document) {
        return document.createElement(str);
    }
}
