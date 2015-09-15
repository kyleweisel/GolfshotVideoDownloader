import java.io.File;
import java.util.Scanner;

public final class Main {

    /**
     * Program entry point.
     * @param args are a number of String arguments provided from the command line.
     */
    public static void main(String[] args) {

        String targetFilePath = null;
        boolean keepGoing = false;

        final Scanner scanner = new Scanner(System.in);

        while(! keepGoing) {

            System.out.println("Enter path to the targets file: ");
            targetFilePath = scanner.nextLine();

            File file = new File(targetFilePath);
            if (file.exists() && ! file.isDirectory()) {
                System.out.println("Attempting to use target file at: " + targetFilePath);
                keepGoing = true;
            }
            else {
                System.out.println("No file exists at: " + targetFilePath + ".  Try again!\n\n");
                keepGoing = false;
            }

        }

        scanner.close();

        /* Target file path is guaranteed to exist */
        Application application = new Application();
        application.start(targetFilePath);
        
     }

}
