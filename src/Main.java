import java.util.Scanner;

public class Main {
    public static void main(String[] args) {
        MultiThreadCalculator calculator = new MultiThreadCalculator();
        calculator.Start();
    }
}

class MultiThreadCalculator{
    private long MultiThreadSum = 0;
    private  long ThreadCounter = 0;

    public void Start(){
        Scanner input = new Scanner(System.in);

        System.out.print("Input a array size: ");
        int arraySize = input.nextInt();
        int[] calculateArray = new int[arraySize];
        for (int i = 0; i < arraySize; i++){
            calculateArray[i] = i;
        }

        System.out.print("Input number of threads: ");
        int maxThreads = input.nextInt();
        for (int i = 0; i<maxThreads-1; i++){
            Thread newThread = new Thread(new CalculatorThread(this, calculateArray, arraySize / maxThreads * i, (arraySize / maxThreads * (i+1))-1));
            newThread.start();
        }
        Thread newThread = new Thread(new CalculatorThread(this, calculateArray, arraySize / maxThreads * (maxThreads-1), arraySize-1));
        newThread.start();

        synchronized (this) {
            while (ThreadCounter < maxThreads) {
                try {
                    wait();
                } catch (InterruptedException e) {
                    throw new RuntimeException(e);
                }
            }
        }

        int arrayElementsSum = 0;
        for (int i = 0; i<arraySize;i++){
            arrayElementsSum+=calculateArray[i];
        }

        System.out.println("One thread sum: "+ arrayElementsSum);
        System.out.println("Multiply thread sum: " + MultiThreadSum);
    }

    public synchronized  void  AddPartOfSum(long partSum){
        MultiThreadSum +=partSum;
        ThreadCounter +=1;
        notify();
    }
}

class CalculatorThread implements Runnable{

    public long thisSum = 0;
    int[] ThisArray;
    int begin, end;
    MultiThreadCalculator calculatorRef;

    CalculatorThread(MultiThreadCalculator calculator, int[] array, int begin, int end) {
        this.begin = begin;
        this.end = end;
        ThisArray = array;
        calculatorRef = calculator;
    }

    @Override
    public void run() {
        for (int i = begin; i <= end; i++) {
            thisSum = thisSum + ThisArray[i];
        }
        calculatorRef.AddPartOfSum(thisSum);
    }
}