package cn.bluecar.app;

import java.io.IOException;



import java.util.UUID;


import android.app.Activity;
import android.app.AlertDialog;
import android.bluetooth.BluetoothAdapter;
import android.bluetooth.BluetoothDevice;
import android.content.DialogInterface;
import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.MotionEvent;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.View.OnTouchListener;
import android.view.WindowManager;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

public class MainActivity extends Activity {
	private static final String TAG = "BluetoothChat";
	private static final boolean D = true;
	// �� BluetoothChatService ��������͵���Ϣ����
	// Message types sent from the BluetoothChatService Handler
	public static final int MESSAGE_STATE_CHANGE = 1;
	public static final int MESSAGE_READ = 2;
	public static final int MESSAGE_WRITE = 3;
	public static final int MESSAGE_DEVICE_NAME = 4;
	public static final int MESSAGE_TOAST = 5;
	public static final String PREFS_NAME = "BluetoothCar";
	public static final String DEVICE_NAME = "device_name";
	public static final String TOAST = "toast";
	 TextView mTitle;
	TextView DirResult;
	TextView textViewD;
	// Intent request codes�����������
	private static final int REQUEST_CONNECT_DEVICE = 1;
	private static final int REQUEST_ENABLE_BT = 2;
	// ���ӵ��豸����
	private String mConnectedDeviceName = null;
	// �Ի��߳�����������

	// ���ͷ���
	private BluetoothCarService mCarService = null;
	// ��������������
	final BluetoothAdapter bluetoothAdapter = BluetoothAdapter
			.getDefaultAdapter();

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		
		setContentView(R.layout.newn);
	
	       this.getWindow().setFlags(WindowManager.LayoutParams.FLAG_FULLSCREEN,
	    WindowManager.LayoutParams.FLAG_FULLSCREEN);//ʵ��ȫ��
	       
