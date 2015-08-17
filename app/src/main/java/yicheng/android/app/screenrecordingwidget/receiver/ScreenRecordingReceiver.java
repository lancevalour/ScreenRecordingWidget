package yicheng.android.app.screenrecordingwidget.receiver;

import android.app.ActivityManager;
import android.appwidget.AppWidgetManager;
import android.appwidget.AppWidgetProvider;
import android.content.BroadcastReceiver;
import android.content.ComponentName;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.media.projection.MediaProjectionManager;
import android.widget.RemoteViews;
import android.widget.Toast;

import java.util.List;
import java.util.Random;

import yicheng.android.app.screenrecordingwidget.MainWidget;
import yicheng.android.app.screenrecordingwidget.R;
import yicheng.android.app.screenrecordingwidget.activity.ScreenRecordingActivity;
import yicheng.android.app.screenrecordingwidget.util.ScreenRecorder;

/**
 * Created by ZhangY on 8/13/2015.
 */
public class ScreenRecordingReceiver extends BroadcastReceiver {
    Context context;

    boolean isRecording;

    private SharedPreferences local_user_information;
    private SharedPreferences.Editor local_user_editor;
    private String PREFS_NAME = "RecordingInfo";


    static final int REQUEST_CODE = 1;
    MediaProjectionManager mMediaProjectionManager;


    @Override
    public void onReceive(Context context, Intent intent) {
        this.context = context;


        local_user_information = context.getSharedPreferences(PREFS_NAME, 0);
        isRecording = local_user_information.getBoolean("isRecording", false);
        local_user_editor = local_user_information.edit();
        local_user_editor.putBoolean("isRecording", !isRecording);
        local_user_editor.commit();

        System.out.println(isRecording);

        changeWidgetLayout(isRecording);

        if (isRecording) {
            stopRecording();
        } else {
            startRecording();
        }

    }

    public void changeWidgetLayout(boolean isRecording) {
        RemoteViews views = new RemoteViews(context.getPackageName(), R.layout.main_widget);

        if (isRecording) {
            views.setTextViewText(R.id.widget_button, "Start");
        } else {
            views.setTextViewText(R.id.widget_button, "Stop");
        }

        AppWidgetManager.getInstance(context).updateAppWidget(
                new ComponentName(context, MainWidget.class), views);

        // System.out.println("activity running " + isActivityRunning(context));
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


    public void startRecording() {
      /*  Intent captureIntent = mMediaProjectionManager.createScreenCaptureIntent();
        startActivityForResult(captureIntent, REQUEST_CODE);*/
        Intent intent = new Intent(context, ScreenRecordingActivity.class);
        intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK
                | Intent.FLAG_ACTIVITY_CLEAR_TOP
                | Intent.FLAG_ACTIVITY_SINGLE_TOP);
        context.startActivity(intent);
    }

    public void stopRecording() {
        if (ScreenRecordingActivity.mRecorder != null) {
            ScreenRecordingActivity.mRecorder.quit();
            ScreenRecordingActivity.mRecorder = null;
        }
    }
}
