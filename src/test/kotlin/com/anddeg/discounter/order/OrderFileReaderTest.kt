package com.anddeg.discounter.order

import com.anddeg.discounter.shipment.CarrierCode
import com.anddeg.discounter.shipment.ShipmentSize
import org.junit.jupiter.api.Assertions.assertEquals
import org.junit.jupiter.api.Assertions.assertThrows
import org.junit.jupiter.api.BeforeEach
import org.junit.jupiter.api.Test
import org.junit.jupiter.api.extension.ExtendWith
import org.mockito.Mock
import org.mockito.junit.jupiter.MockitoExtension
import org.mockito.kotlin.doReturn
import java.io.File
import java.time.LocalDate

@ExtendWith(MockitoExtension::class)
class OrderFileReaderTest {

    @Mock
    private lateinit var orderFactoryStub: OrderFactory

    private var reader: OrderFileReader = OrderFileReader()

    @BeforeEach
    fun setUp() {
        reader = OrderFileReader(orderFactoryStub)
    }

    @Test
    fun `Incorrect order input path produces an exception`() {
        assertThrows(OrderFileNotFoundException::class.java) {
            reader.createOrders("missingFile.txt")
        }
    }

    @Test
    fun `Order file is processed`() {
        val expectedOrders = listOf(Order(LocalDate.parse("2020-12-12"), ShipmentSize.S, CarrierCode.MR))
        val orderString = File("src/test/resources/minimalOrder.txt").readLines()
        doReturn(expectedOrders).`when`(orderFactoryStub).create(orderString)

        val actualOrders: List<Orderable> = reader.createOrders("src/test/resources/minimalOrder.txt")

        assertEquals(expectedOrders, actualOrders)
    }

}
