package com.demo.carspends.utils.ui.tipShower

import android.app.Activity
import android.util.Log
import android.view.MotionEvent
import android.widget.Button
import com.demo.carspends.R
import com.github.amlcurran.showcaseview.OnShowcaseEventListener
import com.github.amlcurran.showcaseview.ShowcaseView
import com.github.amlcurran.showcaseview.targets.ViewTarget

class TipShower(private val activity: Activity) {
    fun showTip(tipModel: TipModel, onCloseListener: () -> Unit = {}) {
        ShowcaseView.Builder(activity)
            .withNewStyleShowcase()
            .blockAllTouches()
            .setTarget(ViewTarget(tipModel.resId, activity))
            .setContentText(tipModel.description)
            .setStyle(R.style.CustomShowcaseTheme)
            .replaceEndButton(Button.inflate(activity, R.layout.button_tip, null) as Button)
            .setShowcaseEventListener(object : OnShowcaseEventListener {
                override fun onShowcaseViewHide(showcaseView: ShowcaseView?) {
                    onCloseListener()
                }
                override fun onShowcaseViewDidHide(showcaseView: ShowcaseView?) {}
                override fun onShowcaseViewShow(showcaseView: ShowcaseView?) {}
                override fun onShowcaseViewTouchBlocked(motionEvent: MotionEvent?) {}
            })
            .build()
            .apply { tipModel.title?.let { setContentTitle(it) } }
            .show()
    }
}
