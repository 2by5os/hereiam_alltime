package com.geekwims.hereiam.activity;

import android.app.Activity;
import android.app.Notification;
import android.app.NotificationManager;
import android.app.PendingIntent;
import android.app.ProgressDialog;
import android.bluetooth.BluetoothAdapter;
import android.bluetooth.BluetoothDevice;
import android.bluetooth.BluetoothSocket;
import android.content.Context;
import android.content.Intent;
import android.os.AsyncTask;
import android.os.Bundle;
import android.os.Vibrator;
import android.support.annotation.Nullable;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;
import android.widget.Toast;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.geekwims.hereiam.R;
import com.geekwims.hereiam.api.UserInfo;
import com.geekwims.hereiam.attendance.Request;
import com.geekwims.hereiam.attendance.Response;
import com.geekwims.hereiam.autocheck.AutoCheckManager;
import com.geekwims.hereiam.dialog.CustomDialogUI;
import com.geekwims.hereiam.response.course.NextCourseInfo;

import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.util.Locale;
import java.util.UUID;

import butterknife.ButterKnife;

import static com.geekwims.hereiam.attendance.Response.AttendStatus.FAIL;

public class AttendanceActivity extends AppCompatActivity {
    private static final String TAG = "AttentanceActivity";
    ProgressDialog progressDialog;

    private final String DEVICE_NAME = "hereiam";
    BluetoothAdapter mBluetoothAdapter;
    NextCourseInfo courseInfo;

    // Intent request codes
    private static final int REQUEST_CONNECT_DEVICE_SECURE = 1;
    private static final int REQUEST_CONNECT_DEVICE_INSECURE = 2;
    private static final int REQUEST_ENABLE_BT = 3;

    // Unique UUID for this application
    private static final UUID MY_UUID_SECURE =
            UUID.fromString("fa87c0d0-afac-11de-8a39-0800200c9a66");
    private static final UUID MY_UUID_INSECURE =
            UUID.fromString("8ce255c0-200a-11e0-ac64-0800200c9a66");

    private static final UUID SerialPortProtocol = UUID.fromString("00001101-0000-1000-8000-00805F9B34FB");
    private BluetoothDevice mDevice;
    private BluetoothSocket mSocket;
    OutputStream mmOutputStream;
    InputStream mmInputStream;


    AutoCheckManager mAutoCheckManager;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        mBluetoothAdapter = BluetoothAdapter.getDefaultAdapter();

        Log.d(TAG, "creating activity");

        setContentView(R.layout.activity_attendance);
        ButterKnife.bind(this);

        if (getSupportActionBar() != null)
            getSupportActionBar().setDisplayHomeAsUpEnabled(true);

        // draw layout
        courseInfo = (NextCourseInfo) getIntent().getSerializableExtra("curCourseInfo");

        TextView textCurCourseName = (TextView) findViewById(R.id.cur_course_name);
        TextView textCurCourseInfo = (TextView) findViewById(R.id.cur_course_info);
        TextView textCurCourseRoom = (TextView) findViewById(R.id.cur_course_room);

        String[] weeks = new String[]{"일", "월", "화", "수", "목", "금", "토"};

        textCurCourseName.setText(courseInfo.getCourseInfo().getCourseName());
        textCurCourseInfo.setText(String.format(
                Locale.KOREA, "강의 시간 : %s요일 %d시",
                weeks[courseInfo.getCourseInfo().getDay()],
                courseInfo.getCourseInfo().getTime())
        );
        textCurCourseRoom.setText(String.format("강의실 : %s", courseInfo.getCourseInfo().getRoom()));

