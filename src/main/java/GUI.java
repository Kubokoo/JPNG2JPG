import org.apache.commons.io.FilenameUtils;
import javax.swing.*;
import java.awt.*;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;
import java.io.File;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.Locale;
import java.util.MissingResourceException;
import java.util.ResourceBundle;
import java.util.stream.Collectors;
import java.util.stream.Stream;
import java.util.ArrayList;
import java.util.List;

public class GUI {
    private JPanel MainGui;
    private JTextField userPath;
    private JButton button1;
    private JButton start;
    private JProgressBar progressBar1;
    private JFileChooser fileChooser;
    private List<Path> pathsCollection = new ArrayList<>();

    static private ResourceBundle mybundle;
    private boolean isCurrentlyRunning;

    public GUI() {
        start.addMouseListener(new MouseAdapter() {
            @Override
            public void mouseReleased(MouseEvent e) {
                super.mouseReleased(e);
                if(!isCurrentlyRunning) fileGetter();
                else System.out.println("App is already running"); //TODO create window for this
            }
        });
    }

    private void fileGetter(){
        isCurrentlyRunning = true;
        try {
            pathsCollection = Files.walk(Paths.get(userPath.getText()))
                    .filter(Files::isRegularFile)
                    .collect(Collectors.toList());
        } catch (IOException ex){
            System.out.println("Path doesn't exist"); //TODO create window for this
            isCurrentlyRunning = false;
            return;
        }
        fileFilter();
    }

    private void fileFilter (){
        if (!pathsCollection.isEmpty()){
            int sizeOfPathsCollection = pathsCollection.size();
            int stepSize = sizeOfPathsCollection/99;
            
            for (int i = 0; i < sizeOfPathsCollection; i++){
                if(i%stepSize==0) {
                    progressBar1.setValue(progressBar1.getValue()+1); //TODO check if multithreading solves this.
                }

                String extension = FilenameUtils.getExtension(pathsCollection.get(i).toString().toLowerCase());
                if(extension.equals("png")){
                    new PNG2JPG(pathsCollection.get(i).toString(),1); //TODO Add changing of quality in GUI
                }
                //TODO Add some form of later removal of non PNG files
            }
            isCurrentlyRunning = false;
        }
        //TODO create window for this
    }

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
