package jp.juggler.screenshotbutton

import android.Manifest
import android.annotation.SuppressLint
import android.content.Context
import android.content.Intent
import android.graphics.Bitmap
import android.graphics.BitmapFactory
import android.graphics.Typeface
import android.net.Uri
import android.os.Bundle
import android.os.PersistableBundle
import android.util.Log
import android.view.MotionEvent
import android.view.View
import android.view.Window
import android.view.animation.AnticipateOvershootInterpolator
import android.widget.Button
import android.widget.ImageView
import android.widget.TextView
import android.widget.Toast
import androidx.annotation.RequiresPermission
import androidx.annotation.VisibleForTesting
import androidx.appcompat.widget.LinearLayoutCompat
import androidx.constraintlayout.widget.ConstraintLayout
import androidx.constraintlayout.widget.ConstraintSet
import androidx.documentfile.provider.DocumentFile
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import androidx.transition.ChangeBounds
import androidx.transition.TransitionManager
import com.google.android.material.bottomsheet.BottomSheetDialogFragment
import ja.burhanrashid52.photoeditor.*
import ja.burhanrashid52.photoeditor.shape.ShapeBuilder
import ja.burhanrashid52.photoeditor.shape.ShapeType
import jp.juggler.coman
import jp.juggler.photoediting.*
import jp.juggler.photoediting.base.BaseActivity
import jp.juggler.photoediting.filters.FilterListener
import jp.juggler.photoediting.filters.FilterViewAdapter
import jp.juggler.photoediting.tools.EditingToolsAdapter
import jp.juggler.photoediting.tools.ToolType
import jp.juggler.util.*
import kotlinx.coroutines.*
import java.io.File
import java.io.FileNotFoundException
import java.io.FileOutputStream
import java.io.IOException
import java.util.*
import kotlin.coroutines.CoroutineContext


class ActViewer() : BaseActivity(), CoroutineScope, View.OnClickListener , OnPhotoEditorListener,
    PropertiesBSFragment.Properties, ShapeBSFragment.Properties, EmojiBSFragment.EmojiListener,
    StickerBSFragment.StickerListener,
    EditingToolsAdapter.OnItemSelected, FilterListener ,EraserBSFragment.EraserListner

