package com.anddeg.discounter.shipment

import java.math.BigInteger

data class Shipment(val size: ShipmentSize, val carrierCode: CarrierCode, val price: BigInteger)
