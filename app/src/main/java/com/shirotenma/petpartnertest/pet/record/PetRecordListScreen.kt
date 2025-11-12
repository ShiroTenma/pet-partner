// PetRecordListScreen.kt
@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun PetRecordListScreen(
    nav: NavController,
    petId: Long,
    vm: PetRecordListViewModel = hiltViewModel()
) {
    val all by vm.records(petId).collectAsState(initial = emptyList())

    var expanded by remember { mutableStateOf(false) }
    var selectedType by remember { mutableStateOf<String?>(null) } // null = all

    val types = remember(all) {
        buildList {
            add(null) // "All"
            addAll(all.map { it.type }.filter { it.isNotBlank() }.distinct())
        }
    }

    val items = remember(all, selectedType) {
        if (selectedType.isNullOrBlank()) all
        else all.filter { it.type == selectedType }
    }

    Scaffold(
        topBar = {
            TopAppBar(
                title = { Text("Medical Records") },
                actions = {
                    // Filter dropdown
                    Box {
                        TextButton(onClick = { expanded = true }) {
                            Text(selectedType ?: "All types")
                        }
                        DropdownMenu(expanded = expanded, onDismissRequest = { expanded = false }) {
                            types.forEach { t ->
                                DropdownMenuItem(
                                    text = { Text(t ?: "All types") },
                                    onClick = {
                                        selectedType = t
                                        expanded = false
                                    }
                                )
                            }
                        }
                    }
                }
            )
        },
        floatingActionButton = {
            FloatingActionButton(onClick = {
                nav.navigate("${Route.RECORD_EDIT}/$petId")
            }) { Text("+") }
        }
    ) { pad ->
        if (items.isEmpty()) {
            Box(
                modifier = Modifier
                    .padding(pad)
                    .fillMaxSize()
                    .padding(24.dp)
            ) { Text("No records${if (selectedType != null) " for \"$selectedType\"" else ""}.") }
        } else {
            LazyColumn(
                modifier = Modifier
                    .padding(pad)
                    .fillMaxSize(),
                contentPadding = PaddingValues(bottom = 88.dp)
            ) {
                items(items, key = { it.id }) { r ->
                    ListItem(
                        headlineContent = { Text(r.title) },
                        supportingContent = {
                            val sub = listOfNotNull(r.type.ifBlank { null }, r.date.ifBlank { null })
                                .joinToString(" • ")
                            if (sub.isNotBlank()) Text(sub)
                        },
                        modifier = Modifier
                            .fillMaxWidth()
                            .clickable {
                                nav.navigate("${Route.RECORD_EDIT}/${r.petId}/${r.id}")
                            }
                            .padding(horizontal = 8.dp, vertical = 4.dp)
                    )
                    HorizontalDivider()
                }
            }
        }
    }
}
