package com.igor.finansee.view.screens

import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ExitToApp
import androidx.compose.material.icons.filled.AccountCircle
import androidx.compose.material.icons.filled.CalendarToday
import androidx.compose.material.icons.filled.ChevronRight
import androidx.compose.material.icons.filled.Edit
import androidx.compose.material.icons.filled.Group
import androidx.compose.material.icons.filled.Person
import androidx.compose.material.icons.filled.WorkspacePremium
import androidx.compose.material3.*
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.navigation.NavHostController
import com.igor.finansee.data.models.User
import com.igor.finansee.view.theme.*
import java.time.format.DateTimeFormatter
import java.util.Locale

@Composable
fun ProfileScreen(
    navController: NavHostController,
    currentUser: User,
    modifier: Modifier = Modifier
) {

    LazyColumn(
        modifier = modifier
            .fillMaxSize()
            .background(MaterialTheme.colorScheme.background)
            .padding(horizontal = 16.dp),
        horizontalAlignment = Alignment.CenterHorizontally
    ) {
        item {
            Spacer(modifier = Modifier.height(24.dp))
            ProfileHeader(currentUser)
            Spacer(modifier = Modifier.height(32.dp))
        }

        item {
            ProfileMenuItem(
                icon = Icons.Filled.Person,
                title = "Meu cadastro",
                onClick = { /* TODO: Navigate to Meu cadastro */ }
            )
        }
        item {
            ProfileMenuItem(
                icon = Icons.Filled.WorkspacePremium,
                title = "FinanSee Premium",
                onClick = { /* TODO: Navigate to Premium screen or action */ }
            )
        }
        item {
            ProfileMenuItem(
                icon = Icons.Filled.Group,
                title = "Amigos",
                onClick = { /* TODO: Navigate to Amigos screen */ }
            )
        }
        item {
            Spacer(modifier = Modifier.height(16.dp))
            ProfileMenuItem(
                icon = Icons.AutoMirrored.Filled.ExitToApp,
                title = "Encerrar sessão",
                isLogout = true,
                onClick = { /* TODO: Handle logout */ }
            )
            Spacer(modifier = Modifier.height(24.dp))
        }
    }
}

@Composable
fun ProfileHeader(user: User) {
    Column(
        horizontalAlignment = Alignment.CenterHorizontally,
        modifier = Modifier.fillMaxWidth()
    ) {
        Box(contentAlignment = Alignment.BottomEnd) {
            if (user.fotoPerfil != null) {
                Image(
                    painter = painterResource(id = user.fotoPerfil),
                    contentDescription = "Foto de perfil",
                    modifier = Modifier
                        .size(100.dp)
                        .clip(CircleShape)
                        .border(2.dp, Color.LightGray, CircleShape),
                    contentScale = ContentScale.Crop
                )
            } else {
                Icon(
                    imageVector = Icons.Filled.AccountCircle,
                    contentDescription = "Foto de perfil",
                    modifier = Modifier
                        .size(100.dp)
                        .clip(CircleShape)
                        .background(Color.LightGray.copy(alpha = 0.5f)),
                    tint = IconColorLight.copy(alpha = 0.8f)
                )
            }
            Box(
                modifier = Modifier
                    .size(32.dp)
                    .clip(CircleShape)
                    .background(LightPurple)
                    .clickable { /* TODO: Handle edit profile picture */ }
                    .padding(6.dp),
                contentAlignment = Alignment.Center
            ) {
                Icon(
                    imageVector = Icons.Filled.Edit,
                    contentDescription = "Editar foto",
                    tint = Color.White,
                    modifier = Modifier.size(18.dp)
                )
            }
        }
        Spacer(modifier = Modifier.height(16.dp))
        Text(
            text = user.name,
            fontSize = 22.sp,
            fontWeight = FontWeight.Bold,
            color = TextPrimaryLight
        )
        Text(
            text = user.email,
            fontSize = 14.sp,
            color = TextSecondaryLight
        )
        Spacer(modifier = Modifier.height(24.dp))
        Card(
            modifier = Modifier.fillMaxWidth(),
            shape = RoundedCornerShape(12.dp),
            colors = CardDefaults.cardColors(containerColor = LightCardBackgroundColor),
            elevation = CardDefaults.cardElevation(defaultElevation = 2.dp)
        ) {
            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(horizontal = 16.dp, vertical = 12.dp),
                verticalAlignment = Alignment.CenterVertically,
                horizontalArrangement = Arrangement.SpaceAround
            ) {
                ProfileInfoItem(
                    icon = Icons.Filled.WorkspacePremium,
                    text = if (user.statusPremium) "Status Premium" else "Status Grátis"
                )
                val dateFormatter = DateTimeFormatter.ofPattern("dd/MMM", Locale("pt", "BR"))
                ProfileInfoItem(
                    icon = Icons.Filled.CalendarToday,
                    text = "No FinanSee desde ${user.registrationDate.format(dateFormatter)}"
                )
            }
        }
    }
}

@Composable
fun ProfileInfoItem(icon: ImageVector, text: String) {
    Row(verticalAlignment = Alignment.CenterVertically) {
        Icon(
            imageVector = icon,
            contentDescription = null,
            tint = IconColorLight,
            modifier = Modifier.size(20.dp)
        )
        Spacer(modifier = Modifier.width(8.dp))
        Text(
            text = text,
            fontSize = 13.sp,
            color = TextSecondaryLight,
            fontWeight = FontWeight.Medium
        )
    }
}

@Composable
fun ProfileMenuItem(
    icon: ImageVector,
    title: String,
    isLogout: Boolean = false,
    onClick: () -> Unit
) {
    val itemContentColor = if (isLogout) LightPurple else TextPrimaryLight
    val itemIconColor = if (isLogout) LightPurple else IconColorLight

    Card(
        modifier = Modifier
            .fillMaxWidth()
            .padding(vertical = 6.dp)
            .clickable(onClick = onClick),
        shape = RoundedCornerShape(12.dp),
        colors = CardDefaults.cardColors(
            containerColor = LightCardBackgroundColor,
            contentColor = itemContentColor
        ),
        elevation = CardDefaults.cardElevation(defaultElevation = 1.dp)
    ) {
        Row(
            modifier = Modifier
                .padding(horizontal = 16.dp, vertical = 14.dp),
            verticalAlignment = Alignment.CenterVertically
        ) {
            Icon(
                imageVector = icon,
                contentDescription = title,
                modifier = Modifier.size(24.dp),
                tint = itemIconColor
            )
            Spacer(modifier = Modifier.width(16.dp))
            Text(
                text = title,
                fontSize = 16.sp,
                fontWeight = FontWeight.Medium,
                color = itemContentColor,
                modifier = Modifier.weight(1f)
            )
            if (!isLogout) {
                Icon(
                    imageVector = Icons.Filled.ChevronRight,
                    contentDescription = "Ir para $title",
                    tint = IconColorLight.copy(alpha = 0.7f)
                )
            }
        }
    }
}