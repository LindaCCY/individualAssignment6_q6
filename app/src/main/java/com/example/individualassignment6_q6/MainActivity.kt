package com.example.individualassignment6_q6

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.unit.dp
import com.example.individualassignment6_q6.ui.theme.IndividualAssignment6_q6Theme
import com.google.android.gms.maps.model.CameraPosition
import com.google.android.gms.maps.model.LatLng
import com.google.maps.android.compose.*

/**
 * Main Activity - Entry point for the Map Overlays application
 * This app demonstrates Google Maps integration with polylines and polygons
 */
class MainActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge() // Enable edge-to-edge display
        setContent {
            // Apply the app's theme
            IndividualAssignment6_q6Theme {
                MapOverlaysScreen() // Display the main map screen
            }
        }
    }
}

/**
 * Main composable function that displays the Google Map with overlays
 * Features:
 * - Hiking trail polyline
 * - Park area polygon
 * - Customization controls
 * - Information dialogs
 */
@Composable
fun MapOverlaysScreen() {
    // Define hiking trail coordinates (polyline points)
    // These points create a path through Boston Common
    val trailPoints = remember {
        listOf(
            LatLng(42.3601, -71.0589), // Trail start point
            LatLng(42.3615, -71.0600), // Waypoint 1
            LatLng(42.3630, -71.0610), // Waypoint 2
            LatLng(42.3645, -71.0605), // Waypoint 3
            LatLng(42.3660, -71.0595), // Waypoint 4
            LatLng(42.3670, -71.0580)  // Trail end point
        )
    }

    // Define park boundary coordinates
    // These points outline the rectangular park area
    val parkBoundary = remember {
        listOf(
            LatLng(42.3590, -71.0620), // Bottom left corner
            LatLng(42.3680, -71.0620), // Top left corner
            LatLng(42.3680, -71.0550),
            LatLng(42.3590, -71.0550)
        )
    }

    // State variables for polyline customization
    var polylineColor by remember { mutableStateOf(Color.Blue) }
    var polylineWidth by remember { mutableFloatStateOf(10f) }

    // State variables for polygon customization
    var polygonFillColor by remember { mutableStateOf(Color.Green.copy(alpha = 0.3f)) }
    var polygonStrokeColor by remember { mutableStateOf(Color.Green) }
    var polygonStrokeWidth by remember { mutableFloatStateOf(5f) }

    // State variables for controlling dialog visibility
    var showTrailInfo by remember { mutableStateOf(false) }
    var showParkInfo by remember { mutableStateOf(false) }
    var showCustomization by remember { mutableStateOf(false) }

    // Camera position state --> controls map view
    val cameraPositionState = rememberCameraPositionState {
        position = CameraPosition.fromLatLngZoom(
            LatLng(42.3635, -71.0585), // Center point (Boston Common)
            14f // Zoom level (higher = more zoomed in)
        )
    }

    // Main container using Box layout for layering UI elements
    Box(modifier = Modifier.fillMaxSize()) {
        // Google Map composable --> displays the map
        GoogleMap(
            modifier = Modifier.fillMaxSize(),
            cameraPositionState = cameraPositionState,
            properties = MapProperties(isMyLocationEnabled = false), // Disable location tracking
            uiSettings = MapUiSettings(zoomControlsEnabled = true)   // Enable zoom buttons
        ) {
            // Polyline overlay,  represents the hiking trail
            Polyline(
                points = trailPoints,           // List of coordinates for the trail
                color = polylineColor,          // Line color
                width = polylineWidth,          // Line width in pixels is customizable
                clickable = true,               // Enable click detection
                onClick = {
                    showTrailInfo = true        // Show trail info when clicked
                }
            )

            // Polygon overlay - represents the park area
            Polygon(
                points = parkBoundary,          // List of coordinates defining the boundary
                fillColor = polygonFillColor,   // Interior fill color
                strokeColor = polygonStrokeColor, // Border color
                strokeWidth = polygonStrokeWidth, // Border width in pixels
                clickable = true,               // Enable click detection
                onClick = {
                    showParkInfo = true         // Show park info when clicked
                }
            )
        }

        // Floating Action Button
        FloatingActionButton(
            onClick = { showCustomization = true },
            modifier = Modifier
                .align(Alignment.BottomEnd)     // Position at bottom right
                .padding(16.dp)                 // Add padding from edges
        ) {
            Text("⚙️", style = MaterialTheme.typography.headlineMedium)
        }

        // Conditional UI --> Show trail information dialog when user clicks polyline
        if (showTrailInfo) {
            InfoDialog(
                title = "Hiking Trail",
                info = """
                    Freedom Trail Extension
                    
                    Distance: 2.3 miles
                    Difficulty: Easy
                    Duration: ~45 minutes
                    
                    This scenic trail winds through historic Boston Common, offering beautiful views and easy walking paths suitable for all skill levels.
                """.trimIndent(),
                onDismiss = { showTrailInfo = false } // Close dialog callback
            )
        }

        // Conditional UI -->  Show park information dialog when user clicks polygon
        if (showParkInfo) {
            InfoDialog(
                title = "Boston Common",
                info = """
                    America's Oldest Public Park
                    
                    Established: 1634
                    Area: 50 acres
                    Features: 
                    • Central Plaza
                    • Frog Pond
                    • Historic monuments
                    • Seasonal activities
                    
                    Boston Common is the starting point of the Freedom Trail and hosts numerous events throughout the year.
                """.trimIndent(),
                onDismiss = { showParkInfo = false } // Close dialog callback
            )
        }

        // Conditional UI --> Show customization dialog when user clicks FAB
        if (showCustomization) {
            CustomizationDialog(
                // Pass current values
                polylineColor = polylineColor,
                polylineWidth = polylineWidth,
                polygonFillColor = polygonFillColor,
                polygonStrokeColor = polygonStrokeColor,
                polygonStrokeWidth = polygonStrokeWidth,
                // Pass callbacks to update values
                onPolylineColorChange = { polylineColor = it },
                onPolylineWidthChange = { polylineWidth = it },
                onPolygonFillColorChange = { polygonFillColor = it },
                onPolygonStrokeColorChange = { polygonStrokeColor = it },
                onPolygonStrokeWidthChange = { polygonStrokeWidth = it },
                onDismiss = { showCustomization = false } // Close dialog callback
            )
        }
    }
}