{

    lateinit var mPhotoEditor: PhotoEditor

    private lateinit var mPropertiesBSFragment: PropertiesBSFragment
    private lateinit var mShapeBSFragment: ShapeBSFragment
    private lateinit var mShapeBuilder: ShapeBuilder
    private lateinit var mEmojiBSFragment: EmojiBSFragment
    private lateinit var mEraserBSFragment: EraserBSFragment
    private lateinit var mStickerBSFragment: StickerBSFragment
    private lateinit var mTxtCurrentTool: TextView
    private lateinit var mWonderFont: Typeface
    private lateinit var mRvTools: RecyclerView
    private lateinit var mRvFilters: RecyclerView
    private lateinit var iv_cancel: ImageView

    private val mEditingToolsAdapter = EditingToolsAdapter(this)
    private val mFilterViewAdapter = FilterViewAdapter(this)
    private lateinit var mRootView: ConstraintLayout
    private lateinit var relbottom: LinearLayoutCompat

    private val mConstraintSet = ConstraintSet()
    private var mIsFilterVisible = false

    private var bm: Bitmap? = null

    @VisibleForTesting

    companion object {
        var mBitmap: Bitmap? = null
        private val log = LogCategory("${App1.tagPrefix}/ActViewer")
        private const val RC_CROP_IMAGE = 102

        const val EXTRA_URI = "uri"

        fun open(context: Context, uri: Uri) {
            context.startActivity(
                Intent(context, ActViewer::class.java)
                    .apply {
                        data = uri
                        addFlags(Intent.FLAG_ACTIVITY_NEW_TASK or Intent.FLAG_ACTIVITY_NO_ANIMATION)
                    }
            )
        }

        private fun delete(context: Context, uri: Uri?) {
            uri ?: return

            when {
                isExternalStorageDocument(uri) -> {
                    if (!deleteDocument(context, uri)) error("deleteDocument returns false")
                }

                else -> {
                    // may MediaStore content url
                    findMedia(context, uri)?.let { media ->
                        val count = context.contentResolver.delete(media.uri, null, null)
                        if (count != 1) error("delete() returns $count")
                        return
                    }
                    error("missing media for the uri.")
                }
            }

        }

        private fun share(context: Context, uri: Uri?) {
            uri ?: return

            fun actionSend(uri: Uri, mimeTypeArg: String?) {
                val mimeType = mimeTypeArg ?: context.contentResolver.getType(uri)
                context.startActivity(
                    Intent.createChooser(
                        Intent(Intent.ACTION_SEND).apply {
                            putExtra(Intent.EXTRA_STREAM, uri)
                            if (mimeType != null) type = mimeType
                        },
                        context.getString(R.string.share)
                    )
                )
            }

            when {
                isExternalStorageDocument(uri) ->
                    actionSend(uri, DocumentFile.fromSingleUri(context, uri)?.type)

                else -> {
                    findMedia(context, uri)?.let { media ->
                        return actionSend(media.uri, media.mimeType)
                    }
                    log.eToast(context, true, "can't find media uri for $uri")
                }
            }
        }
    }

    private lateinit var activityJob: Job

    override val coroutineContext: CoroutineContext
        get() = Dispatchers.Main + activityJob

    private lateinit var tvDesc: TextView
    private lateinit var ivImage: PhotoEditorView
    private var bitmap: Bitmap? = null

    private var lastUri: Uri? = null
    var mSaveImageUri: Uri? = null

    private lateinit var mSaveFileHelper: FileSaveHelper

    @RequiresPermission(allOf = [Manifest.permission.WRITE_EXTERNAL_STORAGE])
    private fun saveImage() {
        onSaveIm(bitmap = bitmap!!)
        Toast.makeText(applicationContext,"Save Sucessfully",Toast.LENGTH_LONG).show()
        val i = Intent(this, Historydemo::class.java)
        coman.page = "History"
        coman.bm = bitmap
        this.startActivity(i)
    }


    fun onSaveIm(bitmap: Bitmap) {
        var outStream: FileOutputStream?
        var fileName: String?
        try {
            val file1 = getExternalFilesDir("MyScreenshot")
            val imgFolder = file1!!.absolutePath + File.separator + "screenshot"
            val file = File(imgFolder)

            Log.d("TAG13","imgFolder" + imgFolder)
            Toast.makeText(applicationContext,"2323232" + imgFolder ,Toast.LENGTH_LONG)

            if (!file.exists()) {
                file.mkdirs()
            }
            val generator = Random()
            var n = 10000
            n = generator.nextInt(n)
            fileName = "Image_$n.jpg"
            val outFile = File(file, fileName)
            outStream = FileOutputStream(outFile)
            bitmap.compress(Bitmap.CompressFormat.PNG, 100, outStream)
            outStream.flush()
            outStream.close()
        } catch (e: FileNotFoundException) {
            e.printStackTrace()
        } catch (e: IOException) {
            e.printStackTrace()
        } finally {
        }
    }

    @Deprecated("Deprecated in Java")
    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        super.onActivityResult(requestCode, resultCode, data)


        if (requestCode == ActViewer.RC_CROP_IMAGE) {
            data?.data?.let {
                Log.v("TEST", it.toString())
                ivImage.source.setImageURI(it)
            }
        }
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        addBackPressed {
            log.d("backPressed")
            finish()
        }

        activityJob = Job()
        super.onCreate(savedInstanceState)
        App1.prepareAppState(this)

        initUI()


        if (savedInstanceState != null) {
            savedInstanceState.getString(EXTRA_URI)?.let { load(Uri.parse(it)) }
        } else {
            intent?.data?.let { load(it) }
        }

        handleIntentImage(ivImage.source)

        mWonderFont = Typeface.createFromAsset(assets, "beyond_wonderland.ttf")

        mPropertiesBSFragment = PropertiesBSFragment()
        mEmojiBSFragment = EmojiBSFragment()
        mStickerBSFragment = StickerBSFragment()
        mShapeBSFragment = ShapeBSFragment()
        mEraserBSFragment = EraserBSFragment()
        mEraserBSFragment.setPropertiesChangeListener(this)
        mStickerBSFragment.setStickerListener(this)
        mEmojiBSFragment.setEmojiListener(this)
        mPropertiesBSFragment.setPropertiesChangeListener(this)
        mShapeBSFragment.setPropertiesChangeListener(this)

        val llmTools = LinearLayoutManager(this, LinearLayoutManager.HORIZONTAL, false)
        mRvTools.layoutManager = llmTools
        mRvTools.adapter = mEditingToolsAdapter

        val llmFilters = LinearLayoutManager(this, LinearLayoutManager.HORIZONTAL, false)
        mRvFilters.layoutManager = llmFilters
        mRvFilters.adapter = mFilterViewAdapter



        mPhotoEditor = PhotoEditor.Builder(this, ivImage)

            .build()

        mPhotoEditor.setOnPhotoEditorListener(this)

        ivImage.source.setImageResource(R.drawable.paris_tower)

        mSaveFileHelper = FileSaveHelper(this)
    }

    override fun onNewIntent(intent: Intent?) {
        super.onNewIntent(intent)
        intent?.data?.let { load(it) }
    }
    private fun handleIntentImage(source: ImageView) {
        if (intent == null) {
            return
        }

        when (intent.action) {

            else -> {
                val intentType = intent.type
                if (intentType != null && intentType.startsWith("image/")) {
                    val imageUri = intent.data
                    if (imageUri != null) {
                        source.setImageURI(imageUri)
                    }
                }
            }
        }
    }

    override fun onDestroy() {
        activityJob.cancel()
        ivImage.source.setImageDrawable(null)
        bitmap?.recycle()
        super.onDestroy()
    }

    override fun onSaveInstanceState(outState: Bundle, outPersistentState: PersistableBundle) {
        super.onSaveInstanceState(outState, outPersistentState)
        outState.saveState()
    }

    override fun onSaveInstanceState(outState: Bundle) {
        super.onSaveInstanceState(outState)
        outState.saveState()
    }

    private fun Bundle.saveState() {
        lastUri?.let { putString(EXTRA_URI, it.toString()) }
    }

    @SuppressLint("MissingPermission")
    override fun onClick(v: View?) {
        when (v?.id) {
            R.id.imgUndo -> mPhotoEditor.undo()
            R.id.imgRedo -> mPhotoEditor.redo()
            R.id.imgSave ->
            saveImage()
            R.id.imgClose -> finish()
            R.id.imgcrop -> crop()

                
            R.id.btnDelete -> try {
                delete(this, lastUri)
                finish()
            } catch (ex: Throwable) {
                log.eToast(this, ex, "delete failed. $lastUri")
            }


            R.id.imgShare -> try {
                share(this, lastUri)
            } catch (ex: Throwable) {
                log.eToast(this, ex, "share failed. $lastUri")
            }
        }
    }
    private fun crop() {
        val intent = Intent(this, ScreenshootCrop::class.java)
        mBitmap = bitmap
        Toast.makeText(applicationContext,"this is toast message" + bitmap,Toast.LENGTH_SHORT).show()
        startActivity(intent)
    }
    @SuppressLint("MissingInflatedId", "WrongViewCast")
    private fun initUI() {
        requestWindowFeature(Window.FEATURE_NO_TITLE)
        setContentView(R.layout.act_viewer)

        findViewById<View>(R.id.btnDelete).setOnClickListener(this)
        mRvTools = findViewById(R.id.rvConstraintTools)
        mRootView = findViewById(R.id.rootView)
        relbottom = findViewById(R.id.relbottom)
        ivImage = findViewById(R.id.ivImage)
        tvDesc = findViewById(R.id.tvDesc)
        mRvTools = findViewById(R.id.rvConstraintTools)
        mRvFilters = findViewById(R.id.rvFilterView)
        iv_cancel = findViewById(R.id.iv_cancel)
        iv_cancel.setOnClickListener(View.OnClickListener {

            if (relbottom.visibility == View.VISIBLE) {
                relbottom.visibility = View.INVISIBLE
            } else {
                relbottom.visibility = View.VISIBLE
                settoolsview(false)

            }
        })

        val imgUndo: Button = findViewById(R.id.imgUndo)
        imgUndo.setOnClickListener(this)

        val imgRedo: Button = findViewById(R.id.imgRedo)
        imgRedo.setOnClickListener(this)

        val imgSave: Button = findViewById(R.id.imgSave)
        imgSave.setOnClickListener(this)

        val imgClose: Button = findViewById(R.id.imgClose)
        imgClose.setOnClickListener(this)

        val imgShare: Button = findViewById(R.id.imgShare)
        imgShare.setOnClickListener(this)

        val imgcrop: Button = findViewById(R.id.imgcrop)
        imgcrop.setOnClickListener(this)
    }

    private fun settoolsview(hideColorPicker: Boolean) {
        if (hideColorPicker) {
            mRootView.visibility = View.GONE
            mRvTools.visibility = View.VISIBLE
        } else {
            mRootView.visibility = View.VISIBLE
            mRvTools.visibility = View.GONE
        }
    }
    @SuppressLint("SetTextI18n")
    private fun load(uri: Uri) {

        this.lastUri = uri

        val path = pathFromDocumentUri(this, uri)
            ?: error("can't get path from document uri")

        launch{
            try {
                tvDesc.text = "loading…\n$path"

                val bitmap = withContext(Dispatchers.IO) {
                    @Suppress("BlockingMethodInNonBlockingContext")
                    contentResolver.openInputStream(uri).use {
                        BitmapFactory.decodeStream(it)
                    }
                }
                    ?: error("bitmap is null")
                if (coroutineContext.isActive) {
                    ivImage.source.setImageBitmap(bitmap)
                    this@ActViewer.bitmap = bitmap

                    tvDesc.text = "${bitmap.width}x${bitmap.height}\n$path"


                }
            } catch (ex: Throwable) {
                log.eToast(this@ActViewer, ex, "load failed.")
                if (coroutineContext.isActive) {
                  //  ivImage.source.setImageResource(R.drawable.)
                    tvDesc.text = "load error\n$path"
                }
            }
        }
    }

    override fun onEmojiClick(emojiUnicode: String?) {
    }

    override fun onColorChanged(colorCode: Int) {
        mPhotoEditor.setShape(mShapeBuilder.withShapeColor(colorCode))
    }

    override fun onOpacityChanged(opacity: Int) {
        mPhotoEditor.setShape(mShapeBuilder.withShapeOpacity(opacity))
    }


    override fun onShapePicked(shapeType: ShapeType) {
        mPhotoEditor.setShape(mShapeBuilder.withShapeType(shapeType))
    }

    override fun onShapeSizeChanged(shapeSize: Int) {
        mPhotoEditor.setShape(mShapeBuilder.withShapeSize(shapeSize.toFloat()))
    }

    override fun onStickerClick(bitmap: Bitmap?) {

        mPhotoEditor.addImage(bitmap)
    }

    override fun onFilterSelected(photoFilter: PhotoFilter) {
        mPhotoEditor.setFilterEffect(photoFilter)
    }

    override fun onToolSelected(toolType: ToolType) {
        when (toolType) {
            ToolType.SHAPE -> {
                mPhotoEditor.setBrushDrawingMode(true)
                mShapeBuilder = ShapeBuilder()
                mPhotoEditor.setShape(mShapeBuilder)
                showBottomSheetDialogFragment(mShapeBSFragment)
            }
            ToolType.TEXT -> {
                val textEditorDialogFragment = TextEditorDialogFragment.show(this)
                textEditorDialogFragment.setOnTextEditorListener(object :
                    TextEditorDialogFragment.TextEditorListener {
                    override fun onDone(inputText: String?, colorCode: Int) {
                        val styleBuilder = TextStyleBuilder()
                        styleBuilder.withTextColor(colorCode)
                        mPhotoEditor.addText(inputText, styleBuilder)
                    }
                })
            }
            ToolType.ERASER -> {
               // mPhotoEditor.brushEraser()
                mPhotoEditor.setBrushDrawingMode(true)
                mPhotoEditor.brushEraser()
             //   mTxtCurrentTool.setText(R.string.label_eraser_mode)
                showBottomSheetDialogFragment(mEraserBSFragment)
              //  mTxtCurrentTool.setText(R.string.label_eraser_mode)
            }
            ToolType.FILTER -> {
//                mTxtCurrentTool.setText(R.string.label_filter)
                if (relbottom.visibility == View.VISIBLE){
                    relbottom.visibility = View.INVISIBLE
                } else {
                    relbottom.visibility = View.VISIBLE
                    showFilter(true)
                }
            }
            ToolType.EMOJI -> showBottomSheetDialogFragment(mEmojiBSFragment)
            ToolType.STICKER -> showBottomSheetDialogFragment(mStickerBSFragment)
        }
    }

    private fun showBottomSheetDialogFragment(fragment: BottomSheetDialogFragment?) {
        if (fragment == null || fragment.isAdded) {
            return
        }
        fragment.show(supportFragmentManager, fragment.tag)
    }

    private fun showFilter(isVisible: Boolean) {
        mIsFilterVisible = isVisible
        mConstraintSet.clone(mRootView)

        val rvFilterId: Int = mRvFilters.id

        if (isVisible) {
            mConstraintSet.clear(rvFilterId, ConstraintSet.START)
            mConstraintSet.connect(
                rvFilterId, ConstraintSet.START,
                ConstraintSet.PARENT_ID, ConstraintSet.START
            )
            mConstraintSet.connect(
                rvFilterId, ConstraintSet.END,
                ConstraintSet.PARENT_ID, ConstraintSet.END
            )
        } else {
            mConstraintSet.connect(
                rvFilterId, ConstraintSet.START,
                ConstraintSet.PARENT_ID, ConstraintSet.END
            )
            mConstraintSet.clear(rvFilterId, ConstraintSet.END)
        }

        val changeBounds = ChangeBounds()
        changeBounds.duration = 350
        changeBounds.interpolator = AnticipateOvershootInterpolator(1.0f)
        TransitionManager.beginDelayedTransition(mRootView, changeBounds)

        mConstraintSet.applyTo(mRootView)
    }

    override fun onAddViewListener(viewType: ViewType?, numberOfAddedViews: Int) {

    }


    override fun onEditTextChangeListener(rootView: View?, text: String?, colorCode: Int) {
        val textEditorDialogFragment =
            TextEditorDialogFragment.show(this, text.toString(), colorCode)
        textEditorDialogFragment.setOnTextEditorListener(object :
            TextEditorDialogFragment.TextEditorListener {
            override fun onDone(inputText: String?, colorCode: Int) {
                val styleBuilder = TextStyleBuilder()
                styleBuilder.withTextColor(colorCode)
                if (rootView != null) {
                    mPhotoEditor.editText(rootView, inputText, styleBuilder)
                }
                mTxtCurrentTool.setText(R.string.label_text)
            }
        })
    }


    override fun onRemoveViewListener(viewType: ViewType?, numberOfAddedViews: Int) {

    }

    override fun onStartViewChangeListener(viewType: ViewType?) {

    }


    override fun onStopViewChangeListener(viewType: ViewType?) {
    }


    override fun onTouchSourceImage(event: MotionEvent?) {
    }

}





