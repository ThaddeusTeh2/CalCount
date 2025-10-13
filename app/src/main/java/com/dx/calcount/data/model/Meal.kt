package com.dx.calcount.data.model

import java.time.LocalDateTime

// meal model
data class Meal(
    val id: Int,
    val name: String,
    // use localdatetime class as recorded date as def value
    val date: LocalDateTime = LocalDateTime.now(),
    val totalCalories: Int
)
