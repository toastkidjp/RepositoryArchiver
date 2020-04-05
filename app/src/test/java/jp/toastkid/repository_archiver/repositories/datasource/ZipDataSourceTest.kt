package jp.toastkid.repository_archiver.repositories.datasource

import jp.toastkid.repository_archiver.repositories.datasource.zip.ZipDataSource
import org.junit.Assert.fail
import org.junit.Test

/**
 * @author toastkidjp
 */
class ZipDataSourceTest {

    @Test
    fun test() {
        val inputStream =
            javaClass.classLoader?.getResourceAsStream("datasource/test-master.zip") ?: return fail()
        ZipDataSource()
            .invoke("test", inputStream).forEach { println(it) }
    }
}