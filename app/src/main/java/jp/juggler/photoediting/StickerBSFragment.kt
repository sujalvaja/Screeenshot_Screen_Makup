package jp.juggler.photoediting

import android.annotation.SuppressLint
import android.app.Dialog
import android.content.res.AssetManager
import android.graphics.Bitmap
import android.graphics.drawable.Drawable
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import androidx.coordinatorlayout.widget.CoordinatorLayout
import androidx.recyclerview.widget.GridLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.bumptech.glide.Glide
import com.bumptech.glide.request.target.CustomTarget
import com.bumptech.glide.request.transition.Transition
import com.google.android.material.bottomsheet.BottomSheetBehavior
import com.google.android.material.bottomsheet.BottomSheetBehavior.BottomSheetCallback
import com.google.android.material.bottomsheet.BottomSheetDialogFragment
import jp.juggler.screenshotbutton.R
import java.io.InputStream
import java.util.*


class StickerBSFragment : BottomSheetDialogFragment() {
    private var mStickerListener: StickerListener? = null
    fun setStickerListener(stickerListener: StickerListener?) {
        mStickerListener = stickerListener
    }

    interface StickerListener {
        fun onStickerClick(bitmap: Bitmap?)
    }

    private val mBottomSheetBehaviorCallback: BottomSheetCallback = object : BottomSheetCallback() {
        override fun onStateChanged(bottomSheet: View, newState: Int) {
            if (newState == BottomSheetBehavior.STATE_HIDDEN) {
                dismiss()
            }
        }

        override fun onSlide(bottomSheet: View, slideOffset: Float) {}
    }

    @SuppressLint("RestrictedApi")
    override fun setupDialog(dialog: Dialog, style: Int) {
        super.setupDialog(dialog, style)
        val contentView = View.inflate(context, R.layout.fragment_bottom_sticker_emoji_dialog, null)
        dialog.setContentView(contentView)
        val params = (contentView.parent as View).layoutParams as CoordinatorLayout.LayoutParams
        val behavior = params.behavior
        if (behavior != null && behavior is BottomSheetBehavior<*>) {
            behavior.setBottomSheetCallback(mBottomSheetBehaviorCallback)
        }
        (contentView.parent as View).setBackgroundColor(resources.getColor(android.R.color.transparent))
        val rvEmoji: RecyclerView = contentView.findViewById(R.id.rvEmoji)
        val gridLayoutManager = GridLayoutManager(activity, 3)
        rvEmoji.layoutManager = gridLayoutManager
        val stickerAdapter = StickerAdapter()
        rvEmoji.adapter = stickerAdapter
        rvEmoji.setHasFixedSize(true)
        rvEmoji.setItemViewCacheSize(stickerPathList.size)
    }

    inner class StickerAdapter : RecyclerView.Adapter<StickerAdapter.ViewHolder>() {
        override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {
            val view = LayoutInflater.from(parent.context).inflate(R.layout.row_sticker, parent, false)
            return ViewHolder(view)
        }

        override fun onBindViewHolder(holder: ViewHolder, position: Int) {
            Glide.with(requireContext())
                    .asBitmap()
                    .load(stickerPathList[position])
                    .into(holder.imgSticker)
        }

        override fun getItemCount(): Int {
            return stickerPathList.size
        }

        inner class ViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {
            val imgSticker: ImageView = itemView.findViewById(R.id.imgSticker)

            init {
                itemView.setOnClickListener {
                    if (mStickerListener != null) {
                        Glide.with(requireContext())
                                .asBitmap()
                                .load(stickerPathList[layoutPosition])
                                .into(object : CustomTarget<Bitmap?>(256, 256) {
                                    override fun onResourceReady(resource: Bitmap, transition: Transition<in Bitmap?>?) {
                                        mStickerListener!!.onStickerClick(resource)
                                    }

                                    override fun onLoadCleared(placeholder: Drawable?) {}
                                })
                    }
                    dismiss()
                }
            }
        }
    }


