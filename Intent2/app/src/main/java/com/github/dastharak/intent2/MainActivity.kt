package com.github.dastharak.intent2

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.tooling.preview.Preview
import com.github.dastharak.intent2.ui.theme.Intent2Theme
import android.content.Intent
//import android.os.Bundle
import android.util.Log
import android.widget.Toast

import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.Button
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text

import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.unit.dp
import androidx.constraintlayout.compose.ConstraintLayout
import androidx.compose.runtime.getValue
import androidx.compose.runtime.setValue


//Activity does not manually modify the views.
class MainActivity : ComponentActivity() {
    private val tag:String = MainActivity::class.simpleName.toString()
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()// Configures the system bars to draw the UI behind them transparently.
        // setContent acts as the bridge between legacy views and the Compose runtime.
        setContent {
            Intent2Theme {
                /**
                 * State Hoisting : Single Source of Truth
                 * mutableStateOf(""): Creates an observable variable wrapper.
                 * When the internal string changes forces Compose runtime engine to trigger updating the UI.
                 * remember { }: Tells the compiler to preserve this state inside memory across UI updates.
                 * Without 'remember', the variables would reset to empty strings every time the screen redraws.
                 * Delegated Properties: Simplifies Kotlin syntax so we can read/write the variables directly
                 * as plain Strings, instead of constantly writing '.value'.
                 */
                var titleText by remember { mutableStateOf("") }
                var descriptionText by remember { mutableStateOf("") }

                //Scaffold(modifier = Modifier.fillMaxSize()) { innerPadding ->
                //Greeting(
                //   name = "Android",
                // modifier = Modifier.fillMaxSize() //.padding(innerPadding)
                //)
                //  context from the environment provider
                val context = LocalContext.current
                ComplexConstraintSample(
                    title = titleText,
                    description = descriptionText,
                    onTitleChange = { titleText = it
                        Log.d(tag,titleText)
                    },             // Updates state when user types in Title field
                    onDescriptionChange = { descriptionText = it }, // Updates state when user types in Description field
                    onSubmitClicked = { title, desc ->
                        // The layout stays stateless.
                        // Control logic is executed here.

                        Toast.makeText(context, "Saved:\nTitle: $title\nDesc: $desc", Toast.LENGTH_LONG).show()
                    }

                )
                //}
            }
        }
    }
}

@Composable
fun ComplexConstraintSample(
    title: String,                                  // Data input (Title)
    description: String,                            // Data input (Description)
    onTitleChange: (String) -> Unit,                // Event callback lambda parameter
    onDescriptionChange: (String) -> Unit,          // Event callback lambda parameter
    onSubmitClicked: (String, String) -> Unit       // Event callback triggered on click

) {
    ConstraintLayout(modifier = Modifier.fillMaxSize()) {

        //createRefs() add structural reference hooks for elements inside the ConstraintLayout.
        //It acts as the declarative alternative to assigning XML IDs (`android:id="@+id/..."`).
        // initialize in one line rather than three
        val textFieldTitle = createRef()
        val textFieldDescription = createRef()
        val buttonSubmit  = createRef()
        val buttonSecond = createRef()

        /**
         * The Modifier Chain
         * Modifier.fillMaxWidth() stretches the component boundaries horizontally.
         * constrainAs() attaches the element reference hook and maps its anchor targets sequentially.
         */
        OutlinedTextField(
            value = title,   // Pushes data down to display current state value
            onValueChange = onTitleChange,  // Forwards the keyboard keypress string upstream immediately
            label = { Text("Title") },    // Material Design floating label slot parameter
            modifier = Modifier
                .fillMaxWidth(fraction = 0.75f)
                .constrainAs(textFieldTitle) {
                    top.linkTo(parent.top, margin = 32.dp)//gap from the top
                    start.linkTo(parent.start)
                    end.linkTo(parent.end)
                }
        )

        OutlinedTextField(
            value = description,
            onValueChange = onDescriptionChange,
            label = { Text("Description") },
            modifier = Modifier
                .fillMaxWidth(fraction = 0.75f)
                .constrainAs(textFieldDescription) {
                    // Point to the top of this box to the bottom of the Title box
                    top.linkTo(textFieldTitle.bottom, margin = 16.dp)
                    start.linkTo(parent.start)
                    end.linkTo(parent.end)
                }
        )

        Button(
            // The click event calls the hoisted callback parameter
            // passing the snapshots of 'title' and 'description' data upwards.
            onClick = { onSubmitClicked(title, description) },
            modifier = Modifier.constrainAs(buttonSubmit) {
                top.linkTo(textFieldDescription.bottom, margin = 24.dp)
                start.linkTo(parent.start)
                end.linkTo(parent.end)
            }
        ) {
            Text("Submit")
        }

        //  context from the environment provider
        val context = LocalContext.current
        Button(
            onClick = {
                val intent = Intent(context, MainActivity::class.java)
                context.startActivity(intent)
            },
            modifier = Modifier.constrainAs(buttonSecond) {
                top.linkTo(buttonSubmit.bottom, margin = 24.dp)
                start.linkTo(parent.start)
                end.linkTo(parent.end)
            }
        ) {
            Text("Second Activity")
        }

    }
}

@Preview(showBackground = true, showSystemUi = true)
@Composable
fun ComplexConstraintSamplePreview() {
    Intent2Theme()
     {
        ComplexConstraintSample(
            title = "Sample Title",
            description = "Sample description",
            onTitleChange = {},  // Empty lambda placeholder for preview
            onDescriptionChange = {},       // ""
            onSubmitClicked = { _, _ -> }   // ignores arguments using underscores
        )
    }
}