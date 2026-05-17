package com.example.a211082_drnazatul_lab05

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.compose.foundation.*
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.text.BasicTextField
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material.icons.automirrored.filled.ArrowForward
import androidx.compose.material.icons.automirrored.filled.ExitToApp
import androidx.compose.material.icons.automirrored.filled.List
import androidx.compose.material.icons.filled.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.shadow
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.lifecycle.viewmodel.compose.viewModel
import androidx.navigation.NavHostController
import androidx.navigation.compose.*
import androidx.compose.ui.platform.LocalContext
import com.example.a211082_drnazatul_lab05.data.AppDatabase
import com.example.a211082_drnazatul_lab05.data.PlaceRepository

class MainActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContent {
            MaterialTheme {
                val navController = rememberNavController()
                val context = LocalContext.current

                val db = AppDatabase.getDatabase(context)
                val repository = PlaceRepository(db.placeDao())

                val viewModel = remember {
                    OnRoadViewModel(repository)
                }

                NavHost(navController = navController, startDestination = "map") {
                    composable("map") { MapScreen(viewModel, navController) }
                    composable("summary") { SummaryScreen(viewModel, navController) }
                    composable("saved") { SavedPlacesScreen(viewModel, navController) }
                    composable("add_place") { AddPlaceScreen(viewModel, navController) }
                    composable("profile") { ProfileScreen(viewModel, navController) }
                }
            }
        }
    }
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun MapScreen(
    viewModel: OnRoadViewModel,
    navController: NavHostController
) {

    val uiState = viewModel.uiState

    var searchInput by remember { mutableStateOf("") }
    var showBottomSheet by remember { mutableStateOf(false) }

    Box(
        modifier = Modifier.fillMaxSize()
    ) {

        // MAP BACKGROUND
        Image(
            painter = painterResource(id = R.drawable.maps),
            contentDescription = null,
            contentScale = ContentScale.Crop,
            modifier = Modifier.fillMaxSize()
        )

        if (!uiState.isRouting) {

            Column(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(
                        top = 60.dp,
                        start = 16.dp,
                        end = 16.dp
                    )
            ) {

                ElevatedCard(
                    modifier = Modifier.fillMaxWidth(),
                    shape = RoundedCornerShape(32.dp)
                ) {

                    Row(
                        modifier = Modifier.padding(
                            horizontal = 16.dp,
                            vertical = 8.dp
                        ),
                        verticalAlignment = Alignment.CenterVertically
                    ) {

                        Icon(
                            Icons.Default.Search,
                            contentDescription = null,
                            tint = Color.Gray
                        )

                        BasicTextField(
                            value = searchInput,

                            onValueChange = {
                                searchInput = it
                            },

                            modifier = Modifier
                                .weight(1f)
                                .padding(horizontal = 12.dp),

                            decorationBox = {

                                if (searchInput.isEmpty()) {

                                    Text(
                                        "Search destination...",
                                        color = Color.Gray
                                    )
                                }

                                it()
                            }
                        )

                        IconButton(
                            onClick = {

                                if (searchInput.isNotBlank()) {

                                    viewModel.updateDestination(
                                        searchInput,
                                        true
                                    )
                                }
                            }
                        ) {

                            Icon(
                                Icons.AutoMirrored.Filled.ArrowForward,
                                contentDescription = null,
                                tint = MaterialTheme.colorScheme.primary
                            )
                        }
                    }
                }
            }
        }
        if (uiState.isRouting) {

            Box(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(
                        top = 60.dp,
                        start = 16.dp,
                        end = 16.dp
                    )
            ) {

                ElevatedCard(
                    modifier = Modifier.fillMaxWidth(),

                    shape = RoundedCornerShape(20.dp),

                    colors = CardDefaults.elevatedCardColors(
                        containerColor = Color(0xFFEDE7F6)
                    )
                ) {

                    Column(
                        modifier = Modifier.padding(16.dp)
                    ) {

                        Row(
                            verticalAlignment = Alignment.CenterVertically
                        ) {

                            IconButton(
                                onClick = {

                                    viewModel.setRouting(false)
                                }
                            ) {

                                Icon(
                                    Icons.AutoMirrored.Filled.ArrowBack,
                                    contentDescription = null,
                                    tint = Color.DarkGray
                                )
                            }

                            Spacer(modifier = Modifier.width(4.dp))

                            Column(
                                modifier = Modifier.weight(1f)
                            ) {

                                Text(
                                    text = "Your location",
                                    color = Color.Gray,
                                    fontSize = 12.sp
                                )

                                HorizontalDivider(
                                    modifier = Modifier.padding(vertical = 6.dp),
                                    color = Color.LightGray
                                )

                                Text(
                                    text = uiState.destination,
                                    fontWeight = FontWeight.Bold,
                                    fontSize = 18.sp
                                )
                            }
                        }
                    }
                }
            }
        }

        // BOTTOM SECTION
        Box(
            modifier = Modifier
                .align(Alignment.BottomCenter)
                .fillMaxWidth()
        ) {

            ElevatedCard(
                modifier = Modifier.fillMaxWidth(),

                shape = RoundedCornerShape(
                    topStart = 28.dp,
                    topEnd = 28.dp
                )
            ) {

                Column(
                    modifier = Modifier.padding(24.dp),
                    horizontalAlignment = Alignment.CenterHorizontally
                ) {

                    Box(
                        modifier = Modifier
                            .width(40.dp)
                            .height(4.dp)
                            .background(
                                Color.LightGray,
                                RoundedCornerShape(2.dp)
                            )
                            .clickable {
                                showBottomSheet = true
                            }
                    )

                    Spacer(modifier = Modifier.height(20.dp))

                    if (uiState.isRouting) {

                        Button(
                            onClick = {

                                navController.navigate("summary")
                            },

                            modifier = Modifier
                                .fillMaxWidth()
                                .height(58.dp),

                            shape = RoundedCornerShape(30.dp),

                            colors = ButtonDefaults.buttonColors(
                                containerColor = Color(0xFF5C6BC0)
                            )
                        ) {

                            Icon(
                                Icons.Default.Send,
                                contentDescription = null
                            )

                            Spacer(modifier = Modifier.width(8.dp))

                            Text(
                                "Start Navigation",
                                fontWeight = FontWeight.Bold
                            )
                        }

                    } else {

                        // BOTTOM MENU
                        Row(
                            modifier = Modifier.fillMaxWidth(),
                            horizontalArrangement = Arrangement.SpaceEvenly
                        ) {

                            // EXPLORE
                            Column(
                                horizontalAlignment = Alignment.CenterHorizontally,

                                modifier = Modifier.clickable {
                                    showBottomSheet = true
                                }
                            ) {

                                Icon(
                                    Icons.Default.LocationOn,
                                    contentDescription = null,
                                    tint = MaterialTheme.colorScheme.primary
                                )

                                Text(
                                    "Explore",
                                    fontSize = 12.sp
                                )
                            }

                            // SAVED
                            Column(
                                horizontalAlignment = Alignment.CenterHorizontally,

                                modifier = Modifier.clickable {
                                    navController.navigate("saved")
                                }
                            ) {

                                Icon(
                                    Icons.Default.FavoriteBorder,
                                    contentDescription = null
                                )

                                Text(
                                    "Saved",
                                    fontSize = 12.sp
                                )
                            }

                            // PROFILE
                            Column(
                                horizontalAlignment = Alignment.CenterHorizontally,

                                modifier = Modifier.clickable {
                                    navController.navigate("profile")
                                }
                            ) {

                                Icon(
                                    Icons.Default.Person,
                                    contentDescription = null
                                )

                                Text(
                                    "Profile",
                                    fontSize = 12.sp
                                )
                            }
                        }
                    }
                }
            }
        }

        // BOTTOM SHEET
        if (showBottomSheet) {

            ModalBottomSheet(
                onDismissRequest = {
                    showBottomSheet = false
                }
            ) {

                Column(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(24.dp)
                ) {

                    Text(
                        "Explore Nearby Places",
                        style = MaterialTheme.typography.headlineSmall,
                        fontWeight = FontWeight.Bold
                    )

                    Spacer(modifier = Modifier.height(16.dp))

                    Text("• McDonald's UKM")
                    Text("• Library UKM")
                    Text("• CIMB Bank")

                    Spacer(modifier = Modifier.height(30.dp))
                }
            }
        }
    }
}
@Composable
fun SummaryScreen(
    viewModel: OnRoadViewModel,
    navController: NavHostController
) {
    val places = viewModel.savedPlaces.collectAsState().value
    val dest = viewModel.uiState.destination

    val isSaved = places.any { it.name == dest }

    Column(
        modifier = Modifier
            .fillMaxSize()
            .padding(24.dp),
        verticalArrangement = Arrangement.Center,
        horizontalAlignment = Alignment.CenterHorizontally
    ) {

        Icon(
            Icons.Default.CheckCircle,
            contentDescription = null,
            tint = Color(0xFF4CAF50),
            modifier = Modifier.size(100.dp)
        )

        Text(
            "Trip Finished!",
            style = MaterialTheme.typography.headlineMedium,
            fontWeight = FontWeight.Bold
        )

        ElevatedCard(
            modifier = Modifier
                .fillMaxWidth()
                .padding(vertical = 20.dp)
        ) {
            Row(
                modifier = Modifier.padding(16.dp),
                verticalAlignment = Alignment.CenterVertically
            ) {

                Text(
                    dest,
                    modifier = Modifier.weight(1f),
                    fontWeight = FontWeight.Bold
                )

                IconButton(
                    onClick = {
                        if (isSaved) {
                            val place = places.find { it.name == dest }
                            if (place != null) {
                                viewModel.deletePlace(place)
                            }
                        } else {
                            viewModel.addPlace(dest)
                        }
                    }
                ) {
                    Icon(
                        imageVector = if (isSaved)
                            Icons.Default.Favorite
                        else
                            Icons.Default.FavoriteBorder,
                        contentDescription = null,
                        tint = if (isSaved) Color.Red else Color.Gray
                    )
                }
            }
        }

        Button(
            onClick = {
                viewModel.setRouting(false)
                navController.navigate("map") {
                    popUpTo(0)
                    launchSingleTop = true
                }
            },
            modifier = Modifier.fillMaxWidth()
        ) {
            Text("Back to Home")
        }
    }
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun SavedPlacesScreen(
    viewModel: OnRoadViewModel,
    navController: NavHostController
) {

    val places = viewModel.savedPlaces.collectAsState().value

    Scaffold(
        topBar = {
            TopAppBar(
                title = {
                    Text(
                        "Saved Places",
                        fontWeight = FontWeight.Bold
                    )
                },

                navigationIcon = {
                    IconButton(
                        onClick = {
                            navController.popBackStack()
                        }
                    ) {
                        Icon(
                            Icons.AutoMirrored.Filled.ArrowBack,
                            contentDescription = null
                        )
                    }
                }
            )
        },

        floatingActionButton = {
            FloatingActionButton(
                onClick = {
                    navController.navigate("add_place")
                }
            ) {
                Icon(Icons.Default.Add, contentDescription = null)
            }
        }
    ) { padding ->

        if (places.isEmpty()) {

            Box(
                modifier = Modifier
                    .fillMaxSize()
                    .padding(padding),
                contentAlignment = Alignment.Center
            ) {
                Text(
                    "No saved places yet",
                    color = Color.Gray
                )
            }

        } else {

            LazyColumn(
                modifier = Modifier
                    .padding(padding)
                    .padding(horizontal = 16.dp),

                verticalArrangement = Arrangement.spacedBy(12.dp)
            ) {

                items(places) { place ->

                    ElevatedCard(
                        modifier = Modifier
                            .fillMaxWidth()
                            .clickable {

                                // OPEN MAP SCREEN WITH ROUTE
                                viewModel.updateDestination(
                                    place.name,
                                    true
                                )

                                navController.navigate("map")
                            },

                        shape = RoundedCornerShape(20.dp),

                        colors = CardDefaults.elevatedCardColors(
                            containerColor = Color(0xFFF5F5F5)
                        )
                    ) {

                        Row(
                            modifier = Modifier
                                .fillMaxWidth()
                                .padding(20.dp),

                            verticalAlignment = Alignment.CenterVertically
                        ) {

                            // LOCATION ICON
                            Surface(
                                shape = CircleShape,
                                color = Color(0xFFE8EAF6)
                            ) {

                                Icon(
                                    Icons.Default.LocationOn,
                                    contentDescription = null,
                                    tint = Color(0xFF5C6BC0),
                                    modifier = Modifier.padding(10.dp)
                                )
                            }

                            Spacer(modifier = Modifier.width(16.dp))

                            // PLACE NAME
                            Column(
                                modifier = Modifier.weight(1f)
                            ) {

                                Text(
                                    text = place.name,
                                    fontWeight = FontWeight.Bold,
                                    fontSize = 18.sp
                                )

                                Spacer(modifier = Modifier.height(4.dp))

                                Text(
                                    text = "Tap to navigate",
                                    color = Color.Gray,
                                    fontSize = 13.sp
                                )
                            }

                            // RED FAVORITE BUTTON
                            IconButton(
                                onClick = {
                                    viewModel.deletePlace(place)
                                }
                            ) {

                                Icon(
                                    Icons.Default.Favorite,
                                    contentDescription = null,
                                    tint = Color.Red
                                )
                            }
                        }
                    }
                }
            }
        }
    }
}

@Composable
fun AddPlaceScreen(
    viewModel: OnRoadViewModel,
    navController: NavHostController
) {

    var name by remember { mutableStateOf("") }

    Box(
        modifier = Modifier
            .fillMaxSize()
            .padding(24.dp)
    ) {

        // CONTENT
        Column(
            modifier = Modifier
                .fillMaxWidth()
                .align(Alignment.TopCenter)
        ) {

            Text(
                text = "Add New Place",
                style = MaterialTheme.typography.headlineSmall,
                fontWeight = FontWeight.Bold
            )

            Spacer(modifier = Modifier.height(24.dp))

            OutlinedTextField(
                value = name,

                onValueChange = {
                    name = it
                },

                label = {
                    Text("Place Name")
                },

                modifier = Modifier.fillMaxWidth(),

                shape = RoundedCornerShape(16.dp)
            )
        }

        Button(
            onClick = {

                if (name.isNotBlank()) {
                    viewModel.addPlace(name)
                    navController.popBackStack()
                }
            },

            modifier = Modifier
                .fillMaxWidth()
                .height(56.dp)
                .align(Alignment.BottomCenter),

            shape = RoundedCornerShape(20.dp),

            colors = ButtonDefaults.buttonColors(
                containerColor = Color(0xFF5C6BC0)
            )
        ) {

            Text(
                "Save Place",
                fontWeight = FontWeight.Bold
            )
        }
    }
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun ProfileScreen(viewModel: OnRoadViewModel, navController: NavHostController) {
    val places by viewModel.savedPlaces.collectAsState()
    val savedCount = places.size

    Scaffold(
        topBar = {
            TopAppBar(
                title = { Text("My Profile") },
                navigationIcon = {
                    IconButton(onClick = { navController.popBackStack() }) {
                        Icon(Icons.AutoMirrored.Filled.ArrowBack, null)
                    }
                }
            )
        }
    ) { padding ->
        Column(
            modifier = Modifier.padding(padding).fillMaxSize().padding(24.dp),
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            Surface(
                modifier = Modifier.size(100.dp),
                shape = CircleShape,
                color = MaterialTheme.colorScheme.secondaryContainer
            ) {
                Icon(Icons.Default.Person, null, modifier = Modifier.padding(20.dp), tint = MaterialTheme.colorScheme.onSecondaryContainer)
            }

            Text("User OnRoad", style = MaterialTheme.typography.headlineSmall, fontWeight = FontWeight.Bold, modifier = Modifier.padding(top = 16.dp))
            Text("Explore the journey with ease!", color = Color.Gray)

            Row(modifier = Modifier.fillMaxWidth().padding(vertical = 32.dp), horizontalArrangement = Arrangement.SpaceEvenly) {
                Column(horizontalAlignment = Alignment.CenterHorizontally) {
                    Text("24", fontWeight = FontWeight.Bold, fontSize = 20.sp)
                    Text("Trips", fontSize = 12.sp, color = Color.Gray)
                }
                Column(horizontalAlignment = Alignment.CenterHorizontally) {
                    Text("$savedCount", fontWeight = FontWeight.Bold, fontSize = 20.sp, color = MaterialTheme.colorScheme.primary)
                    Text("Saved", fontSize = 12.sp, color = Color.Gray)
                }
            }

            HorizontalDivider(modifier = Modifier.padding(bottom = 16.dp))

            ListItem(headlineContent = { Text("Travel History") }, leadingContent = { Icon(Icons.AutoMirrored.Filled.List, null) })
            ListItem(headlineContent = { Text("Settings") }, leadingContent = { Icon(Icons.Default.Settings, null) })
            ListItem(headlineContent = { Text("Logout", color = Color.Red) }, leadingContent = { Icon(Icons.AutoMirrored.Filled.ExitToApp, null, tint = Color.Red) })
        }
    }
}