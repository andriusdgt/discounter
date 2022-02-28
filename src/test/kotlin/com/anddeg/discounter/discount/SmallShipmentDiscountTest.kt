package com.anddeg.discounter.discount

import com.anddeg.discounter.order.InvalidOrder
import com.anddeg.discounter.order.Order
import com.anddeg.discounter.shipment.CarrierCode
import com.anddeg.discounter.shipment.ShipmentCatalogueSingleton
import com.anddeg.discounter.shipment.ShipmentSize
import com.anddeg.discounter.transaction.Transaction
import org.junit.jupiter.api.AfterEach
import org.junit.jupiter.api.Assertions.assertEquals
import org.junit.jupiter.api.BeforeEach
import org.junit.jupiter.api.Test
import java.math.BigInteger
import java.time.LocalDate

class SmallShipmentDiscountTest {

    private val smallOrder = Order(LocalDate.of(2021, 9, 25), ShipmentSize.S, CarrierCode.MR)

    @BeforeEach
    fun setUp(){
        ShipmentCatalogueSingleton.load("src/test/resources/shipmentCatalogue.txt")
    }

    @AfterEach
    fun tearDown(){
        ShipmentCatalogueSingleton.loadDefault()
    }

    @Test
    fun `Small shipments are discounted against cheapest provider (of price 1,11 eur)`() {
        val transactions = listOf(
            Transaction(InvalidOrder(), BigInteger.valueOf(0)),
            Transaction(smallOrder, BigInteger.valueOf(200)),
            Transaction(smallOrder, BigInteger.valueOf(450))
        )

        val discountedTransactions = SmallShipmentDiscountDecorator().discount(transactions)

        assertEquals(
            listOf(
                Transaction(InvalidOrder(), BigInteger.valueOf(0), BigInteger.valueOf(0)),
                Transaction(smallOrder, BigInteger.valueOf(200), BigInteger.valueOf(89)),
                Transaction(smallOrder, BigInteger.valueOf(450), BigInteger.valueOf(339))
            ),
            discountedTransactions
        )
    }

    @Test
    fun `Discounts against cheapest provider threshold`() {
        val transactions = listOf(
            Transaction(smallOrder, BigInteger.valueOf(2)),
            Transaction(smallOrder, BigInteger.valueOf(111))
        )

        val discountedTransactions = SmallShipmentDiscountDecorator().discount(transactions)

        assertEquals(
            listOf(
                Transaction(smallOrder, BigInteger.valueOf(2), BigInteger.valueOf(0)),
                Transaction(smallOrder, BigInteger.valueOf(111), BigInteger.valueOf(0))
            ),
            discountedTransactions
        )
    }

}
