import javax.imageio.IIOImage;
import javax.imageio.ImageIO;
import javax.imageio.ImageWriteParam;
import javax.imageio.ImageWriter;
import javax.imageio.plugins.jpeg.JPEGImageWriteParam;
import javax.imageio.stream.FileImageOutputStream;
import java.awt.image.BufferedImage;
import java.io.*;

public class PNG2JPG {
    protected boolean status;

     PNG2JPG(String path,float quality){
        File file = new File(path);

        try {
            BufferedImage image = ImageIO.read(file);
            String fileName = file.getName();
            path = path.substring(0,path.lastIndexOf('\\'));
            fileName = fileName.substring(0,fileName.lastIndexOf('.'));

            JPEGImageWriteParam jpegParams = new JPEGImageWriteParam(null);
            jpegParams.setCompressionMode(ImageWriteParam.MODE_EXPLICIT);
            jpegParams.setCompressionQuality(quality); //Quality from 0 to 1

            final ImageWriter writer = ImageIO.getImageWritersByFormatName("jpg").next();
            writer.setOutput(new FileImageOutputStream(
                    new File(path + "/" + fileName + ".jpg")));
            writer.write(null, new IIOImage(image, null, null), jpegParams);
            status = true;
        }

        catch (IOException e){
            System.out.println("No file found");
            status = false;
        }
    }
}