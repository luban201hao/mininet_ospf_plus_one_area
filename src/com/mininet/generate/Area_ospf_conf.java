package com.mininet.generate;

import com.mininet.config.Config;
import com.mininet.entity.tc_info_help;
import com.mininet.utils.*;

import java.io.FileWriter;
import java.io.IOException;
import java.text.DecimalFormat;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class Area_ospf_conf {

    int leo_per_orbit;//每个轨道的卫星数量
    int orbit;

    public Area_ospf_conf(int leo_per_orbit, int orbit) {
        this.leo_per_orbit = leo_per_orbit;
        this.orbit = orbit;
    }

    public void mkisls(String islspath) throws IOException {
        FileWriter fileWriter=new FileWriter(islspath);
        //该条链路对应的172网段的IP编号
        int ip = 0;
        int i,j,id;
        //列举area0内部链路
        //列举轨道内
        for(i=0;i<orbit;++i){
            for(j=0;j<leo_per_orbit-1;++j){
                id=j+i*leo_per_orbit;
                fileWriter.write(id+","+(id+1)+",M,"+ip+"\n");
                ip++;
            }
            // 只需要写M,表示位于骨干区域
            fileWriter.write(((i+1)*leo_per_orbit-1)+","+(i*leo_per_orbit)+",M,"+ip+"\n");
            ip++;
        }
        //列举轨道间
        for(i=0;i<orbit-1;++i){
            for(j=0;j<leo_per_orbit;++j){
                id=j+i*leo_per_orbit;
                fileWriter.write(id+","+(id+leo_per_orbit)+",M,"+ip+"\n");
                ip++;
            }
        }
        fileWriter.close();
    }

    public void mktxt(String islspath,String ethpath,String linkpath,int k) throws Exception {
        String[][] islslist;
        islslist= FileToMatrix.fileToString(islspath);
        int maxleo=0;
        for(int i=0;i<islslist.length;++i){
            if(Integer.parseInt(islslist[i][0])>maxleo){
                maxleo=Integer.parseInt(islslist[i][0]);
            }
            if(Integer.parseInt(islslist[i][1])>maxleo){
                maxleo=Integer.parseInt(islslist[i][1]);
            }
        }
        maxleo++;//实际卫星数比最大编号大1
        String[] ethlist=new String[maxleo];
        String[] linklist=new String[islslist.length];
        int[] ethnum=new int[maxleo];

        for(int i=0;i<maxleo;++i){
            ethlist[i]="leo"+String.valueOf(i)+",";
            ethnum[i]=0;
        }
        for(int i=0;i<islslist.length;++i){
            linklist[i]="link"+String.valueOf(i)+",";
        }

        IpUtils172 ipUtils172=new IpUtils172(0);
        int linkNumber,source_num,dest_num;
        char areaType;

        for(int i=0;i<islslist.length;++i){
            source_num=Integer.parseInt(islslist[i][0]);
            dest_num=Integer.parseInt(islslist[i][1]);
            linkNumber=Integer.parseInt(islslist[i][3]);

            ipUtils172.setLinkNumber(linkNumber);
            ethlist[source_num]+="leo"+String.valueOf(source_num)+"-eth"+String.valueOf(ethnum[source_num])+","+ipUtils172.ipStr01+"/30,M,";
            ethlist[dest_num]+="leo"+String.valueOf(dest_num)+"-eth"+String.valueOf(ethnum[dest_num])+","+ipUtils172.ipStr10+"/30,M,";
            linklist[i]+="leo"+String.valueOf(source_num)+","+"leo"+String.valueOf(source_num)+"-eth"+String.valueOf(ethnum[source_num])+","+ipUtils172.ipStr01+"/30,M,";
            linklist[i]+="leo"+String.valueOf(dest_num)+","+"leo"+String.valueOf(dest_num)+"-eth"+String.valueOf(ethnum[dest_num])+","+ipUtils172.ipStr10+"/30,M,";
            ethnum[source_num]+=1;
            ethnum[dest_num]+=1;
        }

        //ethlist中，写入星地接口
        IpUtils_AS ipUtils_se=new IpUtils_AS(10,0,0);
        for(int a=0; a < this.orbit; ++a)
        {
            for(int b=0; b < leo_per_orbit; ++b)
            {
                int leo_num=a*leo_per_orbit+b;
                System.out.println("in func make_link_eth,add se,leo_num="+leo_num);
                int x=a;
                int y=b-a/k;
                while(y<0)
                {
                    y+=leo_per_orbit;
                }
                ipUtils_se.setIp2Ip3(x,y);
                ethlist[leo_num] += "leo" + leo_num + "-eth" + ethnum[leo_num] + "," + ipUtils_se.ipStr1 + "/24,X,";
                ethnum[leo_num]+=1;
            }
        }

        FileWriter fileWriter=new FileWriter(ethpath);
        for(int i=0;i<maxleo;++i){  //maxleo比实际卫星数少1，也比行数少1
            fileWriter.write(ethlist[i]);
            fileWriter.write("\n");
            //System.out.println(ethlist[i]);
        }

        fileWriter.close();
        FileWriter fileWriter1=new FileWriter(linkpath);
        for(int i=0;i<linklist.length;++i){
            fileWriter1.write(linklist[i]);
            fileWriter1.write("\n");
            //System.out.println(ethlist[i]);
        }

        fileWriter1.close();

    }

    public void maketopo(String mktxtpath,String linkpath,String topopath,String conf_dir,int k,int n,int P,int phaseAll) throws Exception {
        String[][] ethlist,linklist,updownlist;
        ethlist= FileToMatrix.fileToString(mktxtpath); //此处输出二维矩阵，网卡较少的leo出现null
        linklist=FileToMatrix.fileToString(linkpath);
        updownlist=FileToMatrix.fileToString("Area_updown.txt");

        FileWriter fileWriter=new FileWriter(topopath);

        //找出每个卫星对应的星地接口的地址，并存入表中
        Map<Integer, List<String>> se_if_map=new HashMap<>();

        for(int a=0;a<ethlist.length;++a)
        {
            for(int b=3;b<ethlist[a].length;b+=3)
            {
                if(ethlist[a][b]!=null)
                {
                    if(ethlist[a][b].toCharArray()[0]=='X')
                    {
                        System.out.println("se_if_map add <"+a+","+ethlist[a][b-1]+">");
                        List<String> tmpList=new ArrayList<>();
                        // 星地网卡名
                        tmpList.add(0,ethlist[a][b-1]);
                        // 星地网卡IP地址
                        tmpList.add(1,ethlist[a][b-2]);
                        // 星地网卡的OVS端口号
                        tmpList.add(2,String.valueOf(2000+a));
                        se_if_map.put(a,tmpList);
                        break;
                    }
                }
            }
        }
        // 此处写入星间通断的tc信息
        List<tc_info_help> tc_info_list=new ArrayList<>();
        IpUtils172 ipUtils172=new IpUtils172(0);
        for(int a=0; a<P-1; ++a)
        {
            for(int b=0; b<n; ++b)
            {
                int num1 = a * n + b;
                int num2 = num1 + n;
                int linkNumber = 0, x =0;
                //找出一号对应
                for(x=1;x<updownlist.length;x+=2)
                {
                    if(Integer.valueOf(updownlist[x][0])==num1)
                    {
                        linkNumber=Integer.valueOf(updownlist[x][3]);
                        break;
                    }
                }
                String linkinfo="";
                for(int y=0;y<updownlist[x+1].length-1;++y)
                {
                    linkinfo+=updownlist[x+1][y]+",";
                }
                linkinfo+=updownlist[x+1][updownlist[x+1].length-1];
                ipUtils172.setLinkNumber(linkNumber);
                String ip01str=ipUtils172.ipStr01;
                String ethname1="";
                String ethname2="";
                for(int y=0;y<linklist.length;++y)
                {
                    if(linklist[y][3].split("/")[0].equals(ip01str))
                    {
                        ethname1=linklist[y][2];
                        ethname2=linklist[y][6];
                    }
                }
                System.out.println(num1+","+num2+","+ethname1+","+ethname2+","+linkinfo);
                tc_info_help tcInfoHelp=new tc_info_help(num1,num2,ethname1,ethname2,linkinfo);
                tc_info_list.add(tcInfoHelp);
            }
        }

        fileWriter.write("import os\n");
        fileWriter.write("import time\n");
        fileWriter.write("from mininet.net import MininetE\n");
        fileWriter.write("from mininet.net import Mininet\n");
        fileWriter.write("from mininet.node import Node,ContainerHost\n");
        fileWriter.write("from mininet.topo import Topo\n");
        fileWriter.write("from mininet.node import Controller,RemoteController\n");
        fileWriter.write("from mininet.cli import CLI\n");
        fileWriter.write("from mininet.link import TCLink\n");
        fileWriter.write("from mininet.log import info, setLogLevel\n");
        fileWriter.write("setLogLevel('info')\n\n\n");
        fileWriter.write("class LinuxRouter( ContainerHost ):\n");
        fileWriter.write("    \"A Node with IP forwarding enabled.\"\n");
        fileWriter.write("    def config( self, **params ):\n");
        fileWriter.write("        super( LinuxRouter, self).config( **params )\n");
        fileWriter.write("        # Enable forwarding on the router\n");
        fileWriter.write("        self.cmd( 'sysctl net.ipv4.ip_forward=1' )\n");
        fileWriter.write("        self.cmd( 'ulimit -s unlimited' )\n");
        fileWriter.write("        self.cmd( 'sysctl net.ipv4.conf.all.rp_filter=2' )\n\n");
        fileWriter.write("    def terminate( self ):\n");
        fileWriter.write("        self.cmd( 'sysctl net.ipv4.ip_forward=0' )\n");
        fileWriter.write("        super( LinuxRouter, self ).terminate()\n\n\n");
        fileWriter.write("class NetworkTopo( Topo ):\n");
        fileWriter.write("    def build( self, **_opts ):\n");
        for(int i=0;i<ethlist.length;++i){
            fileWriter.write("        defaultIp" + ethlist[i][0] + " = '" + ethlist[i][2] + "'\n" );
        }
        fileWriter.write("\n");
        for(int i=0;i<ethlist.length;++i){
            fileWriter.write("        " + ethlist[i][0] + "= self.addNode('" +  ethlist[i][0] + "', cls=LinuxRouter, ip=defaultIp" + ethlist[i][0]+ ")\n");

        }
        fileWriter.write("\n");
        fileWriter.write("        s1=self.addSwitch('s1')\n");
        fileWriter.write("\n");


        for(int i=0;i<linklist.length;++i){
            if(Config.hasDelay == 1) {
                DecimalFormat decimalFormat = new DecimalFormat("#.######"); // 设置要显示的小数位数为6位
                String delayStr = decimalFormat.format(Config.delayInterStar);
                if(Config.lossInterStar >= 1e-4){
                    String lossStr = decimalFormat.format(Config.lossInterStar);
                    fileWriter.write("        self.addLink(" +linklist[i][1] + ","+ linklist[i][5]+",infName1='"+linklist[i][2]+"',params1={'ip':'"+ linklist[i][3]+"'},infName2='"+linklist[i][6]+"',params2={'ip':'"+linklist[i][7]+"'}, delay = '"+ delayStr+"ms', loss = " + lossStr + ")\n");
                }
                else{
                    fileWriter.write("        self.addLink(" +linklist[i][1] + ","+ linklist[i][5]+",infName1='"+linklist[i][2]+"',params1={'ip':'"+ linklist[i][3]+"'},infName2='"+linklist[i][6]+"',params2={'ip':'"+linklist[i][7]+"'}, delay = '"+ delayStr+"ms')\n");
                }
            }
            else {
                fileWriter.write("        self.addLink(" +linklist[i][1] + ","+ linklist[i][5]+",infName1='"+linklist[i][2]+"',params1={'ip':'"+ linklist[i][3]+"'},infName2='"+linklist[i][6]+"',params2={'ip':'"+linklist[i][7]+"'})\n");
            }
        }
        //增加星地网关之间的连接
        for(int i=0;i<se_if_map.size();++i)
        {
            List<String> tmpList=se_if_map.get(i);
            if(Config.hasDelay == 1) {
                DecimalFormat decimalFormat = new DecimalFormat("#.######"); // 设置要显示的小数位数为6位
                String delayStr = decimalFormat.format(Config.delayStarEarth);
                if(Config.lossStarEarth >= 1e-4){
                    String lossStr = decimalFormat.format(Config.lossStarEarth);
                    fileWriter.write("        self.addLink(leo" + i + ",s1,intfName1='" + tmpList.get(1) + "',params1={'ip' : '" + tmpList.get(0) + "'}, port2=" + tmpList.get(2) + ", delay = '"+delayStr+"ms', loss = " + lossStr +")\n");
                }
                else{
                    fileWriter.write("        self.addLink(leo" + i + ",s1,intfName1='" + tmpList.get(1) + "',params1={'ip' : '" + tmpList.get(0) + "'}, port2=" + tmpList.get(2) + ", delay = '"+delayStr+"ms')\n");
                }

            }
            else {
                fileWriter.write("        self.addLink(leo" + i + ",s1,intfName1='" + tmpList.get(1) + "',params1={'ip' : '" + tmpList.get(0) + "'}, port2=" + tmpList.get(2) + ")\n");
            }
        }

        fileWriter.write("\n\n\n");
        fileWriter.write("class tc_info(object):\n");
        fileWriter.write("    num1=0\n");
        fileWriter.write("    num2=0\n");
        fileWriter.write("    ethname1=''\n");
        fileWriter.write("    ethname2=''\n");
        fileWriter.write("    linkinfo=[]\n");
        fileWriter.write("    def __init__(self,num1,num2,ethname1,ethname2,linkinfo):\n");
        fileWriter.write("        self.num1=num1\n");
        fileWriter.write("        self.num2=num2\n");
        fileWriter.write("        self.ethname1=ethname1\n");
        fileWriter.write("        self.ethname2=ethname2\n");
        fileWriter.write("        self.linkinfo=linkinfo\n");
        fileWriter.write("\n\n\n");

        fileWriter.write("class TestMininet(Mininet):\n");
        fileWriter.write("    def __init__(self,controller,topo):\n");
        fileWriter.write("        Mininet.__init__(self,controller=controller,topo=topo,link=TCLink)\n");
        for(int i=0;i<ethlist.length;++i)
        {
            fileWriter.write("        self.leo"+i+"=self.getNodeByName('leo"+i+"')\n");
        }
        fileWriter.write("\n");
        fileWriter.write("        self.n=" + this.leo_per_orbit + "\n");
        fileWriter.write("        self.P=" + this.orbit + "\n");
        fileWriter.write("        self.k=" + k + "\n");
        fileWriter.write("        self.phase=0\n");
        fileWriter.write("        self.phaseAll=" + phaseAll + "\n");
        fileWriter.write("\n");
        //写入四个信息列表
        fileWriter.write("        self.leodic={}\n");
        for(int i=0;i<ethlist.length;++i)
        {
            fileWriter.write("        self.leodic["+i+"]=self.leo"+i+"\n");
        }
        fileWriter.write("\n");
        fileWriter.write("        self.se_addr={}\n");
        for(int i=0;i<ethlist.length;++i)
        {
            List<String> tmpList=se_if_map.get(i);
            fileWriter.write("        self.se_addr["+i+"]=\'"+tmpList.get(0).split("/")[0]+"\'\n");
        }
        fileWriter.write("\n");
        fileWriter.write("        self.se_ethname={}\n");
        for(int i=0;i<ethlist.length;++i)
        {
            List<String> tmpList=se_if_map.get(i);
            fileWriter.write("        self.se_ethname["+i+"]=\'"+tmpList.get(1)+"\'\n");
        }
        fileWriter.write("\n");
        fileWriter.write("        self.orbit_k={}\n");
        for(int i=0; i<this.orbit; ++i)
        {
            fileWriter.write("        self.orbit_k["+i+"]="+(i%k)+"\n");
        }
        fileWriter.write("\n\n");
        //写入tc_info信息
        fileWriter.write("        self.tc_info_array={}\n");
        for(int i=0;i<tc_info_list.size();++i)
        {
            fileWriter.write("        self.tc_info_array["+i+"]=tc_info("+tc_info_list.get(i).num1+","+tc_info_list.get(i).num2+",'"+tc_info_list.get(i).ethname1+"','"+tc_info_list.get(i).ethname2+"',["+tc_info_list.get(i).linkinfo+"])\n");
        }
        fileWriter.write("\n\n\n");
        //写入星地接口转换相关函数
        fileWriter.write("    def ipaddr_sub_ip3(self,ipaddr):\n");
        fileWriter.write("        ipaddr_arr=ipaddr.split('.')\n");
        fileWriter.write("        tmp= int(ipaddr_arr[2])-1\n");
        fileWriter.write("        if tmp<0:\n");
        fileWriter.write("            tmp+=self.n\n");
        fileWriter.write("        return ipaddr_arr[0]+'.'+ipaddr_arr[1]+'.'+str(tmp)+'.'+ipaddr_arr[3]\n\n");
        fileWriter.write("    def ipaddr_add_ip2(self,ipaddr):\n");
        fileWriter.write("        ipaddr_arr=ipaddr.split('.')\n");
        fileWriter.write("        tmp= int(ipaddr_arr[1])+1\n");
        fileWriter.write("        if tmp>=self.P:\n");
        fileWriter.write("            tmp-=self.P\n");
        fileWriter.write("        return ipaddr_arr[0]+'.'+ str(tmp) + '.' + ipaddr_arr[2]+'.'+ipaddr_arr[3]\n\n");
        fileWriter.write("    def add_y(self):\n");
        fileWriter.write("        for i in range(0,self.P):\n");
        fileWriter.write("            self.orbit_k[i]+=1\n");
        fileWriter.write("            if self.orbit_k[i] >= self.k:\n");
        fileWriter.write("                self.orbit_k[i]=0\n");
        fileWriter.write("                for j in range(0,self.n):\n");
        fileWriter.write("                    self.se_addr[i*self.n+j]=self.ipaddr_sub_ip3(self.se_addr[i*self.n+j])\n");
        fileWriter.write("                    cmd_y='ifconfig ' + self.se_ethname[i*self.n+j]+' '+self.se_addr[i*self.n+j]+'/24'\n");
        fileWriter.write("                    self.leodic[i*self.n+j].cmd(cmd_y)\n");
        fileWriter.write("                    print(cmd_y)\n\n");
        fileWriter.write("    def add_x(self):\n");
        fileWriter.write("        for i in range(0,self.P):\n");
        fileWriter.write("            for j in range(0,self.n):\n");
        fileWriter.write("                leo_num=i*self.n+j\n");
        fileWriter.write("                self.se_addr[leo_num]=self.ipaddr_add_ip2(self.se_addr[leo_num])\n");
        fileWriter.write("                cmd_x='ifconfig '+self.se_ethname[leo_num]+' '+self.se_addr[leo_num]+'/24'\n");
        fileWriter.write("                self.leodic[leo_num].cmd(cmd_x)\n");
        fileWriter.write("                print(cmd_x)\n\n\n");

        String apiDir = "";
        if(Config.isServer == 1) {
            apiDir = "/home/test";
        } else {
            apiDir = "/home/wyc";
        }


        fileWriter.write("    def add_phase(self):\n");
        fileWriter.write("        last_phase=self.phase\n");
        fileWriter.write("        self.phase+=1\n");
        fileWriter.write("        print(str(self.phase)+\",\"+str(last_phase))\n");
        fileWriter.write("        if self.phase>=self.phaseAll:\n");
        fileWriter.write("            self.phase=0\n");
        fileWriter.write("        for i in range(0,len(self.tc_info_array)):\n");
        fileWriter.write("            if self.tc_info_array[i].linkinfo[self.phase]==0 and self.tc_info_array[i].linkinfo[last_phase]==1:\n");
        fileWriter.write("                print(str(self.tc_info_array[i].num1)+\":\"+\"tc qdisc del dev \"+str(self.tc_info_array[i].ethname1)+\" root netem loss 100%\")\n");
        fileWriter.write("                print(str(self.tc_info_array[i].num2)+\":\"+\"tc qdisc del dev \"+str(self.tc_info_array[i].ethname2)+\" root netem loss 100%\")\n");
        fileWriter.write("                self.leodic[self.tc_info_array[i].num1].cmd(\"tc qdisc del dev \"+self.tc_info_array[i].ethname1+\" root netem loss 100%\")\n");
        fileWriter.write("                self.leodic[self.tc_info_array[i].num2].cmd(\"tc qdisc del dev \"+self.tc_info_array[i].ethname2+\" root netem loss 100%\")\n\n");
        fileWriter.write("            if self.tc_info_array[i].linkinfo[self.phase]==1 and self.tc_info_array[i].linkinfo[last_phase]==0:\n");
        fileWriter.write("                print(str(self.tc_info_array[i].num1)+\":\"+\"tc qdisc add dev \"+str(self.tc_info_array[i].ethname1)+\" root netem loss 100%\")\n");
        fileWriter.write("                print(str(self.tc_info_array[i].num2)+\":\"+\"tc qdisc add dev \"+str(self.tc_info_array[i].ethname2)+\" root netem loss 100%\")\n");
        fileWriter.write("                self.leodic[self.tc_info_array[i].num1].cmd(\"tc qdisc add dev \"+self.tc_info_array[i].ethname1+\" root netem loss 100%\")\n");
        fileWriter.write("                self.leodic[self.tc_info_array[i].num2].cmd(\"tc qdisc add dev \"+self.tc_info_array[i].ethname2+\" root netem loss 100%\")\n");
        for(int i=0;i<ethlist.length;++i)
        {
            fileWriter.write("        self.leo"+i+".cmd(\"vtysh -f "+conf_dir+"/cmd/add_phase.conf -w "+apiDir+"/vtyzebraleo"+i+".api -q "+apiDir+"/vtyospfdleo"+i+".api -r\")\n");
        }
        fileWriter.write("\n\n");


        fileWriter.write("    def if_se(self):\n");
        for(int i=0;i<ethlist.length;++i)
        {
            fileWriter.write("        self.leo"+i+".cmd(\"vtysh -f "+conf_dir+"/if_se/if_se"+i+".conf -w "+apiDir+"/vtyzebraleo"+i+".api -q "+apiDir+"/vtyospfdleo"+i+".api -r\")\n");
        }
        fileWriter.write("\n\n");

        fileWriter.write("    def begin_running(self):\n");
        fileWriter.write("        for i in range(0,len(self.tc_info_array)):\n");
        fileWriter.write("            if self.tc_info_array[i].linkinfo[0]==1:\n");
        fileWriter.write("                print(str(self.tc_info_array[i].num1)+\":\"+\"tc qdisc add dev \"+str(self.tc_info_array[i].ethname1)+\" root netem loss 100%\")\n");
        fileWriter.write("                print(str(self.tc_info_array[i].num2)+\":\"+\"tc qdisc add dev \"+str(self.tc_info_array[i].ethname2)+\" root netem loss 100%\")\n");
        fileWriter.write("                self.leodic[self.tc_info_array[i].num1].cmd(\"tc qdisc add dev \"+self.tc_info_array[i].ethname1+\" root netem loss 100%\")\n");
        fileWriter.write("                self.leodic[self.tc_info_array[i].num2].cmd(\"tc qdisc add dev \"+self.tc_info_array[i].ethname2+\" root netem loss 100%\")\n");
        for(int i=0;i<ethlist.length;++i)
        {
            fileWriter.write("        self.leo"+i+".cmd(\"vtysh -f "+conf_dir+"/cmd/begin_running.conf -w "+apiDir+"/vtyzebraleo"+i+".api -q "+apiDir+"/vtyospfdleo"+i+".api -r\")\n");
        }
        fileWriter.write("\n\n");

        fileWriter.write("    def rp_filter(self):\n");
        fileWriter.write("        for i in range(0,self.P):\n");
        fileWriter.write("            for j in range(0,self.n):\n");
        fileWriter.write("                self.leodic[i*self.n+j].cmd('sysctl net.ipv4.conf.all.rp_filter=2')\n\n\n");

        fileWriter.write("def run():\n");
        fileWriter.write("    topo = NetworkTopo()\n");
        fileWriter.write("    net = TestMininet(controller=RemoteController,topo=topo)\n");
        fileWriter.write("    #c = net.addController('c0',controller=RemoteController,port=6633)\n");
        fileWriter.write("    net.start()\n");
        fileWriter.write("    info( '*** Routing Table on Router:\\n' )\n");
        for(int i=0;i<ethlist.length;++i){
            fileWriter.write("    "+ethlist[i][0]+"=net.getNodeByName('"+ ethlist[i][0]+"')\n");
        }

        for(int i=0;i<ethlist.length;++i){
            fileWriter.write("    "+ethlist[i][0]+".cmd('zebra -f "+conf_dir+"/ospf_conf/"+ethlist[i][0]+"zebra.conf -d -z "+apiDir+"/"+ethlist[i][0]+"zebra.api -i "+apiDir+"/"+ethlist[i][0]+"zebra.interface -w "+apiDir+"/vtyzebra"+ethlist[i][0] +".api')\n");
        }
        fileWriter.write("    time.sleep(2)\n");
        for(int i=0;i<ethlist.length;++i){
            fileWriter.write("    "+ethlist[i][0]+".cmd('ospfd -f "+conf_dir+"/ospf_conf/"+ethlist[i][0]+"ospfd.conf -d -z "+apiDir+"/"+ethlist[i][0]+"zebra.api -i "+apiDir+"/"+ethlist[i][0]+"ospfd.interface -q "+apiDir+"/vtyospfd"+ethlist[i][0] +".api')\n");
        }

        fileWriter.write("    leo0.cmd('wireshark -i leo0-eth0 -k &')\n");
        fileWriter.write("    CLI( net )\n");
        fileWriter.write("    net.stop()\n");
        fileWriter.write("    os.system(\"killall -9 ospfd zebra\")\n");
        fileWriter.write("    os.system(\"cd "+apiDir+" && rm -f *api*\")\n");
        fileWriter.write("    os.system(\"cd "+apiDir+" && rm -f *interface*\")\n");
        fileWriter.write("\n");
        fileWriter.write("if __name__ == '__main__':\n");
        fileWriter.write("    setLogLevel( 'info' )\n");
        fileWriter.write("    run()\n");
        fileWriter.close();

    }
    //此处顺便写了if_se
    public void ospfd_conf(String mktxtpath,String ospf_dir,String running_dir,int phase_all) throws Exception{
        String[][] ethlist;
        ethlist= FileToMatrix.fileToString(mktxtpath); //此处输出二维矩阵，网卡较少的leo出现null

        FileWriter fileWriter,fileWriter1,fileWriter2;
        char areaType;
        int i,j;


        IpUtils32 ipUtils_routerid=new IpUtils32(20,0);
        for(i=0;i<ethlist.length;++i){

            j=2;
            fileWriter=new FileWriter(ospf_dir+"/"+ethlist[i][0]+"ospfd.conf");
            fileWriter1=new FileWriter(ospf_dir+"/"+ethlist[i][0]+"zebra.conf");
            fileWriter2=new FileWriter("if_se/if_se"+i+".conf");


            fileWriter.write("! -*- ospf -*-\n");
            fileWriter.write("hostname "+ethlist[i][0]+"_ospfd\n");
            fileWriter.write("password zebra\n");
            fileWriter.write("enable password zebra\n");
            fileWriter.write("!\n");
            fileWriter.write("router ospf\n");

            ipUtils_routerid.setIp2Ip3(0,i);
            fileWriter.write(" ospf router-id "+ipUtils_routerid.ipStr +"\n");

            do{
                areaType=ethlist[i][j+1].toCharArray()[0];
                if(areaType=='M'){
                    fileWriter.write(" network " + ethlist[i][j] + " area 0.0.0.0\n");
                }
                if(areaType=='N'){
                    fileWriter.write(" network " + ethlist[i][j] + " area 0.0.0.0\n");
                }
                if(areaType=='L'){
                    fileWriter.write(" network " + ethlist[i][j] + " area 0.0.0.0\n");
                }

                j+=3;
            }while (j < ethlist[0].length && ethlist[i][j] != null);

            //对于M，宣告其位于Area0, 对于星地接口，通告其位于Area0, 分别有不同的处理
            j=1;
            while (j < ethlist[0].length && ethlist[i][j] != null) {
                char type=ethlist[i][j+2].toCharArray()[0];
                if(type=='M')
                {
                    fileWriter.write("interface "+ ethlist[i][j]+"\n");
                    fileWriter.write(" ip ospf network point-to-point\n");
                    fileWriter.write(" ip ospf hello-interval 3\n");
                    fileWriter.write(" ip ospf dead-interval 12\n");
                }
                else if (type=='X')
                {
                    fileWriter2.write("interface "+ ethlist[i][j]+"\n");
                    fileWriter2.write(" if_se area 0.0.0.0\n");
                }
                j += 3;
            }
            fileWriter.write("set_phase_all "+phase_all+"\n");
            fileWriter.write("readneighbor "+ running_dir + "/neighbor/neighbor"+i+".conf\n");

            if(i==0) {
                if(Config.isServer == 0) {
                    fileWriter.write("log file /mnt/hgfs/vm_new/ospflog/ospf" + i + ".log\n");
                }
                else
                {
                    fileWriter.write("log file /home/test/wyc/ospflog/ospf" + i + ".log\n");
                }
            }
            fileWriter.close();

            fileWriter1.write("hostname Router\n");
            fileWriter1.write("password zebra\n");
            fileWriter1.write("enable password zebra\n");
            fileWriter1.close();
            fileWriter2.close();
        }
    }
}
