package com.todorenouarthur.tasklist

import kotlinx.serialization.SerialName
import kotlinx.serialization.Serializable
import java.util.*

@Serializable
data class Task(
    @SerialName("id")
    var id: String = UUID.randomUUID().toString(),
    @SerialName("title")
    var title: String = "Default_Title",
    @SerialName("description")
    var description : String = "Default_Description"
) : java.io.Serializable