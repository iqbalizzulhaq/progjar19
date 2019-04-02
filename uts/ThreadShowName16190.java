/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package uts;

/**
 *
 * @author User
 */
import static java.lang.Thread.sleep;
import java.util.Random;

public class ThreadShowName16190 extends Thread
{   
    public static  String dataString[]={"Tomat","jambu","apel","mangga","pisang"};
    public static Random rand = new Random();
    public static void main (String[] args)
    {
        ThreadShowName16190 thread1, thread2,thread3;
        thread1 = new ThreadShowName16190();
        thread2 = new ThreadShowName16190();
        thread3 = new ThreadShowName16190();
        thread1.start(); //Will call run.
        thread2.start();//Will call run.
        thread3.start();
       
    }
public void run()
{
    int pause;
    for (int i=0; i<dataString.length ; i++)
    {
        try
        {   
            
           int a = rand.nextInt(5);
           System.out.println(getName()+" being executed.==>"+dataString[a]);
           sleep(a);
        }
        catch (InterruptedException interruptEx)
        {

        System.out.println(interruptEx);
        }
        }
    }
}
