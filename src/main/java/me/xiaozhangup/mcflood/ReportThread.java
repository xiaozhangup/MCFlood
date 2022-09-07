package me.xiaozhangup.mcflood;

public class ReportThread implements Runnable {
    @Override
    public void run() {
        try {
            while (true) {
                Thread.sleep(3000);
                if (MCFlood.data >= 1024 * 1024 * 1024) {
                    double a = MCFlood.data / (1024.0 * 1024.0 * 1024.0);
                    System.out.println("[攻击统计] " + a + " kb, " + MCFlood.killT + " 次攻击");
                    continue;
                }
                if (MCFlood.data >= 1024 * 1024) {
                    double a = MCFlood.data / (1024.0 * 1024.0);
                    System.out.println("[攻击统计] " + a + " mb, " + MCFlood.killT + " 次攻击");
                    continue;
                }
                if (MCFlood.data >= 1024) {
                    double a = MCFlood.data / 1024.0;
                    System.out.println("[攻击统计] " + a + " kb, " + MCFlood.killT + " 次攻击");
                    continue;
                }
                if (MCFlood.data < 1024) {
                    System.out.println("[攻击统计] " + MCFlood.data + " byte, " + MCFlood.killT + " 次攻击");
                    continue;
                }
            }
        } catch (InterruptedException e) {
            // TODO 自动生成的 catch 块
            e.printStackTrace();
        }

    }
}
