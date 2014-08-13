import java.io.*;
import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;

/**
 * Download Runnable
 *
 * This runnable runs an FFMpeg process that downloads and converts a .m3u8
 * playlist into a .mp4 video.
 *
 * August 13, 2014
 *
 * @author  Kyle Weisel (kyle@present.tv)
 */
public class Downloader implements Runnable {

    private final int id;
    private final String destFileName;
    private final String sourceFileName;
    private final File logFile;

    /**
     * Instantiates the downloader.
     * @param id is a String ID of this download job.  It should be unique, but doesn't necessairly have to be.
     * @param sourceFileName is the String path to a .m3u8 to be downloaded.  It can be local or on the WWW.
     * @param destFileName is the String path to the .mp4 file to be saved after download and conversion.
     * @param logFileName is a String path to a log file for the download.  FFMpeg's output will be written here.
     */
    public Downloader(final int id, final String sourceFileName, final String destFileName, final String logFileName) {
        this.id = id;
        this.destFileName = destFileName;
        this.sourceFileName = sourceFileName;
        this.logFile = new File(logFileName);
        System.out.println("Instantiated runnable with job number " + this.id);
    }

    /**
     * This method is run on it's own individual thread.
     */
    @Override
    public void run() {

        System.out.println("Job " + this.id + ": starting download of file: " + this.sourceFileName);

        try {

            Process process = Runtime.getRuntime().exec("/Applications/ffmpeg -i " + this.sourceFileName + " -acodec copy -vcodec copy  -y -bsf:a aac_adtstoasc -f mp4 " + this.destFileName);

            BufferedReader standardInput = new BufferedReader(new InputStreamReader(process.getInputStream()));
            BufferedReader standardError = new BufferedReader(new InputStreamReader(process.getErrorStream()));

            this.writeToLog("Response from job " + this.id + ":\n");

            String temp;
            while ((temp = standardInput.readLine()) != null) {
                this.writeToLog(temp);
            }

            this.writeToLog("Standard error from job " + this.id + " (if any):\n");
            while ((temp = standardError.readLine()) != null) {
                this.writeToLog(temp);
            }

        }

        catch (IOException exception) {
            exception.printStackTrace();
            System.exit(-1);
        }

        finally {
            System.out.println("Job " + this.id + ": finished!");
        }

    }

    /**
     * Writes a message to the custom log file.
     * @param message is the message to write to the log file as a string.
     */
    private void writeToLog(String message) {

        Date today = Calendar.getInstance().getTime();
        DateFormat dateFormat = new SimpleDateFormat("MM/dd/yyyy HH:mm:ss");
        String incidentDate = dateFormat.format(today);

        if (!logFile.exists()) {
            try {
                logFile.getParentFile().mkdirs();
                logFile.createNewFile();
            }
            catch (IOException e) {
                e.printStackTrace();
            }
        }

        try {
            //BufferedWriter for performance, true to set append to file flag
            BufferedWriter writer = new BufferedWriter(new FileWriter(logFile, true));
            writer.append(incidentDate + "\t" + message);
            writer.newLine();
            writer.close();
        }
        catch (IOException e) {
            e.printStackTrace();
        }

    }

}