/**
 * Reusable dialog component for displaying information
 * Used for both trail and park information displays
 *
 * @param title The dialog title
 * @param info The information text to display
 * @param onDismiss Callback function when dialog is dismissed
 */
@Composable
fun InfoDialog(
    title: String,
    info: String,
    onDismiss: () -> Unit
) {
    AlertDialog(
        onDismissRequest = onDismiss, // Called when user taps outside dialog
        title = {
            Text(title, style = MaterialTheme.typography.headlineSmall)
        },
        text = {
            Text(info) // Display the information content
        },
        confirmButton = {
            TextButton(onClick = onDismiss) {
                Text("Close")
            }
        }
    )
}

/**
 * Customization dialog for modifying polyline and polygon appearance
 * Allows users to change colors and widths for both overlays
 *
 * @param polylineColor Current polyline color
 * @param polylineWidth Current polyline width
 * @param polygonFillColor Current polygon fill color
 * @param polygonStrokeColor Current polygon stroke color
 * @param polygonStrokeWidth Current polygon stroke width
 * @param onPolylineColorChange Callback when polyline color changes
 * @param onPolylineWidthChange Callback when polyline width changes
 * @param onPolygonFillColorChange Callback when polygon fill color changes
 * @param onPolygonStrokeColorChange Callback when polygon stroke color changes
 * @param onPolygonStrokeWidthChange Callback when polygon stroke width changes
 * @param onDismiss Callback when dialog is dismissed
 */
