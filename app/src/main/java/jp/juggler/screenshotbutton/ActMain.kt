package jp.juggler.screenshotbutton

import android.annotation.SuppressLint
import android.app.Dialog
import android.content.Intent
import android.net.Uri
import android.os.Build
import android.os.Bundle
import android.os.SystemClock
import android.provider.DocumentsContract
import android.provider.Settings
import android.view.View
import android.widget.Button
import android.widget.ImageView
import android.widget.Toast
import androidx.appcompat.app.AlertDialog
import androidx.appcompat.app.AppCompatActivity
import androidx.appcompat.widget.AppCompatButton
import androidx.core.content.ContextCompat
import androidx.recyclerview.widget.LinearLayoutManager
import jp.juggler.photoediting.CropActivity
import jp.juggler.photoediting.Historydemo
import jp.juggler.screenshotbutton.App1.Companion.pref
import jp.juggler.screenshotbutton.databinding.ActMainBinding
import jp.juggler.util.*
import jp.juggler.v2mixup.ui.MainActivity
import java.lang.ref.WeakReference


class ActMain : AppCompatActivity(), View.OnClickListener {

    companion object {
        private val log = LogCategory("${App1.tagPrefix}/ActMain")

        private var refActivity: WeakReference<ActMain>? = null

        fun getActivity() = refActivity?.get()
    }

    private val views by lazy {
        ActMainBinding.inflate(layoutInflater)
    }

    private var lastDialog: WeakReference<Dialog>? = null

    private var timeStartButtonTappedStill = 0L
    private var timeStartButtonTappedVideo = 0L

    var mySharedPreference: MySharedPreference? = null


    private var Manager: LinearLayoutManager? = null

    private val arOverlay = ActivityResultHandler {
        mayContinueDispatch(handleOverlayResult())
    }

    private val arScreenCapture = ActivityResultHandler { r ->
        mayContinueDispatch(Capture.handleScreenCaptureIntentResult(this, r.resultCode, r.data))
    }

    private val arDocumentTree = ActivityResultHandler { r ->
        mayContinueDispatch(handleSaveTreeUriResult(r.resultCode, r.data))
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        arOverlay.register(this)
        arScreenCapture.register(this)
        arDocumentTree.register(this)
        App1.prepareAppState(this)
        log.d("onCreate savedInstanceState=$savedInstanceState")
        refActivity = WeakReference(this)
        super.onCreate(savedInstanceState)
        initUI()

        val btnfileeditor = findViewById<Button>(R.id.btnfileeditor)
        val btnstitching = findViewById<Button>(R.id.btnstitching)
        val btnsecreenshotsetting = findViewById<ImageView>(R.id.btnscreenshotsetting)

        val rvhistory = findViewById<AppCompatButton>(R.id.rvhistory)
        Manager = LinearLayoutManager(this)

        rvhistory.setOnClickListener {
            val i = Intent(this@ActMain, Historydemo::class.java)
            i.flags = Intent.FLAG_ACTIVITY_NEW_TASK
            startActivity(i)
        }
        btnfileeditor.setOnClickListener {
            val intent = Intent(this, CropActivity::class.java)
            startActivity(intent)
        }
        btnstitching.setOnClickListener {
            val intent = Intent(this, MainActivity::class.java)
            startActivity(intent)
        }
        btnsecreenshotsetting.setOnClickListener {
            val intent = Intent(this, HomeActivity::class.java)
            startActivity(intent)
        }


        mySharedPreference = MySharedPreference.getPreferences(getActivity())

        views.swStartBootStill.isChecked = mySharedPreference?._active_time_onoff!!

    }


    override fun onStart() {
        log.d("onStart")
        super.onStart()
        showButton()
        showCurrentCodec()
        dispatch()
    }

    override fun onClick(v: View?) {
        timeStartButtonTappedStill = 0L
        timeStartButtonTappedVideo = 0L
        when (v?.id) {
            R.id.btnSaveFolder ->
                openSaveTreeUriChooser()


            R.id.btnStartStopStill ->

                if (mySharedPreference?._active_time_onoff!!) {

                    Toast.makeText(getActivity(), "Plz ", Toast.LENGTH_SHORT).show()
                    Toast.makeText(getActivity(), "Plz Disable Switch", Toast.LENGTH_SHORT).show()

                } else {
                    when (val service = CaptureServiceStill.getService()) {
                        null -> {
                            timeStartButtonTappedStill = SystemClock.elapsedRealtime()
                            dispatch()
                        }

                        else -> {
                            service.stopWithReason("StopButton")
                        }
                    }

                }


            R.id.btnResetPositionStill -> {
                pref.edit()
                    .remove(Pref.fpCameraButtonXStill)
                    .remove(Pref.fpCameraButtonYStill)
                    .apply()
                CaptureServiceStill.getService()?.reloadPosition()
            }


        }

    }


    private fun initUI() {
        setContentView(views.root)
        arrayOf(
            views.btnStartStopStill,

            ).forEach {
            it.setOnClickListener(this)
        }
        val pref = App1.pref


        views.swStartBootStill.setOnClickListener {
        }
    }


