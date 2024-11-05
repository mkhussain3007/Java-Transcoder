import java.io.IOException;
import java.util.Scanner;
public class VideoToMp3Transcoder {
    public static void main(String[] args) {
        Scanner s = new Scanner(System.in);
        System.out.println("Enter Video name present in current dirctory");
        String inputFilePath = s.next(); // Replace with your input video file path
        System.out.println("Enter name to name the output file");
        String outputFilePath = s.next(); // Replace with your desired output MP3 file path
        System.out.println("Enter output format");
        String outputFormat = s.next();
        // FFmpeg command to transcode video to MP3
        String[] ffmpegCommandmp3 = {
                "ffmpeg",
                "-i", inputFilePath,
                "-vn", // Disable video
                "-acodec", "mp3", // Audio codec: MP3
                "-q:a", "2", // Audio quality (0 - highest, 9 - lowest)
                outputFilePath + "." + outputFormat
        };
        String[] ffmpegCommandh264 = {
                "ffmpeg",
                "-i", inputFilePath,
                "-c:v", "libx264", // Video codec: H.264 (libx264)
                "-crf", "23", // Constant Rate Factor (lower value means higher quality)
                "-preset", "medium", // Preset for encoding speed vs. compression efficiency
                "-c:a", "aac", // Audio codec: AAC
                "-b:a", "128k", // Audio bitrate
                outputFilePath + ".mp4"
        };
        String[] ffmpegCommandavi = {
                "ffmpeg",
                "-i", inputFilePath,
                "-c:v", "mpeg4", // Video codec: MPEG-4
                "-q:v", "2", // Video quality (0 - highest, 31 - lowest)
                "-c:a", "copy", // Copy audio codec
                outputFilePath + "." + outputFormat
        };
        String[] ffmpegCommandacc = {
                "ffmpeg",
                "-i", inputFilePath,
                "-vn", // Disable video
                "-c:a", "aac", // Audio codec: AAC
                "-b:a", "128k", // Audio bitrate
                outputFilePath + "." + outputFormat
        };
        String[] ffmpegCommandMkv = {
                "ffmpeg",
                "-i", inputFilePath,
                "-c:v", "copy", // Copy video codec
                "-c:a", "copy", // Copy audio codec
                outputFilePath
        };
        if (outputFormat.compareTo("mp3") == 0) {
            Transcoder.transcode(ffmpegCommandmp3);
        } else if (outputFormat.compareTo("aac") == 0) {
            Transcoder.transcode(ffmpegCommandacc);
        } else if (outputFormat.compareTo("h264") == 0) {
            Transcoder.transcode(ffmpegCommandh264);
        } else if (outputFormat.compareTo("avi") == 0) {
            Transcoder.transcode(ffmpegCommandavi);
        } else if (outputFormat.compareTo("mkv") == 0) {
            Transcoder.transcode(ffmpegCommandMkv);
        } else {
            System.out.println("Enter a valid format");
        }
    }
}

class StreamGobbler implements Runnable {
    private java.io.InputStream inputStream;

    public StreamGobbler(java.io.InputStream inputStream) {
        this.inputStream = inputStream;
    }

    @Override
    public void run() {
        java.util.Scanner scanner = new java.util.Scanner(inputStream);
        while (scanner.hasNextLine()) {
            System.out.println(scanner.nextLine());
        }
        scanner.close();
    }
}

class Transcoder {
    public static void transcode(String[] ffmpegCommand) {
        try {
            Process process = new ProcessBuilder(ffmpegCommand).redirectErrorStream(true).start();
            // Print FFmpeg console output
            StreamGobbler streamGobbler = new StreamGobbler(process.getInputStream());
            new Thread(streamGobbler).start();
            int exitCode = process.waitFor();
            System.out.println("FFmpeg process exited with code " + exitCode);
        } catch (IOException | InterruptedException e) {
            e.printStackTrace();
        }
    }
}