import java.io.File;
import java.util.Scanner;

public class Calculator {
                          // 379.39 seconds boot
                          // 1001000 Files 1002000 Folders
                          // 454.88 seconds shutdown
    
    static int maxNum = 1001; // You can add numbers from 0 to max number - 1
    static int numThreads = maxNum/4;
    static File numberFolder = new File("Numbers");
    public static void main(String[] args) throws Exception {
        // Bootup
        numberFolder.mkdir();
        long bootupStart = System.nanoTime();
        calculateNumbers(numThreads);
        long bootupEnd = System.nanoTime();
        System.out.println("Bootup time was: " + (double)(bootupEnd - bootupStart)/1_000_000_000 + " seconds");
        
        // First input
        Scanner scanner = new Scanner(System.in);
        int first;
        do {
            System.out.println("Input Number between 0-" + (maxNum-1));
        }
        while ((first = scanner.nextInt()) > (maxNum-1));
        
        // Second input
        int second;
        do {
            System.out.println("Input Number between 0-" + (maxNum-1));
        }
        while ((second = scanner.nextInt()) > (maxNum-1));
        
        // Get answer
        File numberFile = new File(numberFolder, first + "\\" + second);
        System.out.println("Your answer is: " + numberFile.list()[0]);
        scanner.close();
        
        // Delete everything
        long shutdownStart = System.nanoTime();
        deleteNumbers(numThreads);
        long shutdownEnd = System.nanoTime();
        System.out.println("Shutdown time was: " + (double)(shutdownEnd - shutdownStart)/1_000_000_000 + " seconds");
        
    }
    
    private static void calculateNumbers(int numThreads) throws Exception {        
        int rowPerThread = maxNum/numThreads;
        CalcThread[] threads = new CalcThread[numThreads];
        for (int i = 0; i < numThreads-1; i++) {
            threads[i] = new CalcThread((rowPerThread * i), (rowPerThread * (i + 1)), maxNum);
            threads[i].start();
        }
        threads[threads.length-1] = new CalcThread(maxNum - rowPerThread, maxNum, maxNum);
        threads[threads.length-1].start();
        for (int i = 0; i < numThreads; i++) {
            threads[i].join();
        }
    }
    
    public static void deleteNumbers(int numThreads) throws Exception {
        int rowPerThread = maxNum/numThreads;
        DeleteThread[] threads = new DeleteThread[numThreads];
        for (int i = 0; i < numThreads-1; i++) {
            threads[i] = new DeleteThread((rowPerThread * i), (rowPerThread * (i + 1)), maxNum);
            threads[i].start();
        }
        threads[threads.length-1] = new DeleteThread(maxNum - rowPerThread, maxNum, maxNum);
        threads[threads.length-1].start();
        for (int i = 0; i < numThreads; i++) {
            threads[i].join();
        }
        numberFolder.delete();
    }
    
    public static class CalcThread extends Thread {
        
        private int startRow;
        private int endRow;
        private int numColumns;
        
        public CalcThread(int startRow, int endRow, int numColumns) {
            this.startRow = startRow;
            this.endRow = endRow;
            this.numColumns = numColumns;
        }
        
        public void run() {
            try {
                for (int i = startRow; i < endRow; i++) {
                    new File("Numbers" + "\\" + i).mkdir();
                    for (int j = 0; j < numColumns; j++) {
                        new File("Numbers" + "\\" + i + "\\" + j).mkdir();
                        new File("Numbers" + "\\" + i + "\\" + j + "\\" + (i + j)).createNewFile();
                    }
                }
            } catch (Exception e) {e.printStackTrace();}
        }
    }
    
    public static class DeleteThread extends Thread {
        
        private int startRow;
        private int endRow;
        private int numColumns;
        
        public DeleteThread(int startRow, int endRow, int numColumns) {
            this.startRow = startRow;
            this.endRow = endRow;
            this.numColumns = numColumns;
        }
        
        public void run() {
            try {
                for (int i = startRow; i < endRow; i++) {
                    for (int j = 0; j < numColumns; j++) {
                        new File("Numbers\\" + i + "\\" + j + "\\" + (i+j)).delete();
                        new File("Numbers\\" + i + "\\" + j).delete();
                    }
                    new File("Numbers\\" + i).delete();
                }
            } catch (Exception e) {e.printStackTrace();}
        }
    }
}

