package bridge

import java.nio.file.Files
import java.nio.file.Path
import java.security.MessageDigest
import javax.inject.Inject
import javax.inject.Singleton

@Singleton
internal class OcctDllResolver @Inject constructor() {
    private val RESOURCE_PATH = "/native/libOcctStatic.dll"

    fun resolve(): Path {
        System.getProperty("occt.dll.path")?.let { return Path.of(it) }

        val devPath = Path.of("native/libOcctStatic.dll")
        if (Files.isRegularFile(devPath)) return devPath

        return extractFromResources()
    }

    private fun extractFromResources(): Path {
        val bytes = OcctDllResolver::class.java.getResourceAsStream(RESOURCE_PATH)
            ?.use { it.readBytes() }
            ?: error("DLL resource not found on classpath: $RESOURCE_PATH")

        val hash = MessageDigest.getInstance("SHA-256")
            .digest(bytes)
            .joinToString("") { "%02x".format(it) }
            .take(16)

        val cacheDir = Path.of(System.getProperty("java.io.tmpdir"), "occt-bridge-$hash")
        val cachedDll = cacheDir.resolve("libOcctStatic.dll")

        if (!Files.isRegularFile(cachedDll)) {
            Files.createDirectories(cacheDir)
            Files.write(cachedDll, bytes)
        }
        return cachedDll
    }
}
