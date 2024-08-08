package cloud.app.avp.ui.paging

import androidx.paging.Pager
import androidx.paging.PagingConfig
import androidx.paging.PagingSource

abstract class ErrorPagingSource<Key : Any, Value : Any> : PagingSource<Key, Value>() {
    fun toFlow() = Pager(
        config = config,
        pagingSourceFactory = { this }
    ).flow

    abstract val config: PagingConfig

    override suspend fun load(params: LoadParams<Key>): LoadResult<Key, Value> {
        return try {
            loadData(params)
        } catch (e: Throwable) {
            LoadResult.Error(e)
        }
    }

    abstract suspend fun loadData(params: LoadParams<Key>): LoadResult.Page<Key, Value>
}
