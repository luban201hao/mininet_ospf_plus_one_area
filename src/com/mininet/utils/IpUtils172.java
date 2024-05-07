package com.mininet.utils;

public class IpUtils172 {
    public long linkNumber2;
    public long ip00,ip01,ip10,ip11;
    public String ipStr00,ipStr01,ipStr10,ipStr11;

    public IpUtils172(int _linkNumber)
    {
        this.linkNumber2=_linkNumber;
        this.calc();
    }

    public void setLinkNumber(long _linkNumber) {
        this.linkNumber2 = _linkNumber;
        this.calc();
    }

    public void calc()
    {
        ip00=((long)172<<24)+((long)1<<20)+(this.linkNumber2<<2);
        ip01=((long)172<<24)+((long)1<<20)+(this.linkNumber2<<2)+1;
        ip10=((long)172<<24)+((long)1<<20)+(this.linkNumber2<<2)+2;
        ip11=((long)172<<24)+((long)1<<20)+(this.linkNumber2<<2)+3;
        ipStr00=int2Str(ip00);
        ipStr01=int2Str(ip01);
        ipStr10=int2Str(ip10);
        ipStr11=int2Str(ip11);
    }

    public String int2Str(long intIp){
        String ipStr,ipStr1,ipStr2,ipStr3,ipStr4;
        long ip1=(intIp&0xff000000)>>24;
        long ip2=(intIp&0xff0000)>>16;
        long ip3=(intIp&0xff00)>>8;
        long ip4=(intIp&0xff);
        //System.out.printf("%x,%x,%x,%x\n",ip1,ip2,ip4,ip4);
        ipStr1=String.valueOf(ip1);
        ipStr2=String.valueOf(ip2);
        ipStr3=String.valueOf(ip3);
        ipStr4=String.valueOf(ip4);
        ipStr=ipStr1+'.'+ipStr2+'.'+ipStr3+'.'+ipStr4;
        return ipStr;
    }
    public long str2Long(String ipStr){
        String[] arr = ipStr.split(".");
        return (Long.parseLong(arr[0])<<24)+(Long.parseLong(arr[1])<<16)+(Long.parseLong(arr[2])<<8)+(Long.parseLong(arr[3]));
    }

    public static int getIP2(String ipStr)
    {
        String tmp=ipStr.split("/")[0];
        System.out.println(tmp);
        String[] arr=tmp.split("\\.");
        System.out.println(arr.length);

        long ip=(Long.parseLong(arr[0])<<24)+(Long.parseLong(arr[1])<<16)+(Long.parseLong(arr[2])<<8)+(Long.parseLong(arr[3]));
        return (int)(ip & (long)0xff000)>>12;
    }

    public static int getEnd(String ipStr)
    {
        String tmp=ipStr.split("/")[0];
        String[] arr=tmp.split("\\.");
        int ipEnd=Integer.parseInt(arr[3]);
        return (ipEnd & 0x11);
    }

}
