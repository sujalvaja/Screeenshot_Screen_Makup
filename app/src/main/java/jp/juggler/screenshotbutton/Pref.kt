package jp.juggler.screenshotbutton

import android.content.Context
import android.content.SharedPreferences
import android.os.Build
import android.text.Editable
import android.text.TextWatcher
import android.widget.CompoundButton
import android.widget.EditText

@Suppress("EqualsOrHashCode")
abstract class BasePref<T>(val key : String) {

    init {
        if( Pref.map[key] != null )
            error("Preference key duplicate: $key")
        else
            @Suppress("LeakingThis")
            Pref.map[key] = this
    }

    override fun equals(other : Any?) : Boolean {
        return this === other
    }

    fun remove(e : SharedPreferences.Editor) {
        e.remove(key)
    }

    abstract fun put(editor : SharedPreferences.Editor, v : T)
    abstract fun invoke(pref : SharedPreferences) : T

    operator fun invoke(context : Context) : T {
        return invoke(Pref.pref(context))
    }

}

@Suppress("unused")
fun SharedPreferences.Editor.remove(item : BasePref<*>) : SharedPreferences.Editor {
    item.remove(this)
    return this
}

class BooleanPref(
    key : String,
    private val defVal : Boolean
) : BasePref<Boolean>(key) {

    override operator fun invoke(pref : SharedPreferences) : Boolean {
        return pref.getBoolean(key, defVal)
    }

    override fun put(editor : SharedPreferences.Editor, v : Boolean) {
        editor.putBoolean(key, v)
    }

    fun bindSwitch(pref:SharedPreferences,btn:CompoundButton,callback :(Boolean)->Unit = {}) {
        btn.isChecked = invoke(pref)
        btn.setOnCheckedChangeListener { _, isChecked ->
            val e =pref.edit()
            if( isChecked == defVal){
                e.remove(key)
            }else{
                e.putBoolean(key,isChecked)
            }
            e.apply()

            callback(isChecked)
        }
    }
}

class IntPref(key : String, @Suppress("MemberVisibilityCanBePrivate") val defVal : Int) : BasePref<Int>(key) {

    override operator fun invoke(pref : SharedPreferences) : Int {
        return pref.getInt(key, defVal)
    }

    override fun put(editor : SharedPreferences.Editor, v : Int) {
        editor.putInt(key, v)
    }

    fun saveIfModified(pref: SharedPreferences, iv: Int) {
        val e =pref.edit()
        if( iv == defVal){
            e.remove(key)
        }else{
            e.putInt(key,iv)
        }
        e.apply()
    }
}

class LongPref(key : String, private val defVal : Long) : BasePref<Long>(key) {

    override operator fun invoke(pref : SharedPreferences) : Long {
        return pref.getLong(key, defVal)
    }

    override fun put(editor : SharedPreferences.Editor, v : Long) {
        editor.putLong(key, v)
    }
}

class FloatPref(key : String, private val defVal : Float) : BasePref<Float>(key) {

    override operator fun invoke(pref : SharedPreferences) : Float {
        return pref.getFloat(key, defVal)
    }

    override fun put(editor : SharedPreferences.Editor, v : Float) {
        editor.putFloat(key, v)
    }
}

class StringPref(
    key: String,
    @Suppress("MemberVisibilityCanBePrivate") val defVal: String
) : BasePref<String>(key) {

    override operator fun invoke(pref : SharedPreferences) : String {
        return pref.getString(key,defVal) ?: defVal
    }

    override fun put(editor : SharedPreferences.Editor, v : String) {
        editor.putString(key, v)
    }

    fun toInt(pref : SharedPreferences) = invoke(pref).toIntOrNull() ?: defVal.toInt()

    fun bindEditText(pref:SharedPreferences,editText: EditText) {
        editText.setText(invoke(pref))
        editText.addTextChangedListener(object : TextWatcher {
            override fun beforeTextChanged(p0: CharSequence?, p1: Int, p2: Int, p3: Int) {
            }

            override fun onTextChanged(p0: CharSequence?, p1: Int, p2: Int, p3: Int) {
            }

            override fun afterTextChanged(e: Editable?) {
                e ?: return
                pref.edit().putString(key, e.toString()).apply()
            }
        })
    }
}

fun SharedPreferences.Editor.put(item : BooleanPref, v : Boolean) : SharedPreferences.Editor {
    item.put(this, v)
    return this
}

fun SharedPreferences.Editor.put(item : StringPref, v : String) : SharedPreferences.Editor {
    item.put(this, v)
    return this
}

fun SharedPreferences.Editor.put(item : IntPref, v : Int) : SharedPreferences.Editor {
    item.put(this, v)
    return this
}

fun SharedPreferences.Editor.put(item : LongPref, v : Long) : SharedPreferences.Editor {
    item.put(this, v)
    return this
}

fun SharedPreferences.Editor.put(item : FloatPref, v : Float) : SharedPreferences.Editor {
    item.put(this, v)
    return this
}

object Pref {

    fun pref(context : Context) :SharedPreferences =
        context.getSharedPreferences("Pref",Context.MODE_PRIVATE)


    val map = HashMap<String, BasePref<*>>()

    val ipCameraButtonSize = IntPref(
        "cameraButtonSize",
        40
    )
    val fpCameraButtonXStill = FloatPref(
        "cameraButtonX",
        70f
    )
    val fpCameraButtonYStill = FloatPref(
        "cameraButtonY",
        70f
    )

    val fpCameraButtonXVideo = FloatPref(
        "cameraButtonXVideo",
        120f
    )

    val fpCameraButtonYVideo = FloatPref(
        "cameraButtonYVideo",
        70f
    )

    // true=PNG,  false=JPEG
    val bpSavePng = BooleanPref(
        "compressPng",
        false
    )
    val bpShowPostView = BooleanPref(
        "ShowPostVie",
        true
    )
    val bpLogToFile = BooleanPref(
        "LogToFile",
        false
    )

    val bpStartBootStill = BooleanPref(
        "StartBootStill",
        false
    )

    val spSaveTreeUri = StringPref(
        "SaveTreeUri",
        ""
    )

    val spCodec = StringPref(
        "Codec",
        ""
    )

    val spFrameRate = StringPref(
        "FrameRate",
        "30"
    )

    val spBitRate = StringPref(
        "BitRate",
        "6000000"
    )

    val toggleButton = BooleanPref(
        "TogalService",
        false
    )
}
