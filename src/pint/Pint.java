/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package pint;

import java.util.List;
import java.util.logging.Level;
import java.util.logging.Logger;
import org.antlr.runtime.ANTLRStringStream;
import org.antlr.runtime.CommonTokenStream;
import org.antlr.runtime.RecognitionException;
import org.antlr.runtime.tree.CommonTree;

/**
 *
 * @author admin
 */
public class Pint {

    private boolean error = false;

    public static void main(String[] args) {
        try {
            new Pint().parse("d1.t1.l2.2340.s3.123410.e");
        } catch (SynTaxErrorException ex) {
            Logger.getLogger(Pint.class.getName()).log(Level.SEVERE, null, ex);
        }
    }

    public CommonTree parse(String src) throws SynTaxErrorException {

        error = false;

        PintLexer lex = new PintLexer(new ANTLRStringStream(src)) {
            @Override
            public void emitErrorMessage(String msg) {
                error = true;
                super.emitErrorMessage(msg); //To change body of generated methods, choose Tools | Templates.
            }

        };

        PintParser par = new PintParser(new CommonTokenStream(lex)) {

            @Override
            public void emitErrorMessage(String msg) {
                error = true;
                super.emitErrorMessage(msg); //To change body of generated methods, choose Tools | Templates.
            }

        };

        try {

            CommonTree root = (CommonTree) par.sub().tree;

            if (error) {
                throw new SynTaxErrorException();
            }

            display(root);

            return root;

        } catch (RecognitionException ex) {
            Logger.getLogger(Pint.class.getName()).log(Level.SEVERE, null, ex);
        }

        return null;

    }

    public static void display(Object root) {
        displayNode(root, 0);
    }

    private static void displayNode(Object node, int parentLength) {

        CommonTree n = (CommonTree) node;

    
        String str = n.toString();
        String toDisplay = parentLength == 0 ? str : createSpaces(parentLength) + "|----" + str;

        System.out.println(toDisplay);

        List children = n.getChildren();
        if (children == null) {
            return;
        }
        for (Object child : children) {
            displayNode(child, toDisplay.length());
        }

    }

    private static String createSpaces(int spaces) {
        StringBuilder b = new StringBuilder();
        for (int i = 0; i < spaces; i++) {
            b.append(" ");
        }
        return b.toString();
    }

}
