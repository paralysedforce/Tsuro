package main.Parser;

/**
 * Created by vyasalwar on 5/21/18.
 */
public class ParserException extends RuntimeException {
    public ParserException(){
        super();
    }

    public ParserException(String msg){
        super(msg);
    }
}
