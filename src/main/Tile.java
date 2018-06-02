package main;

import org.w3c.dom.Document;
import org.w3c.dom.Element;
import org.w3c.dom.Node;

import java.util.ArrayList;
import java.util.Comparator;
import java.util.HashSet;
import java.util.LinkedHashSet;
import java.util.List;
import java.util.Set;

/**
 *
 * Represents a single Tile in Tsuro
 *
 * Created by vyasalwar on 4/15/18.
 *
 */
public class Tile implements Parsable {

    //================================================================================
    // Instance Variables
    //================================================================================

    private final int ROTATIONS_PER_CYCLE = 4;
    private LinkedHashSet<TileConnection> connections;

    //================================================================================
    // Constructors
    //================================================================================

    public Tile() {
        // Default constructor. Should only be used prior to building from an XML object.
        this.connections = new LinkedHashSet<>();
    }

    /**
     * Constructs the tile to the specs listed in xmlElement
     * @param xmlElement Element with specs for this Tile.
     */
    public Tile(Element xmlElement) {
        this.connections = new LinkedHashSet<>();
        this.fromXML(xmlElement);
    }

    /**
     * Constructs an instance of Tile from explicit path endpoints
     *
     * @param startA    endpoint for pathA
     * @param endA      endpoint for pathA
     * @param startB    endpoint for pathB
     * @param endB      endpoint for pathB
     * @param startC    endpoint for pathC
     * @param endC      endpoint for pathC
     * @param startD    endpoint for pathD
     * @param endD      endpoint for pathD
     */
    public Tile(int startA, int endA,
                int startB, int endB,
                int startC, int endC,
                int startD, int endD){

        connections = new LinkedHashSet<>();
        connections.add(new TileConnection(startA, endA));
        connections.add(new TileConnection(startB, endB));
        connections.add(new TileConnection(startC, endC));
        connections.add(new TileConnection(startD, endD));

        if (!isValid())
            throw new InstantiationError("Tile created with invalid arguments");
    }

    // Constructor that reads from an input file.
    //   See TilePile.fillAllTiles to see usage
    public Tile(String fileLine){
        connections = new LinkedHashSet<>();

        // endpoints are separated by single spaces when reading from file
        String[] endpoints = fileLine.split(" ");
        for (int i = 0; i < 4; i++) {
            int endpointA = Integer.parseInt(endpoints[2 * i]);
            int endpointB = Integer.parseInt(endpoints[2 * i + 1]);
            connections.add(new TileConnection(endpointA, endpointB));
        }

        if (!isValid())
            throw new InstantiationError("Tile created with invalid arguments");
    }

    /** Constructor from other object. Clones other tile into this one
     *
     * @param other Tile to clone.
     */
    public Tile(Tile other){
        connections = new LinkedHashSet<>();
        for (TileConnection tileConnection : other.connections){
            connections.add(new TileConnection(tileConnection));
        }
    }

    //================================================================================
    // Public methods
    //================================================================================

    // Rotates the entire tile clockwise.
    //  Modifies the tile state instead of creating a new object.
    public void rotateClockwise() {
        /* Important: modifying the state of a Hash Table in a way that changes hashes will
            cause the hash table to be unusable! Reassign connections to a new HashSet instead.
         */
        LinkedHashSet<TileConnection> newConnections = new LinkedHashSet<>();
        for (TileConnection connection : connections){
            connection.rotateClockwise();
            newConnections.add(connection);
        }
        connections = newConnections;
    }

    // Given one endpoint in the tile, returns the connected endpoint
    public int findMatch(int endpoint){
        for (TileConnection connection: connections){
            if (connection.contains(endpoint)){
                return connection.otherEndpoint(endpoint);
            }
        }
        throw new IllegalArgumentException("Endpoint must be between 0 and 7");
    }

