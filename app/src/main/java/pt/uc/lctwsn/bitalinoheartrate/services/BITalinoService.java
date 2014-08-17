package pt.uc.lctwsn.bitalinoheartrate.services;

import android.app.Activity;
import android.app.IntentService;
import android.bluetooth.BluetoothAdapter;
import android.bluetooth.BluetoothDevice;
import android.bluetooth.BluetoothSocket;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Binder;
import android.os.Handler;
import android.os.IBinder;
import android.util.Log;

import com.bitalino.comm.BITalinoDevice;
import com.bitalino.comm.BITalinoException;
import com.bitalino.comm.BITalinoFrame;
import com.iHealth.DataQueue;
import com.iHealth.ECG;
import com.iHealth.HR;

import java.io.InputStream;
import java.io.OutputStream;
import java.util.ArrayList;
import java.util.StringTokenizer;
import java.util.UUID;

import pt.uc.lctwsn.bitalinoheartrate.listeners.BITalinoListener;


/**
 * Created by pedrocarmona on 31/07/14.
 *
 * TODO: display the ecg on a view
 */

public class BITalinoService extends IntentService {
    private static final String TAG = "BITalinoService";
    private static final boolean UPLOAD = false;
    private static String timeStamp = "";

    private Integer heartRate;
    private String log;
    private long start_time;

    private boolean processStarted;
    /*
     * http://developer.android.com/reference/android/bluetooth/BluetoothDevice.html
     * #createRfcommSocketToServiceRecord(java.util.UUID)
     *
     * "Hint: If you are connecting to a Bluetooth serial board then try using the
     * well-known SPP UUID 00001101-0000-1000-8000-00805F9B34FB. However if you
     * are connecting to an Android peer then please generate your own unique
     * UUID."
     */
    private static final UUID MY_UUID = UUID
            .fromString("00001101-0000-1000-8000-00805F9B34FB");

    public int hr;


    public BITalinoService() {
        super("BITalinoService");
        Log.d(TAG, "BITalinoService Constructor");
        heartRate = 0;
        log = "";
        processStarted = false;


    }

    @Override
    public void onCreate() {
        super.onCreate();
        Log.d(TAG, "onCreate");

        if (!processStarted) {
            connectToBITalino();
            processStarted = true;
        }
    }

    private final IBinder mBinder = new LocalBinder();

    /**
     * Class used for the client Binder.  Because we know this service always
     * runs in the same process as its clients, we don't need to deal with IPC.
     */
    public class LocalBinder extends Binder {
        public BITalinoService getService() {
            Log.d(TAG, "LocalBinder getService");

            // Return this instance of LocalService so clients can call public methods
            return BITalinoService.this;

        }
    }

    @Override
    public IBinder onBind(Intent intent) {
        Log.d(TAG, "onBind");
        return mBinder;

    }

    @Override
    protected void onHandleIntent(Intent workIntent) {
        // Gets data from the incoming Intent
        String dataString = workIntent.getDataString();
        Log.d(TAG, "onHandleIntent");
    }


    // Like in the Service sample code, plus:
    public static String ACTION_START = "com.mypackage.START";

    private final ArrayList<BITalinoListener> mListeners
            = new ArrayList<BITalinoListener>();
    private final Handler mHandler = new Handler();



    public void registerListener(BITalinoListener listener) {
        Log.d(TAG, "registerListener");
        mListeners.add(listener);
        initializeView(listener, this.log, this.heartRate);
        Log.d(TAG, "Listener registed");
        mBITalinoRunnable.run();

    }

    public void unregisterListener(BITalinoListener listener) {
        mListeners.remove(listener);
    }



    public int onStartCommand(Intent intent, int flags, int startId) {
        Log.d(TAG, "onStartCommand");

        if (ACTION_START.equals(intent.getAction())) {
            start_time = System.nanoTime();
            mHandler.removeCallbacks(mBITalinoRunnable);
            mHandler.post(mBITalinoRunnable);
        }
        return START_STICKY;

    }

    public void onDestroy() {
        Log.d(TAG, "onDestroy");
        mHandler.removeCallbacks(mBITalinoRunnable);
        destroyBITalino();
    }


    /*=================================================================================*/


    private final Runnable mBITalinoRunnable = new Runnable() {
        public void run() {
            Log.d(TAG, "mBITalinoRunnable");
            bitalinoBackground();
            mHandler.postDelayed(mBITalinoRunnable, 4000);

        }
    };

    private final Runnable mBITalinoRunnableReTryConnection = new Runnable() {
        public void run() {
            Log.d(TAG, "mBITalinoRunnable");
            boolean connected = connectToBITalino();
            if(!connected) {
                mHandler.postDelayed(mBITalinoRunnableReTryConnection, 15000);
            }

        }
    };

    private BluetoothDevice dev = null;
    private BluetoothSocket sock = null;
    private InputStream is = null;
    private OutputStream os = null;
    private BITalinoDevice bitalino;


    private boolean testInitiated = false;

    private final static int [] selChannels = {2};


