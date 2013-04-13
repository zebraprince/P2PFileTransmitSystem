/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */

package P2PFileTransmitSystem.GUI;

import java.awt.Component;
import javax.swing.BorderFactory;
import javax.swing.JProgressBar;
import javax.swing.JTable;
import javax.swing.table.DefaultTableCellRenderer;

/**
 *
 * @author maxwell
 */
public class ProgressRenderer extends DefaultTableCellRenderer {

private final JProgressBar b= new JProgressBar(0,100);

public ProgressRenderer() {
   super();
    setOpaque(true);
    b.setStringPainted(true);
    b.setBorderPainted(true);   
    b.setBorder(BorderFactory.createEmptyBorder(1, 1, 1, 1));
}

    @Override
public Component getTableCellRendererComponent(JTable table, Object value,
    boolean isSelected, boolean hasFocus, int row, int column) {
   Integer i = (Integer) value;
   b.setValue(i);
   return b;
}
}
