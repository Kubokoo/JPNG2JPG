import java.io.File;

public class main {
    public static void main(String[] args){
        File file = new File("dog_PNG50321.png");
        float quality = 1f;

        PNG2JPG image1 = new PNG2JPG(file.getAbsolutePath(),quality);
        if (image1.status) System.out.println("Image converted");
        else System.out.println("Something went wrong");
    }
}