    private boolean connectToBITalino(){

        try {
            // Let's get the remote Bluetooth device
            //final String remoteDevice = "20:13:08:08:15:83";
            final String remoteDevice = "98:D3:31:B2:13:1A";

            final BluetoothAdapter btAdapter = BluetoothAdapter.getDefaultAdapter();
            dev = btAdapter.getRemoteDevice(remoteDevice);

            /*
             * Establish Bluetooth connection
             *
             * Because discovery is a heavyweight procedure for the Bluetooth adapter,
             * this method should always be called before attempting to connect to a
             * remote device with connect(). Discovery is not managed by the Activity,
             * but is run as a system service, so an application should always call
             * cancel discovery even if it did not directly request a discovery, just to
             * be sure. If Bluetooth state is not STATE_ON, this API will return false.
             *
             * see
             * http://developer.android.com/reference/android/bluetooth/BluetoothAdapter
             * .html#cancelDiscovery()
             */
            Log.d(TAG, "Stopping Bluetooth discovery.");
            btAdapter.cancelDiscovery();

            sock = dev.createRfcommSocketToServiceRecord(MY_UUID);
            sock.connect();
            testInitiated = true;

            bitalino = new BITalinoDevice(1000, selChannels);
            Log.d(TAG, "Connecting to BITalino [" + remoteDevice + "]..");
            logInfoToView("Connecting to BITalino [" + remoteDevice + "]..");
            bitalino.open(sock.getInputStream(), sock.getOutputStream());
            Log.d(TAG, "Connected.");
            logInfoToView("Connected.");

            // start acquisition on predefined analog channels
            bitalino.start();

            // trigger digital outputs
            // int[] digital = { 1, 1, 1, 1 };
            // device.trigger(digital);
            return true;
        } catch (Exception e) {
            Log.e(TAG, "There was an error.", e);
            return false;
        }

    }


    HR hR = new HR();

    private void bitalinoBackground(){
        try {
            final int numberOfSamplesToRead = 1000;
            DataQueue[] dataQueues = new DataQueue[4];
            //read 2 seconds
            for (int i=0; i <4; i++){
                dataQueues[i] = new DataQueue(500);
                BITalinoFrame[] frames = bitalino.read(500);
                for (BITalinoFrame frame : frames) {
                    dataQueues[i].push(frame.getAnalog(2));
                }
            }
            processInThread(dataQueues);

        } catch (BITalinoException e) {
            Log.e(TAG, "Connection Lost, retrying to connect.", e);
            logInfoToView("Connection Lost, retrying to connect.");
            mBITalinoRunnableReTryConnection.run();

        } catch (Exception e) {
            Log.e(TAG, "There was an error.", e);
        }
    }

    public void processInThread(final DataQueue [] dataQueues) throws Exception{
        Runnable processor = (new Runnable(){
            public void run(){
                synchronized (hR) {
                    ECG ecg= new ECG();
                    for (int i = 0; i < 4; i++) {
                        ecg.pushbulk(dataQueues[i].queue);
                        ecg.DetectPeak();
                        dataQueues[i].Clear();
                        hR.CalcHR_ECG(ecg);
                        ecg.ResetNewPeaks();
                    }
                    if (hR.HROK){
                        int hravg = hR.HRV;
                        sendHeartRate(hravg);
                    }else{
                        logInfoToView("Processing ecg. Please wait... ");
                    }
                }
            }
        });
        processor.run();
    }

    public int[] loadFromSharedPreferences(){
        SharedPreferences prefs = getSharedPreferences("arrayecg", MODE_PRIVATE);
        String savedString = prefs.getString("list", "");
        StringTokenizer st = new StringTokenizer(savedString, ",");
        int[] savedList = new int[5000];
        int i =0;
        while(st.hasMoreTokens()){
            savedList[i++] = Integer.parseInt(st.nextToken());
        }
        return savedList;
    }
    public void saveToSharedPreferences(int[] list){

        StringBuilder str = new StringBuilder();
        for (int element : list){
            str.append(""+element).append(",");
        }
        String strlist =  str.toString();
        Log.e(TAG + "model",strlist);

        SharedPreferences prefs = getSharedPreferences("arrayecg", MODE_PRIVATE);
        SharedPreferences.Editor e = prefs.edit();
        e.clear();
        e.putString("list", strlist);
        e.commit();
        Log.e(TAG, "Saved.");
    }



    protected void destroyBITalino() {
        // stop acquisition and close bluetooth connection
        try {
            if (bitalino != null)
                bitalino.stop();
            logInfoToView("BITalino is stopped");
            if (sock != null)
                sock.close();
            logInfoToView("And we're done! :-)");
        } catch (Exception e) {
            Log.e(TAG, "There was an error.", e);
        }

    }

        /*=================================================================================*/


    protected void logInfoToView(String log) {
        this.log = this.log +"\n"+ log;
        for (int i=mListeners.size()-1; i>=0; i--) {
            mListeners.get(i).didGotLogInfo(log);
        }

    }



    private void sendHeartRate(int hr) {
        this.heartRate = hr;
        Log.d(TAG,"number of listners thread:"+mListeners.size());
        Log.d(TAG,"heartrate in sendHeartRate:"+hr);

        for (int i=mListeners.size()-1; i>=0; i--) {
            mListeners.get(i).didGotHeartRate(hr);
        }

    }

    private void initializeView(final BITalinoListener listener, final String log, final int heartRate){
        if(listener instanceof Activity)
        {
            Activity activity = (Activity) listener;
            activity.runOnUiThread(new Runnable()
            {
                public void run()
                {
                    listener.didGotLogInfo(log);
                    listener.didGotHeartRate(heartRate);
                }
            });
        }

    }




        /*=================================================================================*/


}