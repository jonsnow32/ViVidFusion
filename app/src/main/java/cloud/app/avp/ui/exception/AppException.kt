package cloud.app.avp.ui.exception

import cloud.app.common.clients.Extension
import cloud.app.common.helpers.ClientException

sealed class AppException(
    override val cause: Throwable
) : Exception() {

    abstract val extension: Extension<*>

    open class LoginRequired(
        override val cause: Throwable,
        override val extension: Extension<*>
    ) : AppException(cause)

    data class Unauthorized(
        override val cause: Throwable,
        override val extension: Extension<*>,
        val userId: String
    ) : LoginRequired(cause, extension)

    data class NotSupported(
        override val cause: Throwable,
        override val extension: Extension<*>,
        val operation: String
    ) : AppException(cause) {
        override val message: String
            get() = "$operation is not supported in ${extension.name}"
    }

    data class Other(
        override val cause: Throwable,
        override val extension: Extension<*>
    ) : AppException(cause) {
        override val message: String
            get() = "${cause.message} error in ${extension.name}"
    }

    companion object {
        fun Throwable.toAppException(extension: Extension<*>): AppException = when (this) {
            is ClientException.Unauthorized -> Unauthorized(this, extension, userId)
            is ClientException.LoginRequired -> LoginRequired(this, extension)
            is ClientException.NotSupported -> NotSupported(this, extension, operation)
            else -> Other(this, extension)
        }
    }
}
