import java.io.File;
import java.util.Scanner;

public class Calculator {
    
    static int size = 101;
    static int numBootThreads = 101; // Best so far 16
    static int numDeleteThreads = 101;
    static File numberFolder = new File("Numbers");
    public static void main(String[] args) throws Exception {
        // Bootup
        numberFolder.mkdir();
        long bootupStart = System.nanoTime();
        calculateNumbers(numBootThreads);
        long bootupEnd = System.nanoTime();
        System.out.println("Bootup time was: " + (double)(bootupEnd - bootupStart)/1_000_000_000 + " seconds");
        
        // First input
        Scanner scanner = new Scanner(System.in);
        int first;
        do {
            System.out.println("Input Number beetween 0-" + (size-1));
        }
        while ((first = scanner.nextInt()) > (size-1));
        
        // Second input
        int second;
        do {
            System.out.println("Input Number beetween 0-" + (size-1));
        }
        while ((second = scanner.nextInt()) > (size-1));
        
        // Get answer
        File numberFile = new File(numberFolder, first + "\\" + second);
        System.out.println("Your answer is: " + numberFile.list()[0]);
        scanner.close();
        
        // Delete everything
        long shutdownStart = System.nanoTime();
        deleteFiles(numDeleteThreads);
        long shutdownEnd = System.nanoTime();
        System.out.println("Shutdown time was: " + (double)(shutdownEnd - shutdownStart)/1_000_000_000 + " seconds");
        
    }
    
    private static void calculateNumbers(int numThreads) throws Exception {        
        int colPerThread = size/numThreads;
        CalcThread[] threads = new CalcThread[numThreads];
        threads[0] = new CalcThread(0, colPerThread + 1, size);
        threads[0].start();
        for (int i = 1; i < numThreads-1; i++) {
            threads[i] = new CalcThread((colPerThread * i), (colPerThread * (i + 1)), size);
            threads[i].start();
        }
        threads[numThreads-1] = new CalcThread(size - colPerThread - 1, size, size);
        threads[numThreads-1].start();
        for (int i = 0; i < numThreads; i++) {
            threads[i].join();
        }
    }
    
    public static void deleteFiles(int numThreads) throws Exception {
        int colPerThread = size/numThreads;
        DeleteThread[] threads = new DeleteThread[numThreads];
        threads[0] = new DeleteThread(0, colPerThread + 1, size);
        threads[0].start();
        for (int i = 1; i < numThreads-1; i++) {
            threads[i] = new DeleteThread((colPerThread * i), (colPerThread * (i + 1)), size);
            threads[i].start();
        }
        threads[numThreads-1] = new DeleteThread(size - colPerThread - 1, size, size);
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
                    new File("Numbers" + "\\" + i).mkdir();
                    for (int j = 0; j < numRow; j++) {
                        new File("Numbers" + "\\" + i + "\\" + j).mkdir();
                        new File("Numbers" + "\\" + i + "\\" + j + "\\" + (i + j)).createNewFile();
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

