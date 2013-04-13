/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package P2PFileTransmitSystem.FileRelated.WordHandler;

import java.io.CharArrayReader;
import java.io.IOException;
import java.io.Reader;
import java.util.HashSet;
import org.wltea.analyzer.IKSegmentation;
import org.wltea.analyzer.Lexeme;

/**
 *
 * @author Administrator
 */
public class MyIKAnalysis {

    public static HashSet<String> Analysis(final String text) {
        HashSet<String> id=new HashSet<String>();
        boolean isFull = true;
        IKSegmentation ikse = null;
        char[] chars = text.toCharArray();
        Reader reader = new CharArrayReader(chars);
        if (isFull) {
            ikse = new IKSegmentation(reader);
        } else {
            ikse = new IKSegmentation(reader, true);
        }
        try {
            Lexeme lex = ikse.next();
            while (lex != null) {
                String element = lex.getLexemeText();
                id.add(Hash.hash(element));
                /**
                if (!isNumeric(element)) {
                    id.add(Hash.hash(element));
                }
                 * */
                lex = ikse.next();
            }
        } catch (IOException e) {
            // TODO Auto-generated catch block
            e.printStackTrace();
        }
        return id;

    }

    private static boolean isNumeric(String str) {
        for (int i = str.length(); --i >= 0;) {
            if (!Character.isDigit(str.charAt(i))) {
                return false;
            }
        }
        return true;
    }
}
