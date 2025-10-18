package com.example.itda.ui.common.components

import androidx.compose.foundation.Image
import androidx.compose.foundation.layout.size
import androidx.compose.material3.Surface
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.unit.dp

@Composable
fun CircleImage(
    imgId: Int, // TODO - logo imgURL : String 으로 변경해야함
    contentDescription: String,
) {
    Surface(
        shape = androidx.compose.foundation.shape.CircleShape,
        modifier = Modifier.size(36.dp)
    ) {
        //TODO - logo image ID? 어떤 타입으로 넣어둘건지?
        // Image(painter = painterResource(id = logo), contentDescription = department)
        Image(
            painter = painterResource(
                id = imgId
            ),
            contentDescription = contentDescription,
            modifier = Modifier.size(36.dp)
        )
    }
}