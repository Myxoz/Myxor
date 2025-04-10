package com.myxoz.myxor

import android.content.SharedPreferences
import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.OnBackPressedCallback
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.compose.animation.AnimatedVisibility
import androidx.compose.animation.core.spring
import androidx.compose.animation.fadeIn
import androidx.compose.animation.fadeOut
import androidx.compose.animation.scaleIn
import androidx.compose.animation.scaleOut
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.horizontalScroll
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.text.BasicTextField
import androidx.compose.foundation.text.KeyboardActions
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.rounded.Add
import androidx.compose.material.icons.rounded.Check
import androidx.compose.material.icons.rounded.Delete
import androidx.compose.material.icons.rounded.Person
import androidx.compose.material3.AlertDialog
import androidx.compose.material3.Checkbox
import androidx.compose.material3.DatePicker
import androidx.compose.material3.DatePickerDialog
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.FilledTonalButton
import androidx.compose.material3.FilterChip
import androidx.compose.material3.FloatingActionButton
import androidx.compose.material3.HorizontalDivider
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.LocalTextStyle
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.ModalBottomSheet
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.material3.rememberDatePickerState
import androidx.compose.material3.rememberModalBottomSheetState
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateListOf
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.runtime.setValue
import androidx.compose.runtime.toMutableStateList
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.focus.onFocusChanged
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.SolidColor
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.platform.LocalFocusManager
import androidx.compose.ui.res.vectorResource
import androidx.compose.ui.text.AnnotatedString
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.input.ImeAction
import androidx.compose.ui.text.input.KeyboardCapitalization
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.text.input.OffsetMapping
import androidx.compose.ui.text.input.TransformedText
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import com.myxoz.myxor.ui.theme.MyxorMaterialTheme
import kotlinx.coroutines.launch
import rememberColorScheme
import java.util.Calendar
import java.util.UUID
import kotlin.math.abs

private fun Int.getColor(negTotal: Int, posTotal: Int): Color = Color(1-(this.toFloat()/posTotal), 1-(this.toFloat()/negTotal), 0f, 1f)

