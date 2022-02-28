package com.anddeg.discounter.order

import com.anddeg.discounter.shipment.CarrierCode
import com.anddeg.discounter.shipment.ShipmentSize
import java.time.LocalDate
import java.time.format.DateTimeParseException

class OrderFactory {

    fun create(lines: List<String>): List<Orderable> =
        mutableListOf<Orderable>().apply {
            lines.forEach { line ->
                val tokens = line.split(" ")
                if (!isLineValid(line, tokens)) {
                    this.add(InvalidOrder(line))
                    return@forEach
                }
                this.add(
                    Order(
                        LocalDate.parse(tokens[0]),
                        ShipmentSize.valueOf(tokens[1]),
                        CarrierCode.valueOf(tokens[2])
                    )
                )
            }
        }

    private fun isLineValid(line: String, tokens: List<String>) =
        line.isNotEmpty() &&
            isDateFormatValid(tokens[0]) &&
            isShipmentSizeValid(tokens[1]) &&
            isCarrierCodeValid(tokens[2])

    private fun isDateFormatValid(dateString: String): Boolean {
        try {
            LocalDate.parse(dateString)
        } catch (e: DateTimeParseException) {
            return false
        }
        return true
    }

    private fun isShipmentSizeValid(size: String) = ShipmentSize.values().any { it.name == size }

    private fun isCarrierCodeValid(code: String) = CarrierCode.values().any { it.name == code }

}
