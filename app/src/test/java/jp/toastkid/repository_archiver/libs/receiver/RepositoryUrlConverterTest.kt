package jp.toastkid.repository_archiver.libs.receiver

import org.junit.Assert.assertEquals
import org.junit.Test

/**
 * @author toastkidjp
 */
class RepositoryUrlConverterTest {

    @Test
    fun test() {
        assertEquals(
            "https://github.com/toastkidjp/Yobidashi_kt/archive/master.zip",
            RepositoryUrlConverter().toZipUrl("https://github.com/toastkidjp/Yobidashi_kt")
        )
        assertEquals(
            "https://github.com/toastkidjp/Yobidashi_kt/archive/master.zip",
            RepositoryUrlConverter().toZipUrl("https://github.com/toastkidjp/Yobidashi_kt/blob/master/app/src/main/java/jp/toastkid/yobidashi/main/MainActivity.kt")
        )
        assertEquals(
            "https://github.com/toastkidjp/Yobidashi_kt/archive/develop.zip",
            RepositoryUrlConverter().toZipUrl("https://github.com/toastkidjp/Yobidashi_kt/blob/develop/app/src/main/java/jp/toastkid/yobidashi/main/MainActivity.kt")
        )
    }
}