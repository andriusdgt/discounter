package com.anddeg.discounter.shipment

import org.junit.jupiter.api.AfterEach
import org.junit.jupiter.api.Assertions.assertEquals
import org.junit.jupiter.api.Test
import java.math.BigInteger

class ShipmentCatalogueSingletonTest {

    @AfterEach
    fun tearDown(){
        ShipmentCatalogueSingleton.loadDefault()
    }

    @Test
    fun `Shipment catalogue is defined in resource file`() {
        val expected = listOf(
            Shipment(ShipmentSize.S, CarrierCode.LP, BigInteger.valueOf(111)),
            Shipment(ShipmentSize.M, CarrierCode.LP, BigInteger.valueOf(490)),
            Shipment(ShipmentSize.L, CarrierCode.LP, BigInteger.valueOf(680)),
            Shipment(ShipmentSize.S, CarrierCode.MR, BigInteger.valueOf(222)),
            Shipment(ShipmentSize.M, CarrierCode.MR, BigInteger.valueOf(333)),
            Shipment(ShipmentSize.L, CarrierCode.MR, BigInteger.valueOf(666))
        )

        ShipmentCatalogueSingleton.load("src/test/resources/shipmentCatalogue.txt")
        val actual = ShipmentCatalogueSingleton.value

        assertEquals(expected, actual)
    }

}
