package main;

/**
 * Created by vyasalwar on 6/5/18.
 */
public enum ContractViolation {

    PRECONDITION("Precondition violation"), POSTCONDITION("Postcondition violation"), SEQUENTIAL("Sequential violation");

    private String message;

    ContractViolation(String message) {
        this.message = message;
    }

    @Override
    public String toString(){
        return message;
    }
}
