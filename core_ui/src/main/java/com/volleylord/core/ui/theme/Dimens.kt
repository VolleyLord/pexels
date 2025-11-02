package com.volleylord.core.ui.theme

import androidx.compose.ui.unit.dp

object Spacing {
  // Extra small spacing - between small elements like icons
  val xs = 4.dp
  
  // Small
  val sm = 8.dp
  
  // Medium
  val md = 16.dp
  
  /** Standard horizontal padding ( 6.4% from 375px) - adaptive */
  val horizontal = 24.dp
  
  /** Search bar top padding ( 6.9% from 812px) - adaptive */
  val searchBarTop = 28.dp
  
  /** Featured Collections top padding (24dp from SearchBar) - adaptive */
  val featuredCollectionsTop = 24.dp
  
  /** Images grid top padding (23dp from FeaturedCollections) - adaptive */
  val imagesGridTop = 23.dp
  
  /** Large spacing (24dp) - between major sections */
  val lg = 24.dp
  
  /** Extra large spacing (32dp) - large gaps */
  val xl = 32.dp
  
  /** Extra extra large spacing (48dp) - maximum spacing */
  val xxl = 48.dp
}

/**
 * Shape corner radius values from Figma design.
 */
object Shapes {
  /** Small corner radius (8dp) - for buttons */
  val small = 8.dp
  
  /** Medium corner radius (12dp) - for cards, search bar */
  val medium = 12.dp
  
  /** Large corner radius (16dp) - for large cards */
  val large = 16.dp
  
  /** Extra large corner radius (24dp) - for very large elements */
  val xlarge = 24.dp
  
  /** Pill shape (100dp) - for category chips - from Figma */
  val pill = 100.dp
}

/**
 * Component size values from Figma design.
 */
object ComponentSizes {
  /** Search bar height (50dp) - fixed */
  val searchBarHeight = 50.dp
  
  /** Search icon size (16x16dp) - fixed */
  val searchIconSize = 16.dp
  
  /** Search icon border width (1.4dp) - fixed */
  val searchIconBorderWidth = 1.4.dp
  
  /** Button height (48dp) - standard */
  val buttonHeight = 48.dp
  
  /** Chip/Collection item height (38dp) - fixed */
  val chipHeight = 38.dp
  
  /** Featured Collections container height (39dp) - fixed */
  val featuredCollectionsHeight = 39.dp
  
  /** Icon size (24dp) - standard */
  val iconSize = 24.dp
  
  /** Large icon size (32dp) */
  val iconSizeLarge = 32.dp
  
  /** Minimum photo grid item size (120dp)  */
  val photoGridMinSize = 120.dp
  
  /** Bottom bar height (64dp) - fixed */
  val bottomBarHeight = 64.dp
  
  /** Selector icon size (24x2dp) - fixed */
  val selectorIconWidth = 24.dp
  val selectorIconHeight = 2.dp
}

