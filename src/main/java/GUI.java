import org.apache.commons.io.FilenameUtils;
import javax.swing.*;
import java.awt.*;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.time.Instant;
import java.util.Locale;
import java.util.MissingResourceException;
import java.util.ResourceBundle;
import java.util.stream.Collectors;
import java.util.ArrayList;
import java.util.List;

public class GUI{
    private JPanel MainGui;
    private JTextField userPath;
    private JButton button1;
    private JButton start;
    private JProgressBar progressBar1;
    private JFileChooser fileChooser;
    private List<String> pathsCollection = new ArrayList<>();
    private List<String> onlyPNGPathsCollection = new ArrayList<>();

    static private ResourceBundle mybundle;
    private boolean isCurrentlyRunning;

    public GUI() {
        start.addMouseListener(new MouseAdapter() {
            @Override
            public void mouseReleased(MouseEvent e) {
                super.mouseReleased(e);
                if(!isCurrentlyRunning) fileGetter();
                else System.out.println(mybundle.getString("app_running")); //TODO create window for this
            }
        });
    }

    /**
     * Gets path form user input and writes them to List pathCollection
     *
     */
    private void fileGetter(){
        isCurrentlyRunning = true;
        try {
            pathsCollection = Files.walk(Paths.get(userPath.getText()))
                    .filter(Files::isRegularFile)
                    .map(p -> p.toString())
                    .collect(Collectors.toList());
        } catch (IOException ex){
            System.out.println(mybundle.getString("path_doesn't_exist")); //TODO create window for this
            isCurrentlyRunning = false;
            return;
        }

        //TODO add Task to set progress bar
        progressBar1.setValue(1);
        fileFilter();
    }

    /**
     * Filters files from pathsCollection (List of all files in user inputted directory)
     * to be only a PNG ones.
     */
    private void fileFilter (){
        if (!pathsCollection.isEmpty()){
            int sizeOfPathsCollection = pathsCollection.size();
            progressBar1.setMaximum(sizeOfPathsCollection+1);

            for (int i = 0; i < sizeOfPathsCollection; i++){
                String extension = FilenameUtils.getExtension(pathsCollection.get(i)
                        .toLowerCase());
                if(extension.equals("png")){
                    onlyPNGPathsCollection.add(pathsCollection.get(i));
                }
            }
            progressBar1.setValue(progressBar1.getMaximum()/4); //TODO Add proper changing of progressBar value when file was converted
            worker.execute();
        }
    }

    /**
     *
     */
    private SwingWorker<Void,Void> worker = new SwingWorker<Void, Void>() {
        @Override
        protected Void doInBackground () {
            System.out.println(Instant.now());

            //TODO create PNG2JPG class object and check it status to move progress bar
            onlyPNGPathsCollection
                    .parallelStream()
                    .forEach(p -> new PNG2JPG(p, 1)); //TODO Add changing of quality in GUI

            System.out.println(Instant.now());
            isCurrentlyRunning = false;
            return null;
        }

        @Override
        protected void done(){
            progressBar1.setValue(progressBar1.getMaximum());
        }
    };

    /**
     * Creates window for user interface and gets language bundle for app.
     * @param args arguments given when launching app.
     */
    public static void main(String[] args) {
        try {
            mybundle = ResourceBundle.getBundle("Strings");
        } catch (MissingResourceException e) {
            Locale.setDefault(new Locale("en", "US"));
            mybundle = ResourceBundle.getBundle("Strings");
        }

        JFrame frame = new JFrame("JPNG2JPG");

        frame.setContentPane(new GUI().MainGui);
        frame.setDefaultCloseOperation(WindowConstants.EXIT_ON_CLOSE);
        frame.pack();
        frame.setVisible(true);

        frame.setSize(800,600);
        Dimension dim = Toolkit.getDefaultToolkit().getScreenSize();
        frame.setLocation(dim.width/2-frame.getSize().width/2,
                dim.height/2-frame.getSize().height/2);
    }
}
