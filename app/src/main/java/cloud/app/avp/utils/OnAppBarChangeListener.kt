package cloud.app.avp.utils

import com.google.android.material.appbar.AppBarLayout

fun AppBarLayout.onAppBarChangeListener(block: (offset: Float) -> Unit) {
    addOnOffsetChangedListener { appBarLayout, verticalOffset ->
        val offset = -verticalOffset / appBarLayout.totalScrollRange.toFloat()
        tryWith { block(offset) }
    }
}
