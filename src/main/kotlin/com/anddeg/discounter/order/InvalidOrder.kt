package com.anddeg.discounter.order

data class InvalidOrder(val orderContent: String = ""): Orderable