@Composable
fun CustomizationDialog(
    polylineColor: Color,
    polylineWidth: Float,
    polygonFillColor: Color,
    polygonStrokeColor: Color,
    polygonStrokeWidth: Float,
    onPolylineColorChange: (Color) -> Unit,
    onPolylineWidthChange: (Float) -> Unit,
    onPolygonFillColorChange: (Color) -> Unit,
    onPolygonStrokeColorChange: (Color) -> Unit,
    onPolygonStrokeWidthChange: (Float) -> Unit,
    onDismiss: () -> Unit
) {
    AlertDialog(
        onDismissRequest = onDismiss,
        title = { Text("Customize Overlays") },
        text = {
            // Scrollable column with all customization options
            Column(
                modifier = Modifier
                    .fillMaxWidth()
                    .heightIn(max = 500.dp), // Limit height to prevent overflow
                verticalArrangement = Arrangement.spacedBy(16.dp) // Space between items
            ) {
                Text("Trail (Polyline)", style = MaterialTheme.typography.titleMedium)

                // Polyline color selection
                Text("Color")
                Row(horizontalArrangement = Arrangement.spacedBy(8.dp)) {
                    // Color option buttons
                    ColorButton(Color.Blue, polylineColor == Color.Blue) {
                        onPolylineColorChange(Color.Blue)
                    }
                    ColorButton(Color.Red, polylineColor == Color.Red) {
                        onPolylineColorChange(Color.Red)
                    }
                    ColorButton(Color.Green, polylineColor == Color.Green) {
                        onPolylineColorChange(Color.Green)
                    }
                    ColorButton(Color.Magenta, polylineColor == Color.Magenta) {
                        onPolylineColorChange(Color.Magenta)
                    }
                }

                // Polyline width slider
                Text("Width: ${polylineWidth.toInt()}px")
                Slider(
                    value = polylineWidth,
                    onValueChange = onPolylineWidthChange,
                    valueRange = 5f..30f // Min: 5px, Max: 30px
                )

                HorizontalDivider() // Visual separator

                Text("Park (Polygon)", style = MaterialTheme.typography.titleMedium)

                // Polygon fill color selection
                Text("Fill Color")
                Row(horizontalArrangement = Arrangement.spacedBy(8.dp)) {
                    // Semi transparent color options for fill
                    ColorButton(
                        Color.Green.copy(alpha = 0.3f),
                        polygonFillColor == Color.Green.copy(alpha = 0.3f)
                    ) {
                        onPolygonFillColorChange(Color.Green.copy(alpha = 0.3f))
                    }
                    ColorButton(
                        Color.Yellow.copy(alpha = 0.3f),
                        polygonFillColor == Color.Yellow.copy(alpha = 0.3f)
                    ) {
                        onPolygonFillColorChange(Color.Yellow.copy(alpha = 0.3f))
                    }
                    ColorButton(
                        Color.Cyan.copy(alpha = 0.3f),
                        polygonFillColor == Color.Cyan.copy(alpha = 0.3f)
                    ) {
                        onPolygonFillColorChange(Color.Cyan.copy(alpha = 0.3f))
                    }
                }

                // Polygon stroke  color selection
                Text("Stroke Color")
                Row(horizontalArrangement = Arrangement.spacedBy(8.dp)) {
                    // Solid color options for stroke
                    ColorButton(Color.Green, polygonStrokeColor == Color.Green) {
                        onPolygonStrokeColorChange(Color.Green)
                    }
                    ColorButton(Color.Yellow, polygonStrokeColor == Color.Yellow) {
                        onPolygonStrokeColorChange(Color.Yellow)
                    }
                    ColorButton(Color.Cyan, polygonStrokeColor == Color.Cyan) {
                        onPolygonStrokeColorChange(Color.Cyan)
                    }
                    ColorButton(Color.Black, polygonStrokeColor == Color.Black) {
                        onPolygonStrokeColorChange(Color.Black)
                    }
                }

                // Polygon stroke width slider
                Text("Stroke Width: ${polygonStrokeWidth.toInt()}px")
                Slider(
                    value = polygonStrokeWidth,
                    onValueChange = onPolygonStrokeWidthChange,
                    valueRange = 2f..15f // Min: 2px, Max: 15px
                )
            }
        },
        confirmButton = {
            TextButton(onClick = onDismiss) {
                Text("Done")
            }
        }
    )
}

/**
 * Custom color button component with selection indicator
 * Displays a colored box that shows visual feedback when selected
 *
 * @param color The color to display
 * @param isSelected Whether this color is currently selected
 * @param onClick Callback when button is clicked
 */
@Composable
fun ColorButton(
    color: Color,
    isSelected: Boolean,
    onClick: () -> Unit
) {
    // Create a layered box with selection indicator
    Box(
        modifier = Modifier
            .size(40.dp)
            .background(color, RoundedCornerShape(8.dp)) // Outer color layer
            .then(
                if (isSelected) Modifier.padding(4.dp)    // Add padding if selected
                else Modifier
            )
            .background(
                if (isSelected) Color.White else Color.Transparent, // White ring if selected
                RoundedCornerShape(4.dp)
            )
            .then(
                if (isSelected) Modifier.padding(4.dp)    // Inner padding if selected
                else Modifier
            )
            .background(color, RoundedCornerShape(2.dp)) // Inner color layer
    ) {
        // Transparent button to capture clicks
        Button(
            onClick = onClick,
            modifier = Modifier.fillMaxSize(),
            colors = ButtonDefaults.buttonColors(containerColor = Color.Transparent),
            contentPadding = PaddingValues(0.dp)
        ) {}
    }
}