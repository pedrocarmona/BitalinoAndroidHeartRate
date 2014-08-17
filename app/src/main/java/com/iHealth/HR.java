package com.iHealth;


import android.util.Log;

public class HR {
    Boolean ECGOK = false;
    public Boolean HROK = false;
	DoubleQ m_ECGHRQueue = new DoubleQ(10), m_PPGHRQueue= new DoubleQ(10);
	public int HRV;
    private static final String TAG = "BITalinoServiceHR";
    double avg = 0;
    int lenght =0;

    public void CalcHR_ECG(ECG ecg)
	{
		ECGOK = false;
		if(ecg.PeaksOK())
		{
			int		hr, nLastPeak = -1;
			int	peak;

			for (int i = 0; i < ecg.buffersize ; i++)
			{
				peak =ecg.GetPeakAt(i);
				if (1 == peak)
					nLastPeak = i;
				else if (2 == peak)
				{
					if (nLastPeak >= 0)
					{
						hr = (int)(((double)(ecg.samplesize * 60) / (i - nLastPeak)) + 0.5);
                        if (hr>=40 && hr<=160) {
                            m_ECGHRQueue.push(hr);
                            avg = (avg * lenght + hr) / (lenght + 1);
                            lenght++;
                        }

                    }
					nLastPeak = i;
				}
			}

			ECGOK = true;
            //HRV = (int)avg;
			if(m_ECGHRQueue.IsFull()){
                HROK=true;
                HRV = (int)m_ECGHRQueue.GetMean();
            }

		}
	}


    
}

