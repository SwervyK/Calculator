import java.io.File;
import java.util.Scanner;

public class Calculator {
    
    static Scanner scanner = new Scanner(System.in);
    static int numCol = 101;
    static int numRow = 101;
    static int numThreads = 4;
    static File numberFolder = new File("Numbers");
    public static void main(String[] args) throws Exception {
        // Bootup
        numberFolder.mkdir();
        long bootupStart = System.nanoTime();
        calculateNumbers(numThreads);
        long bootupEnd = System.nanoTime();
        System.out.println("Bootup time was: " + (double)(bootupEnd - bootupStart)/1_000_000_000 + " seconds"); // 101*101 5.77
        
        // First input
        int first;
        do {
            System.out.println("Input Number beetween 0-" + (numCol-1));
        }
        while ((first = scanner.nextInt()) > (numCol-1));
        
        // Second input
        int second;
        do {
            System.out.println("Input Number beetween 0-" + (numRow-1));
        }
        while ((second = scanner.nextInt()) > (numRow-1));
        
        // Get answer
        File numberFile = new File("Numbers\\" + first + "\\" + second);
        System.out.println(numberFile.list()[0]);
        
        // Delete everything
        long shutdownStart = System.nanoTime();
        deleteFiles(numThreads);
        long shutdownEnd = System.nanoTime();
        System.out.println("Shutdown time was: " + (double)(shutdownEnd - shutdownStart)/1_000_000_000 + " seconds");
        
    }
    
    private static void calculateNumbers(int numThreads) throws Exception {        
        int colPerThread = numCol/numThreads;
        CalcThread[] threads = new CalcThread[numThreads];
        threads[0] = new CalcThread(0, colPerThread + 1, numRow);
        threads[0].start();
        for (int i = 1; i < numThreads-1; i++) {
            threads[i] = new CalcThread((colPerThread * i), (colPerThread * (i + 1)), numRow);
            threads[i].start();
        }
        threads[numThreads-1] = new CalcThread(numCol - colPerThread - 1, numCol, numRow);
        threads[numThreads-1].start();
        for (int i = 0; i < numThreads; i++) {
            threads[i].join();
        }
    }
    
    public static void deleteFiles(int numThreads) throws Exception {
        int colPerThread = numCol/numThreads;
        DeleteThread[] threads = new DeleteThread[numThreads];
        threads[0] = new DeleteThread(0, colPerThread + 1, numRow);
        threads[0].start();
        for (int i = 1; i < numThreads-1; i++) {
            threads[i] = new DeleteThread((colPerThread * i), (colPerThread * (i + 1)), numRow);
            threads[i].start();
        }
        threads[numThreads-1] = new DeleteThread(numCol - colPerThread - 1, numRow, numRow);
        threads[numThreads-1].start();
        for (int i = 0; i < numThreads; i++) {
            threads[i].join();
        }
        numberFolder.delete();
    }
    
    public static class CalcThread extends Thread {
        
        private int startCol;
        private int endCol;
        private int numRow;
        
        public CalcThread(int startCol, int endCol, int numRow) {
            this.startCol = startCol;
            this.endCol = endCol;
            this.numRow = numRow;
        }
        
        public void run() {
            try {
                for (int i = startCol; i < endCol; i++) {
                    File fileCol = new File("Numbers\\" + Integer.toString(i));
                    fileCol.mkdir();
                    for (int j = 0; j < numRow; j++) {
                        File fileRow = new File(fileCol.getPath() + "\\" + Integer.toString(j));
                        fileRow.mkdir();
                        File fileData = new File(fileRow.getPath() + "\\" + Integer.toString(i + j));
                        fileData.createNewFile();
                    }
                }
            } catch (Exception e) {e.printStackTrace();}
        }
    }
    
    public static class DeleteThread extends Thread {
        
        private int startCol;
        private int endCol;
        private int numRow;
        
        public DeleteThread(int startCol, int endCol, int numRow) {
            this.startCol = startCol;
            this.endCol = endCol;
            this.numRow = numRow;
        }
        
        public void run() {
            try {
                for (int i = startCol; i < endCol; i++) {
                    for (int j = 0; j < numRow; j++) {
                        new File("Numbers\\" + i + "\\" + j + "\\" + (i+j)).delete();
                        new File("Numbers\\" + i + "\\" + j).delete();
                    }
                    new File("Numbers\\" + i).delete();
                }
            } catch (Exception e) {e.printStackTrace();}
        }
    }
}

