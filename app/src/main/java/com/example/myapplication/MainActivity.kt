package com.example.myapplication

import android.R.attr.text
import android.os.Bundle
import android.util.Log
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.layout.wrapContentHeight
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material3.Button
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.material3.TextField
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateListOf
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import androidx.room.Room
import com.example.myapplication.ui.theme.MyApplicationTheme
import com.example.myapplication.Todo
import kotlinx.coroutines.launch

class MainActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        val db = TodoDatabase.getDatabase(this)
        setContent {
            MyApplicationTheme {
                Scaffold(modifier = Modifier.fillMaxSize()) { innerPadding ->
                    MainAct(
                        modifier = Modifier.padding(innerPadding),
                        db = db
                    )
                }
            }
        }
    }
}
@Composable
fun MainAct(
    modifier: Modifier = Modifier,
    db: TodoDatabase
): Unit {
    val todoList by db.todoDao().getAllTodos().collectAsStateWithLifecycle(initialValue = emptyList())
    var text by remember { mutableStateOf("") }

    val scope = rememberCoroutineScope()
    Column(modifier = Modifier.padding(16.dp)) {
        InputArea(
            text = text,
            onTextChanged = {text = it},
            onAddClicked = {
                Log.d("MyTheme", "입력된 텍스트: '$text'") // 이거 꼭 추가해서 봐봐
                if (text.isNotBlank()) {
//                    todoList.add(Todo(todoList.size, text))
                    val newTodo = Todo(title = text)
                    Log.d("MyTheme", "생성된 Todo 객체: $newTodo") // 여기서 title이 빈칸이면 생성자 문제

                    scope.launch {
                        db.todoDao().insert(newTodo)
                    }
                    text = ""
                    Log.d("MyTheme", "추가됨 ${todoList.size}");
                }
            },
            modifier = Modifier.fillMaxWidth()
        )
        Spacer(modifier = Modifier.height(16.dp))
        LazyColumn {
            items(todoList) { todo ->
                Log.d("MyTheme", "출력:${todo}")
                Row {
                    Text(
                        text = todo.title
                    )
                    Button(
                        onClick = {
                            Log.d("MyTheme", todo.toString())
//                            todoList.remove(todo)
                            scope.launch {
                                db.todoDao().delete(todo) // DB에서 삭제
                            }
                        }
                    ) {
                        Text("완료");
                    }
                }
            }
        }
    }
}

@Composable
fun InputArea(
    text: String,
    onTextChanged: (String) -> Unit,
    onAddClicked: () -> Unit,
    modifier: Modifier = Modifier
) {
        Row(
            modifier = modifier,
            verticalAlignment = Alignment.CenterVertically
        ) {
            TextField(
                value = text,
                onValueChange = onTextChanged,
                modifier = Modifier.weight(1f),
                placeholder = { Text("할 일을 입력하세요") },
                singleLine = true // 한 줄로 입력하게 제한
            )
            Spacer(modifier = Modifier.width(8.dp))
            Button(onClick = onAddClicked) {
                Text("추가")
            }
        }

}

@Composable
fun ToggleTextScreen(modifier: Modifier = Modifier): Unit {
    // 1. 상태(State) 선언: "Hello"를 기본값으로 갖는 text라는 변수를 만듦
    // remember: 이 컴포넌트가 다시 그려져도 값을 기억하라는 뜻
    var text by remember { mutableStateOf("Hello World") }

    Box(modifier = Modifier.fillMaxSize()) {
        // 2. 가운데 텍스트
        Text(
            text = text,
            modifier = Modifier.align(Alignment.Center)
        )

        // 3. 하단 가운데 버튼
        Button(
            onClick = {
                // 버튼 누를 때마다 상태를 토글
                text = if (text == "Hello World") "GoodBye World" else "Hello World"
            },
            modifier = Modifier
                .align(Alignment.BottomCenter) // 화면 아래 가운데
                .padding(bottom = 150.dp)       // 화면 끝에서 살짝 띄우기
        ) {
            Text("클릭")
        }
    }
}
@Composable
fun Greeting(name: String, modifier: Modifier = Modifier) {
    Text(
        text = "Hello $name!",
        modifier = modifier
    )
}

//@Preview(showBackground = true)
//@Composable
//fun GreetingPreview() {
//    MyApplicationTheme {
//        Scaffold(modifier = Modifier.fillMaxSize()) { innerPadding ->
//            MainAct(modifier = Modifier.padding(innerPadding))
//        }
//    }
//}