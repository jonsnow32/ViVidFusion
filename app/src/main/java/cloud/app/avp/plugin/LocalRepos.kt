package cloud.app.avp.plugin

import cloud.app.avp.network.api.tmdb.AppTmdb
import cloud.app.avp.plugin.tmdb.TmdbExtension
import cloud.app.common.clients.BaseExtension
import cloud.app.plugger.PluginRepo
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow

class LocalRepos(val tmdb: AppTmdb): PluginRepo<BaseExtension> {
  override fun load(): StateFlow<List<BaseExtension>> = MutableStateFlow(listOf(LocalVideoExtension(), TmdbExtension(tmdb)))
}