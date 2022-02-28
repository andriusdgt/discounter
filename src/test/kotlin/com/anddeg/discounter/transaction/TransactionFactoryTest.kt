package com.anddeg.discounter.transaction

import com.anddeg.discounter.order.InvalidOrder
import com.anddeg.discounter.order.Order
import com.anddeg.discounter.shipment.CarrierCode
import com.anddeg.discounter.shipment.ShipmentCatalogueSingleton
import com.anddeg.discounter.shipment.ShipmentSize
import org.junit.jupiter.api.AfterEach
import org.junit.jupiter.api.Assertions.assertEquals
import org.junit.jupiter.api.BeforeEach
import org.junit.jupiter.api.Test
import java.math.BigInteger
import java.time.LocalDate

class TransactionFactoryTest {

    private val sampleDate = LocalDate.of(2021, 9, 25)
    private val smallLPOrder = Order(sampleDate, ShipmentSize.S, CarrierCode.LP)
    private val mediumLPOrder = Order(sampleDate, ShipmentSize.M, CarrierCode.LP)
    private val largeLPOrder = Order(sampleDate, ShipmentSize.L, CarrierCode.LP)
    private val smallMROrder = Order(sampleDate, ShipmentSize.S, CarrierCode.MR)
    private val mediumMROrder = Order(sampleDate, ShipmentSize.M, CarrierCode.MR)
    private val largeMROrder = Order(sampleDate, ShipmentSize.L, CarrierCode.MR)

    @BeforeEach
    fun setUp(){
        ShipmentCatalogueSingleton.load("src/test/resources/shipmentCatalogue.txt")
    }

    @AfterEach
    fun tearDown(){
        ShipmentCatalogueSingleton.loadDefault()
    }

    @Test
    fun `No orders produce no transactions`() {
        val transactions = TransactionFactory().create(listOf())

        assertEquals(emptyList<Transaction>(), transactions)
    }

    @Test
    fun `Converts all kinds of orders`() {
        val expectedTransactions = listOf(
            Transaction(smallLPOrder, BigInteger.valueOf(111)),
            Transaction(mediumLPOrder, BigInteger.valueOf(490)),
            Transaction(largeLPOrder, BigInteger.valueOf(680)),
            Transaction(smallMROrder, BigInteger.valueOf(222)),
            Transaction(mediumMROrder, BigInteger.valueOf(333)),
            Transaction(largeMROrder, BigInteger.valueOf(666))
        )

        val actualTransactions =
            TransactionFactory().create(
                listOf(smallLPOrder, mediumLPOrder, largeLPOrder, smallMROrder, mediumMROrder, largeMROrder)
            )

        assertEquals(expectedTransactions, actualTransactions)
    }

    @Test
    fun `Handles duplicate orders`() {
        val expectedTransactions = listOf(
            Transaction(smallLPOrder, BigInteger.valueOf(111)),
            Transaction(smallLPOrder, BigInteger.valueOf(111))
        )

        val actualTransactions = TransactionFactory().create(listOf(smallLPOrder, smallLPOrder))

        assertEquals(expectedTransactions, actualTransactions)
    }

    @Test
    fun `Handles invalid orders`() {
        val invalidOrder = InvalidOrder("invalidInput")
        val expectedTransactions = listOf(
            Transaction(invalidOrder, BigInteger.valueOf(0)),
            Transaction(smallLPOrder, BigInteger.valueOf(111))
        )

        val actualTransactions = TransactionFactory().create(listOf(invalidOrder, smallLPOrder))

        assertEquals(expectedTransactions, actualTransactions)
    }

}
