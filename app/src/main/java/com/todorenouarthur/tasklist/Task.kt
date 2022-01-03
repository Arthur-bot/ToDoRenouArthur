package com.todorenouarthur.tasklist

import java.util.*

data class Task(
    var id: String = UUID.randomUUID().toString(),
    var title: String = "Default_Title",
    var description : String = "Default_Description") : java.io.Serializable