class MainActivity : ComponentActivity() {
    private lateinit var prefs: SharedPreferences
    private var backPressed: (()->Unit)?=null
    private val onBackPressedCallback = object: OnBackPressedCallback(true){
        override fun handleOnBackPressed() {
            backPressed?.invoke()
        }
    }
    @OptIn(ExperimentalMaterial3Api::class)
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        prefs = getSharedPreferences(localClassName, MODE_PRIVATE)
        enableEdgeToEdge()
        setContent {
            val colorScheme = rememberColorScheme(applicationContext)
            val people = remember {
                Person.parseJSONasPersonList(
                prefs.getString("data", "[]") ?: "[]"
                ).toMutableStateList()
//                mutableStateListOf(
//                    Person("Jajo1",
//                        listOf(
//                            Element("Kaufen",-500,System.currentTimeMillis())
//                        )
//                    ),
//                    Person("Jajo2",
//                        listOf(
//                            Element("Kaufen",1500,System.currentTimeMillis())
//                        )
//                    ),
//                    Person("Jajo3",
//                        listOf(
//                            Element("Kaufen",500,System.currentTimeMillis())
//                        )
//                    ),
//                    Person("Jajo4",
//                        listOf(
//                            Element("Kaufen",-2500,System.currentTimeMillis())
//                        )
//                    ),
//                )
            }
            val save = remember { ({prefs.edit().putString("data", people.map { it.asJSON() }.asJSONArray().toString()).apply()}) }
            val posTotal = people.map { it.elements.total() }.filter { it > 0 }.sum()
            val negTotal = people.map { it.elements.total() }.filter { it < 0 }.sum()
            val sheet = rememberModalBottomSheetState()
            var selectedPersonVisible by remember { mutableStateOf(false) }
            var selectedPerson: Person by remember { mutableStateOf(Person("", listOf())) }
            var personSubjectedForDeletion: UUID? by remember { mutableStateOf(null) }
            val coroutineScope = rememberCoroutineScope()
            var addingElementUUID by remember { mutableStateOf(UUID.randomUUID()) }
            var isSheetVisible by remember { mutableStateOf(false) }
            onBackPressedDispatcher.addCallback(onBackPressedCallback)
            MyxorMaterialTheme(colorScheme) {
                var datePickerSubscription: Subscription<Long>? by remember { mutableStateOf(null) }
                val datePickerState = rememberDatePickerState()
                if(datePickerSubscription!=null) {
                    DatePickerDialog(
                        {datePickerSubscription=null},
                        { TextButton({
                            datePickerState.selectedDateMillis?.let { datePickerSubscription?.send(it) }; datePickerSubscription=null
                        }) { Text("Select") }},
                    ) {DatePicker(datePickerState)}
                }
                Scaffold(modifier = Modifier.fillMaxSize()) { innerPadding ->
                    Box(Modifier.fillMaxSize()){
                        Column (
                            Modifier
                                .fillMaxSize()
                                .padding(innerPadding)
                                .padding(horizontal = 15.dp)
                                .verticalScroll(rememberScrollState())
                        ){
                            Box(
                                Modifier
                                    .fillMaxWidth()
                                    .height(200.dp), contentAlignment = Alignment.Center
                            ) {
                                Column(horizontalAlignment = Alignment.CenterHorizontally) {
                                    val totalSum = people.sumOf { it.elements.total() }
                                    Text(totalSum.centAsEuro(), style = MaterialTheme.typography.displaySmall.copy(totalSum.getColor(negTotal, posTotal), fontWeight = FontWeight.Bold))
                                    Row {
                                        Text(posTotal.centAsEuro(), style = MaterialTheme.typography.titleSmall.copy(color = Color(0xFF99FF99)), modifier = Modifier.weight(1f), textAlign = TextAlign.End)
                                        Text("·", style = MaterialTheme.typography.titleSmall.copy(fontWeight = FontWeight.W900), modifier = Modifier.width(30.dp), textAlign = TextAlign.Center)
                                        Text(negTotal.centAsEuro(), style = MaterialTheme.typography.titleSmall.copy(color = Color(0xFFFF9999)), modifier = Modifier.weight(1f))
                                    }
                                }
                            }
                            Column(Modifier.fillMaxWidth(), verticalArrangement = Arrangement.spacedBy(30.dp)) {
                                val neutral = people.filter { it.elements.total()==0 }
                                val repay = people.filter { it.elements.total()<0 }
                                val owing = people.filter { it.elements.total()>0 }
                                val onClick = {it: Person -> selectedPersonVisible=true; selectedPerson=it; backPressed={selectedPersonVisible=false; backPressed={finishAndRemoveTask()}}}
                                if(repay.isNotEmpty()) {
                                    PersonColumn("Yet to repay", Color.Red, repay, onClick)
                                }
                                if(owing.isNotEmpty()) {
                                    PersonColumn("In debt", Color.Green, owing, onClick)
                                }
                                if(neutral.isNotEmpty()){
                                    PersonColumn("Neutral", Color.Transparent, neutral, onClick)
                                }
                                IconButton({people.add(Person("New", listOf())); save()}, Modifier.fillMaxWidth()) { Icon(Icons.Rounded.Add, "add") }
                            }
                        }
                        FloatingActionButton({isSheetVisible=true;coroutineScope.launch { sheet.show() }; addingElementUUID=UUID.randomUUID() }, Modifier
                            .align(Alignment.BottomEnd)
                            .padding(15.dp)) {
                            Icon(Icons.Rounded.Add,"add")
                        }
                    }
                }
                AnimatedVisibility(selectedPersonVisible,
                    enter = fadeIn() + scaleIn(spring(stiffness = 5000f), initialScale = .5f),
                    exit = fadeOut() + scaleOut(spring(stiffness = 100f), targetScale = .75f)
                ) {
                    var person by remember(selectedPerson) {  mutableStateOf(selectedPerson) }
                    val elements = remember(selectedPerson) {person.elements.toMutableStateList()}
                    val removedUUIDs = remember { mutableStateListOf<UUID>() }
                    Box(Modifier.fillMaxSize().background(MaterialTheme.colorScheme.surfaceContainer)) {
                        Scaffold { padding ->
                            val total = selectedPerson.elements.filter { !removedUUIDs.contains(it.uuid) }.total()
                            Box(Modifier.fillMaxSize().padding(padding).padding(15.dp,0.dp)){
                                Column(
                                    Modifier
                                        .align(Alignment.BottomStart)
                                        .fillMaxWidth(),
                                    verticalArrangement = Arrangement.spacedBy(20.dp)
                                ) {
                                    Column(
                                        Modifier
                                            .weight(1f)
                                            .verticalScroll(rememberScrollState(), reverseScrolling = true),
                                        verticalArrangement = Arrangement.spacedBy(10.dp, Alignment.Bottom)
                                    ) {
                                        elements.sortedBy { it.date }.forEach { elem ->
                                            ElementComposable(
                                                elem,
                                                {if(it) removedUUIDs.add(elem.uuid) else removedUUIDs.remove(elem.uuid)},
                                                {sub, defaultValue -> datePickerSubscription=sub; datePickerState.selectedDateMillis=defaultValue}
                                            ) {
                                                elements.substituteFirst({a -> a.uuid==it.uuid}, it)
                                            }
                                        }
                                        if(elements.isEmpty()) {
                                            IconButton({personSubjectedForDeletion=person.uuid}, Modifier.fillMaxWidth()) {
                                                Icon(Icons.Rounded.Delete, "Delete")
                                            }
                                        }
                                    }
                                    HorizontalDivider()
                                    Row(
                                        Modifier
                                            .fillMaxWidth(),
                                        horizontalArrangement = Arrangement.SpaceBetween
                                    ) {
                                        EditableText(
                                            selectedPerson.name,
                                            style = MaterialTheme.typography.headlineSmall.copy(MaterialTheme.colorScheme.primary),
                                            onDone = { person=person.copy(name = it) },
                                            placeholder = "Name"
                                        )
                                        Text(total.centAsEuro(), style = MaterialTheme.typography.titleMedium.copy(color = total.getBoolColor()))
                                    }
                                    Spacer(Modifier)
                                    Row(
                                        Modifier
                                            .fillMaxWidth(),
                                        horizontalArrangement = Arrangement.End
                                    ) {
                                        TextButton({selectedPersonVisible=false}) { Text("Cancel")}
                                        Spacer(Modifier.width(10.dp))
                                        FilledTonalButton({
                                            people.substituteFirst({it.uuid==person.uuid}, person.copy(elements = elements.filter { !removedUUIDs.contains(it.uuid) }))
                                            println(elements)
                                            println(people.find { it.uuid==person.uuid })
                                            selectedPersonVisible=false
                                            save()
                                        }) { Text("Save") }
                                    }
                                    Spacer(Modifier)
                                }
                            }
                        }
                    }
                }
                if(isSheetVisible) {
                    ModalBottomSheet({coroutineScope.launch { sheet.hide() }; isSheetVisible=false}) {
                        var element by remember {
                            mutableStateOf(
                                Element(
                                    "",
                                    0,
                                    Calendar.getInstance().zero(),
                                )
                            )
                        }
                        var elementPerson: Person? by remember { mutableStateOf(null) }
                        Column(
                            Modifier
                                .padding(horizontal = 15.dp),
                            verticalArrangement = Arrangement.spacedBy(20.dp)
                        ) {
                            Row(
                                verticalAlignment = Alignment.CenterVertically
                            ) {
                                Icon(ImageVector.vectorResource(R.drawable.label_24px), "Name", Modifier.width(70.dp))
                                EditableText(element.name, MaterialTheme.typography.titleLarge.copy(MaterialTheme.colorScheme.primary), placeholder = "Name") {
                                    element=element.copy(name = it)
                                }
                            }
                            Row(
                                verticalAlignment = Alignment.CenterVertically
                            ) {
                                Icon(ImageVector.vectorResource(R.drawable.calendar_month_24px), "Date", Modifier.width(70.dp))
                                Column {
                                    Text(element.date.dateToString(), style = MaterialTheme.typography.titleSmall)
                                    Row(
                                        horizontalArrangement = Arrangement.spacedBy(10.dp)
                                    ) {
                                        val zero = Calendar.getInstance().zero()
                                        FilterChip(zero==element.date,
                                            {element=element.copy(date = Calendar.getInstance().zero())},
                                            {Text("Today")},
                                            leadingIcon = if(zero==element.date) ({Icon(Icons.Rounded.Check, "Check")}) else null
                                        )
                                        FilterChip(zero-1000L*60*60*24==element.date,
                                            {element=element.copy(date = Calendar.getInstance().zero()-1000L*60*60*24)},
                                            {Text("Yesterday")},
                                            leadingIcon = if(zero-1000L*60*60*24==element.date) ({Icon(Icons.Rounded.Check, "Check")}) else null
                                        )
                                        FilterChip(zero!=element.date && zero-1000L*60*60*24!=element.date,
                                            {datePickerSubscription = Subscription<Long>().apply { subscribe { element=element.copy(date = it) } }},
                                            {Text("Custom")},
                                            leadingIcon = if(zero!=element.date && zero-1000L*60*60*24!=element.date) ({Icon(Icons.Rounded.Check, "Check")}) else null
                                        )
                                    }
                                }
                            }
                            Row(
                                verticalAlignment = Alignment.CenterVertically
                            ) {
                                Icon(ImageVector.vectorResource(R.drawable.ic_launcher_foreground), "Money", Modifier.width(70.dp))
                                MoneyInput(element.priceInCent.toString(), MaterialTheme.typography.titleMedium) { element=element.copy(priceInCent = it) }
                            }
                            Row(
                                verticalAlignment = Alignment.CenterVertically
                            ) {
                                Icon(Icons.Rounded.Person, "Person", Modifier.width(70.dp))
                                Row(
                                    Modifier
                                        .horizontalScroll(rememberScrollState()),
                                   horizontalArrangement = Arrangement.spacedBy(10.dp)
                                ) {
                                    people.sortedBy { -it.elements.size }.forEach {
                                        FilterChip(
                                            it.uuid==elementPerson?.uuid,
                                            {elementPerson=it},
                                            {Text(it.name, style = MaterialTheme.typography.titleMedium)},
                                            leadingIcon = if(it.uuid==elementPerson?.uuid) ({ Icon(Icons.Rounded.Check, "tick") }) else null
                                        )
                                    }
                                }
                            }
                            Spacer(Modifier)
                            Row(
                                Modifier
                                    .fillMaxWidth(),
                                horizontalArrangement = Arrangement.End
                            ) {
                                FilledTonalButton({
                                    isSheetVisible=false
                                    val p = elementPerson?:return@FilledTonalButton
                                    people.substituteFirst({it.uuid==elementPerson?.uuid}, p.copy(elements = (p.elements + listOf(
                                        element
                                    ))))
                                    save()
                                }) { Text("Add") }
                            }
                            Spacer(Modifier)
                        }
                    }
                }
                if(personSubjectedForDeletion!=null){
                    AlertDialog(
                        {personSubjectedForDeletion=null},
                        { TextButton({people.removeIf { it.uuid==selectedPerson.uuid}; selectedPersonVisible=false; personSubjectedForDeletion=null; save()}) { Text("Delete") }},
                        title = { Text("Delete ${people.find { it.uuid==personSubjectedForDeletion }?.name?:"Entry"}")},
                        text = { Text("Clicking delete will delete this entry forever, this can't be undone")},
                        dismissButton = { TextButton({personSubjectedForDeletion=null}) { Text("Cancel") } },
                    )
                }
            }
        }
    }
}
@Composable
fun PersonColumn(title: String, background: Color, list: List<Person>, open: (Person)->Unit){
    Column(Modifier.fillMaxWidth()) {
        Text(title, style = MaterialTheme.typography.titleSmall.copy(MaterialTheme.colorScheme.secondary))
        Spacer(Modifier.height(10.dp))
        val nonUrgent = list.filter { !it.isUrgent() }.sortedBy { it.name }
        val urgent = list.filter { it.isUrgent() }.sortedBy { it.name }
        Column(
            Modifier
                .background(background.copy(.1f), RoundedCornerShape(30.dp)),
        ) {
            (urgent + nonUrgent).forEachIndexed { index, it ->
                if(index!=0) HorizontalDivider(Modifier.padding(horizontal = 15.dp))
                PersonComposable(it) { open(it) }
            }
        }
    }
}
@Composable
private fun PersonComposable(it: Person, open: ()->Unit) {
    val total = it.elements.total()
    Column(Modifier
        .clip(RoundedCornerShape(30.dp))
        .clickable { open() }
        .padding(15.dp)
        .fillMaxWidth(),
        verticalArrangement = Arrangement.spacedBy(4.dp)) {
        Row(Modifier.fillMaxWidth(), verticalAlignment = Alignment.CenterVertically, horizontalArrangement = Arrangement.SpaceBetween) {
            Text(it.name, style = MaterialTheme.typography.titleLarge)
            Text(total.centAsEuro(), style = MaterialTheme.typography.titleSmall.copy(total.getBoolColor()))
        }
        if(it.isUrgent())
            Row(
                Modifier
                    .fillMaxWidth()
                    .background(total.getBoolColor().copy(.15f), CircleShape)
                    .padding(10.dp, 3.dp)
                ,
                verticalAlignment = Alignment.CenterVertically,
                horizontalArrangement = Arrangement.spacedBy(5.dp)
            ) {
                Icon(ImageVector.vectorResource(R.drawable.warning), "Warning", Modifier.size(15.dp))
                Text("${if(total>0) "In debt" else "Unpaid"} since ${it.lastSignFlip().dateToString()} (${it.lastSignFlip().formatedTimeAgo()} ago)", style = MaterialTheme.typography.bodyMedium)
            }
    }
}
@Composable
fun Int.getBoolColor(): Color = if(this>0) Color.Green else if(this == 0) MaterialTheme.colorScheme.primary else Color.Red

