package cool.parva.collapseview

import android.animation.ValueAnimator
import android.content.Context
import android.graphics.drawable.Drawable
import android.util.AttributeSet
import android.view.View
import android.view.ViewGroup
import android.view.animation.AccelerateDecelerateInterpolator
import android.widget.LinearLayout

/**
 * @author Parva
 * @since 2021/11/30
 */
class CollapseView : LinearLayout {

    private var open = false

    var title: String = "Title"
    var icon: Drawable? = null
    var duration = 500

    private lateinit var head: CollapseViewHead
    private lateinit var body: View

    private var animator: ValueAnimator? = null
    private var value: Int = 0

    constructor(context: Context) : super(context)

    constructor(context: Context, attrs: AttributeSet?) : super(context, attrs) {
        attrs?.let { init(context, attrs) }
    }

    constructor(context: Context, attrs: AttributeSet?, defStyleAttr: Int)
            : super(context, attrs, defStyleAttr) {
        attrs?.let { init(context, attrs) }
    }

    init {
        orientation = VERTICAL
        post {
            for (i in 0 until childCount) {
                if (getChildAt(i) is CollapseViewHead) break
                if (i == childCount - 1) addView(CollapseViewHead(context), 0)
            }
            checkStructure()
            head.setOnClickListener { collapse() }

            if (!open) {
                body.visibility = View.GONE
            }

            head.post {
                body.post {
                    head.post {
                        value = if (open) {
                            head.collapse(false, 0)
                            body.measure(MeasureSpec.UNSPECIFIED, MeasureSpec.UNSPECIFIED)
                            head.height + body.height.coerceAtLeast(body.measuredHeight)
                        } else {
                            head.height
                        }
                    }
                }
            }
        }
    }


    fun isOpen() = open


    /**
     * open it, or close
     */
    fun collapse() {
        checkStructure()
        body.visibility = VISIBLE
        head.collapse(open)
        collapse(open, duration.toLong())
        open = !open
    }


    /**
     * get xml attrs
     */
    private fun init(context: Context, attrs: AttributeSet) {
        val typedArray = context.obtainStyledAttributes(attrs, R.styleable.CollapseView)
        typedArray.getString(R.styleable.CollapseView_collapse_view_title)?.let { title = it }
        open = typedArray.getBoolean(R.styleable.CollapseView_collapse_view_open, open)
        icon = typedArray.getDrawable(R.styleable.CollapseView_collapse_view_icon)
        duration = typedArray.getInt(R.styleable.CollapseView_collapse_view_duration, duration)
        typedArray.recycle()
    }


    /**
     * like the msg say
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
                if (lp == null) lp = ViewGroup.LayoutParams(LayoutParams.MATCH_PARENT, value)
                else lp.height = value
                layoutParams = lp
            }
            this.start()
        }
    }
}