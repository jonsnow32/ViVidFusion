package cloud.app.vvf.utils

import android.view.View
import android.view.ViewPropertyAnimator
import androidx.core.view.doOnPreDraw
import androidx.core.view.forEach
import androidx.core.view.forEachIndexed
import androidx.core.view.isVisible
import androidx.fragment.app.Fragment
import androidx.interpolator.view.animation.FastOutSlowInInterpolator
import androidx.preference.PreferenceManager.getDefaultSharedPreferences
import com.google.android.material.R
import com.google.android.material.color.MaterialColors
import com.google.android.material.motion.MotionUtils
import com.google.android.material.navigation.NavigationBarView
import com.google.android.material.transition.MaterialArcMotion
import com.google.android.material.transition.MaterialContainerTransform
import com.google.android.material.transition.MaterialSharedAxis

fun startAnimation(
  view: View, animation: ViewPropertyAnimator, durationMultiplier: Float = 1f
) = view.run {
  clearAnimation()
  val interpolator = MotionUtils.resolveThemeInterpolator(
    context, R.attr.motionEasingStandardInterpolator, FastOutSlowInInterpolator()
  )
  val duration = animationDuration * durationMultiplier
  animation.setInterpolator(interpolator).setDuration(duration.toLong()).start()
}

fun NavigationBarView.animateTranslation(
  isRail: Boolean, isMainFragment: Boolean, visible: Boolean, action: () -> Unit
) {
  val value = if (isMainFragment && visible) 0f
  else if (isRail) -width.toFloat() else height.toFloat()
  if (animations) {
    isVisible = true
    var animation = if (isRail) animate().translationX(value)
    else animate().translationY(value)

    animation = if (isMainFragment) animation.withStartAction(action)
      .withEndAction { isVisible = true }
    else animation.withEndAction { action(); isVisible = false }

    startAnimation(this, animation)

    val menuValue =
      if (isMainFragment) 0f else if (isRail) -width.toFloat() else height.toFloat()
    menu.forEachIndexed { index, it ->
      val view = findViewById<View>(it.itemId)
      val dis = menuValue * (index + 1)
      if (isRail) startAnimation(view, view.animate().translationX(dis))
      else startAnimation(view, view.animate().translationY(dis))
    }
  } else {
    isVisible = isMainFragment
    menu.forEach {
      findViewById<View>(it.itemId).apply {
        translationX = 0f
        translationY = 0f
      }
    }
    action()
  }
}

fun View.animateVisibility(isVisible: Boolean) {
  if (animations) startAnimation(this,
    animate().alpha(if (isVisible) 1f else 0f)
      .withEndAction { alpha = if (isVisible) 1f else 0f })
  else alpha = if (isVisible) 1f else 0f
}

fun animateTranslation(view: View, old: Int, newHeight: Int) = view.run {
  if (view.animations) {
    clearAnimation()
    view.translationY = newHeight.toFloat() - old
    startAnimation(this, animate().translationY(0f))
  }
}

private val View.animationDuration: Long
  get() = context.applicationContext.run {
    MotionUtils.resolveThemeDuration(
      this, R.attr.motionDurationMedium1, 350
    ).toLong()
  }
private val View.animations
  get() = context.applicationContext.run {
    getDefaultSharedPreferences(context).getBoolean(getString(cloud.app.vvf.R.string.pref_animations_key), true)
  }

private val View.sharedElementTransitions
  get() = context.applicationContext.run {
    getDefaultSharedPreferences(context).getBoolean(getString(cloud.app.vvf.R.string.pref_shared_element_animations_key), true)
  }

fun Fragment.setupTransition(view: View) {
  val color = MaterialColors.getColor(view, cloud.app.vvf.R.attr.appBackground, 0)
  view.setBackgroundColor(color)
  if (view.animations) {
    val transitionName = arguments?.getString("transitionName")
    if (transitionName != null && view.sharedElementTransitions) {
      view.transitionName = transitionName
      val transition = MaterialContainerTransform().apply {
        drawingViewId = id
        setAllContainerColors(color)
        setPathMotion(MaterialArcMotion())
        duration = view.animationDuration
      }
      sharedElementEnterTransition = transition
    }

    exitTransition = MaterialSharedAxis(MaterialSharedAxis.Y, true)
    reenterTransition = MaterialSharedAxis(MaterialSharedAxis.Y, false)
    enterTransition = MaterialSharedAxis(MaterialSharedAxis.Y, true)
    returnTransition = MaterialSharedAxis(MaterialSharedAxis.Y, false)

    postponeEnterTransition()
    view.doOnPreDraw { startPostponedEnterTransition() }
  }
}