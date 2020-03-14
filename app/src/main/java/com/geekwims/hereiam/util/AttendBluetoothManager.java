package com.geekwims.hereiam.util;

import android.app.Activity;
import android.app.AlarmManager;
import android.app.Notification;
import android.app.NotificationManager;
import android.app.PendingIntent;
import android.bluetooth.BluetoothAdapter;
import android.bluetooth.BluetoothDevice;
import android.bluetooth.BluetoothSocket;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.content.SharedPreferences;
import android.database.Cursor;
import android.os.AsyncTask;
import android.os.BatteryManager;
import android.util.Log;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.geekwims.hereiam.R;
import com.geekwims.hereiam.activity.DeviceListActivity;
import com.geekwims.hereiam.activity.MainActivity;
import com.geekwims.hereiam.api.UserInfo;
import com.geekwims.hereiam.attendance.Request;
import com.geekwims.hereiam.attendance.Response;
import com.geekwims.hereiam.receiver.AlarmReceiver;
import com.geekwims.hereiam.receiver.BatteryStatusReceiver;
import com.geekwims.hereiam.response.course.CourseInfo;
import com.geekwims.hereiam.db.DatabaseHelper;

import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.util.Calendar;
import java.util.UUID;

import static com.geekwims.hereiam.receiver.AlarmReceiver.END_TIME;
import static com.geekwims.hereiam.receiver.AlarmReceiver.KEY_COUNT;
import static com.geekwims.hereiam.receiver.AlarmReceiver.PREFS_NAME;
import static com.geekwims.hereiam.receiver.AlarmReceiver.TOTAL_COUNT;


public class AttendBluetoothManager {
    private static final String TAG = "AttendBluetoothManager";
//    ProgressDialog progressDialog;

    private final String DEVICE_NAME = "hereiam";
    private BluetoothAdapter mBluetoothAdapter;
    private BluetoothDevice mDevice;
    private BluetoothSocket mSocket;
    private OutputStream mmOutputStream;
    private InputStream mmInputStream;

    private Context mContext;
    private CourseInfo mCourseInfo;

    public AttendBluetoothManager(Context context, CourseInfo courseInfo) {
        mContext = context;
        mBluetoothAdapter = BluetoothAdapter.getDefaultAdapter();
        mCourseInfo = courseInfo;


        mContext.getApplicationContext().registerReceiver(new BatteryStatusReceiver(), new IntentFilter(Intent.ACTION_BATTERY_CHANGED));
    }

    private void connectDevice(Intent data, boolean secure) {
        // Get the device MAC address
        String address = data.getExtras()
                .getString(DeviceListActivity.EXTRA_DEVICE_ADDRESS);
        connect(address, secure);
    }

    public void connect(String deviceAddr, boolean secure) {

        // Get the BluetoothDevice object
        BluetoothDevice device = mBluetoothAdapter.getRemoteDevice(deviceAddr);
        // Attempt to connect to the device
//        mChatService.connect(device, secure);
        Log.d(TAG, "secure : " + secure);
        Log.d(TAG, "device addr : " + device.toString());

        doConnect(device);
    }

    protected void doConnect(BluetoothDevice device) {
        mDevice = device;

        //Standard SerialPortService ID
        UUID uuid = UUID.fromString("00001101-0000-1000-8000-00805F9B34FB");

        try {
            mSocket = mDevice.createRfcommSocketToServiceRecord(uuid);
            mBluetoothAdapter.cancelDiscovery();
//            progressDialog.setMessage("출석 중...");
//            progressDialog.show();

            new CheckDeviceTask().execute();
        } catch (IOException e) {
//            progressDialog.dismiss();
            Log.d("doConnect", e.toString());
        }
    }

    private class CheckDeviceTask extends AsyncTask<Void, Void, Boolean> {
        private static final String TAG = "CheckDeviceTask";

        @Override
        protected void onPreExecute() {

        }

