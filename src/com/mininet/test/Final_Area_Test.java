package com.mininet.test;

import com.mininet.config.Config;
import com.mininet.generate.Final_Area;

public class Final_Area_Test {
    public static void main(String[] args) throws Exception {
        Config.setDisInterStarAndCalc((int)(2*Math.PI*(6371.0+780)/20.0));
        Config.setDisStarEarthAndCalc(780);
        // f = 1/2 P
        Final_Area final_area=new Final_Area(20,96,48,67.5,"Area_1920.py","/home/test/wyc/testdata2/Area_1920");
        Final_Area.clearTrash();
        final_area.gen();
    }
}