		if (bluetoothAdapter== null) {
			
			AlertDialog.Builder dialog = new AlertDialog.Builder(this);
			dialog.setTitle("No bluetooth devices");
			dialog.setMessage("Your equipment does not support bluetooth, please change device");

			dialog.setNegativeButton("cancel",
					new DialogInterface.OnClickListener() {
						@Override
						public void onClick(DialogInterface dialog, int which) {

						}
					});
			dialog.show();
		}
		DirResult = (TextView) findViewById(R.id.textView11);
		mTitle= (TextView) findViewById(R.id.textView10);
		textViewD= (TextView) findViewById(R.id.textView12);
		Button button1 = (Button) findViewById(R.id.button1);
		button1.setOnClickListener(new OnClickListener() {

			@Override
			public void onClick(View v) {
				// TODO Auto-generated method stub
				if (!bluetoothAdapter.isEnabled()) {
					Intent enableIntent = new Intent(
							BluetoothAdapter.ACTION_REQUEST_ENABLE);
					startActivityForResult(enableIntent, REQUEST_ENABLE_BT);
					// ���򣬶�ȡָ��

				} else {
					if (mCarService == null)
						setupControl();
				}
			}
		});
		Button button2 = (Button) findViewById(R.id.button2);
		button2.setOnClickListener(new OnClickListener() {

			@Override
			public void onClick(View v) {
				// TODO Auto-generated method stub
				if (bluetoothAdapter.isEnabled()) {
					bluetoothAdapter.disable();
				}
			}
		});
	}

	// ���´���Ϊ���ð�ť�����¼�����Ӧ��ť���º�����Ϣ

	private void setupControl() {

		// ��ʼ��һ���������򣬵����¼��ķ��Ͱ�ť
		Button button3 = (Button) findViewById(R.id.button3);

		button3.setOnTouchListener(new OnTouchListener() {
			@Override
			public boolean onTouch(View v, MotionEvent event) {
				// TODO Auto-generated method stub
				switch (event.getAction()) {
				case MotionEvent.ACTION_DOWN:
					DirResult.setText("ǰ��");
					sendMessage("A");
					break;
				case MotionEvent.ACTION_UP:

					sendMessage("C");
					break;
				}
				return false;
			}

		});

		Button button6 = (Button) findViewById(R.id.button6);
		button6.setOnTouchListener(new OnTouchListener() {
			@Override
			public boolean onTouch(View v, MotionEvent event) {
				// TODO Auto-generated method stub
				switch (event.getAction()) {
				case MotionEvent.ACTION_DOWN:
					DirResult.setText("��ת");
					sendMessage("B");
					break;
				case MotionEvent.ACTION_UP:
					sendMessage("C");
					break;
				}
				return false;
			}

		});
		
		Button button7 = (Button) findViewById(R.id.button7);
		button7.setOnTouchListener(new OnTouchListener() {
			@Override
			public boolean onTouch(View v, MotionEvent event) {
				// TODO Auto-generated method stub
				switch (event.getAction()) {
				case MotionEvent.ACTION_DOWN:
					DirResult.setText("��ת");
					sendMessage("D");
					break;
				case MotionEvent.ACTION_UP:
					sendMessage("C");
					break;
				}
				return false;
			}

		});
		Button button10 = (Button) findViewById(R.id.button10);
		button10.setOnTouchListener(new OnTouchListener() {
			@Override
			public boolean onTouch(View v, MotionEvent event) {
				// TODO Auto-generated method stub
				switch (event.getAction()) {
				case MotionEvent.ACTION_DOWN:
					DirResult.setText("����");
					sendMessage("E");
					break;
				case MotionEvent.ACTION_UP:
					sendMessage("C");
					break;
				}
				return false;
			}

		});
		Button button4 = (Button) findViewById(R.id.button4);
    button4.setOnClickListener(new OnClickListener() {
		
		@Override
		public void onClick(View v) {
			// TODO Auto-generated method stub
			textViewD.setText("1");
			sendMessage("F");//1��
		}
	});
    Button button5 = (Button) findViewById(R.id.button5);
    button5.setOnClickListener(new OnClickListener() {
		
		@Override
		public void onClick(View v) {
			// TODO Auto-generated method stub
			textViewD.setText("2");
			sendMessage("G");//2��
		}
	});
    Button button8 = (Button) findViewById(R.id.button8);
    button8.setOnClickListener(new OnClickListener() {
		
		@Override
		public void onClick(View v) {
			// TODO Auto-generated method stub
			textViewD.setText("3");
			sendMessage("H");//3��
		}
	});
    Button button9 = (Button) findViewById(R.id.button9);
    button9.setOnClickListener(new OnClickListener() {
		
		@Override
		public void onClick(View v) {
			// TODO Auto-generated method stub
			textViewD.setText("4");
			sendMessage("I");//4��
		}
	});
		// ��ʼ��ִ���������� BluetoothCarService
		mCarService = new BluetoothCarService(this, mHandler);
	}

	// �ǴӸ� ���»�ȡ��Ϣ��ʾ��UI�Ĵ������

	private final Handler mHandler = new Handler() {
		@Override
		public void handleMessage(Message msg) {
			switch (msg.what) {
			case MESSAGE_STATE_CHANGE:
				if (D)
					Log.i(TAG, "MESSAGE_STATE_CHANGE: " + msg.arg1);
				switch (msg.arg1) {
				case BluetoothCarService.STATE_CONNECTED:
					mTitle.setText(R.string.title_connected_to);
					mTitle.append(mConnectedDeviceName);
					// mConversationArrayAdapter.clear();
					break;
				case BluetoothCarService.STATE_CONNECTING:
					mTitle.setText(R.string.title_connecting);
					break;
				case BluetoothCarService.STATE_LISTEN:
				case BluetoothCarService.STATE_NONE:
					mTitle.setText(R.string.title_not_connected);
					BluetoothAdapter cwjBluetoothAdapter = BluetoothAdapter
							.getDefaultAdapter();

					if (cwjBluetoothAdapter == null) {
						Toast.makeText(MainActivity.this, "����û���ҵ�����Ӳ����������������",
								Toast.LENGTH_SHORT).show();
					}
					if (!cwjBluetoothAdapter.isEnabled()) {

						Intent TurnOnBtIntent = new Intent(
								BluetoothAdapter.ACTION_REQUEST_ENABLE);

						startActivityForResult(TurnOnBtIntent,
								REQUEST_ENABLE_BT);

					}

					break;
				}
				break;
			case MESSAGE_WRITE:

				break;
			case MESSAGE_READ:

				break;
			case MESSAGE_DEVICE_NAME:
				// ���������豸����
				mConnectedDeviceName = msg.getData().getString(DEVICE_NAME);
				Toast.makeText(getApplicationContext(),
						"�ѳɹ����ӵ��� " + mConnectedDeviceName + "���Կ�ʼ����С���ˣ�",
						Toast.LENGTH_SHORT).show();
				break;
			case MESSAGE_TOAST:
				Toast.makeText(getApplicationContext(),
						msg.getData().getString(TOAST), Toast.LENGTH_SHORT)
						.show();
				break;
			}
		}
	};

	@Override
	public boolean onCreateOptionsMenu(Menu menu) {
		// Inflate the menu; this adds items to the action bar if it is present.
		getMenuInflater().inflate(R.menu.main, menu);
		return true;
	}

	public void onActivityResult(int requestCode, int resultCode, Intent data) {
        switch (requestCode) {
        case REQUEST_CONNECT_DEVICE:  
         if (resultCode == Activity.RESULT_OK) {
                // ��ȡ�豸��ַ
        	
                String address = data.getExtras()
                                     .getString(DeviceListActivity.EXTRA_DEVICE_ADDRESS);
                Toast.makeText(this, "���豸MAC��ַΪ--->"+address, Toast.LENGTH_SHORT).show();
                //��ȡ�豸����
               
                BluetoothDevice device = bluetoothAdapter.getRemoteDevice(address);
                UUID uuid = UUID.fromString("00001101-0000-1000-8000-00805f9b34fb");
                try {
               device.createRfcommSocketToServiceRecord(uuid);
    
    } catch (IOException e1) {
     // TODO Auto-generated catch block
     e1.printStackTrace();
    }
                // Attempt to connect to the device
                mCarService.connect(device);   //����BluetoothCarService��ķ������������豸
             
               
            }

            break;
        case REQUEST_ENABLE_BT:
            //���������������󷵻�ʱ
        	
            if (resultCode == Activity.RESULT_OK) {
                // �����������ã���ȡԤ��ָ��ֵ
             setupControl();
            } else {
                // �û�û������������������
                Log.d(TAG, "BT not enabled");
                Toast.makeText(this, R.string.bt_not_enabled_leaving, Toast.LENGTH_SHORT).show();
                finish();
            }
        }
    }

	/**
	 * ��������
	 * 
	 * @param i
	 *            A string of text to send.
	 */
	private void sendMessage(String message) {
		// ����Ƿ���������
		if (mCarService.getmState() != BluetoothCarService.STATE_CONNECTED) {
			Toast.makeText(this, R.string.not_connected, Toast.LENGTH_SHORT)
					.show();
			return;
		}
		// �����ʲôʵ�ʷ���
		if (message.length() > 0) {
			// ��ȡ��Ϣ�ֽڲ�д�� BluetoothCarService
			byte[] send = message.getBytes();
			mCarService.write(send);
		}
	}

	public boolean onOptionsItemSelected(MenuItem item) {
		// TODO Auto-generated method stub
		switch (item.getItemId()) {
		case R.id.str:
			Intent intent = new Intent(this, DeviceListActivity.class);
			startActivityForResult(intent, REQUEST_CONNECT_DEVICE);
			break;

		case R.id.str2:
			AlertDialog.Builder dlg = new AlertDialog.Builder(this);
			final EditText editText = new EditText(this);
			dlg.setView(editText);
			dlg.setTitle("�������û���");
			dlg.setPositiveButton("����", new DialogInterface.OnClickListener() {

				@Override
				public void onClick(DialogInterface dialog, int which) {
					// TODO Auto-generated method stub
					if (editText.getText().toString().length() != 0)
						bluetoothAdapter.setName(editText.getText().toString());

				}
			});
			dlg.create();
			dlg.show();
			break;
		case R.id.str3:
					Intent intent1 = new Intent(
							BluetoothAdapter.ACTION_REQUEST_DISCOVERABLE);
					intent1.putExtra(BluetoothAdapter.EXTRA_DISCOVERABLE_DURATION,
							300);
					startActivity(intent1);
			break;
		}
		return super.onOptionsItemSelected(item);
	}
	 
	 
}
