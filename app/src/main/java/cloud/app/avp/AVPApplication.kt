package cloud.app.avp

import android.app.Application
import android.content.Context
import android.content.Intent
import cloud.app.avp.plugin.tmdb.TmdbExtension
import cloud.app.avp.utils.catchWith
import cloud.app.avp.utils.tryWith
import cloud.app.avp.viewmodels.SnackBarViewModel
import cloud.app.common.clients.BaseExtension
import cloud.app.plugger.RepoComposer
import dagger.hilt.android.HiltAndroidApp
import kotlinx.coroutines.CoroutineName
import kotlinx.coroutines.MainScope
import kotlinx.coroutines.flow.MutableSharedFlow
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.map
import kotlinx.coroutines.launch
import kotlinx.coroutines.plus
import javax.inject.Inject


@HiltAndroidApp
class AVPApplication : Application() {
  @Inject
  lateinit var throwableFlow: MutableSharedFlow<Throwable>

  @Inject
  lateinit var extensionFlow: MutableStateFlow<BaseExtension?>

  @Inject
  lateinit var extensionRepo: RepoComposer<BaseExtension>

  private val scope = MainScope() + CoroutineName("Application")

  override fun onCreate() {
    super.onCreate()

    Thread.setDefaultUncaughtExceptionHandler { _, exception ->
      exception.printStackTrace()
      ExceptionActivity.start(this, exception)
      Runtime.getRuntime().exit(0)
    }

    scope.launch {
      throwableFlow.collect {
        it.printStackTrace()
      }
    }

    scope.launch {
      extensionRepo.load().catchWith(throwableFlow).map { clients ->
        //clients.forEach { it.setSettings() }
        clients
      }.collect {
        it.forEach { client ->
          tryWith(throwableFlow) {
            if(client is TmdbExtension) {
              extensionFlow.emit(client)
            }
            //client.onExtensionSelected()
          }
        }
      }
    }
  }

  companion object {
    fun Context.restartApp() {
      val mainIntent = Intent.makeRestartActivityTask(
        packageManager.getLaunchIntentForPackage(packageName)!!.component
      )
      startActivity(mainIntent)
      Runtime.getRuntime().exit(0)
    }
    fun Context.noClient() = SnackBarViewModel.Message(
      getString(R.string.error_no_client)
    )
  }
}