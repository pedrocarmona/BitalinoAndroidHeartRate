package com.iHealth;

import java.io.*;

import android.content.Context;
import android.os.Handler;
import android.util.Log;

public class Calculation extends Thread{


    public volatile Boolean Continue = false;
    public DataInputStream dinECG;
    Handler mhandler;
    ECG ecg;
    HR hr;
    int[] results = new int[4];
    private static final String TAG = "BITalinoServiceCALC";


    public Calculation(PipedInputStream pinECG )
    {
        Log.d(TAG, "I am in!");

        dinECG = new DataInputStream(pinECG);
        ecg= new ECG();

    }


    int ec=0,pp=0;
    int count=0, count2 =0;
    public void run()
    {
        Log.d(this.getClass().getName(), "Thread Started");
        Continue = true;
        hr = new HR();
        DataQueue ecgbuffer = new DataQueue(3000);
        while(Continue){
            try{
                ecgbuffer.push(dinECG.readInt());
            }catch(Exception e){ e.printStackTrace(); };


            if(ecgbuffer.IsFull())
            {
                ecg.pushbulk(ecgbuffer.queue);
                ecg.DetectPeak();
                ecgbuffer.Clear();


                try
                {
                    hr.CalcHR_ECG(ecg);
                    results[0] = hr.HRV;
                    Log.d(TAG, "Yayyy:"+results[0]);
                }
                catch(Exception e){}

                ecg.ResetNewPeaks();
            }


        }//end of while
        Log.d("CALCULATION__", "CalThread end");
    }
}