package com.mininet.utils;

public class IpUtils32 {
    public int linkNumber,firstByte;
    public int ipInt;
    public String ipStr;
    public IpUtils32(int firstByte, int linkNumber) {
        this.linkNumber = linkNumber;
        this.firstByte = firstByte;
        this.calIp();
    }
    public void calIp()
    {
        this.ipInt = (firstByte<<24) + linkNumber;
        this.ipStr = int2Str(this.ipInt);
    }

    public String int2Str(int intIp){
        String ipStr,ipStr1,ipStr2,ipStr3,ipStr4;
        ipStr1=String.valueOf((intIp&0xff000000)>>24);
        ipStr2=String.valueOf((intIp&0xff0000)>>16);
        ipStr3=String.valueOf((intIp&0xff00)>>8);
        ipStr4=String.valueOf((intIp&0xff));
        ipStr=ipStr1+'.'+ipStr2+'.'+ipStr3+'.'+ipStr4;
        return ipStr;
    }
    public int str2Int(String ipStr){
        String[] arr = ipStr.split(".");
        return (Integer.parseInt(arr[0])<<24)+(Integer.parseInt(arr[1])<<16)+(Integer.parseInt(arr[2])<<8)+(Integer.parseInt(arr[0]));
    }

    public void addIp(int addNum){
        this.linkNumber+=addNum;
        this.calIp();
    }
    public void subIp(int subNum){
        this.linkNumber-=subNum;
        this.calIp();
    }
    public void setIp(int firstByte,int linkNumber){
        this.firstByte=firstByte;
        this.linkNumber=linkNumber;
        this.calIp();
    }

    public int getFirstByte() {
        return firstByte;
    }

    public void setFirstByte(int firstByte) {
        this.firstByte = firstByte;
    }

    public void setIp2Ip3(int ip2, int ip3)
    {
        this.linkNumber = (ip2<<8) + ip3;
        this.calIp();
    }
}
