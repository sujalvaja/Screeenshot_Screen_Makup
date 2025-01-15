package jp.juggler.screenshotbutton

import android.annotation.SuppressLint
import android.app.Dialog
import android.content.Intent
import android.net.Uri
import android.os.*
import android.provider.DocumentsContract
import android.provider.Settings
import android.text.Editable
import android.text.TextWatcher
import android.view.View
import android.widget.ImageView
import androidx.appcompat.app.AlertDialog
import androidx.appcompat.app.AppCompatActivity
import androidx.appcompat.widget.AppCompatButton
import androidx.core.content.ContextCompat
import jp.juggler.screenshotbutton.databinding.ActivityHomeBinding
import jp.juggler.util.*
import java.lang.ref.WeakReference

@SuppressLint("ParcelCreator")
class HomeActivity() : AppCompatActivity(), View.OnClickListener, Parcelable {
    companion object {
        private val log = LogCategory("${App1.tagPrefix}/ HomeActivity")

        private var refActivity: WeakReference<HomeActivity>? = null


        fun getActivity() = refActivity?.get()
    }

    private val views by lazy {
        ActivityHomeBinding.inflate(layoutInflater)
    }

    private var lastDialog: WeakReference<Dialog>? = null

    private var timeStartButtonTappedStill = 0L
    private var timeStartButtonTappedVideo = 0L

    private var videoCaptureEnabled = false
    private lateinit var btnback :AppCompatButton

    private val arOverlay = ActivityResultHandler {
        mayContinueDispatch(handleOverlayResult())
    }

    private val arScreenCapture = ActivityResultHandler { r ->
        mayContinueDispatch(Capture.handleScreenCaptureIntentResult(this, r.resultCode, r.data))
    }

    private val arDocumentTree = ActivityResultHandler { r ->
        mayContinueDispatch(handleSaveTreeUriResult(r.resultCode, r.data))
    }

    constructor(parcel: Parcel) : this() {
        timeStartButtonTappedStill = parcel.readLong()
        timeStartButtonTappedVideo = parcel.readLong()
        videoCaptureEnabled = parcel.readByte() != 0.toByte()
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

        btnback = findViewById(R.id.btnback)
        // ivImage.source.setImageBitmap(ScreenshootCrop.bitmap)
        btnback.setOnClickListener(View.OnClickListener {
            onBackPressed()
        })

    }


    override fun onStart() {
        log.d("onStart")
        super.onStart()
        showSaveFolder()
        showButton()
        showCurrentCodec()
        dispatch()
    }

    private fun showCurrentCodec() {
        val codecList = MediaCodecInfoAndType.getList(this)
        if (codecList.isEmpty()) {
            log.eToast(this, false, "Oops! this device has no MediaCodec!!")
            return
        }
        val id = Pref.spCodec(App1.pref)
        var codec = codecList.find { it.id == id }
        if (codec == null) {
            codec = codecList[0]
            App1.pref.edit().put(Pref.spCodec, codec.id).apply()
        }
        views.tvCodec.text = codec.toString()
    }

    private fun showButton() {
        views.tvSaveFolder.text = Pref.spSaveTreeUri(App1.pref)
            .notEmpty()
            ?.let { pathFromDocumentUri(this, Uri.parse(it)) }
            ?: getString(R.string.not_selected)
    }

    private fun showSaveFolder() {
        views.tvSaveFolder.text = Pref.spSaveTreeUri(App1.pref)
            .notEmpty()
            ?.let { pathFromDocumentUri(this, Uri.parse(it)) }
            ?: getString(R.string.not_selected)    }

    override fun onClick(v: View?) {
        timeStartButtonTappedStill = 0L
        when (v?.id) {
            R.id.btnSaveFolder ->
                openSaveTreeUriChooser()

            R.id.btnStartStopStill ->
                when (val service = CaptureServiceStill.getService()) {
                    null -> {
                        timeStartButtonTappedStill = SystemClock.elapsedRealtime()
                        dispatch()
                    }
                    else -> {
                        service.stopWithReason("StopButton")
                    }
                }

            R.id.btnResetPositionStill -> {
                App1.pref.edit()
                    .remove(Pref.fpCameraButtonXStill)
                    .remove(Pref.fpCameraButtonYStill)
                    .apply()
                CaptureServiceStill.getService()?.reloadPosition()
            }

            R.id.btnback -> {
                onBackPressed()
            }
        }
    }


    private fun initUI() {
        setContentView(views.root)
        arrayOf(

            views.btnSaveFolder,
            views.btnResetPositionStill,

        ).forEach {
            it.setOnClickListener(this)
        }

        views.tvButtonSizeError.vg(false)

        val pref = App1.pref

        Pref.bpSavePng.bindSwitch(pref, views.swSavePng)
        Pref.bpShowPostView.bindSwitch(pref, views.swShowPostView)

        val etButtonSize = views.etButtonSize
        etButtonSize.setText(Pref.ipCameraButtonSize(pref).toString())
        etButtonSize.addTextChangedListener(object : TextWatcher {
            override fun beforeTextChanged(p0: CharSequence?, p1: Int, p2: Int, p3: Int) {
            }

            override fun onTextChanged(p0: CharSequence?, p1: Int, p2: Int, p3: Int) {
            }

            override fun afterTextChanged(ed: Editable?) {
                val iv = ed?.toString()?.trim()?.toIntOrNull() ?: -1
                if (null == views.tvButtonSizeError.vg(iv < 1)) {
                    Pref.ipCameraButtonSize.saveIfModified(pref, iv)
                }
            }
        })

        Pref.spBitRate.bindEditText(pref, views.etBitRate)

        val message = when {

            Build.VERSION.SDK_INT < API_MEDIA_MUXER_FILE_DESCRIPTER ->
                getString(R.string.media_muxer_too_old)

            MediaCodecInfoAndType.getList(this).isEmpty() ->
                getString(R.string.video_codec_missing)

            else -> null
        }

        videoCaptureEnabled = message == null







        fun showSaveFolder() {

        }

        fun showCurrentCodec() {
            val codecList = MediaCodecInfoAndType.getList(this)
            if (codecList.isEmpty()) {
                log.eToast(this, false, "Oops! this device has no MediaCodec!!")
                return
            }
            val id = Pref.spCodec(App1.pref)
            var codec = codecList.find { it.id == id }
            if (codec == null) {
                codec = codecList[0]
                App1.pref.edit().put(Pref.spCodec, codec.id).apply()
            }
            views.tvCodec.text = codec.toString()
        }




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
                    val saveTreeUri = Pref.spSaveTreeUri(App1.pref)
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
        val treeUri = Pref.spSaveTreeUri(App1.pref).toUriOrNull()

        if (treeUri != null) {
            if (!contentResolver.persistedUriPermissions.any { it.uri == treeUri }) {
                log.eToast(this, true, "missing access permission $treeUri")
            } else {
                try {
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
                App1.pref.edit().put(Pref.spSaveTreeUri, uri.toString()).apply()
                showSaveFolder()
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

    override fun writeToParcel(parcel: Parcel, flags: Int) {
        parcel.writeLong(timeStartButtonTappedStill)
        parcel.writeLong(timeStartButtonTappedVideo)
        parcel.writeByte(if (videoCaptureEnabled) 1 else 0)
    }

    override fun describeContents(): Int {
        return 0
    }
}




