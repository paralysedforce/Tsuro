package main;

/**
 * Created by vyasalwar on 5/8/18.
 */
public class ContractException extends RuntimeException {

    public ContractException(ContractViolation violation) { super(violation.toString());}

    public ContractException(ContractViolation violation, String message) {
        super(violation.toString() + ": " + message);
    }
}