fun Long.formatedTimeAgo(): String{
    return when(val x = (System.currentTimeMillis()-this)/1000){
        in 0..60*60 -> "${x/60}m"
        in 60*60..60*60*24 -> "${x/(60*60)}h"
        in 60*60*24..60*60*24*7 -> "${x/(60*60*24)}d"
        in 60*60*24*7..60*60*24*61 -> "${x/(60*60*24*7)}w"
        in 60*60*24*61..60*60*24*365 -> "${x/(60*60*24*30)}mo"
        else -> "${x/(60*60*24*365)}y"
    }
}

fun Collection<Element>.total(): Int = sumOf { it.priceInCent }

@Composable
fun ElementComposable(elem: Element, setElementDeletion: (Boolean)->Unit, awaitDayPicker: (Subscription<Long>, Long)->Unit, updateElement: (Element)->Unit){
    var isChecked by remember(elem.uuid) { mutableStateOf(false) }
    val dayPickerSubscription by remember {
        mutableStateOf(Subscription<Long>()
            .apply{
                subscribe{
                    updateElement(elem.copy(date = it))
                }
            }
        )
    }
    Row(
        Modifier
            .clip(RoundedCornerShape(20.dp))
            .clickable { isChecked=!isChecked ; setElementDeletion(isChecked) }
            .background(if(!isChecked) MaterialTheme.colorScheme.surfaceContainer else elem.priceInCent.getBoolColor().copy(.1f), RoundedCornerShape(30.dp))
            .padding(15.dp)

        ,
        verticalAlignment = Alignment.CenterVertically
    ) {
        Column(
            Modifier
                .weight(1f),
        ) {
            EditableText(
                elem.name,
                style = MaterialTheme.typography.titleLarge.copy(MaterialTheme.colorScheme.primary),
                onDone = { updateElement(elem.copy(name = it)) },
                placeholder = "Name"
            )
            Row(horizontalArrangement = Arrangement.spacedBy(5.dp)) {
                Text(elem.date.dateToString(), Modifier.clickable { awaitDayPicker(dayPickerSubscription, elem.date) }, style = MaterialTheme.typography.titleMedium.copy(color = MaterialTheme.colorScheme.secondary))
                Text("·", style = MaterialTheme.typography.titleSmall.copy(color = MaterialTheme.colorScheme.secondary))
                MoneyInput(elem.priceInCent.toString(), style = MaterialTheme.typography.titleMedium) { updateElement(elem.copy(priceInCent = it))}
            }
        }
        Checkbox(isChecked, {isChecked=it; setElementDeletion(isChecked)})
    }
}
@Composable
fun EditableText(
    initialText: String,
    style: TextStyle = LocalTextStyle.current,
    imeActions: KeyboardActions? = null,
    imeOptions: KeyboardOptions = KeyboardOptions(imeAction = ImeAction.Done),
    oneLine: Boolean = true,
    placeholder: String,
    onDone: (String) -> Unit,
){
    var text by remember { mutableStateOf(initialText) }
    val focusManager = LocalFocusManager.current
    var hasFocus by remember { mutableStateOf(false) }
    BasicTextField(
        text.ifEmpty { if(!hasFocus) placeholder else ""},
        {if(hasFocus) text=it; onDone(it)},
        Modifier
            .onFocusChanged { hasFocus=it.hasFocus },
        textStyle = style.copy(style.color.copy(if(text.isEmpty()) .5f else 1f)),
        cursorBrush = SolidColor(Color.White),
        singleLine = oneLine,
        keyboardActions = imeActions?: KeyboardActions(onDone = { focusManager.clearFocus(); onDone(text)}),
        keyboardOptions = imeOptions.copy(capitalization = KeyboardCapitalization.Words),
    )
}
fun Long.dateToString(): String{
    val calendar= Calendar.getInstance()
    calendar.timeInMillis=this
    return "${arrayOf("So","Mo","Di","Mi","Do","Fr","Sa")[calendar.get(Calendar.DAY_OF_WEEK)-1]} ${calendar.get(Calendar.DAY_OF_MONTH)}.${calendar.get(Calendar.MONTH)+1}.${calendar.get(Calendar.YEAR)}"
}
class Subscription<T>{
    var listener: (T)->Unit = {}
    fun subscribe(func: (T)->Unit){
        listener=func
    }
    fun send(data: T){
        listener(data)
    }
}
fun Int.centAsEuro(): String="${this/100}.${if(abs(this) % 100>9) "" else "0"}${abs(this) %100} €"

