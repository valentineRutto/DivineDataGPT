package com.valentinerutto.divinedatagpt.ui.theme.screens

import ai.koog.prompt.text.text
import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.WindowInsets
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.ime
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.systemBars
import androidx.compose.foundation.layout.union
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.layout.windowInsetsPadding
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.text.BasicTextField
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.Send
import androidx.compose.material.icons.filled.Add
import androidx.compose.material.icons.filled.Menu
import androidx.compose.material.icons.filled.Mic
import androidx.compose.material.icons.filled.Star
import androidx.compose.material.icons.outlined.Add
import androidx.compose.material.icons.outlined.Refresh

import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateListOf
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.geometry.isEmpty
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.ColorFilter
import androidx.compose.ui.graphics.SolidColor
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.res.colorResource
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import com.valentinerutto.divinedatagpt.R
import com.valentinerutto.divinedatagpt.ui.theme.BottomBarBackground
import com.valentinerutto.divinedatagpt.ui.theme.DarkBackground
import com.valentinerutto.divinedatagpt.ui.theme.IconBlue
import com.valentinerutto.divinedatagpt.ui.theme.IconGreen
import com.valentinerutto.divinedatagpt.ui.theme.IconPurple
import com.valentinerutto.divinedatagpt.ui.theme.PurpleButton
import com.valentinerutto.divinedatagpt.ui.theme.TextGray
import com.valentinerutto.divinedatagpt.ui.theme.TextWhite
import io.ktor.client.request.request
import org.koin.dsl.module


@Composable
fun ChatPageScreen(modifier: Modifier) {
    var inputText by remember { mutableStateOf("") }
    val messages = remember { mutableStateListOf<String>() }

    Box(
        modifier = modifier
            .fillMaxSize()
            .windowInsetsPadding(WindowInsets.systemBars.union(WindowInsets.ime))
            .background(DarkBackground)
    ) {
        Column(
            modifier = modifier.fillMaxSize()
        ) {
            TopBar()

            Box(
                modifier = modifier
                    .weight(1f)
                    .fillMaxWidth(),
                contentAlignment = Alignment.Center
            ) {
                if (messages.isEmpty()) {
                    ChatContent()
                } else {
                    RequestLazyList(itemsList = messages)
                }            }

            BottomBar(inputText = inputText, onValueChange = {inputText = it}, onSendClick ={
                if (inputText.isNotEmpty()) messages.add(inputText)
                inputText = ""
            } )


        }
    }
}

@Composable
fun TopBar() {
    Row(
        modifier = Modifier
            .fillMaxWidth()
            .padding(16.dp),
        horizontalArrangement = Arrangement.SpaceBetween,
        verticalAlignment = Alignment.CenterVertically
    ) {
        IconButton(
            onClick = { },
            modifier = Modifier
                .size(48.dp)
                .clip(CircleShape)
                .background(Color(0xFF1E1E1E))
        ) {
            Icon(imageVector = Icons.Default.Menu, contentDescription = "Menu", tint = TextWhite)
        }

        Button(
            onClick = { },
            colors = ButtonDefaults.buttonColors(containerColor = PurpleButton),
            shape = RoundedCornerShape(50),
            modifier = Modifier.height(40.dp)
        ) {

            Image(
                painter = painterResource(id = R.drawable.logo),
                contentDescription = null,
                colorFilter = ColorFilter.tint(TextWhite),
                modifier = Modifier.size(16.dp)
            )

            Spacer(modifier = Modifier.width(8.dp))
            Text(
                text = "Divine Data",
                color = TextWhite
            )
        }

        // Refresh button
        IconButton(
            onClick = { },
            modifier = Modifier
                .size(48.dp)
                .clip(CircleShape)
                .background(Color(0xFF1E1E1E))
        ) {
            Icon(
                imageVector = Icons.Outlined.Refresh,
                contentDescription = "Refresh",
                tint = TextWhite
            )
        }
    }
}

@Composable
fun ChatContent() {
    Column(
        horizontalAlignment = Alignment.CenterHorizontally,
        verticalArrangement = Arrangement.Center,
        modifier = Modifier.padding(16.dp)
    ) {
        Text(
            text = "How are you feeling today?",
            style = MaterialTheme.typography.titleLarge,
            color = TextWhite,
            textAlign = TextAlign.Center
        )

        Spacer(modifier = Modifier.height(32.dp))

        ActionButtonsRow()
    }
}

