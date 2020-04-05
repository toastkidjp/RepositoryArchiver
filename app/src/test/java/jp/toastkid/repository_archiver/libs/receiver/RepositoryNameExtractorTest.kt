package jp.toastkid.repository_archiver.libs.receiver

import org.junit.Assert.assertEquals
import org.junit.Assert.assertNull
import org.junit.Before
import org.junit.Test

/**
 * @author toastkidjp
 */
class RepositoryNameExtractorTest {

    private lateinit var repositoryNameExtractor: RepositoryNameExtractor

    @Before
    fun setUp() {
        repositoryNameExtractor = RepositoryNameExtractor()
    }

    @Test
    fun test() {
        assertNull(
            repositoryNameExtractor(null)
        )
        assertNull(
            repositoryNameExtractor("")
        )
        assertEquals(
            "",
            repositoryNameExtractor("https://github.com/toastkidjp/")
        )
        assertEquals(
            "Yobidashi_kt",
            repositoryNameExtractor("https://github.com/toastkidjp/Yobidashi_kt")
        )
        assertEquals(
            "Yobidashi_kt",
            repositoryNameExtractor("https://github.com/toastkidjp/Yobidashi_kt/blob/master/app/src/main/java/jp/toastkid/yobidashi/main/MainActivity.kt")
        )
    }
}