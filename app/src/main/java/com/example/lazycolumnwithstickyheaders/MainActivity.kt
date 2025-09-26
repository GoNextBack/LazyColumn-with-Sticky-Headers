package com.example.lazycolumnwithstickyheaders

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.compose.foundation.ExperimentalFoundationApi
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.lazy.rememberLazyListState
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import com.example.lazycolumnwithstickyheaders.ui.theme.LazyColumnWithStickyHeadersTheme
import kotlinx.coroutines.launch

class MainActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        setContent {
            LazyColumnWithStickyHeadersTheme {
                Scaffold(
                    modifier = Modifier.fillMaxSize()
                ) { innerPadding ->
                    ContactList(
                        modifier = Modifier.padding(innerPadding)
                    )
                }
            }
        }
    }
}

data class Contact(val name: String)

@OptIn(ExperimentalFoundationApi::class)
@Composable
fun ContactList(modifier: Modifier = Modifier) {
    val contacts = remember { generateContacts(50) }
    val grouped = contacts.groupBy { it.name.first().uppercaseChar() }
    val listState = rememberLazyListState()
    val coroutineScope = rememberCoroutineScope()

    Box(modifier = modifier.fillMaxSize()) {
        LazyColumn(state = listState) {
            grouped.forEach { (letter, contactsForLetter) ->
                stickyHeader {
                    Surface(
                        tonalElevation = 4.dp,
                        modifier = Modifier.fillMaxWidth()
                    ) {
                        Text(
                            text = letter.toString(),
                            style = MaterialTheme.typography.titleMedium,
                            modifier = Modifier.padding(8.dp)
                        )
                    }
                }
                items(contactsForLetter) { contact ->
                    ListItem(
                        headlineContent = { Text(contact.name) }
                    )
                }
            }
        }

        // FAB appears only after passing item 10
        val showFab by remember {
            derivedStateOf { listState.firstVisibleItemIndex > 10 }
        }

        if (showFab) {
            FloatingActionButton(
                onClick = {
                    coroutineScope.launch {
                        listState.animateScrollToItem(0)
                    }
                },
                modifier = Modifier
                    .align(Alignment.BottomEnd)
                    .padding(16.dp)
            ) {
                Text("UP")
            }
        }
    }
}

fun generateContacts(count: Int): List<Contact> {
    val names = listOf(
        "Alice", "Bob", "Charlie", "David", "Eve", "Frank", "Grace", "Hank",
        "Ivy", "Jack", "Kelly", "Leo", "Mona", "Nate", "Olivia", "Paul",
        "Quincy", "Rachel", "Steve", "Tina", "Uma", "Victor", "Wendy",
        "Xander", "Yara", "Zack"
    )
    return List(count) { index ->
        val name = names[index % names.size] + " ${index + 1}"
        Contact(name)
    }.sortedBy { it.name }
}

@Preview(showBackground = true)
@Composable
fun ContactListPreview() {
    LazyColumnWithStickyHeadersTheme {
        ContactList()
    }
}
