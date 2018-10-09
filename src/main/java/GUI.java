import javafx.collections.FXCollections;
import javafx.collections.ListChangeListener;
import javafx.collections.ObservableList;
import org.apache.commons.io.FilenameUtils;
import javax.swing.*;
import java.awt.*;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
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
    private JTextField oldFiles;
    private JFileChooser fileChooser;

    private List<String> pathsCollection = new ArrayList<>();
    private List<String> onlyPNGPathsCollection = new ArrayList<>();
    private final ObservableList<PNG2JPG> observableListofFiles = FXCollections.observableArrayList();

    static private ResourceBundle mybundle;
    private boolean isCurrentlyRunning;

    public GUI() {
        start.addMouseListener(new MouseAdapter() {
            @Override
            public void mouseReleased(MouseEvent e) {
                super.mouseReleased(e);
                if(!isCurrentlyRunning) fileGetter();
                else infoWindowCreator("app_running");
            }
        });

        observableListofFiles.addListener(new ListChangeListener() {
            @Override
            public void onChanged(ListChangeListener.Change change) {
                progressBar1.setValue(progressBar1.getValue()+1);
            }
        });
    }

    /**
     * Gets path form user input and writes them to List pathCollection
     */
    private void fileGetter(){
        if(!userPath.getText().isEmpty()){
            isCurrentlyRunning = true;
            try {
                pathsCollection = Files.walk(Paths.get(userPath.getText()))
                        .filter(Files::isRegularFile)
                        .map(path -> path.toString())
                        .collect(Collectors.toList());
            } catch (IOException ex){
                infoWindowCreator("path_doesn't_exist");
                isCurrentlyRunning = false;
                return;
            }

            //TODO add Task to set progress bar
            progressBar1.setValue(1);
            fileFilter();

        }
        else infoWindowCreator("no_path");
    }

    /**
     * Filters files from pathsCollection (List of all files in user inputted directory)
     * to be only a PNG ones.
     */
    private void fileFilter (){
        if (!pathsCollection.isEmpty()){
            int sizeOfPathsCollection = pathsCollection.size();
            progressBar1.setMaximum(sizeOfPathsCollection+1);

            for (String aPathsCollection : pathsCollection) { //TODO If files have a alpha used in them give choice should they be converted
                String extension = FilenameUtils.getExtension(aPathsCollection
                        .toLowerCase());
                if (extension.equals("png")) {
                    onlyPNGPathsCollection.add(aPathsCollection);
                }
            }
            progressBar1.setValue(progressBar1.getMaximum()/4); //TODO Add proper changing of progressBar value when file was converted
            worker.execute(); //TODO Lauch4j create exe from jar
        }
    }

    /**
     * Method for file moving, created only because .foreach can't handle try,catch
     * Moves file to location specified in oldFiles textbox
     * @param path Path to moved file
     */

    private void fileMoving (String path){
        final String folderToMoveOldFiles =  oldFiles.getText();

        try {
            if(!Files.exists(Paths.get(folderToMoveOldFiles)))
                Files.createDirectories(Paths.get(folderToMoveOldFiles));
        } catch (IOException ex){
            ex.printStackTrace();
            infoWindowCreator("error_moving_file");
            return;
        }

        try {
            String test = oldFiles.getText()
                    + "\\"
                    + FilenameUtils.getName(path);
            Files.move(Paths.get(path),
                    Paths.get(test));
        }   //TODO Think about moving files just after conversion (maybe creating separete thread for that and use status
        catch (IOException ex){
            ex.printStackTrace();
            infoWindowCreator("error_moving_file");
            return;
        }
    }

    /**
     * Converting and moving files without blocking gui thread
     */
    private SwingWorker<Void,Void> worker = new SwingWorker<Void, Void>() {
        @Override
        protected Void doInBackground () {
            System.out.println(Instant.now());

            //TODO create PNG2JPG class object and check it status to move progress bar
            onlyPNGPathsCollection
                    .parallelStream()
                    .forEach(p -> observableListofFiles.add(new PNG2JPG(p, 1))); //TODO Add changing of quality in GUI

            onlyPNGPathsCollection
                    .parallelStream() //TODO Test overhead/performance
                    .forEach(p -> fileMoving(p));

            progressBar1.setValue(progressBar1.getMaximum());

            pathsCollection.clear();
            onlyPNGPathsCollection.clear();
            observableListofFiles.clear();
            System.out.println(Instant.now());

            isCurrentlyRunning = false;
            System.gc(); //TODO Test does it have impact on performance
            return null;
        }

        @Override
        protected void done(){
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

    private void infoWindowCreator(String text){
        InfoWindow window = new InfoWindow(mybundle.getString(text));
        window.pack();
        window.setLocationRelativeTo(null);
        window.setVisible(true);
    }
}
