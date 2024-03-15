package com.me.harris.ui.widget

import androidx.compose.foundation.*
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.*
import androidx.compose.ui.graphics.*
import androidx.compose.ui.tooling.preview.*
import androidx.compose.ui.unit.*



@Composable
fun RoundedCardWithClick(textModifier:Modifier = Modifier.padding(10.dp), text:String, onclick:()->Unit ) {
    Card(
        shape = RoundedCornerShape(10.dp),
        elevation = CardDefaults.cardElevation(
            defaultElevation = 6.dp
        ),
        colors = CardDefaults.cardColors(
            contentColor = Color.Gray,
            containerColor = Color.White
        ),
        modifier =   Modifier.padding(10.dp).wrapContentHeight(Alignment.CenterVertically).wrapContentWidth(Alignment.CenterHorizontally),
        onClick = onclick
    ) {
        Text(
            text = text,
            modifier = textModifier
        )
    }
}


@Preview
@Composable
fun SimpleCardPreview() {
    val paddingModifier = Modifier.padding(10.dp)
    Card(
        shape = RoundedCornerShape(10.dp),
        elevation = CardDefaults.cardElevation(
            defaultElevation = 10.dp
        ),
        colors = CardDefaults.cardColors(
            contentColor = Color.Gray,
            containerColor = Color.White
        ),
        modifier = paddingModifier
    ) {
        Text(
            text = "Simple Card with elevation",
            modifier = paddingModifier.align(alignment = Alignment.CenterHorizontally).padding(20.dp)
        )
    }
}




// In Compose, sequence of modifiers matter.
//
//If you use padding before everything else, it behaves as padding.
//If you use padding after everything else, it behaves as margin.
// https://stackoverflow.com/questions/62939473/how-to-add-margin-in-jetpack-compose
@Preview
@Composable
fun CardWithContentColor() {
    Card(
        shape = RoundedCornerShape(10.dp),
        elevation = CardDefaults.cardElevation(
            defaultElevation = 10.dp
        ),
        colors = CardDefaults.cardColors(),
        modifier = Modifier
            .padding(0.dp)  // padding
            .wrapContentHeight(Alignment.CenterVertically)
            .wrapContentWidth(Alignment.CenterHorizontally)
            .padding(0.dp) // margin
    ) {
        Column() {
            Text(
                text = "some text",
                modifier = Modifier.padding(5.dp).background(Color.LightGray),
                color = Color.Red,
                fontSize = TextUnit(20F, TextUnitType.Sp)
            )
            Text(
                text = "Text with card custom color",
                color = Color.Black,
                modifier = Modifier.padding(10.dp)
            )

        }
    }
}
