package com.mininet.utils;

import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class CalUpDown {
    private static final double PI=180;

    private int n;
    private int P;
    private int F;
    private double latM;

    private int AS_total;

    private int k;
    private int phaseCount;
    int x0,y0;
    double point1,point2,point3,point4;
    String resultPath;
    String islsPath;
    List<List<link_info>> link_infos;

    public int getK() {
        return k;
    }
    public int getPhaseCount() {return phaseCount;}
    public CalUpDown(int n, int p, int f, double latM, String resultPath, String islsPath, int AS_total) {
        this.n = n;
        P = p;
        F = f;
        this.latM=latM;
        this.resultPath = resultPath;
        this.islsPath = islsPath;
        this.AS_total=AS_total;
        link_infos=new ArrayList<>();
    }

    static public boolean isInteger(double x)
    {
        double x_floor=Math.floor(x);
        double diff=x-x_floor;
        if(diff<=0.5)
        {
            if(Math.abs(x-x_floor)<1e-6)
            {
                return true;
            }
            else
            {
                return false;
            }
        }
        else
        {
            double x_ceil=Math.ceil(x);
            if(Math.abs(x-x_ceil)<1e-6)
            {
                return true;
            }
            else
            {
                return false;
            }
        }
    }

    public void calcK()
    {
        //z从1增加到100，假如出现整数且小于P，认为K存在，
        double tmp=(double)P/(double)F;
        for(int z=1;z<=100;++z)
        {
            if(isInteger(z*tmp))
            {
                this.k=(int)Math.round(z*tmp);
                break;
            }
            if(z*tmp>=P)
            {
                this.k=P;
                break;
            }
        }
    }

    public void calPhaseCount()
    {
        int kn=this.k*this.n;
        if(kn%2==0)
        {
            System.out.println("kn%2=0,result="+kn*(0.5-this.latM/PI));
            if(isInteger(kn*(0.5-this.latM/PI)))
            {
                this.phaseCount=kn;
            }
            else
            {
                this.phaseCount=2*kn;
            }
        }
        else
        {
            //注意 1/2结果为0，因而整数和整数相除，结果取为整数
            System.out.println("kn%2!=0,result1="+kn*(0.5-this.latM/PI)+",result2="+kn*(1-this.latM/PI));
            if(isInteger(kn*(0.5-this.latM/PI)) || isInteger(kn*(1-this.latM/PI)))
            {
                this.phaseCount=2*kn;
            }
            else
            {
                this.phaseCount=4*kn;
            }
        }
        //分配通断信息数组

    }

    //初始化参数，并将通断向量全部置为0
    public void init_para()
    {
        this.point1=this.latM-(2*PI/(double)(n*k));
        this.point2=PI-this.latM;
        this.point3=PI+this.latM-(2*PI/(double)(n*k));
        this.point4=2*PI-this.latM;

        //建立link_info list
        for(int i=0;i<this.k;++i)
        {
            List<link_info> tmp=new ArrayList<>();
            for(int j=0;j<this.n;++j)
            {
                double lat_left=latM+(2*PI*(double)i)/((double)n*(double)k)-2*PI*(double)j/(double)n;
                double lat_right=lat_left+2*PI/n;
                if(lat_left<0)
                {
                    lat_left+=2*PI;
                }
                if(lat_right<0)
                {
                    lat_right+=2*PI;
                }
                else if(lat_right>2*PI)
                {
                    lat_right-=2*PI;
                }
                link_info link_info_tmp=new link_info(lat_left,lat_right,i,j,this.phaseCount);
                tmp.add(link_info_tmp);
            }
            this.link_infos.add(tmp);
        }
    }
    //判断通断性
    boolean break_or_not(link_info link_info_tmp)
    {
        //此处如果只差一点点，应当判断为通，防止误差
        if(link_info_tmp.getLat_left()>point1+1e-6 && link_info_tmp.getLat_left()<point2-1e-6)
        {
            return true;
        }
        if(link_info_tmp.getLat_left()>point3+1e-6 && link_info_tmp.getLat_left()<point4-1e-6)
        {
            return true;
        }
        return false;
    }
    //找出下一步需要增加的纬度
    double find_lat_add()
    {
        double tmp=PI;
        double nearest=PI;
        for(int i=0;i<k;++i)
        {
            for(int j=0;j<n;++j)
            {
                double lat_left=this.link_infos.get(i).get(j).getLat_left();
                if(lat_left>0 && lat_left<=point1)
                {
                    tmp=point1-lat_left;
                }
                else if(lat_left>point4 && lat_left<=2*PI)
                {
                    tmp=2*PI-lat_left+point1;
                }
                else if(lat_left>point1 && lat_left<=point2)
                {
                    tmp=point2-lat_left;
                }
                else if(lat_left>point2 && lat_left<=point3)
                {
                    tmp=point3-lat_left;
                }
                else if(lat_left>point3 && lat_left<=point4)
                {
                    tmp=point4-lat_left;
                }
                else
                {
                    System.out.println("invalid lat_left:"+lat_left);
                }
                if(Math.abs(tmp)<1e-6)
                {
                    continue;
                }
                if(tmp<nearest)
                {
                    nearest=tmp;
                }
            }
        }
        System.out.println("last find, add_lat="+nearest);
        return nearest;
    }

    //增加纬度
    void add_lat(double lat_add)
    {
        for(int i=0;i<k;++i)
        {
            for(int j=0;j<n;++j)
            {
                double lat_left=link_infos.get(i).get(j).getLat_left();
                double lat_right=link_infos.get(i).get(j).getLat_right();
                lat_left+=lat_add;
                lat_right+=lat_add;
                if(lat_left>2*PI)
                {
                    lat_left-=2*PI;
                }
                if(lat_right>2*PI)
                {
                    lat_right-=2*PI;
                }
                link_infos.get(i).get(j).setLat_left(lat_left);
                link_infos.get(i).get(j).setLat_right(lat_right);
            }
        }
    }


    //首先计算k值范围内的通短性，其余对应到k值范围内的位置
    public void calc()
    {
        calcK();
        calPhaseCount();
        init_para();

        System.out.println("this.k="+this.k);
        System.out.println("this.phaseCount="+this.phaseCount);

        int q=0;
        double lat_add;
        double lat_add_accumulate=0;

        while(q<this.phaseCount)
        {
            //判断每条链路在此时隙是否处于断开状态，如果是，将向量对应处设置为1
            for(int i=0;i<k;++i)
            {
                for(int j=0;j<n;++j)
                {
                    if(break_or_not(link_infos.get(i).get(j)))
                    {
                        link_infos.get(i).get(j).getPhase_matrix()[q]=1;
                    }
                }
            }

            lat_add=find_lat_add();
            add_lat(lat_add);
            ++q;
            lat_add_accumulate+=lat_add;
            System.out.println("lat_accumulate="+lat_add_accumulate);
        }
    }

    public void print_info(String outputPath) throws IOException {
        FileWriter fileWriter=new FileWriter(outputPath);
        for(int i=0;i<this.k;++i)
        {
            for(int j=0;j<this.n;++j)
            {
                link_info link_info_tmp=this.link_infos.get(i).get(j);
                fileWriter.write(link_info_tmp.getLat_left()+","+link_info_tmp.getLat_right()+","+link_info_tmp.getX0()+","+link_info_tmp.getY0()+","+link_info_tmp.getPhase_count()+"\n");
                for(int k=0;k<link_info_tmp.getPhase_count();++k)
                {
                    fileWriter.write(link_info_tmp.getPhase_matrix()[k]+",");
                }
                fileWriter.write("\n");
            }
        }
        fileWriter.close();
    }

    public void write_isls() throws Exception {
        String[][] isls= FileToMatrix.fileToString(islsPath);
        FileWriter fileWriter=new FileWriter(resultPath);
        System.out.println("in func write_isls");
        //写入时隙数量
//        //将0时隙都设置为可用，以便进行计算
//        for(int i=0;i<k;++i)
//        {
//            for(int j=0;j<n;++j)
//            {
//                this.link_infos.get(i).get(j).getPhase_matrix()[0]=0;
//            }
//        }

        fileWriter.write(this.phaseCount+"\n");
        //首先写入k范围内的
        for(int i=0;i<k;++i)
        {
            for(int j=0;j<n;++j)
            {
                //找出此处i、j对应的信息，然后写入通断信息向量，注意只取左侧的
                for(int m=0;m<isls.length;++m)
                {
                    //System.out.println(Integer.valueOf(isls[m][0])+","+ Integer.valueOf(i*n+j) +"," +Integer.valueOf(isls[m][0])+","+ Integer.valueOf(i*n+j+n));
                    if(Integer.valueOf(isls[m][0]).equals(Integer.valueOf(i * n + j)) && Integer.valueOf(isls[m][1]).equals(Integer.valueOf(i * n + j + n)))
                    {
                        System.out.println(Integer.valueOf(isls[m][0])+","+ Integer.valueOf(i*n+j) +"," +Integer.valueOf(isls[m][1])+","+ Integer.valueOf(i*n+j+n));
                        //写入原来的行
                        fileWriter.write(isls[m][0]+","+isls[m][1]+","+isls[m][2]+","+isls[m][3]+"\n");
                        //写入通断信息矩阵
                        for(int z=0;z<this.phaseCount;++z)
                        {
                            fileWriter.write(this.link_infos.get(i).get(j).getPhase_matrix()[z]+",");
                        }
                        fileWriter.write("\n");
                    }
                }
            }
        }
        //假如k<P，继续写入
        if(k<P-1)
        {
            for(int i=k;i<P-1;++i)
            {
                for(int j=0;j<n;++j)
                {
                    //找出在0-k范围内对应的j值，并记录为j1
                    int cha=i/k;
                    int j1=j-k*cha;
                    while(j1<0)
                    {
                        j1+=n;
                    }
                    int i1=i%k;
                    System.out.println("i="+i+",j="+j+",i1="+i1+",j1="+j1+",cha="+cha);
                    //找出此处i、j对应的信息，然后写入通断信息向量，注意只取左侧的
                    for(int m=0;m<isls.length;++m)
                    {
                        if(Integer.valueOf(isls[m][0])==i*n+j && Integer.valueOf(isls[m][1])==i*n+j+n)
                        {
                            //写入原来的行
                            fileWriter.write(isls[m][0]+","+isls[m][1]+","+isls[m][2]+","+isls[m][3]+"\n");
                            //写入j1对应的通断信息矩阵
                            for(int z=0;z<this.phaseCount;++z)
                            {
                                fileWriter.write(this.link_infos.get(i1).get(j1).getPhase_matrix()[z]+",");
                            }
                            fileWriter.write("\n");
                        }
                    }
                }
            }
        }
        //写入轨道内链路的信息，全通
        for(int m=0;m<isls.length;++m)
        {
            if(Integer.valueOf(isls[m][0])+this.n!=Integer.valueOf(isls[m][1]))
            {
                fileWriter.write(isls[m][0]+","+isls[m][1]+","+isls[m][2]+","+isls[m][3]+"\n");
                for(int z=0;z<this.phaseCount;++z)
                {
                    fileWriter.write("0,");
                }
                fileWriter.write("\n");
            }
        }

        fileWriter.close();
    }


    public void make_neighbor() throws Exception {
        int a,b;
        IpUtils32 ipUtils_routerid=new IpUtils32(20,0);
        String[][] isls_link=FileToMatrix.fileToString(this.resultPath);

        //第一个int表示编号，第二个int表示在isls_link中第一行的行号
        Map<Integer,List<Integer>> leo_link_map=new HashMap<>();
        System.out.println("in func make neighbor");
        for(int i=0;i<n*P;++i)
        {
            List<Integer> tmpList=new ArrayList<>();
            for(int x=1;x<isls_link.length;x=x+2)
            {
                if(Integer.valueOf(isls_link[x][0])==i || Integer.valueOf(isls_link[x][1])==i )
                {
                    //添加该卫星所有链路的行号到map中
                    System.out.println("add Map<" + i + ",[" + x + "]>");
                    tmpList.add(x);
                }
            }
            leo_link_map.put(i,tmpList);
        }
        for(a=0;a<P;++a)
        {
            for(b=0;b<n;++b)
            {
                int leo_num=a*n+b;
                FileWriter fileWriter=new FileWriter("neighbor/neighbor"+leo_num+".conf");

                if(a==0 || a==P-1)
                {
                    fileWriter.write("3,");
                }
                else
                {
                    fileWriter.write("4,");
                }
                fileWriter.write(this.phaseCount+"\n");

                List<Integer> tmpList=leo_link_map.get(leo_num);
                for(int c=0;c<tmpList.size();++c)
                {
                    int link_line=tmpList.get(c);
                    //自己为1号节点
                    if(Integer.valueOf(isls_link[link_line][0])==leo_num)
                    {
                        ipUtils_routerid.setIp2Ip3(0,Integer.valueOf(isls_link[link_line][1]));

                    }
                    //自己为2号节点
                    if(Integer.valueOf(isls_link[link_line][1])==leo_num)
                    {
                        ipUtils_routerid.setIp2Ip3(0,Integer.valueOf(isls_link[link_line][0]));
                    }
                    fileWriter.write(ipUtils_routerid.ipStr+",");
                    for(int d=0;d<this.phaseCount;++d)
                    {
                        fileWriter.write(isls_link[link_line+1][d]+",");
                    }
                    fileWriter.write("\n");
                }//end for c
                fileWriter.close();
            }//end for b
        }//end for a
    }

}
