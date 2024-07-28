package com.normanfr.todolist

import android.os.Parcel
import android.os.Parcelable

import androidx.room.Entity
import androidx.room.PrimaryKey

@Entity(tableName = "task") // This annotation specifies that this class is a Room entity
data class Task(
    @PrimaryKey(autoGenerate = true) // This specifies that the ID should be auto-generated
    val id: Int = 0,
    val title: String,
    val description: String,
    val dueDate: String,
    val dueTime: String, // Make sure to have this if you're using it in your code
    val isCompleted: Boolean = false
) : Parcelable {
    constructor(parcel: Parcel) : this(
        parcel.readInt(),
        parcel.readString() ?: "",
        parcel.readString() ?: "",
        parcel.readString() ?: "",
        parcel.readString() ?: "", // Ensure this is present if you added dueTime
        parcel.readByte() != 0.toByte()
    )

    override fun writeToParcel(parcel: Parcel, flags: Int) {
        parcel.writeInt(id)
        parcel.writeString(title)
        parcel.writeString(description)
        parcel.writeString(dueDate)
        parcel.writeString(dueTime) // Ensure this is present if you added dueTime
        parcel.writeByte(if (isCompleted) 1 else 0)
    }

    override fun describeContents(): Int {
        return 0
    }

    companion object CREATOR : Parcelable.Creator<Task> {
        override fun createFromParcel(parcel: Parcel): Task {
            return Task(parcel)
        }

        override fun newArray(size: Int): Array<Task?> {
            return arrayOfNulls(size)
        }
    }
}
