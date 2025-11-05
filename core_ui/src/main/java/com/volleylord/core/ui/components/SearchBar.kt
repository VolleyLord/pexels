package com.volleylord.core.ui.components

import androidx.compose.foundation.Image
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Clear
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
import androidx.compose.foundation.text.KeyboardActions
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.input.ImeAction
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.volleylord.core.ui.theme.Dark
import com.volleylord.core.ui.theme.Gray
import com.volleylord.core.ui.theme.LightGray
import com.volleylord.core.ui.theme.PrimaryRed
import com.volleylord.core.ui.theme.ComponentSizes
import com.volleylord.core.ui.theme.Shapes

/**
 * Search bar component matching Figma design specifications.
 * @param query The current search query text.
 * @param onQueryChange Callback when the search query changes.
 * @param onSearchClick Callback when search action is triggered (e.g., IME action or button click).
 * @param placeholder placeholder text
 * @param modifier
 * @param searchIconResId
 */
@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun SearchBar(
    query: String,
    onQueryChange: (String) -> Unit,
    onSearchClick: (() -> Unit)? = null,
    placeholder: String = "Search",
    modifier: Modifier = Modifier,
    searchIconResId: Int = android.R.drawable.ic_menu_search
) {
    val searchBarHeight = 50.dp
    val iconSize = 16.dp

    OutlinedTextField(
        value = query,
        onValueChange = onQueryChange,
        modifier = modifier
            .fillMaxWidth()
            .height(searchBarHeight),
        keyboardOptions = KeyboardOptions(
            imeAction = ImeAction.Search
        ),
        keyboardActions = KeyboardActions(
            onSearch = {
                onSearchClick?.invoke()
            }
        ),
        placeholder = {
            Text(
                text = placeholder,
                style = MaterialTheme.typography.bodyMedium,
                color = Gray
            )
        },
        leadingIcon = {
            Image(
                painter = painterResource(id = searchIconResId),
                contentDescription = "Search",
                modifier = Modifier.size(iconSize)
            )
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
        shape = RoundedCornerShape(50.dp),
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
