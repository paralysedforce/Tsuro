package main;

import org.w3c.dom.Element;

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
}
