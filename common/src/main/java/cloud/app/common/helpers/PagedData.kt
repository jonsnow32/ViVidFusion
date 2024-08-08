package cloud.app.common.helpers

import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext

sealed class PagedData<T : Any> {
  abstract fun clear()
  abstract suspend fun loadFirst(): List<T>
  abstract suspend fun loadAll(): List<T>

  class Single<T : Any>(
    val load: suspend () -> List<T>
  ) : PagedData<T>() {
    private var loaded = false
    val items = mutableListOf<T>()
    suspend fun loadList(): List<T> {
      if (loaded) return items
      items.addAll(load())
      loaded = true
      return items
    }

    override fun clear() {
      println("Clearing items")
      items.clear()
      loaded = false
    }

    override suspend fun loadFirst() = loadList()
    override suspend fun loadAll() = loadList()
}

class Continuous<T : Any>(
  val load: suspend (String?) -> Page<T, String?>
) : PagedData<T>() {

  private val itemMap = mutableMapOf<String?, Page<T, String?>>()
  suspend fun loadList(continuation: String?): Page<T, String?> {
    val page = itemMap.getOrPut(continuation) {
      val (data, cont) = load(continuation)
      Page(data, cont)
    }
    return withContext(Dispatchers.IO) { page }
  }


  fun invalidate(continuation: String?) = itemMap.remove(continuation)
  override fun clear() = itemMap.clear()

  override suspend fun loadFirst() = loadList(null).data

  override suspend fun loadAll(): List<T> {
    val list = mutableListOf<T>()
    val init = loadList(null)
    list.addAll(init.data)
    var cont = init.continuation
    while (cont != null) {
      val page = load(cont)
      list.addAll(page.data)
      cont = page.continuation
    }
    return list
  }
}
}
