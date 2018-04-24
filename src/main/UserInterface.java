package main;

//import com.sun.tools.internal.xjc.reader.xmlschema.bindinfo.BIConversion;

//import javax.jws.soap.SOAPBinding;
import javax.swing.*;
import java.awt.*;

/**
 * Created by vyasalwar on 4/19/18.
 */
public class UserInterface extends JFrame{

    private static final int WIDTH = 400;
    private static final int HEIGHT = 600;

    public UserInterface(){
        setTitle("Tsuro");
        setSize(WIDTH, HEIGHT);
        setVisible(true);
        setDefaultCloseOperation(WindowConstants.EXIT_ON_CLOSE);
    }

    public static void main(String[] args){
        new UserInterface();
    }
}
