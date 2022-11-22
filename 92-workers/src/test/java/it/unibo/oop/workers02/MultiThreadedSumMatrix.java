package it.unibo.oop.workers02;

import java.util.ArrayList;
import java.util.List;

public class MultiThreadedSumMatrix implements SumMatrix {
    private final int nthread;

    public MultiThreadedSumMatrix(int n){
        this.nthread=n;
    }

    private static class Worker extends Thread {
        private final double[][] matrix;
        private final int startpos;
        private final int nelem;
        private long res;

        Worker(final double matrix[][], final int startpos, final int nelem) {
            super();
            this.matrix = matrix;
            this.startpos = startpos;
            this.nelem = nelem;
        }

        @Override
        public void run() {
            System.out.println("Working from position " + startpos + " to position " + (startpos + nelem - 1));
            for (int i = startpos; i < startpos + nelem && i < matrix.length; i++) {
                for(int j=0; j<matrix[i].length; j++){
                    this.res += this.matrix[i][j];
                }
            }
        }

        public long getResult() {
            return this.res;
        }

    }

    @Override
    public double sum(double[][] matrix) {
        final int size = matrix.length % nthread + matrix.length / nthread;
        final List<Worker> workers = new ArrayList<>(nthread);
        for(int i=0; i<matrix.length; i+=size){
            workers.add(new Worker(matrix, i, size));
        }
        for (final Worker w: workers) {
            w.start();
        }
        long sum = 0;
        for (final Worker w: workers) {
            try {
                w.join();
                sum += w.getResult();
            } catch (InterruptedException e) {
                throw new IllegalStateException(e);
            }
        }
        return sum;
    }
    
}
