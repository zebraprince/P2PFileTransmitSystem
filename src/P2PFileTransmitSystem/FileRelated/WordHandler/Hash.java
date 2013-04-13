/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package P2PFileTransmitSystem.FileRelated.WordHandler;

import java.security.MessageDigest;

/**
 *
 * @author Administrator
 */
public class Hash {

    public static String function = "SHA-1";
    public static int KEY_LENGTH = 160;

    public static String hash(String identifier) {

        try {
            MessageDigest md = MessageDigest.getInstance(function);
            md.reset();
            byte[] code = md.digest(identifier.getBytes());
            byte[] value = new byte[KEY_LENGTH / 8];
            int shrink = code.length / value.length;
            int bitCount = 1;
            for (int j = 0; j < code.length * 8; j++) {
                int currBit = ((code[j / 8] & (1 << (j % 8))) >> j % 8);
                if (currBit == 1) {
                    bitCount++;
                }
                if (((j + 1) % shrink) == 0) {
                    int shrinkBit = (bitCount % 2 == 0) ? 0 : 1;
                    value[j / shrink / 8] |= (shrinkBit << ((j / shrink) % 8));
                    bitCount = 1;
                }
            }
            String id=byteToString(value);
            return id;
        } catch (Exception e) {
            e.printStackTrace();
        }

        return null;
    }

    private static String byteToString(byte[] key) {
        StringBuilder sb = new StringBuilder();
        if (key.length > 4) {
            for (int i = 0; i < key.length; i++) {
                sb.append(Integer.toString(((int) key[i]) & 0xff) + ".");
            }
        } else {
            long n = 0;
            for (int i = key.length - 1, j = 0; i >= 0; i--, j++) {
                n |= ((key[i] << (8 * j)) & (0xffL << (8 * j)));
            }
            sb.append(Long.toString(n));
        }
        return sb.substring(0, sb.length() - 1).toString();
    }
}
