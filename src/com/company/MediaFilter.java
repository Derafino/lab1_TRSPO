package com.company;

import java.util.Arrays;
import java.util.Random;
import java.util.concurrent.ExecutionException;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.Future;
import java.util.stream.IntStream;

class MedianFilter {

    public static <T extends Comparable<T>> T[] getMedians(T[] data, int shoulder) {
        T[] result = Arrays.copyOf(data, data.length);
        IntStream.range(shoulder, data.length - shoulder).parallel().forEach(i -> result[i] = medianPoint(data, i, shoulder));
        return result;
    }

    private static <T extends Comparable<T>> T medianPoint(T[] data, int index, int shoulder) {
        T[] window = Arrays.copyOfRange(data, index - shoulder, index + shoulder + 1);
        if (window.length != (shoulder * 2 + 1)) {
            throw new IllegalStateException("Illegal window size " + window.length);
        }
        Arrays.sort(window);
        return window[shoulder];
    }

    public static void main(String[] args) throws InterruptedException, ExecutionException {
        Random rand = new Random(100);
        Integer[] data = rand.ints(10, 0, 20).boxed().toArray(Integer[]::new);
        System.out.println(Arrays.toString(data));
        System.out.println(Arrays.toString(getMedians(data, 4)));
        Future<Integer[]> fut = medians(data, 4);
        System.out.println(Arrays.toString(fut.get()));
    }

    public static <T extends Comparable<T>> Future<T[]> medians(T[] data, int shoulder) {
        ExecutorService service = Executors.newSingleThreadExecutor(r -> {
            Thread t = new Thread(r);
            t.setDaemon(true);
            return t;
        });

        Future<T[]> fut =  service.submit(() -> getMedians(data, shoulder));
        service.shutdown();
        return fut;
    }

}