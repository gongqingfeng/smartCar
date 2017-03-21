package cn.bluecar.app;

import java.io.IOException;

import java.io.InputStream;
import java.io.OutputStream;
import java.util.UUID;

import android.bluetooth.BluetoothAdapter;
import android.bluetooth.BluetoothDevice;
import android.bluetooth.BluetoothSocket;
import android.content.Context;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.util.Log;

public class BluetoothCarService {
    private static final String TAG = "BluetoothCarService";


    private static final UUID MY_UUID = UUID.fromString("00001101-0000-1000-8000-00805f9b34fb");

    private  BluetoothAdapter mAdapter;
    private  Handler mHandler;
    private ConnectThread mConnectThread;
    private ConnectedThread mConnectedThread;
     int mState;

    public int getmState() {
		return mState;
	}

	public static final int STATE_NONE = 0;       // ���κ�״̬ 

   public static final int STATE_LISTEN = 1;     // �ȴ�����

    public static final int STATE_CONNECTING = 2; // ��ʼ������

    public static final int STATE_CONNECTED = 3;  // �������豸

    public BluetoothCarService(Context context, Handler handler) {
        mAdapter = BluetoothAdapter.getDefaultAdapter();
        mState = STATE_NONE;
        mHandler = handler;
    }

//�����豸����״̬

    private synchronized void setState(int state) {
        mState = state;

        mHandler.obtainMessage(MainActivity.MESSAGE_STATE_CHANGE, state, -1).sendToTarget();
    }

     public synchronized void start() {

            setState(STATE_LISTEN);
    }

//�����豸������

    public synchronized void connect(BluetoothDevice device) {

        if (mState == STATE_CONNECTING) {
            if (mConnectThread != null) {mConnectThread.cancel(); mConnectThread = null;}
        }

        // ��ȡ������ǰ�������߳�

        if (mConnectedThread != null) {mConnectedThread.cancel(); mConnectedThread = null;}

        // ���ݸ���MAC����ʼһ�������߳� 
        
       mConnectThread = new ConnectThread(device);
        mConnectThread.start();
        setState(STATE_CONNECTING);
    }

    /**
     * ��ʼһ��ConnectedThread����ʼ������������
     */
    public synchronized void connected(BluetoothSocket socket, BluetoothDevice device) {

        // ȡ����������ӵ��߳�
        if (mConnectThread != null) {mConnectThread.cancel(); mConnectThread = null;}

        if (mConnectedThread != null) {mConnectedThread.cancel(); mConnectedThread = null;}

       

        // �������ӵ��������߳�
        mConnectedThread = new ConnectedThread(socket);
        mConnectedThread.start();

        // ��MAC��ַ���� UI Activity
        Message msg = mHandler.obtainMessage(MainActivity.MESSAGE_DEVICE_NAME);
        Bundle bundle = new Bundle();
        bundle.putString(MainActivity.DEVICE_NAME, device.getName());
        msg.setData(bundle);
        mHandler.sendMessage(msg);

        setState(STATE_CONNECTED);
    }

    /**
     * ֹͣ�����߳�

     */
    public synchronized void stop() {
             if (mConnectThread != null) {mConnectThread.cancel(); mConnectThread = null;}
        if (mConnectedThread != null) {mConnectedThread.cancel(); mConnectedThread = null;}
       
        setState(STATE_NONE);
    }

//�������ݵ������ĺ�����

    public void write(byte[] send) {
                ConnectedThread r;
            synchronized (this) {
            if (mState != STATE_CONNECTED) return;
            r = mConnectedThread;
        }
           r.write(send);
    }

    /**
     * ����ʧ��ʱ��UI����ʾ.
     */
    private void connectionFailed() {
        setState(STATE_LISTEN);

        // ʧ����Ϣ����Activity
        Message msg = mHandler.obtainMessage(MainActivity.MESSAGE_TOAST);
        Bundle bundle = new Bundle();
        bundle.putString(MainActivity.TOAST, "�޷����ӵ��豸����ȷ����λ�����������Ƿ�����");
        msg.setData(bundle);
        mHandler.sendMessage(msg);
    }

    /**
     * ��ʧ����ʱ��UI����ʾ.
     */
    private void connectionLost() {
        setState(STATE_LISTEN);

        // ʧ����Ϣ����Activity
        Message msg = mHandler.obtainMessage(MainActivity.MESSAGE_TOAST);
        Bundle bundle = new Bundle();
        bundle.putString(MainActivity.TOAST, "��Ŀ���豸���Ӷ�ʧ");
        msg.setData(bundle);
        mHandler.sendMessage(msg);
    }

//�����̣߳�

    private class ConnectThread extends Thread {
        private final BluetoothSocket mmSocket;
        private final BluetoothDevice mmDevice;

        public ConnectThread(BluetoothDevice device) {
            mmDevice = device;
            BluetoothSocket tmp = null;

            //ͨ�������豸�����������л�ȡһ��socket
            
            try {
                tmp = device.createRfcommSocketToServiceRecord(MY_UUID);
            } catch (IOException e) {
                Log.e(TAG, "create() failed", e);
            }
            mmSocket = tmp;
        }

        public void run() {
                setName("�����߳�");

            // ���ر����Ӻ��豸�ɼ�����Ϊ���ɼ�
            mAdapter.cancelDiscovery();

            try {
                 mmSocket.connect();
            } catch (IOException e) {
                connectionFailed();
                // �ر�Socket
                try {
                    mmSocket.close();
                } catch (IOException e2) {
                    Log.e(TAG, "unable to close() socket during connection failure", e2);
                }
               BluetoothCarService.this.start();
                return;
            }

        synchronized (BluetoothCarService.this) {
                mConnectThread = null;
            }

            connected(mmSocket, mmDevice);
        }

        public void cancel() {
            try {
                mmSocket.close();
            } catch (IOException e) {
                Log.e(TAG, "close() of connect socket failed", e);
            }
        }
    }

    /**
     * ���߳���ȡ�����Ӻ�ִ��
     */
    private class ConnectedThread extends Thread {
        private final BluetoothSocket mmSocket;
        private final InputStream mmInStream;
        private final OutputStream mmOutStream;

        public ConnectedThread(BluetoothSocket socket) {
            mmSocket = socket;
            InputStream tmpIn = null;
            OutputStream tmpOut = null;

            try {
                tmpIn = socket.getInputStream();
                tmpOut = socket.getOutputStream();
            } catch (IOException e) {
                   }

            mmInStream = tmpIn;
            mmOutStream = tmpOut;
        }

        public void run() {
            Log.i(TAG, "BEGIN mConnectedThread");
            byte[] buffer = new byte[1024];
            int bytes;

            //���ּ���
            while (true) {
                try {
                    // ��InputStream��ȡ
                    bytes = mmInStream.read(buffer);

                    // �ѻ�ȡ����Ϣ�ش���UI

                    mHandler.obtainMessage(MainActivity.MESSAGE_READ, bytes, -1, buffer)
                            .sendToTarget();
                } catch (IOException e) {
                    connectionLost();
                    break;
                }
            }
        }

        public void write(byte[] send) {
            try {
                mmOutStream.write(send);
                // �ѷ��͵�������UI����ʾ
                mHandler.obtainMessage(MainActivity.MESSAGE_WRITE, -1, -1, send)
                        .sendToTarget();
            } catch (IOException e) {
               }
        }

        public void cancel() {
            try {
                mmSocket.close();
            } catch (IOException e) {
               }
        }
    }

}
