package com.example.todocompose.ui

import android.util.Log
import androidx.compose.foundation.ExperimentalFoundationApi
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxHeight
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.itemsIndexed
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.FloatingActionButton
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme.typography
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.style.TextDecoration
import androidx.compose.ui.unit.dp
import androidx.lifecycle.viewmodel.compose.viewModel
import com.example.todo.view.screens.Todo.TodoViewModel
import com.example.todocompose.R
import com.example.todocompose.data.entities.Todo
import com.example.todocompose.ui.theme.DarkGrey
import com.example.todocompose.ui.theme.Green
import com.example.todocompose.ui.theme.LightGrey
import com.example.todocompose.ui.theme.OffWhite
import com.example.todocompose.ui.theme.Red

var todoList = TodoViewModel().apply {
    addTodo(Todo(title = "Walk the dogs"))
    addTodo(Todo(title = "Write email to doctor"))
    addTodo(Todo(title = "Read through Kotlin docs"))
    addTodo(Todo(title = "Buy new monitor", isChecked = true))
    addTodo(Todo(title = "Take out trash", isChecked = true))
    addTodo(Todo(title = "Walk the dogs"))
    addTodo(Todo(title = "Write email to doctor"))
    addTodo(Todo(title = "Read through Kotlin docs"))
    addTodo(Todo(title = "Buy new monitor", isChecked = true))
    addTodo(Todo(title = "Take out trash", isChecked = true))
}

@Composable
fun HomeScreen() {
    Box(
        modifier = Modifier
            .background(LightGrey)
            .fillMaxSize()
            .padding(horizontal = 15.dp, vertical = 25.dp)
    ) {
        Column {
            Text(text = "Welcome, Younes!", style = typography.titleLarge)
            Todos(todoList)
        }
        Fabs(todoList, modifier = Modifier.align(Alignment.BottomCenter))
    }
}

@OptIn(ExperimentalFoundationApi::class)
@Composable
fun Todos(todoViewModel: TodoViewModel = viewModel()) {
    LazyColumn(
        modifier = Modifier
            .fillMaxHeight()
            .padding(top = 15.dp, start = 5.dp, end = 5.dp),
        verticalArrangement = Arrangement.spacedBy(5.dp)
    ) {
        itemsIndexed(items = todoViewModel.getAllTodos(), key = {index, item -> item.id }) { index, it ->
            TodoItem(item = it, index = index, todoViewModel = todoViewModel, modifier = Modifier
                .animateItemPlacement()
            )
        }
    }
}

@Composable
fun TodoItem(item: Todo, index: Int, todoViewModel: TodoViewModel, modifier: Modifier) {
    Row(
        modifier
            .fillMaxWidth()
            .height(36.dp)
            .clip(RoundedCornerShape(8.dp))
            .background(OffWhite)
            .padding(horizontal = 15.dp),
        horizontalArrangement = Arrangement.SpaceBetween,
        verticalAlignment = Alignment.CenterVertically
    ) {
        Text(
            text = item.title,
            style = if (item.isChecked) {
                typography.bodyMedium.copy(textDecoration = TextDecoration.LineThrough)
            } else typography.bodyMedium
        )
        IconButton(onClick = {
            todoViewModel.toggleChecked(index, value = !item.isChecked)
        }) {
            Icon(
                painter = painterResource(
                    id = if (item.isChecked) R.drawable.check else R.drawable.uncheck
                ),
                contentDescription = if (item.isChecked) "Mark undone" else "Mark done",
                tint = DarkGrey,
                modifier = Modifier.size(18.dp)
            )
        }

    }
}

@Composable
fun Fabs(todoViewModel: TodoViewModel, modifier: Modifier) {
    Row(
        horizontalArrangement = Arrangement.SpaceBetween,
        verticalAlignment = Alignment.Bottom,
        modifier = modifier
            .fillMaxHeight()
            .fillMaxWidth()
            .padding(horizontal = 10.dp)
    ) {
        MyFab(iconId = R.drawable.trash,
            color = Red,
            contentDescription = "Delete done todos",
            onClick = { todoViewModel.removeDoneTodos() })
        MyFab(iconId = R.drawable.plus,
            color = Green,
            contentDescription = "Add todo",
            onClick = { Log.d("TAG", "Add todo") })
    }
}

@Composable
fun MyFab(iconId: Int, color: Color, contentDescription: String, onClick: () -> Unit) {
    FloatingActionButton(
        shape = CircleShape,
        containerColor = color,
        onClick = onClick,
        modifier = Modifier.size(50.dp)
    ) {
        Icon(
            painter = painterResource(iconId),
            contentDescription = contentDescription,
            tint = OffWhite,
            modifier = Modifier.size(30.dp)
        )
    }
}