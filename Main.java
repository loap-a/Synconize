package CD_Shop;
import java.util.ArrayList;
import java.util.Random;
import java.util.Vector;
import java.io.*;
import static java.lang.Thread.*;
class rent_cd_all
{
    ArrayList<rent_cd> my_rent_cd;
    public rent_cd_all()
    {
        my_rent_cd = new ArrayList<rent_cd>();
        for(int i=1;i<=10;i++)
        {
            my_rent_cd.add(new rent_cd(i-1));
        }
    }
    public void rent(int number,int id) {
        Random r = new Random();
        if (my_rent_cd.get(number).flag) {
            Main.outFiles(" cd " + number + " has been occupied ");
            if (r.nextBoolean()) {
                Main.outFile("and choose to wait for cd " +number+" at " + System.currentTimeMillis());
                try {
                    wait(310);
                } catch (InterruptedException e) {
                    e.printStackTrace();
                }
                if(!my_rent_cd.get(number).flag) {

                    Main.outFile("rent-thread id " + id + " cd " + number + " has been rented after some wait " + "at " + System.currentTimeMillis());
                    my_rent_cd.get(number).flag = true;
                    try {
                        wait(310);
                    } catch (InterruptedException e) {
                        e.printStackTrace();
                    }
                    my_rent_cd.get(number).flag = false;
//                    notifyAll();
                    Main.outFile("rent-thread id " + id + " cd " + number + " has been returned" + " at " + System.currentTimeMillis());

                }
            } else {
                Main.outFile("choose not to wait" + " at " + System.currentTimeMillis());
            }
        } else {
            Main.outFile("cd " + number + " now has been rented by you " + " at " + System.currentTimeMillis());
            my_rent_cd.get(number).flag = true;
            try {
                wait(300);
            } catch (InterruptedException e) {
                e.printStackTrace();
            }
            my_rent_cd.get(number).flag = false;
//            notifyAll();
            Main.outFile("rent-thread id " + id + " cd " + number + " has been returned" + " at " + System.currentTimeMillis());
        }
    }
}
class rent_cd
{
    int id;
    boolean flag;
    public rent_cd(int i){id=i;flag=false;}
}

public class Main {
    public static void main(String[] args)
    {
        all_cd cd=new all_cd();
        rent_cd_all rcd=new rent_cd_all();
        control_thread ct=new control_thread(cd,rcd);
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
    public static void outFiles(String s) {
        File file = new File("E:/record.txt");
        BufferedWriter bw = null;
        try {
            bw = new BufferedWriter(new FileWriter(file,true));
            bw.write(s);
            bw.close();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }
}
class control_thread extends Thread {
    all_cd cd;
    rent_cd_all rcd;
    control_thread(all_cd c,rent_cd_all cc){this.cd=c;this.setDaemon(false);this.rcd=cc;}

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
        new rent_thread(rcd).start();
        new rent_thread(rcd).start();
        new rent_thread(rcd).start();
        new rent_thread(rcd).start();
        new rent_thread(rcd).start();

        try {
            sleep(120*1000);
        } catch (InterruptedException e) {
            e.printStackTrace();
        }

    }
}

class rent_thread extends Thread{
    rent_cd_all rcd;
    static int ids=0;
    int id;
    rent_thread(rent_cd_all c) {
        this.rcd = c;
        this.setDaemon(true);
        id=++ids;
    }
    @Override
    public void run() {
        Main.outFile("rent thread id "+id+" start"+" at "+System.currentTimeMillis());
        Random r = new Random();
        while (true) {
            try {
                sleep(100+r.nextInt(200));
            } catch (InterruptedException e) {
                e.printStackTrace();
            }
            int rent_num=r.nextInt(10);
            synchronized (rcd)
            {
                Main.outFiles("rent-thread id "+id+" ");
                rcd.rent(rent_num,id);
            }
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
        Main.outFile("sale thread id "+id+" start "+" at "+System.currentTimeMillis());
        Random r = new Random();
        while (true) {
            try {
                sleep(r.nextInt(200));
            } catch (InterruptedException e) {
                e.printStackTrace();
            }
            int n = r.nextInt(5);
            int sell_num=r.nextInt(10);
            synchronized (cd) {
                    Main.outFiles("sell-thread id "+id+" ");
                    cd.sale(sell_num,n,id);
            }
        }
    }
}

class all_cd {
    Vector<CD>my_cd=new Vector<CD>();
    all_cd()
    {
        for(int i=0;i<10;i++)
        {
            my_cd.add(new CD("cd "+(i),10));
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
        Main.outFile("all cd "+"'s num has been set to 10 "+" at "+System.currentTimeMillis());
        notifyAll();
    }
    void sale(int number,int num,int id)
    {
        Random r=new Random();
        while(my_cd.get(number).num - num < 0) {
            Main.outFiles((" cd "+number+" stock not enough for "+num+" "));
            notifyAll();
            if(r.nextBoolean()) {
                Main.outFile("choose to wait"+" for cd "+number+" at "+System.currentTimeMillis());
                try {
                    wait();
                } catch (InterruptedException e) {
                    e.printStackTrace();
                }
                if(my_cd.get(number).num-num>0)
                {
                    my_cd.get(number).num -= num;
                    Main.outFile("sale-thread id "+id+" successful sale cd "+number+" for "+num+" after some wait at "+System.currentTimeMillis());
                }
                return;
            }
            else
            {
                Main.outFile( "choose not to wait "+" at "+System.currentTimeMillis());
                return;
            }
        }

            Main.outFile((my_cd.get(number).name + " successful sale " + num) + " at " + System.currentTimeMillis());
            my_cd.get(number).num -= num;

    }
    void rent(int number,int id)
    {
        Random r=new Random();
        if(my_cd.get(number).rent_flag)
        {
            Main.outFiles(" cd "+number+" has been occupied ");
            if(r.nextBoolean())
            {
                Main.outFile("after some wait  cd "+number+" has been rented "+" at "+System.currentTimeMillis());
                my_cd.get(number).rent_flag=true;
                try {
                    sleep(310);
                } catch (InterruptedException e) {
                    e.printStackTrace();
                }
                Main.outFile("rent-thread id "+id+" cd "+number+ " has been returned "+"at "+System.currentTimeMillis());
                my_cd.get(number).rent_flag=false;
                return;
            }
            else
            {
                Main.outFile("choose not to wait"+" at "+System.currentTimeMillis());
                return;
            }


        }
        Main.outFile("cd "+number+" now has been rented by you "+" at "+System.currentTimeMillis());
        my_cd.get(number).rent_flag=true;
        try {
            sleep(300);
        } catch (InterruptedException e) {
            e.printStackTrace();
        }
        my_cd.get(number).rent_flag=false;
        Main.outFile("rent-thread id "+id+ " cd "+number+" has been returned" +" at "+System.currentTimeMillis());
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