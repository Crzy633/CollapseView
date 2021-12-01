package cool.parva.app

import android.annotation.SuppressLint
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.widget.TextView
import cool.parva.collapseview.CollapseView

class MainActivity : AppCompatActivity() {

    private val tvBtn: TextView by lazy { findViewById(R.id.tv_btn) }
    private val collapseView: CollapseView by lazy { findViewById(R.id.collapse_view) }

    @SuppressLint("SetTextI18n")
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)
        tvBtn.setOnClickListener {
            collapseView.collapse()
            if (collapseView.isOpen()) {
                tvBtn.text = "close it"
            } else {
                tvBtn.text = "open it"
            }
        }
    }
}