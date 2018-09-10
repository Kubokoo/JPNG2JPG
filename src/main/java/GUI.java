import javax.swing.*;
import java.awt.*;
import java.util.Locale;
import java.util.MissingResourceException;
import java.util.ResourceBundle;

public class GUI {
    private JPanel MainGui;
    private JComboBox comboBox1;
    private JButton button1;
    private JButton start;
    private JProgressBar progressBar1;
    private JFileChooser fileChooser;
    static private ResourceBundle mybundle;

    public static void main(String[] args) {
        try {
            mybundle= ResourceBundle.getBundle("Strings");
        } catch (MissingResourceException e) {
            Locale.setDefault(new Locale("en", "US"));
            mybundle= ResourceBundle.getBundle("Strings");
        }


        JFrame frame = new JFrame("Test");

        frame.setContentPane(new GUI().MainGui);
        frame.setDefaultCloseOperation(WindowConstants.EXIT_ON_CLOSE);
        frame.pack();
        frame.setVisible(true);

        frame.setSize(800,600);
        Dimension dim = Toolkit.getDefaultToolkit().getScreenSize();
        frame.setLocation(dim.width/2-frame.getSize().width/2, dim.height/2-frame.getSize().height/2);
    }
}
