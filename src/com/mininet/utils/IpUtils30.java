package com.mininet.utils;
//已经设定子网掩码长度为30,无法进行修改
public class IpUtils30 {
    public int linkNumber,firstByte;
    public int ip00,ip01,ip10,ip11;
    //分别代表结尾为0（网络地址）、1（1号主机）、2（2号主机）、3（广播地址）IP地址的情况
    public String ipStr00,ipStr01,ipStr10,ipStr11;

    public IpUtils30(int firstByte,int linkNumber) {
        this.linkNumber = linkNumber;
        this.firstByte = firstByte;
        this.calIp();
    }
    public void calIp(){
        this.ip00=(this.linkNumber<<2)+(this.firstByte<<24);
        this.ip01=this.ip00+1;
        this.ip10=this.ip00+2;
        this.ip11=this.ip00+3;
        this.ipStr00=this.int2Str(ip00);
        this.ipStr01=this.int2Str(ip01);
        this.ipStr10=this.int2Str(ip10);
        this.ipStr11=this.int2Str(ip11);
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
    public int int2Number(int intIp){
        return ((intIp&0xffffff)>>2);
    }
    public int str2Number(String strIp){
        int intIp=this.str2Int(strIp);
        return this.int2Number(intIp);
    }

    @Override
    public String toString() {
        return "IpUtils30{" +
                "linkNumber=" + linkNumber +
                ", firstByte=" + firstByte +
                ", ip00=" + ip00 +
                ", ip01=" + ip01 +
                ", ip10=" + ip10 +
                ", ip11=" + ip11 +
                ", ipStr00='" + ipStr00 + '\'' +
                ", ipStr01='" + ipStr01 + '\'' +
                ", ipStr10='" + ipStr10 + '\'' +
                ", ipStr11='" + ipStr11 + '\'' +
                '}';
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
        this.linkNumber=(ip2<<14)+ip3;
        this.calIp();
    }

}
