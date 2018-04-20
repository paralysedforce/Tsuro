package main;

import java.util.HashSet;
import java.util.Objects;
import java.util.Set;

/**
 *
 * Represents a Single main.Tile in Tsuro
 *
 * Created by vyasalwar on 4/15/18.
 *
 * TODO: isValid methods for all main.Tile classes
 *
 */
public class Tile implements Cloneable {

    private final int ROTATIONS_PER_CYCLE = 4;
    /* Helper Classes */

    private class TileConnection {

        private int endpointA;
        private int endpointB;

        public TileConnection (int endpointA, int endpointB) {
            this.endpointA = endpointA;
            this.endpointB = endpointB;
        }

        public TileConnection (TileConnection other) {
            this.endpointA = other.endpointA;
            this.endpointB = other.endpointB;
        }

        public boolean isValid(){
            return endpointA > 0 && endpointA < 7 &&
                   endpointB > 0 && endpointB < 7;
        }

        public boolean contains(int endpoint){
            return endpoint == endpointA || endpoint == endpointB;
        }

        public int otherEndpoint(int endpoint){
            if (endpoint == endpointA) return endpointB;
            if (endpoint == endpointB) return endpointA;
            throw new IllegalArgumentException("Endpoint object does not contain input");
        }

        @Override
        public boolean equals(Object obj){
            if (obj instanceof TileConnection){
                TileConnection other = (TileConnection) obj;
                return (this.endpointA == other.endpointA && this.endpointB == other.endpointB) ||
                       (this.endpointB == other.endpointA && this.endpointA == other.endpointB);
            }
            else
                return false;
        }

        @Override
        public int hashCode(){
            // We'll see if this is a good hash function later :P
            int x = endpointA + 1, y = endpointB + 1;
            return x * x * y + y * y * x;
        }

//        public TileConnection clone(){
//            return new TileConnection(endpointA, endpointB);
//        }

        public void rotateClockwise(){
            endpointA = (endpointA + 2) % 8;
            endpointB = (endpointB + 2) % 8;
        }
    }

    /* Object variables */
    private Set<TileConnection> connections;

    public Tile(int startA, int endA,
                int startB, int endB,
                int startC, int endC,
                int startD, int endD){

        connections = new HashSet<>();
        connections.add(new TileConnection(startA, endA));
        connections.add(new TileConnection(startB, endB));
        connections.add(new TileConnection(startC, endC));
        connections.add(new TileConnection(startD, endD));
    }

    public Tile(String fileLine){
        connections = new HashSet<>();

        // endpoints are separated by spaces when reading from file
        String[] endpoints = fileLine.split(" ");
        for (int i = 0; i < 4; i++) {
            int endpointA = Integer.parseInt(endpoints[2 * i]);
            int endpointB = Integer.parseInt(endpoints[2 * i + 1]);
            connections.add(new TileConnection(endpointA, endpointB));
        }
    }

    private Tile(Tile other){
        connections = new HashSet<>();
        for (TileConnection tileConnection : other.connections){
            connections.add(new TileConnection(tileConnection));
        }
    }

    public void rotateClockwise() {
        for (TileConnection connection : connections){
            connection.rotateClockwise();
        }
    }

    public int findMatch(int endpoint){
        for (TileConnection connection: connections){
            if (connection.contains(endpoint)){
                return connection.otherEndpoint(endpoint);
            }
        }
        throw new IllegalArgumentException("Endpoint not found");
    }

    @Override
    public boolean equals(Object obj) {
        if (obj instanceof Tile) {
            Tile other = new Tile((Tile) obj);
            for (int i = 0; i < ROTATIONS_PER_CYCLE; i++) {
                if (connections.equals(other.connections)) {
                    return true;
                }
                other.rotateClockwise();
            }
        }

        return false;
    }

    @Override
    public int hashCode(){
        return connections.hashCode();
    }

}
