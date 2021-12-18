package cool.parva.collapseview

import android.animation.ValueAnimator
import android.content.Context
import android.util.AttributeSet
import android.view.LayoutInflater
import android.view.View
import android.widget.FrameLayout
import android.widget.TextView
import androidx.core.content.res.ResourcesCompat

/**
 * @author Parva
 * @since 2021/11/30
 */
class CollapseViewHead : FrameLayout {

    private var iconToggle: View? = null
    private var animator: ValueAnimator? = null
    private var rotationDegree = 0f


    constructor(context: Context) : super(context)
    constructor(context: Context, attrs: AttributeSet?) : super(context, attrs)
    constructor(context: Context, attrs: AttributeSet?, defStyleAttr: Int)
            : super(context, attrs, defStyleAttr)


    override fun onAttachedToWindow() {
        super.onAttachedToWindow()
        if (childCount == 0) createDefaultHead()
    }

    private fun createDefaultHead() {
        val headView = LayoutInflater.from(context)
            .inflate(R.layout.layout_default_head, this, false)
        headView.layoutParams = LayoutParams(LayoutParams.MATCH_PARENT,
            LayoutParams.WRAP_CONTENT)
        headView.findViewById<TextView>(R.id.tv_title).text = parent().title
        iconToggle = headView.findViewById(R.id.icon_toggle)
        iconToggle?.background = parent().icon ?:
            ResourcesCompat.getDrawable(resources, R.drawable.ic_toggle, null)
        addView(headView)
    }

    /**
     * play rotate animation
     */
    internal fun collapse(isOpen: Boolean, durationTime: Long = -1) {
        iconToggle?.let { view ->
            animator?.cancel()
            val to = if (isOpen) 0f else 90f
            animator = ValueAnimator.ofFloat(rotationDegree, to).apply {
                duration = if (durationTime >= 0) durationTime else parent().duration.toLong() / 2
                addUpdateListener {
                    rotationDegree = it.animatedValue as Float
                    view.rotation = rotationDegree
                }
                start()
            }
        }
    }

    private fun parent() : CollapseView {
        if (parent !is CollapseView) {
            throw Exception("CollapseViewHead's parent should be CollapseView")
        }
        return parent as CollapseView
    }

}