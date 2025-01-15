package jp.juggler.v2mixup.ui

import android.content.Intent
import android.graphics.Bitmap
import android.graphics.Canvas
import android.net.Uri
import android.os.Bundle
import android.util.Log
import android.view.Menu
import android.view.MenuItem
import android.view.View
import android.widget.FrameLayout
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import androidx.appcompat.widget.AppCompatButton
import androidx.fragment.app.Fragment
import androidx.navigation.NavController
import androidx.navigation.NavDestination
import androidx.navigation.findNavController
import androidx.navigation.ui.AppBarConfiguration
import androidx.navigation.ui.navigateUp
import jp.juggler.screenshotbutton.App1
import jp.juggler.screenshotbutton.R
import jp.juggler.v2mixup.collageview.util.ImageUtil
import jp.juggler.v2mixup.collageview.views.AbstractCollageView
import jp.juggler.v2mixup.ui.editor.EditorFragment
import java.io.File
import java.io.FileNotFoundException
import java.io.FileOutputStream
import java.io.IOException
import java.util.*
import javax.inject.Inject


@Suppress("DEPRECATION")
class MainActivity : AppCompatActivity(), ImageUtil.ImageSavedListener,
    NavController.OnDestinationChangedListener {

    // -------------------- PROPERTIES --------------------

    private lateinit var appBarConfig: AppBarConfiguration
    private var menu: Menu? = null
    private var collageContainer: FrameLayout? = null
    private var progressBarContainer: FrameLayout? = null

    @Inject
    lateinit var imageUtil: ImageUtil

    private lateinit var action_reset: AppCompatButton
    private lateinit var action_save: AppCompatButton
    private lateinit var imgClose: AppCompatButton

    private var savedImageUri: Uri? = null
    private lateinit var mbitmap: Bitmap


    // -------------------- INIT -------------------- //

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main2)

        (application as App1).getAppComponent().activityComponent().create().inject(this)

        action_reset = findViewById(R.id.action_reset)
        action_save = findViewById(R.id.action_save)
        imgClose = findViewById(R.id.imgClose)
        imageUtil.printDispatchers()
        action_reset.setOnClickListener(View.OnClickListener { resetImage() })
        action_save.setOnClickListener(View.OnClickListener {
            collageContainer?.let{
                    it1 -> saveCollageToGallery(it1)
            }
        })
        imgClose.setOnClickListener(View.OnClickListener { onBackPressed() })

    }


    // -------------------- NAVIGATION -------------------- //

    override fun onCreateOptionsMenu(menu: Menu): Boolean {
        this.menu = menu
        menuInflater.inflate(R.menu.main_options_menu, menu)
        return super.onCreateOptionsMenu(menu)
    }

    override fun onDestinationChanged(
        controller: NavController,
        dest: NavDestination, args: Bundle?,
    ) {

    }

    override fun onOptionsItemSelected(item: MenuItem): Boolean {
        if (getCurrentFragment() !is EditorFragment) {
            Log.d(TAG, "onOptionsItemSelected: current fragment is not the editor!")
            return super.onOptionsItemSelected(item)
        }

        when (item.itemId) {

            else -> return false
        }
        return true
    }

    override fun onSupportNavigateUp(): Boolean {
        val navController = findNavController(R.id.nav_host_fragment)
        return navController.navigateUp(appBarConfig) || super.onSupportNavigateUp()
    }


    // -------------------- SHARE COLLAGE --------------------

    private fun shareImage() {
        prepareToCaptureCollage()
        imageUtil.prepareViewForSharing(this, collageContainer!!, this)
    }

    override fun onReadyToShareImage(uri: Uri?) {
        setProgressBarVisibility(View.GONE)

        if (uri == null) toastErrorAndLog("Unable to share.", "Unable to share!")
        else {
            savedImageUri = uri
            startShareIntent(uri)
        }
    }

    private fun startShareIntent(uri: Uri?) {
        val shareIntent: Intent = Intent().apply {
            action = Intent.ACTION_SEND
            putExtra(Intent.EXTRA_STREAM, uri)
            addFlags(Intent.FLAG_GRANT_READ_URI_PERMISSION)
            type = "image/jpeg"
        }
        startActivity(Intent.createChooser(shareIntent, "Share to..."))
    }


    // -------------------- SAVE COLLAGE --------------------

    fun onSaveIm(bitmap: Bitmap) {
        var outStream: FileOutputStream?
        var fileName: String?
        try {
            val file1 = getExternalFilesDir("MyScreenshot")
            val imgFolder = file1!!.absolutePath + File.separator + "screenshot"
            val file = File(imgFolder)
            Log.d("TAG13", "imgFolder" + file)
            Toast.makeText(applicationContext, "2323232" + savedImageUri, Toast.LENGTH_LONG)

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

    private fun saveCollageToGallery(view: FrameLayout) {

        val bitmap = Bitmap.createBitmap(view.width, view.height, Bitmap.Config.ARGB_8888)
        val canvas = Canvas(bitmap)
        view.draw(canvas)

        onSaveIm(bitmap)
    }


    override fun onCollageSavedToGallery(isSaveSuccessful: Boolean, uri: Uri?) {

    }


    // -------------------- UTIL --------------------

    private fun resetImage() {
        if (collageContainer == null) collageContainer = findViewById(R.id.collage_container)
        (collageContainer!!.getChildAt(0) as AbstractCollageView).reset()
        (getCurrentFragment() as EditorFragment).reset()
    }

    private fun getCurrentFragment(): Fragment {
        val navHostFragment = supportFragmentManager.findFragmentById(R.id.nav_host_fragment)
        return navHostFragment!!.childFragmentManager.fragments[0]
    }

    private fun prepareToCaptureCollage() {
        savedImageUri = null
        setProgressBarVisibility(View.VISIBLE)
        if (collageContainer == null) {
            collageContainer = findViewById(R.id.collage_container)
        }
    }

    private fun setProgressBarVisibility(visibility: Int) {
        if (progressBarContainer == null) {
            progressBarContainer = findViewById(R.id.progress_bar_container)
        }
        progressBarContainer?.visibility = visibility
    }

    private fun toastErrorAndLog(logMsg: String, toastMsg: String = "Something went wrong :(") {
        Log.d(TAG, "toastErrorAndLog: $logMsg")
        Toast.makeText(this, toastMsg, Toast.LENGTH_LONG).show()
    }

    companion object {
        private const val TAG = "MainActivity"
        var mBitmap: Bitmap? = null
    }
}