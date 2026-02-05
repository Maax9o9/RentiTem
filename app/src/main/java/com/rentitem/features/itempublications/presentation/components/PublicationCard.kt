package com.rentitem.features.itempublications.presentation.components

import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.*
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import coil.compose.AsyncImage
import com.rentitem.features.itempublications.domain.entities.Publication

@Composable
fun PublicationCard(publication: Publication) {
    Card(
        modifier = Modifier.fillMaxWidth().padding(8.dp),
        shape = RoundedCornerShape(16.dp),
        elevation = CardDefaults.cardElevation(4.dp)
    ) {
        Column {
            // Imagen del producto
            AsyncImage(
                model = publication.imageUrl ?: "https://via.placeholder.com/300", // Placeholder
                contentDescription = null,
                contentScale = ContentScale.Crop,
                modifier = Modifier.fillMaxWidth().height(180.dp)
            )
            Column(modifier = Modifier.padding(16.dp)) {
                Text(text = publication.title, style = MaterialTheme.typography.titleLarge, fontWeight = FontWeight.Bold)
                Text(text = "$${publication.price}", style = MaterialTheme.typography.titleMedium, color = MaterialTheme.colorScheme.primary)
                Spacer(modifier = Modifier.height(4.dp))
                Text(text = publication.description, style = MaterialTheme.typography.bodyMedium, maxLines = 2)
            }
        }
    }
}