    private fun showCurrentCodec() {
        val codecList = MediaCodecInfoAndType.getList(this)
        if (codecList.isEmpty()) {
            log.eToast(this, false, "Oops! this device has no MediaCodec!!")
            return
        }
        val id = Pref.spCodec(pref)
        var codec = codecList.find { it.id == id }
        if (codec == null) {
            codec = codecList[0]
            pref.edit().put(Pref.spCodec, codec.id).apply()
        }
    }


    fun showButton() {
        log.d("showButton")
        if (isDestroyed) return

        val isCapturing = Capture.isCapturing

        views.btnStartStopStill.isEnabledWithColor = !isCapturing


    }


    private fun mayContinueDispatch(r: Boolean) {
        if (r) {
            dispatch()
        } else {
            timeStartButtonTappedStill = 0L
            timeStartButtonTappedVideo = 0L
        }
    }


    private fun dispatch() {
        log.d("dispatch")

        if (!prepareOverlay()) return

        if (!prepareSaveTreeUri()) return

        if (timeStartButtonTappedStill > 0L) {

            if (!Capture.prepareScreenCaptureIntent(arScreenCapture)) return

            timeStartButtonTappedStill = 0L
            ContextCompat.startForegroundService(
                this,
                Intent(this, CaptureServiceStill::class.java).apply {
                    Capture.screenCaptureIntent?.let {
                        putExtra(CaptureServiceBase.EXTRA_SCREEN_CAPTURE_INTENT, it)
                    }
                }
            )
        }

        if (timeStartButtonTappedVideo > 0L) {

            if (!Capture.prepareScreenCaptureIntent(arScreenCapture)) return

            timeStartButtonTappedVideo = 0L
            ContextCompat.startForegroundService(
                this,
                Intent(this, CaptureServiceVideo::class.java).apply {
                    Capture.screenCaptureIntent?.let {
                        putExtra(CaptureServiceBase.EXTRA_SCREEN_CAPTURE_INTENT, it)
                    }
                }
            )
        }
    }


    private fun openSaveTreeUriChooser() {
        arDocumentTree.launch(
            Intent(Intent.ACTION_OPEN_DOCUMENT_TREE).apply {
                if (Build.VERSION.SDK_INT >= API_EXTRA_INITIAL_URI) {
                    val saveTreeUri = Pref.spSaveTreeUri(pref)
                    if (saveTreeUri.isNotEmpty()) {
                        putExtra(
                            DocumentsContract.EXTRA_INITIAL_URI,
                            saveTreeUri
                        )
                    }
                }
            }
        )
    }

    private fun prepareSaveTreeUri(): Boolean {
        val treeUri = Pref.spSaveTreeUri(pref).toUriOrNull()

        if (treeUri != null) {
            if (!contentResolver.persistedUriPermissions.any { it.uri == treeUri }) {
                log.eToast(this, true, "missing access permission $treeUri")
            } else {
                try {
                    // pathの検証。例外を出す
                    pathFromDocumentUriOrThrow(this, treeUri)
                    return true
                } catch (ex: Throwable) {
                    log.eToast(this, ex, "can't use this folder.")
                }
            }
        }

        AlertDialog.Builder(this)
            .setMessage(R.string.please_select_save_folder)
            .setPositiveButton(R.string.ok)
            { _, _ ->
                openSaveTreeUriChooser()
            }
            .setNegativeButton(R.string.cancel, null)
            .showEx()

        return false
    }

    private fun handleSaveTreeUriResult(resultCode: Int, data: Intent?): Boolean {
        try {
            if (resultCode == RESULT_OK) {
                val uri = data?.data ?: error("missing document tree URI")
                contentResolver.takePersistableUriPermission(
                    uri,
                    Intent.FLAG_GRANT_READ_URI_PERMISSION or
                            Intent.FLAG_GRANT_WRITE_URI_PERMISSION
                )
                pref.edit().put(Pref.spSaveTreeUri, uri.toString()).apply()
                //    showSaveFolder()
                return true
            }
        } catch (ex: Throwable) {
            log.eToast(this, ex, "takePersistableUriPermission failed.")
        }
        return false
    }


    @SuppressLint("InlinedApi")
    private fun prepareOverlay(): Boolean {
        if (canDrawOverlaysCompat(this)) return true

        return AlertDialog.Builder(this)
            .setMessage(R.string.please_allow_overlay_permission)
            .setNegativeButton(R.string.cancel, null)
            .setPositiveButton(R.string.ok) { _, _ ->
                arOverlay.launch(
                    Intent(
                        Settings.ACTION_MANAGE_OVERLAY_PERMISSION,
                        Uri.parse("package:$packageName")
                    )
                )
            }
            .showEx()
    }

    private fun handleOverlayResult(): Boolean {

        return canDrawOverlaysCompat(this)
    }

    private fun AlertDialog.Builder.showEx(): Boolean {
        if (lastDialog?.get()?.isShowing == true) {
            log.w("dialog is already showing.")
        } else {
            lastDialog = WeakReference(this.create().apply { show() })
        }
        return false
    }
}
