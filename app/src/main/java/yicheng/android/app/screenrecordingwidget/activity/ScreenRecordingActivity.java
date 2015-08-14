package yicheng.android.app.screenrecordingwidget.activity;

import android.app.Activity;
import android.app.ActivityManager;
import android.appwidget.AppWidgetManager;
import android.content.ComponentName;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.media.projection.MediaProjection;
import android.media.projection.MediaProjectionManager;
import android.os.Bundle;
import android.os.Environment;
import android.util.Log;
import android.widget.RemoteViews;
import android.widget.Toast;

import java.io.File;
import java.util.List;

import yicheng.android.app.screenrecordingwidget.MainWidget;
import yicheng.android.app.screenrecordingwidget.R;
import yicheng.android.app.screenrecordingwidget.util.ScreenRecorder;

/**
 * Created by ZhangY on 8/14/2015.
 */
public class ScreenRecordingActivity extends Activity {

    private static final int REQUEST_CODE = 1;
    private MediaProjectionManager mMediaProjectionManager;
    public static ScreenRecorder mRecorder;

    private SharedPreferences local_user_information;
    private SharedPreferences.Editor local_user_editor;
    private String PREFS_NAME = "RecordingInfo";


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        mMediaProjectionManager = (MediaProjectionManager) getSystemService(MEDIA_PROJECTION_SERVICE);

        Toast.makeText(getBaseContext(), "haha", Toast.LENGTH_SHORT).show();

        startRecording();

    }

    private void startRecording() {
        Intent captureIntent = mMediaProjectionManager.createScreenCaptureIntent();
        startActivityForResult(captureIntent, REQUEST_CODE);
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        if (REQUEST_CODE == requestCode) {
            if (resultCode == RESULT_OK) {
                MediaProjection mediaProjection = mMediaProjectionManager.getMediaProjection(resultCode, data);
                if (mediaProjection == null) {
                    Log.e("@@", "media projection is null");
                    return;
                }
                // video size
                final int width = 1280;
                final int height = 720;
                File file = new File(Environment.getExternalStorageDirectory(),
                        "record-" + width + "x" + height + "-" + System.currentTimeMillis() + ".mp4");
                final int bitrate = 6000000;
                mRecorder = new ScreenRecorder(width, height, bitrate, 1, mediaProjection, file.getAbsolutePath());
                mRecorder.start();

                Toast.makeText(this, "Screen recorder is running...", Toast.LENGTH_SHORT).show();
                moveTaskToBack(true);


            } else {
                Toast.makeText(getBaseContext(), "Cancelled", Toast.LENGTH_SHORT).show();

                local_user_information = getBaseContext().getSharedPreferences(PREFS_NAME, 0);
                boolean isRecording = local_user_information.getBoolean("isRecording", false);
                local_user_editor = local_user_information.edit();
                local_user_editor.putBoolean("isRecording", !isRecording);
                local_user_editor.commit();

                RemoteViews views = new RemoteViews(getBaseContext().getPackageName(), R.layout.main_widget);
                views.setTextViewText(R.id.widget_button, "Start Recording");

                AppWidgetManager.getInstance(getBaseContext()).updateAppWidget(
                        new ComponentName(getBaseContext(), MainWidget.class), views);

                finish();
            }
        }




 /*       ActivityManager am = (ActivityManager) getBaseContext().getSystemService(Context.ACTIVITY_SERVICE);
        System.out.println(am.getRunningAppProcesses());*/
       /* finish();*/
    }

    public boolean isActivityRunning(Context ctx) {
        ActivityManager activityManager = (ActivityManager) ctx.getSystemService(Context.ACTIVITY_SERVICE);
        List<ActivityManager.RunningTaskInfo> tasks = activityManager.getRunningTasks(Integer.MAX_VALUE);

        for (ActivityManager.RunningTaskInfo task : tasks) {
            if (ctx.getPackageName().equalsIgnoreCase(task.baseActivity.getPackageName()))
                return true;
        }

        return false;
    }


    @Override
    protected void onDestroy() {
        super.onDestroy();
        if (mRecorder != null) {
            mRecorder.quit();
            mRecorder = null;
        }
    }
}
