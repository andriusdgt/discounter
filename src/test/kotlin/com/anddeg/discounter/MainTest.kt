package com.anddeg.discounter

import org.junit.jupiter.api.Test
import org.junit.jupiter.api.extension.ExtendWith
import org.mockito.Mock
import org.mockito.junit.jupiter.MockitoExtension
import org.mockito.kotlin.verify
import java.io.File

@ExtendWith(MockitoExtension::class)
class MainTest {

    @Mock
    private lateinit var outputWriter: OutputWriter

    @Test
    fun `Integration test checks if order input outputs discounted transactions`() {
        val expectedFile = File("src/test/resources/expectedTransactions.txt")

        Main(outputWriter).execute("src/test/resources/manyOrders.txt")

        val expectedOutput = expectedFile.readLines().reduce { acc, string -> acc + "\n" + string } + "\n"
        verify(outputWriter).write(expectedOutput)
    }

}