    public int calculateSymmetries(){
        Tile copy = new Tile(this);
        int symmetries = 0;

        for (int i = 0; i < 4; i++){
            if (connections.equals(copy.connections)){
                symmetries++;
            }
            copy.rotateClockwise();
        }
        return symmetries;
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

    @Override
    public String toString() {
        String ret = "Tile{";
        for (TileConnection connection: connections){
            ret += " " + connection.toString();
        }
        return ret + "}";
    }

    //================================================================================
    // Private helper methods
    //================================================================================

    // Checks to make sure the tile contains all numbers 0..7 exactly once
    private boolean isValid(){
        // A valid tile is a bijective map from {0..7} to itself
        try {
            Set<Integer> nums = new HashSet<>();
            for (int i = 0; i < 8; i++) {
                nums.add(findMatch(i));
            }

            // if nums.size() != 8, then the map is not onto
            return nums.size() == 8;
        }
        catch (IllegalArgumentException e) {
            // Does not contain some value in {0..7}, i.e. it's not one-to-one
            return false;
        }
    }

    //================================================================================
    // XML Conversion methods
    //================================================================================

    @Override
    public Element toXML(Document document) {
        Element tileElement = document.createElement("tile");
        List<TileConnection> sortedConnections =  new ArrayList<>(connections);
        sortedConnections.sort(Comparator.comparingInt(o -> Math.min(o.endpointA, o.endpointB)));
        for (TileConnection tileConnection: sortedConnections){
            tileElement.appendChild(tileConnection.toXML(document));
        }
        return tileElement;
    }

    @Override
    public void fromXML(Element xmlElement) {
        if (!xmlElement.getTagName().equals("tile"))
            throw new IllegalArgumentException("Tag for Tile construction not <tile>: " + xmlElement.getTagName());

        Node child = xmlElement.getFirstChild();
        for (int i=0; i<4; i++) {
            this.connections.add(new TileConnection((Element) child));
            child = child.getNextSibling();
        }

        if (child != null)
            throw new IllegalArgumentException("More than four xml children in <tile> tag");
    }


    //================================================================================
    // Private helper class
    //================================================================================
    private class TileConnection implements Parsable {

        private int endpointA;
        private int endpointB;

        /**
         * Default constructor for constructing from XML.
         *
         * @param xmlElement Element containing the data for this TileConnection.
         */
        public TileConnection(Element xmlElement) {
            this.fromXML(xmlElement);
        }

        // Explicit constructor
        public TileConnection (int endpointA, int endpointB) {
            this.endpointA = endpointA;
            this.endpointB = endpointB;
        }

        // Constructor from another TileConnection, i.e. clone
        public TileConnection (TileConnection other) {
            this.endpointA = other.endpointA;
            this.endpointB = other.endpointB;
        }

        // Returns true if the argument is one of the endpoints
        public boolean contains(int endpoint){
            return endpoint == endpointA || endpoint == endpointB;
        }

        // Finds the matching endpint of the argument if it exists
        public int otherEndpoint(int endpoint){
            if (endpoint == endpointA) return endpointB;
            if (endpoint == endpointB) return endpointA;
            throw new IllegalArgumentException("Endpoint object does not contain input");
        }

        // Moves both of the endpoints clockwise around
        public void rotateClockwise(){
            endpointA = (endpointA + 2) % 8;
            endpointB = (endpointB + 2) % 8;
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

        @Override
        public String toString() {
            return "(" + endpointA + ", " + endpointB + ")";
        }


        @Override
        public Element toXML(Document document) {
            // Connection ontains two number elements
            Element connectionElement = document.createElement("connect");

            Element endpointAElement = document.createElement("n");
            endpointAElement.appendChild(document.createTextNode(Integer.toString(endpointA)));

            Element endpointBElement = document.createElement("n");
            endpointBElement.appendChild(document.createTextNode(Integer.toString(endpointB)));

            if (endpointA > endpointB) {
                connectionElement.appendChild(endpointBElement);
                connectionElement.appendChild(endpointAElement);
            } else {
                connectionElement.appendChild(endpointAElement);
                connectionElement.appendChild(endpointBElement);
            }

            return connectionElement;
        }

        @Override
        public void fromXML(Element xmlElement) {
            if (!xmlElement.getTagName().equals("connect"))
                throw new IllegalArgumentException("Tag of element passed to TileConnection.from" +
                        "XML was not <connect>: " + xmlElement.getTagName());

            Node child = xmlElement.getFirstChild();
            if (!child.getNodeName().equals("n"))
                throw new IllegalArgumentException("Child of <connect> is not <n>: " +
                        child.getNodeName());

            this.endpointA = Integer.valueOf(child.getTextContent());

            child = child.getNextSibling();
            if (!child.getNodeName().equals("n"))
                throw new IllegalArgumentException("Child of <connect> is not <n>: " +
                        child.getNodeName());

            this.endpointB = Integer.valueOf(child.getTextContent());

            if (!(child.getNextSibling() == null))
                throw new IllegalArgumentException("extra children of <connect> found.");
        }
    }

}
