import java.awt.Color;
import java.awt.Component;
import java.awt.Dimension;

import javax.swing.BoxLayout;
import javax.swing.JPanel;
import javax.swing.JTextField;
import javax.swing.border.Border;
import javax.swing.border.LineBorder;
import javax.swing.SwingConstants;
import javax.swing.Box;
import java.awt.Font;
import java.util.concurrent.atomic.AtomicBoolean;

public class WordCard extends JPanel {

    private static final long serialVersionUID = 4552157447285524498L;

    private JTextField cardName;

    private boolean isSelected;

    static public Border normalBorder = new LineBorder(Color.BLACK);
    static public Border selectedBorder = new LineBorder(Color.RED, 5);
    static public Border testBorder = new LineBorder(Color.BLUE, 5);

    AtomicBoolean isFirstInput = new AtomicBoolean(false);

    /**
     * Create the panel.
     */
    public WordCard(String text, int screeDefaultSize) {

        Dimension cardSize = new Dimension((int) (screeDefaultSize / 3.66), (int) (screeDefaultSize / 8));
        int fontSize = (int) (screeDefaultSize / 19.7);
        int spaceSize = (int) (screeDefaultSize / 64);

        setLayout(new BoxLayout(this, BoxLayout.X_AXIS));

        setPreferredSize(cardSize);
        setMinimumSize(cardSize);
        setMaximumSize(cardSize);

        setForeground(Color.WHITE);
        setBackground(Color.WHITE);

        isSelected = false;
        setBorder(normalBorder);

        Component horizontalStrut = Box.createHorizontalStrut(spaceSize);
        add(horizontalStrut);

        Box verticalBox = Box.createVerticalBox();
        add(verticalBox);

        Component verticalStrut = Box.createVerticalStrut(spaceSize);
        verticalBox.add(verticalStrut);

        cardName = new JTextField(text);

        verticalBox.add(cardName);

        cardName.setBorder(null);
        cardName.setEditable(false);
        cardName.setFocusable(false);

        cardName.setBackground(Color.WHITE);

        cardName.setFont(MainFrame.getInstance().font.deriveFont(Font.PLAIN, fontSize));
        cardName.setHorizontalAlignment(SwingConstants.CENTER);
        cardName.setAlignmentX(Component.CENTER_ALIGNMENT);

        Component verticalStrut_1 = Box.createVerticalStrut(spaceSize);
        verticalBox.add(verticalStrut_1);

        Component horizontalStrut_1 = Box.createHorizontalStrut(spaceSize);
        add(horizontalStrut_1);
    }

    public void setSelection(boolean select) {
        if (select) {
            setBorder(selectedBorder);
            isSelected = true;
        }
        else {
            setBorder(normalBorder);
            isSelected = false;
        }
    }

    public boolean getSelection() {
        return isSelected;
    }

    public void setText(String text) {
        cardName.setText(text);
    }

    public void setTestMode(boolean testMode) {
        if (testMode) {
            setBorder(testBorder);
            cardName.setEditable(true);
            cardName.setFocusable(true);
            cardName.requestFocusInWindow();
        }
        else {
            setBorder(normalBorder);
            cardName.setEditable(false);
            MainFrame.getInstance().getContentPane().setFocusable(true);
            MainFrame.getInstance().getContentPane().requestFocusInWindow();
        }
    }

    public JTextField getTextField() {
        return cardName;
    }
}
