package com.dx.calcount.data.model

import java.time.LocalDateTime

data class Meal(
    val id: Int,
    val name: String,
    val date: LocalDateTime = LocalDateTime.now(),
    val totalCalories: Int
)
