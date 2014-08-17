package com.iHealth;


import java.util.Observable;
import java.util.Observer;
import java.io.*;


public class SampleDynamicXYDatasource implements Runnable {

	// encapsulates management of the observers watching this datasource for update events:
	class MyObservable extends Observable {
		@Override
		public void notifyObservers() {
			setChanged();
			super.notifyObservers();
		}
	}
	double Amptup = 0, Amptdown=0;
	double ampFactor=1;
	public static final int SINE1 = 0;
	public static final int SINE2 = 1;
	private static int SAMPLE_SIZE = 100;
	private MyObservable notifier;
	private double d = 0.1;
	private double k=1;
	private int[] ecg =new int[SAMPLE_SIZE];
	private int[] ppg = new int[SAMPLE_SIZE], ppgauto = new int[SAMPLE_SIZE];
	double mean;
	int ec, pp;
	public DataInputStream dinECG, dinPPG;
	public SampleDynamicXYDatasource(PipedInputStream pinECG, PipedInputStream pinPPG)	{

		notifier = new MyObservable();
		dinECG = new DataInputStream(pinECG);
		dinPPG = new DataInputStream(pinPPG);
	}
	
	public  synchronized void update(int ec, int pp)
	{
;

	}
	public Boolean chan= true;
	public void channelswitch()
	{
		chan = !chan;
		
		
	}

	DataQueue ecgN = new DataQueue(SAMPLE_SIZE), ppgN= new DataQueue(SAMPLE_SIZE);
	//@Override
	public void run() {
		//		try {
int count = 0;
		while(true){
			
			
			
				
				try {
				
					ec= dinECG.readInt();
					System.arraycopy(ecg, 1, ecg, 0, ecg.length-1);
					pp=dinPPG.readInt() ;
					
				
					
					
					System.arraycopy(ppg, 1, ppg, 0, ppg.length-1);
//					System.arraycopy(ecgN.queue, 0,ecg ,  ecg.length-11, 10);
//					System.arraycopy(ppgN.queue, 0,ppg ,  ppg.length-11, 10);
//					ecgN.Clear();
//					ppgN.Clear();
					ecg[ecg.length-1]=ec;
					ppg[ppg.length-1]=pp;
					
				count++;	
				} catch (IOException e) {
					// TODO Auto-generated catch block
					e.printStackTrace();
				}
				if (count%38==0){
					int sum= 0;
					double max = 0, min = 10000;
					for(int i =0;i<ppg.length;i++)
					{
						sum++;
						if (ppg[i]>max) max = ppg[i];
						if (ppg[i]<min) min = ppg[i];
						
					}
					ampFactor = (max-min)/900;
					ampFactor = 0.8/ampFactor;

					 
					
					 mean = 1.0*sum/ppg.length;
					 Amptup = 900-max;
					 Amptdown = min-100;

					notifier.notifyObservers();}
                                          
			
			
		}

	}
	
	

	public int getItemCount(int series) {
		return SAMPLE_SIZE;
	}

	public Number getX(int series, int index) {
		if (index >= SAMPLE_SIZE) {
			throw new IllegalArgumentException();
		}
		return index;
	}

	public Number getY(int series, int index) {
		if (index >= SAMPLE_SIZE) {
			throw new IllegalArgumentException();
		}


		if(chan){
		d= ecg[index];
		}else
		{	
//			if(ppg[index]>mean){
//			d= ppg[index]+Amptup;
//			}else if(ppg[index]<mean)
//			{
//				d= ppg[index]-Amptdown;
//			}
//			if(mean!=500)
			{d = ppg[index];
//				d = d - (mean -500);
			}
//			d = ppg[index]*ampFactor;
		}
		
//		double amp = d;
//		switch (series) {
//		case SINE1:
			return d;
//		case SINE2:
//			return k;
//		default:
//			throw new IllegalArgumentException();
//		}
	}

	public void addObserver(Observer observer) {
		notifier.addObserver(observer);
	}

	public void removeObserver(Observer observer) {
		notifier.deleteObserver(observer);
		
	}

	

}
