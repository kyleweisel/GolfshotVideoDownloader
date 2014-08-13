import java.io.*;
import java.util.ArrayList;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;


/**
 * Application to download the GolfShot teaching videos.
 */
public final class Application {

    /* Change these settings */
    public static final String DOWNLOAD_DIRECTORY = "/Users/kbw28/Desktop/Golfshot";
    public static final String FFMPEG_BINARY = "/Applications/FFMPEG_BINARY";
    private static final int NUMBER_OF_THREADS = 4;

    /* No touchy below here! */
    private final ExecutorService executorService;

    /**
     * Instantiates the application and sets up an executor service.
     */
    public Application() {
        
        this.executorService = Executors.newFixedThreadPool(NUMBER_OF_THREADS);
        
    }

    /**
     * Starts the application.  A call to this public method will put the
     * application in motion and begin downloading the files.
     * @param targetFile is a String path to a file that contains line-delimited target URLs.
     */
    public final void start(final String targetFile) {
        
        ArrayList<String> targetList = this.getTargetsFromFile(targetFile);
        this.downloadTargetsOnList(targetList);
        
    }

    /**
     * Downloads target URLs in a String List.
     * @param list is the String list that describes each target to download.
     */
    private void downloadTargetsOnList(final ArrayList<String> list) {

        final int listSize = list.size();

        for (int i = 0; i < listSize; i++) {

            final String temp = list.get(i);
            final String sourceFile = temp;
            final String destinationFile = DOWNLOAD_DIRECTORY + "/" + this.getFileNameFromURL(temp) + ".mp4";
            final String destinationLogFile = DOWNLOAD_DIRECTORY + "/" + this.getFileNameFromURL(temp) + ".log";

            this.executorService.submit(new Downloader(i, sourceFile, destinationFile, destinationLogFile));

        }

        this.executorService.shutdown();

        while (! this.executorService.isTerminated()) {
            // Busy wait
        }

        System.out.println("The executor service has shut down.  Job finished!");

    }

    /**
     * Super duper hackish way to determine the file name of the video from a
     * URL.  This should really be done using regex...
     * @param url is a String URL to grab the file name from.
     * @return a String that is the file name without the extension.
     */
    private String getFileNameFromURL(final String url) {

        String[] temp = url.split("/");
        String fileNameWithExtension = temp[temp.length - 1];
        temp = fileNameWithExtension.split("\\.");
        String fileName = temp[0];

        return fileName;

    }

    /**
     * Parses a file for targets.  Each target URL should be delimited with
     * a line.
     * @param fileName is the name of the file to read as a String.
     * @return an ArrayList of <String> with the URL data from the file.
     */
    private ArrayList<String> getTargetsFromFile(final String fileName) {

        ArrayList<String> product = new ArrayList<String>();

        FileInputStream fileInputStream;

        try {

            fileInputStream = new FileInputStream(fileName);
            BufferedReader bufferedReader = new BufferedReader(new InputStreamReader(fileInputStream));

            String line;
            while ((line = bufferedReader.readLine()) != null)   {
                product.add(line);
            }

            bufferedReader.close();

        }

        catch (FileNotFoundException exception) {
            System.out.println("Unable to find that file!");
            exception.printStackTrace();

        }

        catch (IOException exception) {
            exception.printStackTrace();
        }

        return product;

    }

}