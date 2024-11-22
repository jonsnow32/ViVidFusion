package cloud.app.vvf.utils

import android.os.Build
import android.os.Bundle
import java.io.Serializable

@Suppress("DEPRECATION")
inline fun <reified T> Bundle.getParcel(key: String?) =
    if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.TIRAMISU)
        getParcelable(key, T::class.java)
    else getParcelable(key)

@Suppress("DEPRECATION")
inline fun <reified T> Bundle.getParcelArray(key: String?) =
    if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.TIRAMISU)
        getParcelableArray(key, T::class.java)?.toList()
    else getParcelableArray(key)?.map { it as T }

@Suppress("DEPRECATION")
inline fun <reified T : Serializable> Bundle.getSerial(key: String?) =
    if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.TIRAMISU)
        getSerializable(key, T::class.java)
    else getSerializable(key) as T
