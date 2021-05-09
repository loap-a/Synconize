package CD_Shop;
import java.util.Random;
import java.util.Vector;
import java.io.*;
public class Main {
    public static void main(String[] args)
    {
        all_cd cd=new all_cd();
        control_thread ct=new control_thread(cd);
        ct.start();
    }
    public static void outFile(String s) {
        File file = new File("E:/record.txt");
        BufferedWriter bw = null;
        try {
            bw = new BufferedWriter(new FileWriter(file,true));
            bw.write(s);
            bw.newLine();
            bw.close();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }
}

class  in_thread extends Thread {
    all_cd cd;
    in_thread(all_cd c){
        this.cd=c;
        this.setDaemon(true);
    }
    @Override
    public void run()
    {
        synchronized (cd) {
            while (true) {
                cd.get_in();

                try {
                    cd.wait(1000);
                } catch (InterruptedException e) {
                    e.printStackTrace();
                }
            }
        }
    }
}

class sale_thread extends Thread {
    all_cd cd;
    static int ids=0;
    int id;
    sale_thread(all_cd c) {
        this.cd = c;
        this.setDaemon(true);
        id=++ids;
    }
    @Override
    public void run() {
        Main.outFile("sale thread start");
        Random r = new Random();
        while (true) {
            try {
                Thread.sleep(r.nextInt(200));
            } catch (InterruptedException e) {
                e.printStackTrace();
            }
            int n = r.nextInt(5);
            int sell_num=r.nextInt(10);
            synchronized (cd) {
                if (cd.get(sell_num).num < n) {
                    if(r.nextBoolean())
                    {
                        cd.sale(sell_num, n);
                    }
                    else {
                        Main.outFile("cd "+sell_num+" give up");
                    }
                }
                else
                    cd.get(sell_num).sale(n,cd.get(sell_num).name);
            }
        }
    }
}
class control_thread extends Thread
{
    all_cd cd;
    control_thread(all_cd c){this.cd=c;this.setDaemon(false);}

    @Override
    public void run()
    {

        new in_thread(cd).start();
        new sale_thread(cd).start();
        new sale_thread(cd).start();
        new sale_thread(cd).start();
        new sale_thread(cd).start();
        new sale_thread(cd).start();
        new sale_thread(cd).start();
        new rent_thread(cd).start();
        new rent_thread(cd).start();
        new rent_thread(cd).start();
        new rent_thread(cd).start();


        try {
            Thread.sleep(120*1000);
        } catch (InterruptedException e) {
            e.printStackTrace();
        }

    }
}

class all_cd
{
    Vector<CD>my_cd=new Vector<CD>();
    all_cd()
    {
        for(int i=0;i<10;i++)
        {
            my_cd.add(new CD("cd "+(i+1),10));
        }
    }
    public CD get(int index){
        return my_cd.get(index);
    }
    void get_in()
    {
        for(int i=0;i<10;i++)
        {
            my_cd.get(i).num=10;
        }
        Main.outFile("all cd "+"'s num has been set to 10 ");
        notifyAll();
    }
    void sale(int number,int num)
    {
        while (my_cd.get(number).num - num < 0) {
            Main.outFile((my_cd.get(number).name+" stock not enough "));
            notifyAll();                            //强制唤醒其他等待的线程
            try {
                wait();
            } catch (InterruptedException e) {
                e.printStackTrace();
            }
        }
        Main.outFile((my_cd.get(number).name+" successful sale " + num));
        my_cd.get(number).num -= num;
    }
    void rent(int number)
    {
        while(my_cd.get(number).rent_flag)
        {
            Main.outFile(my_cd.get(number).name+" has been rented ");
        }
    }
}

class rent_thread extends Thread{
    all_cd cd;
    static int ids=0;
    int id;
    rent_thread(all_cd c) {
        this.cd = c;
        this.setDaemon(true);
        id=++ids;
    }
    @Override
    public void run() {
        Main.outFile("rent thread start");
        Random r = new Random();
        while (true) {
            try {
                Thread.sleep(r.nextInt(200));
            } catch (InterruptedException e) {
                e.printStackTrace();
            }
            int rent_num=r.nextInt(10);
            synchronized (cd) {
                if(!cd.get(rent_num).rent_flag)
                {
                    if(r.nextBoolean())
                    {
                        Main.outFile("I will wait for cd "+rent_num);
                        try {
                            sleep(300);
                        } catch (InterruptedException e) {
                            e.printStackTrace();
                        }
                        if(!cd.get(rent_num).rent_flag)
                        {
                            Main.outFile("after wait cd "+rent_num+" has been rented ");
                        }
                    }
                    else
                    {
                        Main.outFile("No Wait for cd "+rent_num);
                    }
                }
                else
                {
                    Main.outFile(cd.get(rent_num).name+" now has been rented ");
                    cd.get(rent_num).rent_flag=true;
                    try {
                        sleep(r.nextInt(100)+200);
                    } catch (InterruptedException e) {
                        e.printStackTrace();
                    }
                    cd.get(rent_num).rent_flag=false;
                    Main.outFile(cd.get(rent_num).name +" has been returned ");

                }
            }
        }
    }
}
class CD {
    String name;
    int num;
    boolean rent_flag;
    public CD(String n,int nn)
    {
        name=n;
        num=nn;
        rent_flag=false;
    }
    synchronized void get_in(String getname) {
        this.num = 10;
        Main.outFile(getname+" get_in start now ");
        notifyAll();
    }

    synchronized void sale(int n , String sellname) {
        while (num - n < 0) {
            Main.outFile(sellname+" stock not enough ");
            notifyAll();//强制唤醒其他等待的线程
            try {
                wait();
            } catch (InterruptedException e) {
                e.printStackTrace();
            }
        }
        Main.outFile(sellname+" successful sale " + n);
        num = num - n;
    }
}

//对象同步 或 进程上同步