    companion object {

        private val stickerPathList = arrayOf(
                "https://w7.pngwing.com/pngs/630/871/png-transparent-heart-eyes-emoji-emoji-heart-iphone-love-emoji-smiley-sticker-emoticon-thumbnail.png",
                "https://w7.pngwing.com/pngs/625/201/png-transparent-shock-emoji-emoji-iphone-computer-icons-emoji-head-smiley-sticker-thumbnail.png",
                "https://cdn-icons-png.flaticon.com/256/8073/8073615.png",
                "https://cdn-icons-png.flaticon.com/128/4160/4160724.png",
                "e5.png",
                "e6.png",
                "e7.png",
                "e8.png",
                "e9.png",
                "e10.png",
                "e11.png",
                "e12.png",
                "e13.png",
                "e14.png",
                "e15.png",
                "e16.png",
                "e17.png",
                "e18.png",
                "e19.png",
                "e20.png",
                "e21.png",
                "e22.png",
                "e23.png",
                "e24.png",

            "https://cdn-icons-png.flaticon.com/256/8073/8073615.png",
            "https://cdn-icons-png.flaticon.com/256/8379/8379945.png",
            "https://cdn-icons-png.flaticon.com/256/7863/7863444.png",
            "https://cdn-icons-png.flaticon.com/256/7598/7598102.png",
            "https://cdn-icons-png.flaticon.com/256/9476/9476161.png",
            "https://cdn-icons-png.flaticon.com/256/7276/7276616.png",
            "https://cdn-icons-png.flaticon.com/256/7276/7276624.png",
            "https://cdn-icons-png.flaticon.com/256/8445/8445843.png",
            "https://cdn-icons-png.flaticon.com/128/166/166527.png",
            "https://cdn-icons-png.flaticon.com/128/742/742752.png",
            "https://cdn-icons-png.flaticon.com/128/4160/4160755.png",
            "https://cdn-icons-png.flaticon.com/128/9663/9663620.png",
            "https://cdn-icons-png.flaticon.com/128/6124/6124503.png",
            "https://cdn-icons-png.flaticon.com/256/8379/8379931.png",
            "https://cdn-icons-png.flaticon.com/128/10575/10575194.png",
            "https://cdn-icons-png.flaticon.com/128/7195/7195016.png",

           " https://cdn-icons-png.flaticon.com/256/4481/4481023.png",
            "https://cdn-icons-png.flaticon.com/256/5784/5784082.png",
           "https://cdn-icons-png.flaticon.com/256/6143/6143047.png",
            "https://cdn-icons-png.flaticon.com/256/9021/9021601.png",
            "https://cdn-icons-png.flaticon.com/256/4359/4359744.png",
            "https://cdn-icons-png.flaticon.com/256/9292/9292392.png",
            "https://cdn-icons-png.flaticon.com/256/9292/9292413.png",
            "https://cdn-icons-png.flaticon.com/256/9292/9292429.png",
            "https://cdn-icons-png.flaticon.com/256/6225/6225308.png",
            "https://cdn-icons-png.flaticon.com/256/8079/8079530.png",
            "https://cdn-icons-png.flaticon.com/256/4471/4471045.png",
            "https://cdn-icons-png.flaticon.com/256/6917/6917446.png",
            "https://cdn-icons-png.flaticon.com/256/4392/4392505.png",

            "https://cdn-icons-png.flaticon.com/256/8359/8359881.png",


            "https://cdn-icons-png.flaticon.com/256/10252/10252209.png",
            "https://cdn-icons-png.flaticon.com/256/7144/7144387.png",
            "https://cdn-icons-png.flaticon.com/128/3313/3313341.png",
            "https://cdn-icons-png.flaticon.com/256/7039/7039050.png",
            "https://cdn-icons-png.flaticon.com/256/6443/6443634.png",
            "https://cdn-icons-png.flaticon.com/256/7603/7603523.png",
            "https://cdn-icons-png.flaticon.com/256/6443/6443634.png",
            "https://cdn-icons-png.flaticon.com/256/6443/6443628.png",
            "https://cdn-icons-png.flaticon.com/256/8937/8937871.png",
            "https://cdn-icons-png.flaticon.com/256/4359/4359865.png",
            "https://cdn-icons-png.flaticon.com/256/4383/4383975.png",
            "https://cdn-icons-png.flaticon.com/256/10684/10684928.png",
            "https://cdn-icons-png.flaticon.com/256/5928/5928267.png",
            "https://cdn-icons-png.flaticon.com/256/9440/9440194.png",
            "https://cdn-icons-png.flaticon.com/256/8937/8937954.png",
            "https://www.flaticon.com/free-sticker/plant_6347224",
            "https://cdn-icons-png.flaticon.com/256/7438/7438956.png",
            "https://cdn-icons-png.flaticon.com/256/6983/6983993.png",



            "https://cdn-icons-png.flaticon.com/256/9933/9933616.png",
            "https://cdn-icons-png.flaticon.com/128/3770/3770960.png",
            "https://cdn-icons-png.flaticon.com/256/4383/4383909.png",
            "https://cdn-icons-png.flaticon.com/256/6820/6820138.png",
            "https://cdn-icons-png.flaticon.com/256/6039/6039641.png",
            "https://cdn-icons-png.flaticon.com/256/6039/6039680.png",
            "C:\\Users\\DELL\\Downloads\\text art\\text art\\5.png",
            "https://cdn-icons-png.flaticon.com/256/4524/4524509.png",
            "https://cdn-icons-png.flaticon.com/256/4364/4364202.png",
            "https://cdn-icons-png.flaticon.com/256/6030/6030521.png",



                "https://cdn-icons-png.flaticon.com/256/4392/4392455.png",
                "https://cdn-icons-png.flaticon.com/256/4392/4392459.png",
                "https://cdn-icons-png.flaticon.com/256/4392/4392462.png",
                "https://cdn-icons-png.flaticon.com/256/4392/4392465.png",
                "https://cdn-icons-png.flaticon.com/256/4392/4392467.png",
                "https://cdn-icons-png.flaticon.com/256/4392/4392469.png",
                "https://cdn-icons-png.flaticon.com/256/4392/4392471.png",
                "https://cdn-icons-png.flaticon.com/256/4392/4392522.png",
            "E://screenshot&screenshot_makup SS//ui design//Emoji",
            "https://encrypted-tbn0.gstatic.com/images?q=tbn:ANd9GcQ0vRWEWzR8S7LATq1aBfiTghDNHUrxsirnh1RBYgfdLA&usqp=CAU&ec=48665701"
        )

    }


    /*fun getFileFromAssets(context: Context, fileName: String): File = File(context.cacheDir, fileName)
        .also {
            if (!it.exists()) {
                it.outputStream().use { cache ->
                    context.assets.open(fileName).use { inputStream ->
                        inputStream.copyTo(cache)
                    }
                }
            }
        }*/
}