package com.volleylord.core.ui.components

import androidx.compose.foundation.border
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Clear
import androidx.compose.material.icons.filled.Search
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.OutlinedTextFieldDefaults
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.unit.dp
import com.volleylord.core.ui.theme.Dark
import com.volleylord.core.ui.theme.Gray
import com.volleylord.core.ui.theme.LightGray
import com.volleylord.core.ui.theme.PrimaryRed
import com.volleylord.core.ui.theme.ComponentSizes
import com.volleylord.core.ui.theme.Shapes

/**
 * Search bar component matching Figma design specifications.
 *
 * @param query The current search query text.
 * @param onQueryChange Callback when the search query changes.
 * @param placeholder The placeholder text to display when the field is empty.
 * @param modifier Modifier to be applied to the search bar.
 */
@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun SearchBar(
    query: String,
    onQueryChange: (String) -> Unit,
    placeholder: String = "Search",
    modifier: Modifier = Modifier
) {
    OutlinedTextField(
        value = query,
        onValueChange = onQueryChange,
        modifier = modifier
            .fillMaxWidth()
            .height(ComponentSizes.searchBarHeight), // fixed height
        placeholder = {
            Text(
                text = placeholder,
                style = MaterialTheme.typography.bodyMedium,
                color = Gray // 0xFF868686 из Figma
            )
        },
        leadingIcon = {
            Box(
                modifier = Modifier
                    .size(ComponentSizes.searchIconSize)
                    .border(
                        width = ComponentSizes.searchIconBorderWidth,
                        color = PrimaryRed,
                        shape = RoundedCornerShape(2.dp)
                    ),
                contentAlignment = Alignment.Center
            ) {
                Icon(
                    imageVector = Icons.Default.Search,
                    contentDescription = "Search",
                    modifier = Modifier.size(12.dp),
                    tint = PrimaryRed
        )
      }
    },
    trailingIcon = {
      if (query.isNotEmpty()) {
        IconButton(
          onClick = { onQueryChange("") }
        ) {
          Icon(
            imageVector = Icons.Default.Clear,
            contentDescription = "Clear",
            tint = Gray
          )
        }
      }
    },
    singleLine = true,
    shape = RoundedCornerShape(Shapes.medium),
    colors = OutlinedTextFieldDefaults.colors(
      focusedContainerColor = LightGray,
      unfocusedContainerColor = LightGray,
      focusedBorderColor = Color.Transparent,
      unfocusedBorderColor = Color.Transparent,
      focusedTextColor = Dark,
      unfocusedTextColor = Dark,
      cursorColor = Dark,
      focusedPlaceholderColor = Gray,
      unfocusedPlaceholderColor = Gray
    ),
    textStyle = MaterialTheme.typography.bodyMedium.copy(
      color = Dark
    )
  )
}

