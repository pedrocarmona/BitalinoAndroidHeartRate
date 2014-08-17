package com.iHealth;

import android.os.Handler;
import android.os.Message;
import android.util.Log;

import java.io.*;
public class Decode implements  Runnable {

	DataQueue DQ = new DataQueue(8);
	DataInputStream din;
	DataOutputStream doutECG, doutPPG, coutECG,coutPPG;

	public Decode(PipedInputStream pin, PipedOutputStream poutECG, PipedOutputStream poutPPG, PipedOutputStream coutECG, PipedOutputStream coutPPG)
	{
		din = new DataInputStream(pin);
		doutECG = new DataOutputStream(poutECG);
		doutPPG = new DataOutputStream(poutPPG);
		this.coutECG = new DataOutputStream(coutECG);
		this.coutPPG = new DataOutputStream(coutPPG);
	}
	
	public void CheckPackage(int b) throws IOException
	{
		
		int header;

			DQ.push(b);
			header = DQ.getHead();
			if( (header & 0x80) == 0x80)
			{
				decode();
				DQ.Clear();
			}
	}
	int ecg , ppg;
	int count=0;
	int checksum;
	public void decode() throws IOException
	{
		for(int i = 1;i< DQ.queue.length-1;i++)
		{
			checksum+=DQ.queue[i];
		}
		checksum = (0xff & checksum);
		checksum =  (0xff - checksum);
		if( checksum == DQ.queue[DQ.queue.length-1]){
		ecg = ((DQ.queue[1] & 0x7F) << 3) +((DQ.queue[2] & 0x70) >> 4);   
		ppg = ((DQ.queue[2] & 0x07) << 7) + (DQ.queue[3] & 0x7F);
		ppg = 1024-ppg;
		String j = ""+ppg;
		Log.d(null, j);
		DQ.Clear();
		count++;
		
		if(count% 30==0){
		try {
			doutECG.writeInt(ecg);
			doutPPG.writeInt(ppg);

		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}}
		checksum = 0;}

	}
	int b;
	public void run() 
	{
		while(true)
		{
			try {
				b = din.readInt();
				CheckPackage(b);
			} catch (IOException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
		}
		
	}
}