        @Override
        protected Boolean doInBackground(Void... voids) {
            boolean result = false;

            mBluetoothAdapter.cancelDiscovery();
            UUID uuid = UUID.fromString("00001101-0000-1000-8000-00805F9B34FB");
            try {
                mSocket = mDevice.createRfcommSocketToServiceRecord(uuid);
                mSocket.connect();

                Log.i(TAG, "Connected");
                result = true;
            } catch (Throwable t) {
                Log.e(TAG, "connect? " + t.getMessage());

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

            return result;
        }

        @Override
        protected void onPostExecute(Boolean result) {
            SharedPreferences values = mContext.getSharedPreferences(PREFS_NAME, Activity.MODE_PRIVATE);
            int currentCount = values.getInt(KEY_COUNT, 0);  //Sets to zero if not in prefs yet
            int totalCount = values.getInt(TOTAL_COUNT, 0);
            long endTime = values.getLong(END_TIME, 0);

            currentCount = result ? currentCount + 1 : currentCount;

            //Write the value back to storage for later use
            Log.i(TAG, " update count.... : "  + currentCount);
            SharedPreferences.Editor editor = values.edit();

            editor.putInt(KEY_COUNT, currentCount);
            editor.apply();

            NotificationManager notificationmanager = (NotificationManager) mContext.getSystemService(Context.NOTIFICATION_SERVICE);
            PendingIntent pendingIntent = PendingIntent.getActivity(mContext, 0, new Intent(mContext, MainActivity.class), PendingIntent.FLAG_UPDATE_CURRENT);
            Notification.Builder builder = new Notification.Builder(mContext);
            builder.setSmallIcon(R.drawable.icon_attend).setWhen(System.currentTimeMillis())
                    .setContentTitle("HereIAm").setContentText("자동 출석 진행 중..." + currentCount + " / " + totalCount).setContentIntent(pendingIntent).setAutoCancel(true).setOngoing(true);

            notificationmanager.notify(1, builder.build());

            if (currentCount >= totalCount) {

                Log.i(TAG, "|" + "========= unregist============" + "|");
                Intent intent = new Intent(mContext, AlarmReceiver.class);
                PendingIntent pIntent = PendingIntent.getBroadcast(mContext, 1, intent, 0);
                ((AlarmManager) mContext.getSystemService(Context.ALARM_SERVICE)).cancel(pIntent);

                new AttendTask().execute();
            }

            if (endTime < Calendar.getInstance().getTimeInMillis()) {
                Log.i(TAG, "|" + "========= unregist============" + "|");

                Intent intent = new Intent(mContext, AlarmReceiver.class);
                PendingIntent pIntent = PendingIntent.getBroadcast(mContext, 1, intent, 0);

                ((AlarmManager) mContext.getSystemService(Context.ALARM_SERVICE)).cancel(pIntent);

                String filename = "battery_log_" + Calendar.getInstance().getTimeInMillis() + ".txt";

                FileOutputStream outputStream;

                DatabaseHelper databaseHelper = DatabaseHelper.getInstance(mContext);

                StringBuilder batteryLog = new StringBuilder();
                if (databaseHelper != null) {
                    Cursor cursor = databaseHelper.getBatteryLogCursor();
                    while (cursor.moveToNext()) {
                        batteryLog.append(cursor.getInt(0)).append(" ").append(cursor.getInt(1)).append(cursor.getInt(2)).append(" ").append(cursor.getInt(3));
                    }
                }

                try {
                    outputStream = mContext.openFileOutput(filename, Context.MODE_PRIVATE);
                    outputStream.write(batteryLog.toString().getBytes());
                    outputStream.close();
                } catch (Exception e) {
                    e.printStackTrace();
                }
            }
        }
    }

    private class AttendTask extends AsyncTask<Void, Void, Response> {
        public static final String TAG = "AlarmReceiver|Task";

        private BluetoothDevice mDevice;
        private BluetoothSocket mSocket;

        OutputStream mmOutputStream;
        InputStream mmInputStream;
        BluetoothAdapter mBluetoothAdapter;

        @Override
        protected void onPreExecute() {
            mBluetoothAdapter = BluetoothAdapter.getDefaultAdapter();
        }

        @Override
        protected Response doInBackground(Void... params) {

            mBluetoothAdapter.cancelDiscovery();
            UUID uuid = UUID.fromString("00001101-0000-1000-8000-00805F9B34FB");
            mDevice = mBluetoothAdapter.getRemoteDevice(mCourseInfo.getDeviceAddr());

            try {
                mSocket = mDevice.createRfcommSocketToServiceRecord(uuid);
                mSocket.connect();
                Log.e(TAG, "======== bluetooth connected ==========");
                mmOutputStream = mSocket.getOutputStream();
                mmInputStream = mSocket.getInputStream();
            } catch (Throwable t) {
                Log.e(TAG, " can not found? " + t.getMessage());
                t.printStackTrace();

                return null;
            }

            ObjectMapper objectMapper = new ObjectMapper();

            Request attendanceRequest = new Request(UserInfo.getInstance().getId(), mCourseInfo);
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

                Log.i(TAG, res.toString());

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
            if (result == null) {
                Log.i(TAG, "error occured");
            } else {
                NotificationManager notificationmanager = (NotificationManager) mContext.getSystemService(Context.NOTIFICATION_SERVICE);
                PendingIntent pendingIntent = PendingIntent.getActivity(mContext, 0, new Intent(mContext, MainActivity.class), PendingIntent.FLAG_UPDATE_CURRENT);
                Notification.Builder builder = new Notification.Builder(mContext);
                builder.setSmallIcon(R.drawable.icon_attend).setWhen(System.currentTimeMillis())
                        .setContentTitle("HereIAm").setContentText(mCourseInfo.getCourseName() + " : 출결 완료!").setContentIntent(pendingIntent).setAutoCancel(true).setOngoing(false);

                notificationmanager.notify(1, builder.build());
            }
        }
    }
}
