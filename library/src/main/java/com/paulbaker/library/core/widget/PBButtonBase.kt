package com.paulbaker.library.core.widget

import android.animation.ArgbEvaluator
import android.animation.ValueAnimator
import android.content.Context
import android.content.res.TypedArray
import android.graphics.drawable.Drawable
import android.graphics.drawable.TransitionDrawable
import android.util.AttributeSet
import android.view.Gravity
import android.view.MotionEvent
import androidx.core.content.ContextCompat
import com.paulbaker.library.R

abstract class PBButtonBase : androidx.appcompat.widget.AppCompatButton {
    companion object {
        private const val ANIMATE_DURATION = 200
    }

    constructor(context: Context) : this(context, null)
    constructor(context: Context, attrs: AttributeSet?) : this(context, attrs, 0)
    constructor(context: Context, attrs: AttributeSet?, defStyleAttr: Int) : super(
        context,
        attrs,
        defStyleAttr
    ) {
        initViews(context, attrs)
    }

    private var mDrawableEnableState: Drawable? =
        ContextCompat.getDrawable(context, initDrawableStateEnabled())
    private var mDrawableDisableState: Drawable? =
        ContextCompat.getDrawable(context, initDrawableStateDisabled())
    private var mDrawablePressState: Drawable? =
        ContextCompat.getDrawable(context, initDrawableStatePressed())

    private var disableToEnableTransition =
        TransitionDrawable(arrayOf(mDrawableDisableState, mDrawableEnableState))
    private var enableToPressTransition =
        TransitionDrawable(arrayOf(mDrawableEnableState, mDrawablePressState))

    private var lastState: Boolean? = null
    private var inClickSession: Boolean = false

    private var withAnimation = true

    private var clickListener: OnClickListener? = null

    private var mTextColorEnabled: Int = ContextCompat.getColor(context, R.color.black_color)
    private var mTextColorDisabled: Int =
        ContextCompat.getColor(context, R.color.pb_button_disabled_text_color)


    private fun initViews(context: Context, attrs: AttributeSet?) {
        val typedArray = context.theme.obtainStyledAttributes(attrs, R.styleable.PBButton, 0, 0)
        mTextColorEnabled = ContextCompat.getColor(context, initTextColorEnabled())
        mTextColorDisabled = ContextCompat.getColor(context, initTextColorDisable())
        getValueAttributes(typedArray)
        gravity = Gravity.CENTER
        lastState = isEnabled

        var textColor = mTextColorEnabled
        if (!isEnabled)
            textColor = mTextColorDisabled
        setTextColor(textColor)

        initTransition()
    }

    fun initTransition() {
        background = if (isEnabled) mDrawableEnableState
        else mDrawableDisableState
        disableToEnableTransition =
            TransitionDrawable(arrayOf(mDrawableDisableState, mDrawableEnableState))
        enableToPressTransition =
            TransitionDrawable(arrayOf(mDrawableEnableState, mDrawablePressState))
    }

    private fun getValueAttributes(typedArray: TypedArray?) {
        if (typedArray != null) {
            if (typedArray.hasValue(R.styleable.PBButton_pbb_bg_enable)) {
                mDrawableEnableState = typedArray.getDrawable(R.styleable.PBButton_pbb_bg_enable)
            }
            if (typedArray.hasValue(R.styleable.PBButton_pbb_bg_disable)) {
                mDrawableDisableState = typedArray.getDrawable(R.styleable.PBButton_pbb_bg_disable)
            }
            if (typedArray.hasValue(R.styleable.PBButton_pbb_bg_press)) {
                mDrawablePressState = typedArray.getDrawable(R.styleable.PBButton_pbb_bg_press)
            }
            if (typedArray.hasValue(R.styleable.PBButton_pbb_bg_enable)
                && !typedArray.hasValue(R.styleable.PBButton_pbb_bg_press)
            ) {
                mDrawablePressState = typedArray.getDrawable(R.styleable.PBButton_pbb_bg_enable)
            }
            if (!typedArray.hasValue(R.styleable.PBButton_pbb_bg_enable)
                && typedArray.hasValue(R.styleable.PBButton_pbb_bg_press)
            ) {
                mDrawableEnableState = typedArray.getDrawable(R.styleable.PBButton_pbb_bg_enable)
            }
            if (typedArray.hasValue(R.styleable.PBButton_pbb_text_color_enable)) {
                mTextColorEnabled = typedArray.getColor(
                    R.styleable.PBButton_pbb_text_color_enable,
                    ContextCompat.getColor(context, initTextColorEnabled())
                )
            }
            if (typedArray.hasValue(R.styleable.PBButton_pbb_text_color_disable)) {
                mTextColorEnabled = typedArray.getColor(
                    R.styleable.PBButton_pbb_text_color_disable,
                    ContextCompat.getColor(context, initTextColorDisable())
                )
            }
        }
    }

    abstract fun initDrawableStateEnabled(): Int
    abstract fun initDrawableStatePressed(): Int
    abstract fun initTextColorEnabled(): Int

    fun initTextColorDisable(): Int = R.color.pb_button_disabled_text_color

    fun initDrawableStateDisabled(): Int = R.drawable.pbbutton_disabled_default

    override fun setEnabled(enabled: Boolean) {
        if (getWithAnimation()) {
            super.setEnabled(enabled)
            if (lastState != null && lastState != enabled) {
                if (enabled) {
                    setTextColorWithAnimation(mTextColorDisabled, mTextColorEnabled)
                    background = disableToEnableTransition
                    disableToEnableTransition.startTransition(ANIMATE_DURATION)
                } else {
                    setTextColorWithAnimation(mTextColorEnabled, mTextColorDisabled)
                    background = disableToEnableTransition
                    disableToEnableTransition.startTransition(0)
                    disableToEnableTransition.reverseTransition(ANIMATE_DURATION)
                }
                lastState = enabled
            }
        } else {
            super.setEnabled(enabled)
            if (lastState != null && lastState != enabled) {
                background = if (enabled) {
                    setTextColor(mTextColorEnabled)
                    mDrawableEnableState
                } else {
                    setTextColor(mTextColorDisabled)
                    mDrawableDisableState
                }
                lastState = enabled
            }
        }
    }

    private fun setTextColorWithAnimation(fromColor: Int, toColor: Int) {
        val colorAnimation = ValueAnimator.ofObject(ArgbEvaluator(), fromColor, toColor)
        colorAnimation.duration = ANIMATE_DURATION.toLong()
        colorAnimation.addUpdateListener {
            setTextColor(it.animatedValue as Int)
        }
        colorAnimation.start()
    }

    override fun onTouchEvent(event: MotionEvent?): Boolean {
        if (!isEnabled) return false
        if (!inClickSession) {
            background = enableToPressTransition
        }
        when (event?.action) {
            MotionEvent.ACTION_DOWN -> {
                inClickSession = true
                enableToPressTransition.startTransition(ANIMATE_DURATION)
            }
            MotionEvent.ACTION_UP, MotionEvent.ACTION_CANCEL -> {
                inClickSession = false
                enableToPressTransition.reverseTransition(ANIMATE_DURATION)
                if (event.action != MotionEvent.ACTION_CANCEL) {
                    clickListener?.onClick(this)
                }
            }
        }
        return true
    }

    override fun setOnClickListener(listener: OnClickListener?) {
        clickListener = listener
    }

    fun setWithAnimation(withAnimation: Boolean) {
        this.withAnimation = withAnimation
    }

    private fun getWithAnimation(): Boolean = this.withAnimation

}