fun <T> MutableList<T>.substituteFirst(condition: (T)->Boolean, replacement: T) {
    val index = indexOfFirst { condition(it) }
    if(index!=-1) set(index, replacement)
}
fun Calendar.zero(): Long{
    set(Calendar.HOUR_OF_DAY, 0); set(Calendar.MINUTE, 0); set(Calendar.SECOND, 0); set(Calendar.MILLISECOND, 0)
    return timeInMillis
}
@Composable
fun MoneyInput(initialText: String, style: TextStyle = MaterialTheme.typography.titleMedium, updateElement: (Int) -> Unit){
    var isNegative by remember { mutableStateOf(initialText.getOrNull(0)=='-') }
    var content by remember { mutableStateOf(initialText.filter { it!='-' }) }
    val focusManager = LocalFocusManager.current
    val contentAsInt = (if(isNegative) -1 else 1)*(content.toIntOrNull()?:0)
    BasicTextField(
        content,
        {
            if(it.filter { !it.isDigit() }.isNotEmpty()) isNegative=!isNegative
            content=it.filter { it.isDigit() }
            updateElement((if(isNegative) -1 else 1)*(content.toIntOrNull()?:0))
        },
        visualTransformation = { currencyTransformation(it) },
        singleLine = true,
        cursorBrush = SolidColor(MaterialTheme.colorScheme.primary),
        keyboardActions = KeyboardActions(onDone = { focusManager.clearFocus(); updateElement(contentAsInt)}),
        keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Decimal, imeAction = ImeAction.Done),
        textStyle = style.copy(contentAsInt.getBoolColor())
    )
}
fun currencyTransformation(text: AnnotatedString): TransformedText{
    val rawValue = text.text
    val amountOfZeros = (3-rawValue.length).coerceIn(0,3)
    val numericValue = rawValue.toLongOrNull() ?: 0L
    val formattedValue = "%.2f €".format(numericValue / 100.0)
    val offsetMapping = object : OffsetMapping {
        override fun originalToTransformed(offset: Int): Int {
            return if(numericValue==0L) 4 else when(offset){
                in 0..formattedValue.split(".")[0].length ->
                    offset+amountOfZeros.let { if(it!=0) it+1 else 0}
                in formattedValue.split(".")[0].length+1..formattedValue.split(".")[0].length+2 ->
                    offset+1+amountOfZeros
                else ->
                    formattedValue.length-2
            }
        }

        override fun transformedToOriginal(offset: Int): Int {
            return (offset - amountOfZeros - if(amountOfZeros!=0  || offset>=formattedValue.split(".")[0].length-2) 1 else 0).coerceIn(0,rawValue.length)
        }
    }
    return TransformedText(AnnotatedString(formattedValue), offsetMapping)
}
