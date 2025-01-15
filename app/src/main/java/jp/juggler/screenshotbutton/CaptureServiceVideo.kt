package jp.juggler.screenshotbutton

import android.app.NotificationChannel
import android.app.NotificationManager
import android.app.PendingIntent
import android.content.Intent
import android.os.Build
import androidx.core.app.NotificationCompat
import jp.juggler.screenshotbutton.R.*

class CaptureServiceVideo : CaptureServiceBase(isVideo = true) {

    companion object {
        fun getService() = getServices().find { it is CaptureServiceVideo }
        fun isAlive() = getService() != null

    }

    override fun createNotificationChannel(channelId: String) {
        if (Build.VERSION.SDK_INT >= API_NOTIFICATION_CHANNEL) {
            notificationManager.createNotificationChannel(
                NotificationChannel(
                    channelId,
                    getString(string.capture_standby_video),
                    NotificationManager.IMPORTANCE_LOW
                ).apply {
                    description = getString(string.capture_standby_description_video)
                }
            )
        }
    }

    override fun arrangeNotification(
        builder: NotificationCompat.Builder,
        isRecording: Boolean
    ): IntArray {

        val closeIntent = PendingIntent.getBroadcast(
            context,
            PI_CODE_RUNNING_DELETE_VIDEO,
            Intent(context, MyReceiver::class.java)
                .apply { action = MyReceiver.ACTION_RUNNING_DELETE_VIDEO },
            PendingIntent.FLAG_UPDATE_CURRENT or PendingIntent.FLAG_IMMUTABLE
        )

        builder
            .setSmallIcon(R.drawable.app_icon)
            .setContentTitle(getString(if (isRecording) string.capture_standby_video_recording else string.capture_standby_video))
            .setContentText(getString(if (isRecording) string.capture_standby_description_video_recording else string.capture_standby_description_video))
            .setContentIntent(
                PendingIntent.getActivity(
                    context,
                    PI_CODE_RUNNING_TAP,
                    Intent(context, ActMain::class.java)
                        .apply {
                            flags = Intent.FLAG_ACTIVITY_NEW_TASK or Intent.FLAG_ACTIVITY_CLEAR_TASK
                        },
                    PendingIntent.FLAG_UPDATE_CURRENT or PendingIntent.FLAG_IMMUTABLE
                )
            )
            .setDeleteIntent(
                closeIntent
            )
            .addAction(
                if (isRecording) {
                    NotificationCompat.Action(
                        drawable.shape_btn,
                        getString(string.stop),
                        PendingIntent.getBroadcast(
                            context,
                            PI_CODE_VIDEO_STOP,
                            Intent(context, MyReceiver::class.java)
                                .apply { action = MyReceiver.ACTION_RUNNING_VIDEO_STOP },
                            PendingIntent.FLAG_UPDATE_CURRENT or PendingIntent.FLAG_IMMUTABLE
                        )
                    )
                } else {
                    NotificationCompat.Action(
                       drawable.redo_icn,
                        getString(string.record),
                        PendingIntent.getBroadcast(
                            context,
                            PI_CODE_VIDEO_START,
                            Intent(context, MyReceiver::class.java)
                                .apply { action = MyReceiver.ACTION_RUNNING_VIDEO_START },
                            PendingIntent.FLAG_UPDATE_CURRENT or PendingIntent.FLAG_IMMUTABLE
                        )
                    )
                }
            )
            .addAction(
                NotificationCompat.Action(
                    drawable.back_icn,
                    getString(string.close),
                    closeIntent
                )
            )
        return intArrayOf(0, 1)
    }

    override fun openPostView(captureResult: Capture.CaptureResult) {
        if (!isDestroyed && captureResult.mediaUri != null) {
            startActivity(
                Intent(Intent.ACTION_VIEW)
                    .apply {
                        addFlags(Intent.FLAG_ACTIVITY_NEW_TASK)
                        setDataAndType(captureResult.mediaUri, captureResult.mimeType)
                    }
            )
        }
    }

}