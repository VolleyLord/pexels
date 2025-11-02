package com.volleylord.core.ui.components

import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.wrapContentHeight
import androidx.compose.foundation.lazy.LazyRow
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import com.volleylord.core.domain.models.Collection
import com.volleylord.core.ui.theme.Dark
import com.volleylord.core.ui.theme.LightGray
import com.volleylord.core.ui.theme.PrimaryRed
import com.volleylord.core.ui.theme.White

/**
 * Horizontal list of featured collections (categories) for search.
 * Collections are displayed as chips that can be selected.
 *
 * @param collections List of collections to display.
 * @param onCollectionClick Callback when a collection is clicked.
 * @param modifier Modifier to be applied to the component.
 */
@Composable
fun FeaturedCollections(
  collections: List<Collection>,
  onCollectionClick: (Collection) -> Unit,
  modifier: Modifier = Modifier
) {
  LazyRow(
    modifier = modifier
      .fillMaxWidth()
      .wrapContentHeight(), // adaptive height
    horizontalArrangement = Arrangement.spacedBy(10.dp), // gap: 10px between chips
    contentPadding = androidx.compose.foundation.layout.PaddingValues(
      horizontal = 0.dp
    )
  ) {
    items(
      items = collections,
      key = { it.id }
    ) { collection ->
      CollectionChip(
        collection = collection,
        onClick = { onCollectionClick(collection) },
        modifier = Modifier.padding(vertical = 4.dp)
      )
    }
  }
}

/**
 * Individual collection chip component.
 *
 * @param collection The collection to display.
 * @param onClick Callback when the chip is clicked.
 * @param modifier Modifier to be applied to the chip.
 */
@Composable
private fun CollectionChip(
  collection: Collection,
  onClick: () -> Unit,
  modifier: Modifier = Modifier
) {
  val backgroundColor = if (collection.isSelected) {
    PrimaryRed
  } else {
    LightGray
  }

  val textColor = if (collection.isSelected) {
    White
  } else {
    Dark
  }

      Box(
    modifier = modifier
      .height(com.volleylord.core.ui.theme.ComponentSizes.chipHeight)
      .clip(RoundedCornerShape(100.dp))
      .background(backgroundColor)
      .clickable(onClick = onClick)
      .padding(
        horizontal = 20.dp,
        vertical = 10.dp
      ),
    contentAlignment = Alignment.Center
  ) {
    Text(
      text = collection.name,
      style = MaterialTheme.typography.labelLarge.copy(
        fontWeight = if (collection.isSelected) FontWeight.SemiBold else FontWeight.Normal
      ),
      color = textColor
    )
  }
}

