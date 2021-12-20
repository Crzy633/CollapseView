package cool.parva.collapseview

import android.animation.ValueAnimator
import android.content.Context
import android.graphics.drawable.Drawable
import android.util.AttributeSet
import android.view.View
import android.view.ViewGroup
import android.view.animation.AccelerateDecelerateInterpolator
import android.widget.LinearLayout
import androidx.core.animation.doOnEnd

/**
* @author Parva
* @since 2021/11/30
*/
class CollapseView : LinearLayout {

    private var init = true
    private var open = false

    var title: String = "Title"
    var icon: Drawable? = null
    var duration = 500

    private lateinit var head: CollapseViewHead
    private lateinit var body: View

    private var animator: ValueAnimator? = null
    private var value: Int = 0

    private var onCollapseListener: OnCollapseListener? = null

    constructor(context: Context) : super(context)

    constructor(context: Context, attrs: AttributeSet?) : super(context, attrs) {
        attrs?.let { loadAttribute(context, attrs) }
    }

    constructor(context: Context, attrs: AttributeSet?, defStyleAttr: Int)
            : super(context, attrs, defStyleAttr) {
        attrs?.let { loadAttribute(context, attrs) }
    }

    private fun loadAttribute(context: Context, attrs: AttributeSet) {
        val typedArray = context.obtainStyledAttributes(attrs, R.styleable.CollapseView)
        typedArray.getString(R.styleable.CollapseView_collapse_view_title)?.let { title = it }
        open = typedArray.getBoolean(R.styleable.CollapseView_collapse_view_open, open)
        icon = typedArray.getDrawable(R.styleable.CollapseView_collapse_view_icon)
        duration = typedArray.getInt(R.styleable.CollapseView_collapse_view_duration, duration)
        typedArray.recycle()
    }

    init { orientation = VERTICAL }


    override fun onAttachedToWindow() {
        super.onAttachedToWindow()
        if (init) {
            init = false
            for (i in 0 until childCount) {
                if (getChildAt(i) is CollapseViewHead) break
                if (i == childCount - 1) addView(CollapseViewHead(context), 0)
            }
            checkStructure()
            head.setOnClickListener { collapse() }
        }
    }

    override fun onMeasure(widthMeasureSpec: Int, heightMeasureSpec: Int) {
        super.onMeasure(widthMeasureSpec, heightMeasureSpec)
        if (animator == null) {
            if (open) {
                value = head.measuredHeight + body.measuredHeight
                head.collapse(false, 0)
            } else {
                value = head.measuredHeight
                setMeasuredDimension(head.measuredWidth, head.measuredHeight)
            }
        }
    }

    fun isOpen() = open

    /**
     * open or close it
     */
    fun collapse() {
        checkStructure()
        head.collapse(open)
        collapse(open, duration.toLong())
        open = !open
    }

    /**
     * CollapseView should have either a head view and a body view or just a body view
     */
    private fun checkStructure() {
        val msg = "CollapseView should have either a head view and a body view or just a body view"
        if (childCount != 2) throw Exception(msg)
        else {
            val firstChild = getChildAt(0)
            val secondChild = getChildAt(1)
            if (firstChild is CollapseViewHead && secondChild !is CollapseViewHead) {
                head = firstChild
                body = secondChild
            } else if (firstChild !is CollapseViewHead && secondChild is CollapseViewHead) {
                head = secondChild
                body = firstChild
            } else {
                throw throw Exception(msg)
            }
        }
    }

    /**
     * collapse body view
     */
    private fun collapse(isOpen: Boolean, durationTime: Long = 0) {
        val to =  head.height + if(isOpen) 0 else {
            body.measure(MeasureSpec.UNSPECIFIED, MeasureSpec.UNSPECIFIED)
            body.height.coerceAtLeast(body.measuredHeight)
        }
        animator?.cancel()
        animator = ValueAnimator.ofInt(value, to).apply {
            interpolator = AccelerateDecelerateInterpolator()
            duration = durationTime
            addUpdateListener { valueAnimator ->
                value = valueAnimator.animatedValue as Int
                var lp = layoutParams
                if (lp == null) lp = ViewGroup.LayoutParams(LayoutParams.WRAP_CONTENT, value)
                else lp.height = value
                layoutParams = lp
            }
            doOnEnd { animator = null }
        }
        animator?.start()
        onCollapseListener?.onCollapse(!isOpen)
    }

    /**
     * setListener
     */
    fun setOnCollapseListener(listener: OnCollapseListener) {
        onCollapseListener = listener
    }
}