        Button btnAttendance = (Button) findViewById(R.id.btn_attendance);
        btnAttendance.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                attendance();
            }
        });

        progressDialog = new ProgressDialog(AttendanceActivity.this, R.style.AppTheme_Dialog);
        progressDialog.setIndeterminate(true);

        //connect(courseInfo.getCourseInfo().getDeviceAddr(), false);
        //connect("00:11:22:33:AA:BB", false);

        Log.e(TAG, courseInfo.toString());

        mAutoCheckManager = new AutoCheckManager(this);
        mAutoCheckManager.onRegist(courseInfo);
    }


    @Override
    protected void onPostCreate(@Nullable Bundle savedInstanceState) {
        super.onPostCreate(savedInstanceState);
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.menu_main, menu);

        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        // Handle action bar item clicks here. The action bar will
        // automatically handle clicks on the Home/Up button, so long
        // as you specify a parent activity in AndroidManifest.xml.

        switch (item.getItemId()) {
            case android.R.id.home:
                finish();
                return true;
        }

        return super.onOptionsItemSelected(item);
    }

    @Override
    protected void onDestroy() {
        try {
            try {
                mmOutputStream.close();
            } catch (Throwable t) {/*ignore*/}
            try {
                mmInputStream.close();
            } catch (Throwable t) {/*ignore*/}
            if (mSocket != null)
                mSocket.close();
        } catch (Throwable t) {
            t.printStackTrace();
        }

        mAutoCheckManager.onUnregist();

        NotificationManager notificationmanager = (NotificationManager) this.getSystemService(Context.NOTIFICATION_SERVICE);
        PendingIntent pendingIntent = PendingIntent.getActivity(this, 0, new Intent(this, MainActivity.class), PendingIntent.FLAG_UPDATE_CURRENT);
        Notification.Builder builder = new Notification.Builder(this);
        builder.setSmallIcon(R.drawable.icon_attend).setWhen(System.currentTimeMillis())
                .setContentTitle("HereIAm").setContentText("출결 취소").setContentIntent(pendingIntent).setAutoCancel(true).setOngoing(false);

        notificationmanager.notify(1, builder.build());

        super.onDestroy();
    }

    @Override
    protected void onPause() {
        super.onPause();
    }

    @Override
    protected void onResume() {
        super.onResume();
    }


    public void attendance() {
        // bluetooth setup

        mBluetoothAdapter = BluetoothAdapter.getDefaultAdapter();

        if (mBluetoothAdapter == null) {
            Toast.makeText(getApplicationContext(), "이 장비는 블루투스를 지원하지 않습니다.", Toast.LENGTH_LONG).show();
            finish();
            return;
        }

        if (!mBluetoothAdapter.isEnabled()) {
            Intent enableIntent = new Intent(BluetoothAdapter.ACTION_REQUEST_ENABLE);
            startActivityForResult(enableIntent, REQUEST_ENABLE_BT);
        }

        Intent serverIntent = new Intent(getApplicationContext(), DeviceListActivity.class);
        startActivityForResult(serverIntent, REQUEST_CONNECT_DEVICE_INSECURE);
    }

    public void onActivityResult(int requestCode, int resultCode, Intent data) {
        switch (requestCode) {
            case REQUEST_CONNECT_DEVICE_SECURE:
                // When DeviceListActivity returns with a device to connect
                if (resultCode == Activity.RESULT_OK) {
                    connectDevice(data, true);
                }
                break;
            case REQUEST_CONNECT_DEVICE_INSECURE:
                // When DeviceListActivity returns with a device to connect
                if (resultCode == Activity.RESULT_OK) {
                    connectDevice(data, false);
                }
                break;
            case REQUEST_ENABLE_BT:
                // When the request to enable Bluetooth returns
                if (resultCode == Activity.RESULT_OK) {
                    // Bluetooth is now enabled, so set up a chat session
                } else {
                    // User did not enable Bluetooth or an error occurred
                    Log.d(TAG, "BT not enabled");
                    Toast.makeText(getApplicationContext(), R.string.bt_not_enabled_leaving,
                            Toast.LENGTH_SHORT).show();
                    finish();
                }
        }
    }

    private void connectDevice(Intent data, boolean secure) {
        // Get the device MAC address
        String address = data.getExtras()
                .getString(DeviceListActivity.EXTRA_DEVICE_ADDRESS);
        connect(address, secure);
    }

    private void connect(String deviceAddr, boolean secure) {

        // Get the BluetoothDevice object
        BluetoothDevice device = mBluetoothAdapter.getRemoteDevice(deviceAddr);
        // Attempt to connect to the device
//        mChatService.connect(device, secure);
        Log.d(TAG, "secure : " + secure);
        Log.d(TAG, "device addr : " + device.toString());

        doConnect(device);
    }

    public void doConnect(BluetoothDevice device) {
        mDevice = device;

        //Standard SerialPortService ID
        UUID uuid = UUID.fromString("00001101-0000-1000-8000-00805F9B34FB");

        try {
            mSocket = mDevice.createRfcommSocketToServiceRecord(uuid);
            mBluetoothAdapter.cancelDiscovery();
            progressDialog.setMessage("출석 중...");
            progressDialog.show();
            new ConnectTask().execute();
        } catch (IOException e) {
            progressDialog.dismiss();
            Log.d("doConnect", e.toString());
            e.printStackTrace();
        }
    }

    public class ConnectTask extends AsyncTask<Void, Void, Response> {
        @Override
        protected void onPreExecute() {

        }

        @Override
        protected Response doInBackground(Void... params) {

            mBluetoothAdapter.cancelDiscovery();

            try {
                mSocket.connect();
                Log.e("", "Connected");
                mmOutputStream = mSocket.getOutputStream();
                mmInputStream = mSocket.getInputStream();
            } catch (Throwable t) {
                Log.e("ConnectTask", "connect? " + t.getMessage());
                t.printStackTrace();

                return null;
            }

            ObjectMapper objectMapper = new ObjectMapper();

            Request attendanceRequest = new Request(UserInfo.getInstance().getId(), courseInfo.getCourseInfo());
            Response res = null;

            try {
                mmOutputStream.write(objectMapper.writeValueAsString(attendanceRequest).getBytes());
                byte[] response = new byte[1024];
                int len = 0;
                if ((len = mmInputStream.read(response)) < 0) {
                    throw new IOException();
                }
                String strReponse = new String(response).substring(0, len);
                res = objectMapper.readValue(strReponse, Response.class);

                Log.d(TAG, res.toString());

            } catch (IOException e) {
                e.printStackTrace();
            }

            try {
                if (mmOutputStream != null)
                    mmOutputStream.close();
                if (mmInputStream != null)
                    mmInputStream.close();
                if (mSocket != null)
                    mSocket.close();
            } catch (IOException e) {
                e.printStackTrace();
            }

            return res;
        }


        @Override
        protected void onPostExecute(Response result) {
            progressDialog.dismiss();

            if (result == null)
                result = new Response(FAIL);

            resultDialog(result);
        }
    }

    private void resultDialog(Response response) {

        CustomDialogUI customDialogUI = new CustomDialogUI();

        Vibrator vib = (Vibrator) getSystemService(VIBRATOR_SERVICE);
        vib.vibrate(500);

        switch (response.getAttendStatus()) {
            case ABSENCE:
                customDialogUI.dialog(AttendanceActivity.this, "결석", "결석", null);
                break;
            case ATTENDANCE:
                customDialogUI.dialog(AttendanceActivity.this, "출석 완료", "출석 완료", null);
                break;
            case ALREADY:
                customDialogUI.dialog(AttendanceActivity.this, "이미 출석 됨", "이미 출석 됨", null);
                break;
            case LATENESS:
                customDialogUI.dialog(AttendanceActivity.this, "지각", "지각", null);
                break;
            case FAIL:
                customDialogUI.dialog(AttendanceActivity.this, "실패", "실패",
                        new ConnectTask()
                );
                break;
        }
    }
}
