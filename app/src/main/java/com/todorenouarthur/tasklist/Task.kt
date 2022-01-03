package com.todorenouarthur.tasklist

import java.util.*

data class Task(
    var id: String = UUID.randomUUID().toString(),
    val title: String = "Default_Title",
    val description : String = "Default_Description") : java.io.Serializable