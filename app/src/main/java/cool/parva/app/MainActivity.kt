package cool.parva.app

import android.annotation.SuppressLint
import android.os.Bundle
import android.widget.TextView
import androidx.appcompat.app.AppCompatActivity
import cool.parva.collapseview.CollapseView

class MainActivity : AppCompatActivity() {

    private val tvBtn: TextView by lazy { findViewById(R.id.tv_btn) }
    private val collapseView: CollapseView by lazy { findViewById(R.id.collapse_view) }

    @SuppressLint("SetTextI18n")
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)

        tvBtn.setOnClickListener { collapseView.collapse() }

        collapseView.setOnCollapseListener { isOpen ->
            tvBtn.text = if (isOpen) "close it" else "open it"
        }
    }
}