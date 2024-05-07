package com.mininet.utils;

import java.util.Arrays;

public class IpUtils_AS {
    public int Byte1,Byte2,Byte3;
    public int ipInt0,ipInt1,ipInt2,ipInt255;
    public String ipStr0,ipStr1,ipStr2,ipStr255;

    public IpUtils_AS(int byte1, int byte2, int byte3) {
        Byte1 = byte1;
        Byte2 = byte2;
        Byte3 = byte3;
        this.calculate();
    }
    public void calculate(){

        ipInt0=(Byte1<<24)+(Byte2<<16)+(Byte3<<8);
        ipInt1=(Byte1<<24)+(Byte2<<16)+(Byte3<<8)+1;
        ipInt1=(Byte1<<24)+(Byte2<<16)+(Byte3<<8)+2;
        ipInt255=(Byte1<<24)+(Byte2<<16)+(Byte3<<8)+255;
        ipStr0=String.valueOf(Byte1)+"."+String.valueOf(Byte2)+"."+String.valueOf(Byte3)+".0";
        ipStr1=String.valueOf(Byte1)+"."+String.valueOf(Byte2)+"."+String.valueOf(Byte3)+".1";
        ipStr2=String.valueOf(Byte1)+"."+String.valueOf(Byte2)+"."+String.valueOf(Byte3)+".2";
        ipStr255=String.valueOf(Byte1)+"."+String.valueOf(Byte2)+"."+String.valueOf(Byte3)+".255";
    }
    //设置对应位后，就对于IP地址进行重新计算
    public int getByte1() {
        return Byte1;
    }

    public void setByte1(int byte1) {
        Byte1 = byte1;
        this.calculate();
    }

    public int getByte2() {
        return Byte2;
    }

    public void setByte2(int byte2) {
        Byte2 = byte2;
        this.calculate();
    }

    public int getByte3() {
        return Byte3;
    }

    public void setByte3(int byte3) {
        Byte3 = byte3;
        this.calculate();
    }
    public void addByte2(int addnum){
        if(Byte2>=255){
            System.out.println("can't add any more\n");
            return;
        }
        Byte2+=addnum;
        this.calculate();

    }
    public void addByte3(int addnum){
        if(Byte3>=255){
            System.out.println("can't add any more\n");
            return;
        }
        Byte3+=addnum;
        this.calculate();
    }
    public void subByte2(int subnum){
        if(Byte2<=0){
            System.out.println("can't sub any more\n");
            return;
        }
        Byte2-=subnum;
        this.calculate();

    }
    public void subByte3(int subnum){
        if(Byte2<=0){
            System.out.println("can't sub any more\n");
            return;
        }
        Byte2-=subnum;
        this.calculate();
    }

    public void setIp2Ip3(int ip2,int ip3){
        this.Byte2=ip2;
        this.Byte3=ip3;
        this.calculate();
    }

    public void addNumber(){
        if(Byte3>255){
            Byte3=0;
            Byte2++;
            if(Byte2>255){
                System.out.println(">255*255 nets,error");
            }
            this.calculate();
            return;
        }
        Byte3++;
        this.calculate();
    }

    public static int[] ipStrPrefixToIntArray(String ipStr_prefix){
        int[] ipArray=new int[5];

        String[] arr = ipStr_prefix.split("/");

        String[] arr1=arr[0].split("\\.");

        ipArray[0]=Integer.parseInt(arr1[0]);
        ipArray[1]=Integer.parseInt(arr1[1]);
        ipArray[2]=Integer.parseInt(arr1[2]);
        ipArray[3]=Integer.parseInt(arr1[3]);
        ipArray[4]=Integer.parseInt(arr[1]);

        return ipArray;
    }
    public static String ipStrPeer(String ipStr_prefix){
        int[] ipArray=ipStrPrefixToIntArray(ipStr_prefix);
        if(ipArray[3]%2==1){
            ipArray[3]+=1;
        }
        else if(ipArray[3]%2==0){
            ipArray[3]-=1;
        }
        return String.valueOf(ipArray[0])+"."+String.valueOf(ipArray[1])+"."+String.valueOf(ipArray[2])+"."+String.valueOf(ipArray[3]);
    }
}
