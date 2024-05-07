package com.mininet.utils;

public class IpUtils_AS30 {
    //其中num1占据前8位，num2占据9-16位，num3占据17-30位
    public int num1,num2,num3;
    public int ipInt00,ipInt01,ipInt10,ipInt11;
    public String ipStr00,ipStr01,ipStr10,ipStr11;

    public IpUtils_AS30(int num1, int num2, int num3) {
        this.num1 = num1;
        this.num2 = num2;
        this.num3 = num3;
        this.calc();
    }
    public void calc()
    {
        ipInt00=(num1<<24)+(num2<<16)+(num3<<2);
        ipInt01=ipInt00+1;
        ipInt10=ipInt00+2;
        ipInt11=ipInt00+3;
        this.ipStr00=this.int2Str(ipInt00);
        this.ipStr01=this.int2Str(ipInt01);
        this.ipStr10=this.int2Str(ipInt10);
        this.ipStr11=this.int2Str(ipInt11);
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

    public void setNum1(int num1) {
        this.num1 = num1;
        this.calc();
    }

    public void setNum2(int num2) {
        this.num2 = num2;
        this.calc();
    }

    public void setNum3(int num3) {
        this.num3 = num3;
        this.calc();
    }

    public void addNum3()
    {
        this.num3+=1;
        if(this.num3>(1<<14))
        {
            this.num3=0;
            this.num2++;
        }
        this.calc();
    }
    public void addNum2()
    {
        this.num2+=1;
        this.calc();
    }
}
