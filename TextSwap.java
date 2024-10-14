import java.io.*;
import java.util.*;

public class TextSwap {
    public static final int ENGLISH_ALPHABET_LENGTH = 26;

    private static String readFile(String filename, int chunkSize) throws Exception {
        String line;
        StringBuilder buffer = new StringBuilder();
        File file = new File(filename);
        BufferedReader br = new BufferedReader(new FileReader(file));
        while ((line = br.readLine()) != null){
            buffer.append(line);
        }
        br.close();
        return buffer.toString();
    }

    private static Interval[] getIntervals(int numChunks, int chunkSize) {
        Interval[] intervals = new Interval[numChunks];

        for(int i = 0; i < numChunks; i++) {
            intervals[i] = new Interval(i * chunkSize, (i + 1) * chunkSize);
        }
  
        return intervals;
    }

    private static List<Character> getLabels(int numChunks) {
        Scanner scanner = new Scanner(System.in);
        List<Character> labels = new ArrayList<Character>();
        int endChar = numChunks == 0 ? 'a' : 'a' + numChunks - 1;
        System.out.printf("Input %d character(s) (\'%c\' - \'%c\') for the pattern.\n", numChunks, 'a', endChar);
        for (int i = 0; i < numChunks; i++) {
            labels.add(scanner.next().charAt(0));
        }
        scanner.close();
        return labels;
    }

    private static char[] runSwapper(String content, int chunkSize, int numChunks) {
        List<Character> labels = getLabels(numChunks);
        Interval[] intervals = getIntervals(numChunks, chunkSize);
        Thread[] threads = new Thread[numChunks];
        char[] buff = new char[numChunks * chunkSize];

        for(int i = 0; i < numChunks; i++) {
           threads[i] = new Thread(new Swapper(intervals[labels.get(i) - 'a'], content, buff, i * chunkSize));
           threads[i].start();
        }

        for(Thread t : threads){
            try{
                t.join();
            } catch (Exception e) {
                e.printStackTrace();
            }
        }
  
        return buff;
      }

    private static void writeToFile(String contents, int chunkSize, int numChunks) throws Exception {
        char[] buff = runSwapper(contents, chunkSize, contents.length() / chunkSize);
        PrintWriter writer = new PrintWriter("output.txt", "UTF-8");
        writer.print(buff);
        writer.close();
    }

     public static void main(String[] args) {
        if (args.length != 2) {
            System.out.println("Usage: java TextSwap <chunk size> <filename>");
            return;
        }
        String contents = "";
        int chunkSize = Integer.parseInt(args[0]);
        try {
            contents = readFile(args[1],chunkSize);

            if (contents.length() / (double) chunkSize > (double) ENGLISH_ALPHABET_LENGTH) {
                System.out.println("Chunk size too small");
                return;
            } else if (contents.length() % chunkSize != 0) {
                System.out.println("File size must be a multiple of the chunk size");
                return;
            }

            writeToFile(contents, chunkSize, contents.length() / chunkSize);
        } catch (Exception e) {
            System.out.println("Error with IO.");
            return;
        }
    }
}
