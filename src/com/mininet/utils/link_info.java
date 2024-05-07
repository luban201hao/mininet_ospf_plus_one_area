package com.mininet.utils;

public class link_info {
    private double lat_left;
    private double lat_right;
    private int x0;
    private int y0;

    private int[] phase_matrix;
    private int phase_count;

    public link_info(double lat_left, double lat_right, int x0, int y0,int phase_count) {
        this.lat_left = lat_left;
        this.lat_right = lat_right;
        this.x0 = x0;
        this.y0 = y0;
        this.phase_count=phase_count;
        this.phase_matrix=new int[phase_count];
        for(int i=0;i<this.phase_count;++i)
        {
            this.phase_matrix[i]=0;
        }
    }

    public double getLat_left() {
        return lat_left;
    }

    public void setLat_left(double lat_left) {
        this.lat_left = lat_left;
    }

    public double getLat_right() {
        return lat_right;
    }

    public void setLat_right(double lat_right) {
        this.lat_right = lat_right;
    }

    public int getX0() {
        return x0;
    }

    public void setX0(int x0) {
        this.x0 = x0;
    }

    public int getY0() {
        return y0;
    }

    public void setY0(int y0) {
        this.y0 = y0;
    }

    public int[] getPhase_matrix() {
        return phase_matrix;
    }

    public void setPhase_matrix(int[] phase_matrix) {
        this.phase_matrix = phase_matrix;
    }

    public int getPhase_count() {
        return phase_count;
    }

    public void setPhase_count(int phase_count) {
        this.phase_count = phase_count;
    }
}
