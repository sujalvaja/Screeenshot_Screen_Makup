package jp.juggler.photoediting

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.CompoundButton
import android.widget.ImageView
import android.widget.RadioGroup
import android.widget.SeekBar
import androidx.appcompat.widget.AppCompatCheckBox
import androidx.appcompat.widget.AppCompatImageView
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.google.android.material.bottomsheet.BottomSheetDialogFragment
import ja.burhanrashid52.photoeditor.shape.ShapeType
import jp.juggler.photoediting.ColorPickerAdapter.OnColorPickerClickListener
import jp.juggler.screenshotbutton.R


class ShapeBSFragment : BottomSheetDialogFragment(), SeekBar.OnSeekBarChangeListener {
    private var mProperties: Properties? = null

    interface Properties {
        fun onColorChanged(colorCode: Int)
        fun onOpacityChanged(opacity: Int)
        fun onShapeSizeChanged(shapeSize: Int)
        fun onShapePicked(shapeType: ShapeType)
    }

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        return inflater.inflate(R.layout.fragment_bottom_shapes_dialog, container, false)
    }

    override fun getTheme() = R.style.CustomBottomSheetDialogTheme

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        val rvColor: RecyclerView = view.findViewById(R.id.shapeColors)
        val sbOpacity = view.findViewById<SeekBar>(R.id.shapeOpacity)
        val sbBrushSize = view.findViewById<SeekBar>(R.id.shapeSize)
        val shapeGroup = view.findViewById<RadioGroup>(R.id.shapeRadioGroup)
        val lineRadioButton = view.findViewById<AppCompatCheckBox>(R.id.lineRadioButton)
        val arrowRadioButton = view.findViewById<AppCompatCheckBox>(R.id.arrowRadioButton)
        val ovalRadioButton = view.findViewById<AppCompatCheckBox>(R.id.ovalRadioButton)
        val rectRadioButton = view.findViewById<AppCompatCheckBox>(R.id.rectRadioButton)
        val iv_cancel = view.findViewById<AppCompatImageView>(R.id.iv_cancel)


        iv_cancel.setOnClickListener(View.OnClickListener { dismiss() })

        lineRadioButton.setOnCheckedChangeListener(CompoundButton.OnCheckedChangeListener { compoundButton, b ->
            if (b) {
                mProperties!!.onShapePicked(ShapeType.Line)
                arrowRadioButton.isChecked = false
                ovalRadioButton.isChecked = false
                rectRadioButton.isChecked = false
            } else {
                mProperties!!.onShapePicked(ShapeType.Brush)
                lineRadioButton.isChecked = false
                arrowRadioButton.isChecked = false
                ovalRadioButton.isChecked = false
                rectRadioButton.isChecked = false
            }
        })

        arrowRadioButton.setOnCheckedChangeListener(CompoundButton.OnCheckedChangeListener { compoundButton, b ->
            if (b) {
                mProperties!!.onShapePicked(ShapeType.Arrow())
                lineRadioButton.isChecked = false
                ovalRadioButton.isChecked = false
                rectRadioButton.isChecked = false
            } else {
                mProperties!!.onShapePicked(ShapeType.Brush)
                lineRadioButton.isChecked = false
                arrowRadioButton.isChecked = false
                ovalRadioButton.isChecked = false
                rectRadioButton.isChecked = false            }
        })

        ovalRadioButton.setOnCheckedChangeListener(CompoundButton.OnCheckedChangeListener { compoundButton, b ->
            if (b) {
                mProperties!!.onShapePicked(ShapeType.Oval)
                arrowRadioButton.isChecked = false
                lineRadioButton.isChecked = false
                rectRadioButton.isChecked = false
            } else {
                mProperties!!.onShapePicked(ShapeType.Brush)
                lineRadioButton.isChecked = false
                arrowRadioButton.isChecked = false
                ovalRadioButton.isChecked = false
                rectRadioButton.isChecked = false            }
        })

        rectRadioButton.setOnCheckedChangeListener(CompoundButton.OnCheckedChangeListener { compoundButton, b ->
            if (b) {
                mProperties!!.onShapePicked(ShapeType.Rectangle)
                arrowRadioButton.isChecked = false
                ovalRadioButton.isChecked = false
                lineRadioButton.isChecked = false
            } else {
                mProperties!!.onShapePicked(ShapeType.Brush)
                lineRadioButton.isChecked = false
                arrowRadioButton.isChecked = false
                ovalRadioButton.isChecked = false
                rectRadioButton.isChecked = false            }
        })

        shapeGroup.setOnCheckedChangeListener { _: RadioGroup?, checkedId: Int ->
            when (checkedId) {
                R.id.lineRadioButton -> {
                    mProperties!!.onShapePicked(ShapeType.Line)
                }
                R.id.arrowRadioButton -> {
                    mProperties!!.onShapePicked(ShapeType.Arrow())
                }
                R.id.ovalRadioButton -> {
                    mProperties!!.onShapePicked(ShapeType.Oval)
                }
                R.id.rectRadioButton -> {
                    mProperties!!.onShapePicked(ShapeType.Rectangle)
                }
                else -> {
                    mProperties!!.onShapePicked(ShapeType.Brush)
                }
            }
        }
        sbOpacity.setOnSeekBarChangeListener(this)
        sbBrushSize.setOnSeekBarChangeListener(this)

        val activity = requireActivity()

        // TODO(lucianocheng): Move layoutManager to a xml file.
        val layoutManager = LinearLayoutManager(activity, LinearLayoutManager.HORIZONTAL, false)
        rvColor.layoutManager = layoutManager
        rvColor.setHasFixedSize(true)
        val colorPickerAdapter = ColorPickerAdapter(activity)
        colorPickerAdapter.setOnColorPickerClickListener(object : OnColorPickerClickListener {
            override fun onColorPickerClickListener(colorCode: Int) {
                if (mProperties != null) {
                    dismiss()
                    mProperties!!.onColorChanged(colorCode)
                }
            }
        })
        rvColor.adapter = colorPickerAdapter
    }

    fun setPropertiesChangeListener(properties: Properties?) {
        mProperties = properties
    }

    override fun onProgressChanged(seekBar: SeekBar, i: Int, b: Boolean) {
        when (seekBar.id) {
            R.id.shapeOpacity -> if (mProperties != null) {
                mProperties!!.onOpacityChanged(i)
            }
            R.id.shapeSize -> if (mProperties != null) {
                mProperties!!.onShapeSizeChanged(i)
            }
        }
    }

    override fun onStartTrackingTouch(seekBar: SeekBar) {}
    override fun onStopTrackingTouch(seekBar: SeekBar) {}
}