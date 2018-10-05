import javax.swing.*;
import java.awt.event.*;
import java.util.Locale;
import java.util.MissingResourceException;
import java.util.ResourceBundle;

public class InfoWindow extends JDialog {
    private JPanel contentPane;
    private JButton buttonCancel;
    private JTextPane textPane1;
    private JButton buttonOK;

    static private ResourceBundle mybundle;

    public InfoWindow (String text) {
        if(!text.equals("")) textPane1.setText(text);

        setContentPane(contentPane);
        setModal(true);
        getRootPane().setDefaultButton(buttonOK);

        buttonOK.addActionListener(new ActionListener() {
            public void actionPerformed (ActionEvent e) {
                onOK();
            }
        });

        buttonCancel.addActionListener(new ActionListener() {
            public void actionPerformed (ActionEvent e) {
                onCancel();
            }
        });

        // call onCancel() when cross is clicked
        setDefaultCloseOperation(DO_NOTHING_ON_CLOSE);
        addWindowListener(new WindowAdapter() {
            public void windowClosing (WindowEvent e) {
                onCancel();
            }
        });

        // call onCancel() on ESCAPE
        contentPane.registerKeyboardAction(new ActionListener() {
            public void actionPerformed (ActionEvent e) {
                onCancel();
            }
        }, KeyStroke.getKeyStroke(KeyEvent.VK_ESCAPE, 0), JComponent.WHEN_ANCESTOR_OF_FOCUSED_COMPONENT);
    }

    private void onOK () {
        // add your code here
        dispose();
    }

    private void onCancel () {
        // add your code here if necessary
        dispose();
    }

    public static void main (String[] args) {
        try {
            mybundle = ResourceBundle.getBundle("Strings");
        } catch (MissingResourceException e) {
            Locale.setDefault(new Locale("en", "US"));
            mybundle = ResourceBundle.getBundle("Strings");
        }

        InfoWindow dialog = new InfoWindow("");
        dialog.pack();
        //dialog.setSize(200,200);
        dialog.setLocationRelativeTo(null);
        dialog.setVisible(true);
    }
}