@Composable
fun RequestLazyList(itemsList: List<String>){
    LazyColumn{
        items(itemsList){ item ->
            RequestListItem(item)
        }
    }
}
@Composable
fun RequestListItem(text: String ) {
    Box(modifier = Modifier
        .fillMaxWidth()
        .padding(start = 48.dp, end = 4.dp, top = 8.dp, bottom = 8.dp))
    Text(text=text, color = TextWhite, textAlign = TextAlign.Center,
        modifier = Modifier
            .background(
                color = colorResource(id = R.color.purple_200),
                shape = RoundedCornerShape(8.dp)
            )
            .padding(8.dp))

}
@Composable
fun ActionButtonsRow() {
    Column(
        horizontalAlignment = Alignment.CenterHorizontally
    ) {
        Row(
            horizontalArrangement = Arrangement.spacedBy(16.dp)
        ) {
            ActionButton(
                icon = Icons.Default.Star,
                text = "GrateFull",
                iconTint = IconGreen
            )

            ActionButton(
                icon = Icons.Default.Star,
                text = "Joyful",
                iconTint = IconBlue
            )

        }

        Spacer(modifier = Modifier.height(16.dp))

        Row(
            horizontalArrangement = Arrangement.spacedBy(16.dp)
        ) {
            ActionButton(
                icon = Icons.Default.Star,
                text = "Anxious",
                iconTint = IconPurple
            )

            ActionButton(
                icon = Icons.Default.Star,
                text = "Discouraged",
                iconTint = TextGray
            )
        }
    }
}

@Composable
fun ActionButton(
    icon: ImageVector,
    text: String,
    iconTint: Color
) {
    Button(
        onClick = { },
        colors = ButtonDefaults.buttonColors(containerColor = Color(0xFF1E1E1E)),
        shape = RoundedCornerShape(50),
        modifier = Modifier
            .height(56.dp)
            .width(170.dp)
    ) {
        Row(
            verticalAlignment = Alignment.CenterVertically
        ) {
            Icon(
                imageVector = icon,
                contentDescription = null,
                tint = iconTint,
                modifier = Modifier.size(24.dp)
            )
            Spacer(modifier = Modifier.width(8.dp))
            Text(
                text = text,
                color = TextWhite
            )
        }
    }
}

@Composable
fun BottomBar( inputText: String,
               onValueChange: (String) -> Unit,
               onSendClick: () -> Unit) {
    Box(
        modifier = Modifier
            .fillMaxWidth()
            .background(BottomBarBackground)
            .padding(16.dp)
    ) {

        Spacer(modifier = Modifier.width(8.dp))

        Row(
            modifier = Modifier
                .fillMaxWidth()
                .height(48.dp)
                .clip(RoundedCornerShape(24.dp))
                .background(Color(0xFF2C2C2E))
                .padding(horizontal = 16.dp),
            horizontalArrangement = Arrangement.SpaceBetween,
            verticalAlignment = Alignment.CenterVertically
        ) {

                IconButton(
                    onClick = { },
                    modifier = Modifier
                        .size(28.dp)
                        .clip(CircleShape)
                        .background(Color.Gray)

                ) {
                    Icon(
                        imageVector = Icons.Filled.Add,
                        contentDescription = "Add",
                        tint = TextWhite
                    )
                }

                Spacer(modifier = Modifier.width(8.dp))
Box(modifier = Modifier.weight(1f), contentAlignment = Alignment.CenterStart){
    BasicTextField(value = inputText, onValueChange= onValueChange, modifier = Modifier.fillMaxWidth(),
        textStyle = TextStyle(color = TextWhite), cursorBrush = SolidColor(TextWhite), singleLine = true)
    if (inputText.isEmpty()){
                Text(
                    text = "How are you feeling today?",
                    color = TextGray,

                )}
}

                Spacer(modifier = Modifier.width(8.dp))

                IconButton(
                    onClick = { },
                    modifier = Modifier.size(32.dp)
                ) {
                    Icon(
                        imageVector = Icons.Default.Mic,
                        contentDescription = "Microphone",
                        tint = TextGray
                    )
                }

                Spacer(modifier = Modifier.width(8.dp))

                IconButton(
                    onClick = { onSendClick},
                    modifier = Modifier
                        .size(28.dp)
                        .clip(CircleShape)
                        .background(Color.Gray)
                ) {
                    Icon(
                        imageVector = Icons.AutoMirrored.Filled.Send,
                        contentDescription = "Send",
                        tint = TextWhite,
                        modifier = Modifier.size(16.dp)
                    )
                }
            }
        }

}
