package com.mininet.utils;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileReader;
import java.util.ArrayList;
import java.util.List;

public class FileToMatrix {
    public static double[][] fileToDouble(String pathName) throws Exception {
        File file = new File(pathName);
        if (!file.exists())
            throw new RuntimeException("Not File!");
        BufferedReader br = new BufferedReader(new FileReader(file));
        String str;
        List<double[]> list = new ArrayList<double[]>();
        while ((str = br.readLine()) != null) {
            int s = 0;
            String[] arr = str.split(",");
            double[] dArr = new double[arr.length];
            for (String ss : arr) {
                if (ss != null) {
                    dArr[s++] = Double.parseDouble(ss);
                }

            }
            list.add(dArr);
        }
        int max = 0;
        for (int i = 0; i < list.size(); i++) {
            if (max < list.get(i).length)
                max = list.get(i).length;
        }
        double[][] array = new double[list.size()][max];
        for (int i = 0; i < array.length; i++) {
            for (int j = 0; j < list.get(i).length; j++) {
                array[i][j] = list.get(i)[j];
            }
        }
        return array;
    }
    public static int[][] fileToInt(String pathName) throws Exception {
        File file = new File(pathName);
        if (!file.exists())
            throw new RuntimeException("Not File!");
        BufferedReader br = new BufferedReader(new FileReader(file));
        String str;
        List<int[]> list = new ArrayList<int[]>();
        while ((str = br.readLine()) != null) {
            int s = 0;
            String[] arr = str.split(",");
            int[] dArr = new int[arr.length];
            for (String ss : arr) {
                if (ss != null) {
                    dArr[s++] = Integer.parseInt(ss);
                }

            }
            list.add(dArr);
        }
        int max = 0;
        for (int i = 0; i < list.size(); i++) {
            if (max < list.get(i).length)
                max = list.get(i).length;
        }
        int[][] array = new int[list.size()][max];
        for (int i = 0; i < array.length; i++) {
            for (int j = 0; j < list.get(i).length; j++) {
                array[i][j] = list.get(i)[j];
            }
        }
        return array;
    }
    public static String[][] fileToString(String pathName) throws Exception {
        File file = new File(pathName);
        if (!file.exists())
            throw new RuntimeException("Not File!");
        BufferedReader br = new BufferedReader(new FileReader(file));
        String str;
        List<String[]> list = new ArrayList<String[]>();
        while ((str = br.readLine()) != null) {
            int s = 0;
            String[] arr = str.split(",");
            String[] dArr = new String[arr.length];
            for (String ss : arr) {
                if (ss != null) {
                    dArr[s++] = ss;
                }

            }
            list.add(dArr);
        }
        int max = 0;
        for (int i = 0; i < list.size(); i++) {
            if (max < list.get(i).length)
                max = list.get(i).length;
        }
        String[][] array = new String[list.size()][max];
        for (int i = 0; i < array.length; i++) {
            for (int j = 0; j < list.get(i).length; j++) {
                array[i][j] = list.get(i)[j];
            }
        }
        return array;
    }
}
