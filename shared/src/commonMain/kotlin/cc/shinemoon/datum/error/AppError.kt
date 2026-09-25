package cc.shinemoon.datum.error

sealed class AppError(
    open val message: String,
    open val cause: Throwable? = null
) {
    data class FileNotFound(val path: String, override val cause: Throwable? = null) :
        AppError("File not found: $path", cause)

    data class FileUnreadable(val path: String, override val cause: Throwable? = null) :
        AppError("Cannot read file: $path. Check file permissions.", cause)

    data class UnsupportedFileType(val extension: String, override val cause: Throwable? = null) :
        AppError("Unsupported file type \".$extension\". Only .step and .stp files are supported.", cause)

    data class InvalidStepFile(val details: String, override val cause: Throwable? = null) :
        AppError("Datum could not read this STEP file: $details", cause)

    data class NativeLibraryUnavailable(override val message: String = "Native OCCT library is unavailable.", override val cause: Throwable? = null) :
        AppError(message, cause)

    data class InspectionFailed(override val message: String, override val cause: Throwable? = null) :
        AppError(message, cause)

    data class DatabaseError(override val message: String, override val cause: Throwable? = null) :
        AppError("Database error: $message", cause)

    data class Unexpected(override val message: String = "An unexpected error occurred.", override val cause: Throwable? = null) :
        AppError(message, cause)

    companion object {
        fun fromThrowable(throwable: Throwable): AppError {
            val message = throwable.message.orEmpty()
            return when {
                throwable is UnsatisfiedLinkError -> NativeLibraryUnavailable(cause = throwable)
                message.contains("STEP file does not exist", ignoreCase = true) -> FileNotFound(message, throwable)
                message.contains("Failed to load STEP file", ignoreCase = true) -> InvalidStepFile(message, throwable)
                message.contains("transferable shape", ignoreCase = true) -> InvalidStepFile("STEP file did not produce a transferable shape", throwable)
                else -> Unexpected(message.ifBlank { "An unexpected error occurred." }, throwable)
            }
        }
    }
}
