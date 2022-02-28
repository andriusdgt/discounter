package com.anddeg.discounter.order

import com.anddeg.discounter.shipment.CarrierCode
import com.anddeg.discounter.shipment.ShipmentSize
import java.time.LocalDate

data class Order(val date: LocalDate, val shipmentSize: ShipmentSize, val carrierCode: CarrierCode): Orderable
