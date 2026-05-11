package yuteledez

actual fun getCurrentTimeMillis(): Long = System.currentTimeMillis()

actual val isWearOS: Boolean by lazy {
    android.os.Build.MODEL.contains("Watch", ignoreCase = true) ||
    android.os.Build.PRODUCT.contains("Watch", ignoreCase = true) ||
    android.os.Build.HARDWARE.contains("watch", ignoreCase = true)
}
