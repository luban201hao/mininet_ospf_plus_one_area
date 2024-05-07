package com.mininet.config;
import org.apache.commons.math3.special.Erf;

public class Config {
    public static int isServer = 1;
    public static int hasDelay = 0;
    public static double delayInterStar = 10;  // ms
    public static double lossInterStar = 1;   // %
    public static int distanceInterStar = 1000;  // km
    public static double delayStarEarth = 10;
    public static double lossStarEarth = 10;
    public static int distanceStarEarth = 780;

    private static double calcLoss(Double dis_km)
    {
        double EIRP = 50.0;
        double GT = 18.5;
        double f = 40.0;
        double Rb = 1000.0;
        double EbN0 = EIRP + GT - (92.45 + 20.0 * Math.log10(dis_km) + 20.0 * Math.log10(f)) + 228.6 -60.0 - 10.0 * Math.log10(Rb);
        double BER = 0.5 * Erf.erfc(Math.sqrt(EbN0));
        double loss = 1 - Math.pow((1 - BER), 1500 * 8);
        return loss * 100.0 / 2;
    }
    public static void setDisInterStarAndCalc(int dis_km)
    {
        distanceInterStar = dis_km;
        delayInterStar = (double) dis_km / 300.0f / 2;
        lossInterStar = calcLoss(Double.valueOf(dis_km));
    }
    public static void setDisStarEarthAndCalc(int dis_km)
    {
        distanceStarEarth = dis_km;
        delayStarEarth = (double) dis_km / 300.0f / 2;
        lossStarEarth = calcLoss(Double.valueOf(dis_km));
    }

    public static void main(String[] args) {
        double ret = calcLoss(Double.valueOf(2*Math.PI *(6371+780) / 20));
        System.out.println(ret);
    }
}
