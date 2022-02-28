package com.anddeg.discounter.shipment

import java.io.File
import java.math.BigInteger

object ShipmentCatalogueSingleton {

    private const val SHIPMENT_CATALOGUE_PATH = "src/main/resources/shipmentCatalogue.txt"

    var value: List<Shipment> = loadShipmentCatalogue(SHIPMENT_CATALOGUE_PATH)
        private set

    fun load(filePath: String) {
        value = loadShipmentCatalogue(filePath)
    }

    fun loadDefault() {
        value = loadShipmentCatalogue(SHIPMENT_CATALOGUE_PATH)
    }

    private fun loadShipmentCatalogue(filePath: String) =
        File(filePath)
            .readLines()
            .map { it.split(" ").run { this.toShipment() } }

    private fun List<String>.toShipment() =
        Shipment(
            ShipmentSize.valueOf(this[0]),
            CarrierCode.valueOf(this[1]),
            BigInteger.valueOf(this[2].toLong())
        )

}
