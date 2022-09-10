package com.company;
import java.awt.Color;
import java.awt.image.BufferedImage;
import java.io.*;
import java.util.Arrays;
import javax.imageio.*;


class Main{



    class MyThread extends Thread {
        private BufferedImage img;
        private int s_h;
        private int e_h;

        public MyThread(BufferedImage img, int s_h, int e_h) {
            this.img = img;
            this.s_h = s_h;
            this.e_h = e_h;

        }


        public void run() {
            Color[] pixel=new Color[9];
            int[] R=new int[9];
            int[] B=new int[9];
            int[] G=new int[9];



            for(int i = 1;i<img.getWidth() - 1;i++) {
                for (int j = s_h + 1; j < e_h - 1; j++) {

                    pixel[0] = new Color(img.getRGB(i - 1, j - 1));
                    pixel[1] = new Color(img.getRGB(i - 1, j));
                    pixel[2] = new Color(img.getRGB(i - 1, j + 1));

                    pixel[3] = new Color(img.getRGB(i, j - 1));
                    pixel[4] = new Color(img.getRGB(i, j));
                    pixel[5] = new Color(img.getRGB(i, j + 1));

                    pixel[6] = new Color(img.getRGB(i + 1, j - 1));
                    pixel[7] = new Color(img.getRGB(i + 1, j));
                    pixel[8] = new Color(img.getRGB(i + 1, j + 1));

                    for (int k = 0; k < 9; k++) {
                        R[k] = pixel[k].getRed();
                        B[k] = pixel[k].getBlue();
                        G[k] = pixel[k].getGreen();

                    }

                    Arrays.sort(R);
                    Arrays.sort(G);
                    Arrays.sort(B);
                    img.setRGB(i, j, new Color(R[4], B[4], G[4]).getRGB());
                }
            }
            black_lines(img, e_h,s_h);
        }
    }


    public  void black_lines(BufferedImage img, int e_h, int s_h)

    {
        for (int i = 0; i <img.getWidth();i++)
        {
            img.setRGB(i, e_h-1, new Color(0, 0, 0).getRGB());

        }

        for (int i = 0; i <img.getWidth();i++)
        {
            img.setRGB(i, s_h, new Color(0, 0, 0).getRGB());

        }

        for (int i = s_h; i <e_h;i++)
        {
            img.setRGB(0, i, new Color(0, 0, 0).getRGB());

        }
        for (int i = s_h; i <e_h;i++)
        {
            img.setRGB(img.getWidth()-1, i, new Color(0, 0, 0).getRGB());

        }

    }
    public void processing_image(BufferedImage img) throws IOException {



        Color[] pixel=new Color[9];
        int[] R=new int[9];
        int[] B=new int[9];
        int[] G=new int[9];

        final double cr = 0.241;
        final double cg = 0.691;
        final double cb = 0.068;

        double[] res = new double [9];

        for(int i = 1;i < img.getWidth() - 1;i++) {
            for (int j = 1; j < img.getHeight()-1; j++) {

                pixel[0] = new Color(img.getRGB(i - 1, j - 1));
                pixel[1] = new Color(img.getRGB(i - 1, j));
                pixel[2] = new Color(img.getRGB(i - 1, j + 1));

                pixel[3] = new Color(img.getRGB(i, j - 1));
                pixel[4] = new Color(img.getRGB(i, j));
                pixel[5] = new Color(img.getRGB(i, j + 1));

                pixel[6] = new Color(img.getRGB(i + 1, j - 1));
                pixel[7] = new Color(img.getRGB(i + 1, j));
                pixel[8] = new Color(img.getRGB(i + 1, j + 1));


                for (int k = 0; k < 9; k++) {
                    R[k] = pixel[k].getRed();
                    B[k] = pixel[k].getBlue();
                    G[k] = pixel[k].getGreen();

                    res[k] = Math.sqrt(cr * R[k] * R[k] + cg * G[k] * G[k] + cb * B[k] * B[k]);
                }

                Arrays.sort(res);
//                Arrays.sort(R);
//                Arrays.sort(G);
//                Arrays.sort(B);
                System.out.println(res[4]);
                img.setRGB(i, j, new Color(R[4], B[4], G[4]).getRGB());

            }
        }
        black_lines(img, img.getHeight(),0);
        File output=new File("outputSequental.jpg");
        ImageIO.write(img,"jpg",output);
    }



    public void processing_image_parallel(BufferedImage img) throws IOException, InterruptedException {
        int threads = 1;
        int img_height = img.getHeight();
        int heightPerThread = img_height / threads;

        int h = 0;

        MyThread[] threads_list = new MyThread[threads];
        for (int i = 0; i < threads; i++) {
            threads_list[i] = new MyThread(img, h,h + heightPerThread);
            threads_list[i].start();
            h += heightPerThread;

        }
        for (int i = 0; i < threads; i++) {
            threads_list[i].join();
        }

        File output=new File("outputParallel.jpg");
        ImageIO.write(img,"jpg",output);



    }

    public static void main(String[] args) throws IOException, InterruptedException {
        Main obj = new Main();
        File img_file = new File("21321.jpg");
        BufferedImage img = ImageIO.read(img_file);
        BufferedImage img2 = ImageIO.read(img_file);

        long startTime = System.currentTimeMillis();
        obj.processing_image(img);
        long endTime = System.currentTimeMillis();

        long startTimePar = System.currentTimeMillis();
        obj.processing_image_parallel(img2);
        long endTimePar = System.currentTimeMillis();


        System.out.println(" Time Sequental -> " + (endTime - startTime));
        System.out.println(" Time Parallel -> " + (endTimePar - startTimePar));

    }

}