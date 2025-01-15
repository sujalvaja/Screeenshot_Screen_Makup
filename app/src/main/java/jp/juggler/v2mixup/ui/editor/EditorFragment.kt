package jp.juggler.v2mixup.ui.editor

import android.app.Activity.RESULT_OK
import android.content.Intent
import android.os.Bundle
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.view.ViewTreeObserver
import android.widget.FrameLayout
import android.widget.ImageView
import android.widget.LinearLayout
import android.widget.RelativeLayout
import androidx.appcompat.widget.SwitchCompat
import androidx.cardview.widget.CardView
import androidx.core.view.updateLayoutParams
import androidx.fragment.app.Fragment
import androidx.lifecycle.Observer
import androidx.lifecycle.ViewModelProvider
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.gerardbradshaw.colorpickerlibrary.views.AbstractColorPickerView
import com.gerardbradshaw.colorpickerlibrary.views.CompactColorPickerView
import com.ortiz.touchview.TouchImageView
import jp.juggler.screenshotbutton.App1
import jp.juggler.screenshotbutton.R
import jp.juggler.v2mixup.collageview.CollageViewFactory
import jp.juggler.v2mixup.collageview.views.AbstractCollageView

class EditorFragment :
    Fragment(),
    View.OnClickListener,
    AbstractColorPickerView.ColorChangedListener {

    private lateinit var rootView: View
    private lateinit var viewModel: EditorViewModel
    private lateinit var collageViewFactory: CollageViewFactory

    private lateinit var collageViewContainerParent: FrameLayout
    private lateinit var collageViewContainer: FrameLayout
    private lateinit var collageView: AbstractCollageView

    private lateinit var recyclerView: RecyclerView
    private lateinit var rvColletionView: RecyclerView
    private lateinit var relbottom: CardView
    private lateinit var iv_cancel: ImageView
    private lateinit var iv_cancel1: ImageView
    private lateinit var iv_cancel2: ImageView
    private lateinit var colorPickerContainer: RelativeLayout
    private lateinit var relbottom1: RelativeLayout
    private lateinit var relbottom2: RelativeLayout
    private lateinit var tools_container: LinearLayout
    private lateinit var borderSwitch: SwitchCompat

    private var lastImageClickedIndex: Int = -1


    // ------------------------ INITIALIZATION ------------------------

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {

        (requireActivity().application as App1)
            .getAppComponent()
            .editorComponent()
            .create()
            .inject(this)

        return inflater.inflate(R.layout.fragment_editor, container, false)
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        viewModel = ViewModelProvider(this)[EditorViewModel::class.java]
        rootView = view
        initOptionsButtons()
        initTools()
        initCollage()
        initBorderColorPicker()
        viewModel.canvasAspectRatio.observe(requireActivity(), Observer {
            onAspectRatioChange(it)
        })
    }

    private fun initTools() {
        recyclerView = requireView().findViewById(R.id.tool_popup_recycler)
        rvColletionView = requireView().findViewById(R.id.rvColletionView)
        relbottom1 = requireView().findViewById(R.id.relbottom1)
        relbottom2 = requireView().findViewById(R.id.relbottom2)
        colorPickerContainer = requireView().findViewById(R.id.color_picker_container)
        relbottom = requireView().findViewById(R.id.relbottom)
        tools_container = requireView().findViewById(R.id.tools_container)
        iv_cancel = requireView().findViewById(R.id.iv_cancel)
        iv_cancel.setOnClickListener(View.OnClickListener {

            if (colorPickerContainer.visibility == View.VISIBLE) {
                colorPickerContainer.visibility = View.INVISIBLE
                settoolsview(false)
            } else {
                colorPickerContainer.visibility = View.VISIBLE

            }
        })
        iv_cancel1 = requireView().findViewById(R.id.iv_cancel1)
        iv_cancel1.setOnClickListener(View.OnClickListener {

            if (relbottom1.visibility == View.VISIBLE) {
                relbottom1.visibility = View.INVISIBLE
                settoolsview(false)

            } else {
                relbottom1.visibility = View.VISIBLE
                //   settoolsview(false)



            }
        })
        iv_cancel2 = requireView().findViewById(R.id.iv_cancel2)
        iv_cancel2.setOnClickListener(View.OnClickListener {

            if (relbottom2.visibility == View.VISIBLE) {
                relbottom2.visibility = View.INVISIBLE
                settoolsview(false)

            } else {
                relbottom2.visibility = View.VISIBLE

            }
        })
    }

    private fun initCollage() {
        collageViewContainerParent = rootView.findViewById(R.id.collage_container_parent)
        collageViewContainer = rootView.findViewById(R.id.collage_container)
        relbottom = rootView.findViewById(R.id.relbottom)
        collageViewContainer.viewTreeObserver.addOnGlobalLayoutListener(
            object : ViewTreeObserver.OnGlobalLayoutListener {
                override fun onGlobalLayout() {
                    if (collageViewContainer.height > 0) {
                        initCollageViewFactory()
                        initDefaultCollageView()

                        collageViewContainer.viewTreeObserver.removeOnGlobalLayoutListener(this)
                    }
                }
            })
    }

    private fun initCollageViewFactory() {
        collageViewFactory = CollageViewFactory(
            context = rootView.context,
            attrs = null,
            layoutWidth = collageViewContainer.width,
            layoutHeight = collageViewContainer.height,
            isBorderEnabled = false,
            imageUris = viewModel.imageUris
        )
    }

    private fun initDefaultCollageView() {
        collageView = collageViewFactory.getView(CollageViewFactory.CollageLayoutType.THREE_IMAGE_2)
        collageViewContainer.addView(collageView)
        collageView.setImageClickListener(this)
    }

    private fun initOptionsButtons() {
        requireView().also {
            it.findViewById<ImageView>(R.id.button_layout).setOnClickListener(this)
            it.findViewById<ImageView>(R.id.button_aspect_ratio).setOnClickListener(this)
            it.findViewById<ImageView>(R.id.button_border).setOnClickListener(this)
        }
    }

    private fun initBorderColorPicker() {
        val colorPicker: CompactColorPickerView = requireView().findViewById(R.id.color_picker_view)
        colorPicker.setOnColorSelectedListener(this)

        borderSwitch = requireView().findViewById(R.id.border_switch)
        borderSwitch.setOnCheckedChangeListener { _, isChecked ->
            collageView.enableBorder(isChecked)
            collageView.setBorderColor(colorPicker.getCurrentColor())
        }
    }

    private fun setIsColorPickerHidden(hideColorPicker: Boolean) {
        if (hideColorPicker) {
            colorPickerContainer.visibility = View.GONE
            recyclerView.visibility = View.VISIBLE
        } else {
            colorPickerContainer.visibility = View.VISIBLE
            recyclerView.visibility = View.GONE
        }
    }

    private fun settoolsview(hideColorPicker: Boolean) {
        if (hideColorPicker) {
            tools_container.visibility = View.GONE
            recyclerView.visibility = View.VISIBLE
        } else {
            tools_container.visibility = View.VISIBLE
            recyclerView.visibility = View.GONE
        }
    }


    // ------------------------ COLLAGE ------------------------

    private fun showCollageLayoutsInRecycler() {

        if (relbottom1.visibility == View.VISIBLE) {
            relbottom1.visibility = View.INVISIBLE

        }
        else {
            relbottom1.visibility = View.VISIBLE
            val adapter = CollageLayoutListAdapter(requireContext(), viewModel.collageIconIdToType)

            adapter.setCollageTypeClickedListener(object :
                CollageLayoutListAdapter.TypeClickedListener {
                override fun onLayoutTypeClicked(collageLayoutType: CollageViewFactory.CollageLayoutType) {
                    onNewCollageTypeSelected(collageLayoutType)
                }
            })
            val llmFilters = LinearLayoutManager(context, LinearLayoutManager.HORIZONTAL, false)
            rvColletionView.layoutManager = llmFilters
            rvColletionView.adapter = adapter
            val rvFilterId: Int = rvColletionView.id
            settoolsview(true)

        }

    }

    private fun onNewCollageTypeSelected(collageLayoutType: CollageViewFactory.CollageLayoutType) {
        collageView = collageViewFactory.getView(collageLayoutType)
        collageViewContainer.removeAllViews()
        collageViewContainer.addView(collageView)
        collageView.setImageClickListener(this@EditorFragment)
    }

    fun reset() {
        viewModel.resetImageUris()
        collageView.reset()
        borderSwitch.isChecked = false
    }


    // ------------------------ ASPECT RATIO ------------------------

    private fun showAspectRatiosInRecycler() {
        if (relbottom2.visibility == View.VISIBLE) {
            relbottom2.visibility = View.INVISIBLE

        } else {
            relbottom2.visibility = View.VISIBLE
            val adapter =
                AspectRatioListAdapter(requireView().context, viewModel.ratioStringToValue)

            adapter
                .setButtonClickedListener(object :
                    AspectRatioListAdapter.AspectRatioButtonClickedListener {
                    override fun onAspectRatioButtonClicked(newRatio: Float) {
                        viewModel.setAspectRatio(newRatio)
                    }
                })

            val llmFilters = LinearLayoutManager(context, LinearLayoutManager.HORIZONTAL, false)
            recyclerView.layoutManager = llmFilters
            recyclerView.adapter = adapter
            val rvFilterId: Int = recyclerView.id
            //showFilter(true)
            settoolsview(true)
        }

    }

    private fun onAspectRatioChange(newRatio: Float) {
        if (collageViewContainer.height > 0) {
            collageView.aspectRatio = newRatio

            collageViewContainerParent.updateLayoutParams {
                this.width = ViewGroup.LayoutParams.WRAP_CONTENT
                this.height = ViewGroup.LayoutParams.WRAP_CONTENT
            }
        }
    }


    // ------------------------ BORDER ------------------------

    private fun showBorderOptions() {
        if (tools_container.visibility == View.VISIBLE) {
            tools_container.visibility = View.INVISIBLE
            setIsColorPickerHidden(false)
            //  settoolsview(false)
        } else {
            tools_container.visibility = View.VISIBLE
        }

    }

    override fun onColorChanged(color: Int) {
        collageView.isBorderEnabled = true
        if (!borderSwitch.isChecked) borderSwitch.isChecked = true
        collageView.setBorderColor(color)
    }


    // ------------------------ IMPORTING IMAGES ------------------------

    private fun startImageImportIntent() {
        val intent = Intent(Intent.ACTION_PICK)
        intent.type = "image/*"
        startActivityForResult(intent, REQUEST_IMAGE_IMPORT_CODE)
    }

    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        when (requestCode) {
            REQUEST_IMAGE_IMPORT_CODE -> {
                if (resultCode == RESULT_OK && data != null) onImageImported(data)
            }
            else -> {
                super.onActivityResult(requestCode, resultCode, data)
            }
        }
    }

    private fun onImageImported(data: Intent) {
        val uri = data.data!!
        viewModel.addImageUri(uri, lastImageClickedIndex)

        if (collageView.childCount > lastImageClickedIndex) {
            collageView.setImageAt(lastImageClickedIndex, uri)
            lastImageClickedIndex = -1
        } else Log.d(TAG, "onImageImported: Selected TouchImageView no longer exists")
    }

    override fun onClick(view: View?) {
        if (view is TouchImageView) {
            lastImageClickedIndex = collageView.indexOfChild(view)
            startImageImportIntent()
        }

        when (view?.id) {
            R.id.button_layout ->
                showCollageLayoutsInRecycler()


            R.id.button_aspect_ratio ->
                showAspectRatiosInRecycler()

            R.id.button_border ->
                showBorderOptions()

        }
    }

    companion object {
        private const val TAG = "EditorFragment"
        private const val REQUEST_IMAGE_IMPORT_CODE = 1000
    }
}


