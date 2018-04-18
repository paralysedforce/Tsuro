import java.util.HashSet;
import java.util.Set;

/**
 *
 * Represents a Single Tile in Tsuro
 *
 * Created by vyasalwar on 4/15/18.
 *
 * TODO: isValid methods for all Tile classes
 *
 */
public class Tile {

    private final int ROTATIONS_PER_CYCLE = 4;
    /* Helper Classes */

    private class TileConnection {

        private Set<Integer> endpoints;

        public TileConnection (int endpointA, int endpointB) {
            endpoints = new HashSet<>();

            this.endpoints.add(endpointA);
            this.endpoints.add(endpointB);
        }

        public boolean isValid(){
            for (int endpoint: endpoints){
                if (endpoint > 7 || endpoint < 0)
                    return false;
            }
            return true;
        }

        public boolean contains(int endpoint){
            return endpoints.contains(endpoint);
        }

        public int otherEndpoint(int endpoint){
            for (int cur: endpoints){
                if (endpoint != cur)
                    return cur;
            }
            throw new IllegalArgumentException("Invalid TileConnection");
        }

        @Override
        public boolean equals(Object obj){
            if (obj instanceof TileConnection){
                TileConnection other = (TileConnection) obj;
                return endpoints.equals(other.endpoints);
            }
            else
                return false;
        }

        @Override
        public int hashCode(){
            return endpoints.hashCode();
        }

        public void rotateClockwise(){
            HashSet<Integer> rotated = new HashSet<>();
            for (int i: endpoints){
                rotated.add((i + 2) % 8);
            }
            endpoints = rotated;
        }
    }

    private class TileConnections {
        private Set<TileConnection> tileConnections;

        public TileConnections(int ...endpoints){
            tileConnections = new HashSet<>();

            for (int i = 0; i < endpoints.length; i+=2) {
                tileConnections.add(new TileConnection(endpoints[i], endpoints[i + 1]));
            }
        }

        public boolean isValid(){
            for (TileConnection connection: tileConnections){
                if (!connection.isValid())
                    return false;
            }
            return true;
        }

        public int findMatch(int endpoint){
            for (TileConnection connection: tileConnections){
                if (connection.contains(endpoint)){
                    return connection.otherEndpoint(endpoint);
                }
            }

            throw new IllegalArgumentException("Endpoint not found");
        }

        @Override
        public boolean equals(Object obj){
            if (obj instanceof TileConnections){
                TileConnections other = (TileConnections) obj;
                return this.tileConnections.equals(other.tileConnections);
            }
            else return false;
        }

        @Override
        public int hashCode(){
            return tileConnections.hashCode();
        }

        public void rotateClockwise(){
            for (TileConnection tileConnection : tileConnections){
                tileConnection.rotateClockwise();
            }

        }
    };


    /* Object variables */
    private TileConnections connections;

    public Tile(int startA, int endA,
                int startB, int endB,
                int startC, int endC,
                int startD, int endD){

        connections = new TileConnections(
                startA, endA,
                startB, endB,
                startC, endC,
                startD, endD);
    }

    public Tile(String fileLine){
        connections = new TileConnections(
                Integer.parseInt(Character.toString(fileLine.charAt(0))),
                Integer.parseInt(Character.toString(fileLine.charAt(2))),
                Integer.parseInt(Character.toString(fileLine.charAt(4))),
                Integer.parseInt(Character.toString(fileLine.charAt(6))),
                Integer.parseInt(Character.toString(fileLine.charAt(8))),
                Integer.parseInt(Character.toString(fileLine.charAt(10))),
                Integer.parseInt(Character.toString(fileLine.charAt(12))),
                Integer.parseInt(Character.toString(fileLine.charAt(14)))
        );
    }

    public void rotateClockwise(){
        connections.rotateClockwise();
    }

    public int findMatch(int endpoint){
        return connections.findMatch(endpoint);
    }

    @Override
    public boolean equals(Object obj) {
        if (obj instanceof Tile) {
            Tile other = (Tile) obj;
            boolean isEqual = false;
            for (int i = 0; i < ROTATIONS_PER_CYCLE; i++) {
                if (connections.equals(other.connections)) {
                    isEqual = true;
                }
                rotateClockwise();
            }
            return isEqual;
        }
        else
            return false;

        /*
        if (obj instanceof Tile){
            Tile other = (Tile) obj;
            Set<Tile> rotations = Tile.generateRotations(this);
            for (Tile t : rotations){
                if (t.(other))
                    return true;
            }
            return false;
        }
        else
            return false;*/
    }

    @Override
    public int hashCode(){
        return connections.hashCode();
    }

    public static void main(String[] args){
        Tile t1 = new Tile(0, 1, 2, 3, 4, 5, 6, 7);
        Tile t2 = new Tile(1, 0, 2, 3, 4, 5,  6, 7);
        Tile t3 = new Tile(4,5,6,7,0,1,2,3);
        Tile t4 = new Tile(0, 2, 1, 3, 4, 5, 6, 7);

        System.out.println( t1.equals(t2) );
        System.out.println( t2.equals(t3) );
        System.out.println( t1.equals(t4) );
    }


}
