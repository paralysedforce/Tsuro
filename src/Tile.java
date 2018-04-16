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

    public void rotateClockwise(){
        connections.rotateClockwise();
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
        Tile t2 = new Tile(1, 0, 2, 3, 4, 5, 6, 7);
        Tile t3 = new Tile(4,5,6,7,0,1,2,3);
        Tile t4 = new Tile(0, 2, 1, 3, 4, 5, 6, 7);

        System.out.println( t1.equals(t2) );
        System.out.println( t2.equals(t3) );
        System.out.println( t1.equals(t4) );
    }


}
