package com.mininet.generate;

import com.mininet.utils.CalUpDown;

import java.io.File;
import java.io.IOException;

public class Final_Area {
    int n;
    int F;
    int P;
    double latM;
    String topoPath;
    String running_dir;

    public Final_Area(int n, int orbit, int f, double latM, String topoPath, String running_dir) {
        this.n = n;
        this.P = orbit;
        this.F = f;
        this.latM = latM;
        this.topoPath = topoPath;
        this.running_dir = running_dir;
    }
    public void gen() throws Exception {
        Area_ospf_conf area_ospf_conf=new Area_ospf_conf(n,P);
        CalUpDown calUpDown=new CalUpDown(n, P, F, latM,"Area_updown.txt","Area_isls.txt",1);

        area_ospf_conf.mkisls("Area_isls.txt");
        calUpDown.calc();
        calUpDown.write_isls();
        calUpDown.make_neighbor();

        area_ospf_conf.mktxt("Area_isls.txt","Area_eth.txt","Area_link.txt",calUpDown.getK());
        area_ospf_conf.maketopo("Area_eth.txt","Area_link.txt",topoPath,running_dir,calUpDown.getK(),n,P,calUpDown.getPhaseCount());
        area_ospf_conf.ospfd_conf("Area_eth.txt","ospf_conf",running_dir,calUpDown.getPhaseCount());
    }
    public static void clearDir(String dir)
    {
        File directory = new File(dir);
        for(String child:directory.list())
        {
            System.out.println(child);
            File childFile=new File(directory.getAbsoluteFile()+"/"+child);
            childFile.delete();
        }
    }

    public static void clearTrash()
    {

        clearDir("neighbor");
        clearDir("ospf_conf");
        clearDir("if_se");
        File file1=new File("Area_eth.txt");
        file1.delete();
        file1=new File("Area_isls.txt");
        file1.delete();
        file1=new File("Area_link.txt");
        file1.delete();
        file1=new File("Area_updown.txt");
        file1.delete();
    }

    public static void main(String[] args) {
        clearTrash();
    }
}
