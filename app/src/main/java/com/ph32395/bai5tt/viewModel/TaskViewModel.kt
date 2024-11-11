package com.ph32395.bai5tt.viewModel


import android.content.ContentResolver
import android.content.Context
import android.net.Uri
import android.provider.BaseColumns
import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import androidx.lifecycle.viewModelScope
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.launch

class TaskViewModel(context: Context) : ViewModel() {

    private val _tasks = MutableStateFlow<List<Task>>(emptyList())

    val tasks: StateFlow<List<Task>> get() = _tasks

    init {
        viewModelScope.launch {
            _tasks.value = loadTasks(context.contentResolver)
        }
    }

    private fun loadTasks(contentResolver: ContentResolver): List<Task> {
        val tasks = mutableListOf<Task>()
        val uri: Uri = TodoContentProvider.CONTENT_URI
        val projection = arrayOf(BaseColumns._ID, COLUMN_NAME, COLUMN_DATE, COLUMN_TIME)

        contentResolver.query(uri, projection, null, null, null)?.use { cursor ->
            val idIndex = cursor.getColumnIndexOrThrow(BaseColumns._ID)
            val nameIndex = cursor.getColumnIndexOrThrow(COLUMN_NAME)
            val dateIndex = cursor.getColumnIndexOrThrow(COLUMN_DATE)
            val timeIndex = cursor.getColumnIndexOrThrow(COLUMN_TIME)

            while (cursor.moveToNext()) {
                val id = cursor.getLong(idIndex)
                val name = cursor.getString(nameIndex)
                val date = cursor.getString(dateIndex)
                val time = cursor.getString(timeIndex)
                tasks.add(Task(id, name, date, time))
            }
        }
        return tasks
    }
}

class TaskViewModelFactory(private val context: Context) : ViewModelProvider.Factory{
    override fun <T : ViewModel> create(modelClass: Class<T>): T {
        if (modelClass.isAssignableFrom(TaskViewModel::class.java)){
            return TaskViewModel(context) as T
        }
        throw IllegalArgumentException("Unknown ViewModel class")
    }
}