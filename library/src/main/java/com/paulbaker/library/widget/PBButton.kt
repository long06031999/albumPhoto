package com.paulbaker.library.widget

import android.content.Context
import android.util.AttributeSet
import com.paulbaker.library.R
import com.paulbaker.library.core.widget.PBButtonBase

class PBButton : PBButtonBase {
    constructor(context: Context) : super(context)
    constructor(context: Context, attrs: AttributeSet?) : super(context, attrs)
    constructor(context: Context, attrs: AttributeSet?, defStyleAttr: Int) : super(
        context,
        attrs,
        defStyleAttr
    )

    override fun initDrawableStateEnabled(): Int = R.drawable.pbbutton_enable_default_type_1

    override fun initDrawableStatePressed(): Int = R.drawable.pbbutton_pressed_default

    override fun initTextColorEnabled(): Int = R.color.pb_button_enable_text_color_type_1

}