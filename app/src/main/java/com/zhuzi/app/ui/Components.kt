package com.zhuzi.app.ui

import androidx.compose.animation.core.Animatable
import androidx.compose.animation.core.animateFloat
import androidx.compose.animation.core.Spring
import androidx.compose.animation.core.animateFloatAsState
import androidx.compose.animation.core.spring
import androidx.compose.animation.core.tween
import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.Canvas
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.interaction.MutableInteractionSource
import androidx.compose.foundation.interaction.collectIsPressedAsState
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.BoxWithConstraints
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.offset
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.OutlinedButton
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.draw.scale
import androidx.compose.ui.draw.shadow
import androidx.compose.ui.geometry.CornerRadius
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.geometry.Size
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.PathEffect
import androidx.compose.ui.graphics.StrokeCap
import androidx.compose.ui.graphics.drawscope.Stroke
import androidx.compose.ui.graphics.drawscope.withTransform
import androidx.compose.ui.graphics.graphicsLayer
import androidx.compose.ui.platform.LocalDensity
import androidx.compose.ui.text.SpanStyle
import androidx.compose.ui.text.buildAnnotatedString
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.text.withStyle
import androidx.compose.ui.unit.Dp
import androidx.compose.ui.unit.TextUnit
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.min
import androidx.compose.ui.unit.sp
import com.zhuzi.app.data.Tones
import com.zhuzi.app.ui.theme.GridLine
import com.zhuzi.app.ui.theme.Ink
import com.zhuzi.app.ui.theme.InkSoft
import com.zhuzi.app.ui.theme.toneColor
import com.zhuzi.app.ui.theme.Kai
import com.zhuzi.app.ui.theme.OnSeal
import com.zhuzi.app.ui.theme.Paper
import com.zhuzi.app.ui.theme.PillShape
import com.zhuzi.app.ui.theme.Rule
import com.zhuzi.app.ui.theme.Seal

/** Coins presque droits, comme une feuille de cahier (cases d'écriture seulement). */
val SquareShape = RoundedCornerShape(2.dp)

/** Décélération exponentielle (ease-out-quart) : arrivée nette, sans rebond. */
val EaseOutQuart = androidx.compose.animation.core.CubicBezierEasing(0.25f, 1f, 0.5f, 1f)

/** Ressort commun : vif, sans rebond. */
fun <T> zhuziSpring() = spring<T>(dampingRatio = 0.95f, stiffness = Spring.StiffnessMediumLow)

/**
 * Cases d'écriture d'un mot : un seul cadre aux coins doux, une case par caractère,
 * guides 田字格 (croix) ou 米字格 (croix + diagonales) en tirets gris discrets.
 * Le caractère, au pinceau, porte la couleur de son ton.
 */
@Composable
private fun GridRow(
    chars: List<String>,
    cell: Dp,
    colors: List<Color>,
    diagonals: Boolean,
    modifier: Modifier = Modifier,
) {
    val density = LocalDensity.current
    val guide = GridLine
    val frame = Ink.copy(alpha = 0.28f)
    val paper = Paper
    val n = chars.size
    Box(modifier.size(cell * n, cell)) {
        Canvas(Modifier.fillMaxSize()) {
            val c = this.size.height
            val border = (if (cell >= 100.dp) 1.6.dp else 1.2.dp).toPx()
            val thin = 0.9.dp.toPx()
            val dash = PathEffect.dashPathEffect(floatArrayOf(5f, 4f))
            val r = CornerRadius(4.dp.toPx(), 4.dp.toPx())
            drawRoundRect(paper, cornerRadius = r)
            for (i in 0 until n) {
                val x0 = i * c
                if (diagonals) {
                    drawLine(guide.copy(alpha = 0.6f), Offset(x0, 0f), Offset(x0 + c, c), thin, pathEffect = dash)
                    drawLine(guide.copy(alpha = 0.6f), Offset(x0 + c, 0f), Offset(x0, c), thin, pathEffect = dash)
                }
                drawLine(guide, Offset(x0 + c / 2, 0f), Offset(x0 + c / 2, c), thin, pathEffect = dash)
                drawLine(guide, Offset(x0, c / 2), Offset(x0 + c, c / 2), thin, pathEffect = dash)
                if (i > 0) drawLine(frame, Offset(x0, 0f), Offset(x0, c), border * 0.8f)
            }
            drawRoundRect(
                frame, topLeft = Offset(border / 2, border / 2),
                size = Size(this.size.width - border, this.size.height - border),
                cornerRadius = r, style = Stroke(border),
            )
        }
        Row {
            chars.forEachIndexed { i, ch ->
                Box(Modifier.size(cell), contentAlignment = Alignment.Center) {
                    Text(
                        ch,
                        fontFamily = Kai,
                        color = colors[i],
                        fontSize = with(density) { (cell * 0.76f).toSp() },
                        // La police pinceau est un peu basse : on remonte légèrement le caractère
                        modifier = Modifier.offset(y = -cell * 0.02f),
                    )
                }
            }
        }
    }
}

/** Une seule case d'écriture (symbole de leçon, aperçu). */
@Composable
fun CharGrid(
    char: String,
    size: Dp,
    modifier: Modifier = Modifier,
    diagonals: Boolean = false,
    color: Color = Ink,
) = GridRow(listOf(char), size, listOf(color), diagonals, modifier)

/** Un mot entier dans ses cases ; avec le pinyin, chaque caractère prend la couleur de son ton. */
@Composable
fun WordGrids(
    word: String,
    maxSize: Dp,
    modifier: Modifier = Modifier,
    diagonals: Boolean = false,
    pinyin: String? = null,
) {
    val chars = word.map { it.toString() }
    val colors = if (pinyin != null) {
        Tones.forWord(word, pinyin).map { toneColor(it) }
    } else {
        chars.map { Ink }
    }
    BoxWithConstraints(modifier.fillMaxWidth(), contentAlignment = Alignment.Center) {
        val cell = min(maxSize, maxWidth / chars.size)
        GridRow(chars, cell, colors, diagonals)
    }
}

/** Le pinyin, chaque syllabe à la couleur de son ton. */
@Composable
fun PinyinText(
    pinyin: String,
    fontSize: TextUnit,
    modifier: Modifier = Modifier,
    fontWeight: FontWeight = FontWeight.Bold,
    textAlign: TextAlign? = null,
    maxLines: Int = 1,
) {
    val pieces = Tones.pieces(pinyin)
    val colors = pieces.map { (_, t) -> if (t == null) Ink else toneColor(t) }
    val text = buildAnnotatedString {
        pieces.forEachIndexed { i, (s, _) -> withStyle(SpanStyle(color = colors[i])) { append(s) } }
    }
    // Police du téléphone : elle a toutes les voyelles à tons (ǎ, ǐ, ǒ…), que Manrope n'a pas
    Text(text, modifier = modifier, fontSize = fontSize, fontWeight = fontWeight, textAlign = textAlign,
        fontFamily = com.zhuzi.app.ui.theme.PinyinFont,
        maxLines = maxLines, overflow = TextOverflow.Ellipsis)
}

/** Légende des couleurs de tons. */
@Composable
fun ToneLegend(modifier: Modifier = Modifier) {
    val items = listOf(
        "ā " + tr("1er", "1st", "1-й") to 1,
        "á " + tr("2e", "2nd", "2-й") to 2,
        "ǎ " + tr("3e", "3rd", "3-й") to 3,
        "à " + tr("4e", "4th", "4-й") to 4,
        "a " + tr("neutre", "neutral", "нейтр.") to 5,
    )
    Row(modifier, horizontalArrangement = androidx.compose.foundation.layout.Arrangement.spacedBy(12.dp),
        verticalAlignment = Alignment.CenterVertically) {
        Text(tr("Tons", "Tones", "Тоны"), fontSize = 12.sp, color = InkSoft, fontWeight = FontWeight.Bold)
        items.forEach { (label, t) ->
            Text(label, fontSize = 12.sp, color = toneColor(t), fontWeight = FontWeight.Bold)
        }
    }
}

/** Petit enfoncement à ressort quand on appuie (boutons, tuiles). */
@Composable
fun Modifier.pressScale(interaction: MutableInteractionSource): Modifier {
    val pressed by interaction.collectIsPressedAsState()
    val s by animateFloatAsState(if (pressed) 0.97f else 1f, zhuziSpring(), label = "press")
    return this.scale(s)
}

/** Bouton principal : pilule pleine en accent, la seule action forte de l'écran. S'assombrit à l'appui. */
@Composable
fun InkButton(text: String, onClick: () -> Unit, modifier: Modifier = Modifier, enabled: Boolean = true) {
    val interaction = remember { MutableInteractionSource() }
    val pressed by interaction.collectIsPressedAsState()
    val a = com.zhuzi.app.ui.theme.AccentFill
    Box(
        modifier.height(52.dp).pressScale(interaction)
            .clip(PillShape)
            .background(if (!enabled) a.copy(alpha = 0.4f) else if (pressed) a.copy(alpha = 0.86f) else a)
            .clickable(interactionSource = interaction, indication = null, enabled = enabled,
                role = androidx.compose.ui.semantics.Role.Button, onClick = onClick)
            .padding(horizontal = 16.dp),
        contentAlignment = Alignment.Center,
    ) {
        Text(text, color = Color.White, style = androidx.compose.material3.MaterialTheme.typography.labelLarge,
            maxLines = 1, overflow = TextOverflow.Ellipsis)
    }
}

/**
 * Carte : fond de carte aux coins continus, sans ombre (comme les listes groupées d'iOS : la carte
 * se détache par sa couleur sur le fond gris).
 */
@Composable
fun Modifier.softCard(shape: androidx.compose.ui.graphics.Shape = com.zhuzi.app.ui.theme.PanelShape): Modifier =
    this.clip(shape).background(Paper)

/** Carte qui flotte au-dessus du contenu (barre du bas, carte retournée) : une ombre douce et décalée. */
@Composable
fun Modifier.raised(shape: androidx.compose.ui.graphics.Shape = com.zhuzi.app.ui.theme.PanelShape): Modifier {
    val shade = Color(0xFF000000)
    return this.shadow(12.dp, shape, ambientColor = shade.copy(alpha = 0.06f), spotColor = shade.copy(alpha = 0.12f))
        .clip(shape).background(Paper)
}

/** Bouton secondaire : pilule teintée (accent très léger), libellé en accent — le style « gris » d'iOS. */
@Composable
fun InkOutlinedButton(text: String, onClick: () -> Unit, modifier: Modifier = Modifier) {
    val interaction = remember { MutableInteractionSource() }
    val pressed by interaction.collectIsPressedAsState()
    val a = com.zhuzi.app.ui.theme.Accent
    Box(
        modifier.height(52.dp).pressScale(interaction)
            .clip(PillShape)
            .background(a.copy(alpha = if (pressed) 0.20f else 0.12f))
            .clickable(interactionSource = interaction, indication = null,
                role = androidx.compose.ui.semantics.Role.Button, onClick = onClick)
            .padding(horizontal = 16.dp),
        contentAlignment = Alignment.Center,
    ) {
        Text(text, color = a, style = androidx.compose.material3.MaterialTheme.typography.labelLarge,
            maxLines = 1, overflow = TextOverflow.Ellipsis)
    }
}

/**
 * Barre de progression « vivante » : elle se remplit depuis zéro à l'ouverture, avec un ressort,
 * en dégradé de sa couleur, et un léger reflet qui la traverse de temps en temps.
 */
@Composable
fun ProgressBar(
    progress: Float,
    modifier: Modifier = Modifier,
    color: Color = com.zhuzi.app.ui.theme.Accent,
    track: Color = Rule,
    height: Dp = 8.dp,
) {
    val target = progress.coerceIn(0f, 1f)
    val anim = remember { Animatable(0f) }
    // Remplissage doux (décélération exponentielle), sans rebond ni reflet
    LaunchedEffect(target) { anim.animateTo(target, tween(700, easing = EaseOutQuart)) }
    Canvas(modifier.fillMaxWidth().height(height)) {
        val r = CornerRadius(size.height / 2, size.height / 2)
        drawRoundRect(track, cornerRadius = r)
        val p = anim.value
        if (p > 0f) {
            val w = (size.width * p).coerceAtLeast(size.height)
            drawRoundRect(color, size = size.copy(width = w), cornerRadius = r)
        }
    }
}

/** Un nombre qui compte depuis zéro jusqu'à sa valeur, à l'ouverture. */
@Composable
fun CountUpText(
    value: Int,
    modifier: Modifier = Modifier,
    style: androidx.compose.ui.text.TextStyle = androidx.compose.material3.MaterialTheme.typography.headlineMedium,
    color: Color = Color.Unspecified,
) {
    val anim = remember { Animatable(0f) }
    LaunchedEffect(value) { anim.animateTo(value.toFloat(), tween(900, easing = androidx.compose.animation.core.FastOutSlowInEasing)) }
    Text("${anim.value.toInt()}", modifier = modifier, style = style, color = color)
}

/**
 * Entrée en cascade : glisse vers le haut et apparaît, avec un petit décalage selon la position.
 * Ne joue qu'une fois par écran (les éléments déjà vus ne se rejouent pas au défilement).
 */
@Composable
fun Modifier.staggerIn(key: Any, index: Int, seen: MutableSet<Any>): Modifier {
    val first = remember(key) { key !in seen }
    val anim = remember(key) { Animatable(if (first) 0f else 1f) }
    LaunchedEffect(key) {
        if (first) {
            seen.add(key)
            kotlinx.coroutines.delay((index.coerceAtMost(6) * 35).toLong())
            anim.animateTo(1f, tween(380, easing = EaseOutQuart))
        }
    }
    return this.graphicsLayer {
        alpha = anim.value
        translationY = (1f - anim.value) * 10.dp.toPx()
    }
}

/** Anneau de progression (mots découverts sur le total). */
@Composable
fun ProgressRing(
    progress: Float,
    size: Dp,
    modifier: Modifier = Modifier,
    stroke: Dp = 8.dp,
    color: Color = com.zhuzi.app.ui.theme.Accent,
    trackColor: Color = Rule,
) {
    // L'anneau se remplit depuis zéro à l'ouverture
    val ring = remember { Animatable(0f) }
    LaunchedEffect(progress) { ring.animateTo(progress.coerceIn(0f, 1f), tween(1100, easing = androidx.compose.animation.core.FastOutSlowInEasing)) }
    val p = ring.value
    val track = trackColor
    val ink = color
    Canvas(modifier.size(size)) {
        val w = stroke.toPx()
        val inset = w / 2
        val arcSize = this.size.copy(this.size.width - w, this.size.height - w)
        drawArc(track, -90f, 360f, false, Offset(inset, inset), arcSize, style = Stroke(w))
        if (p > 0f) {
            drawArc(ink, -90f, 360f * p, false, Offset(inset, inset), arcSize,
                style = Stroke(w, cap = StrokeCap.Round))
        }
    }
}

/**
 * Le sceau rouge qui se tamponne : il arrive grand et transparent, puis s'écrase
 * en place avec un ressort, légèrement penché, comme un vrai tampon.
 */
@Composable
fun SealStamp(char: String, size: Dp, modifier: Modifier = Modifier, animate: Boolean = true) {
    val density = LocalDensity.current
    val stampScale = remember { Animatable(if (animate) 1.8f else 1f) }
    val stampAlpha = remember { Animatable(if (animate) 0f else 1f) }
    LaunchedEffect(Unit) {
        if (animate) {
            stampAlpha.animateTo(1f, tween(120))
        }
    }
    LaunchedEffect(Unit) {
        if (animate) {
            stampScale.animateTo(1f, spring(dampingRatio = 0.45f, stiffness = Spring.StiffnessMedium))
        }
    }
    Box(
        modifier
            .size(size)
            .graphicsLayer {
                scaleX = stampScale.value; scaleY = stampScale.value
                alpha = stampAlpha.value
                rotationZ = -6f
            }
            .clip(RoundedCornerShape(size * 0.08f))
            .background(Seal)
            .border(size * 0.035f, OnSeal.copy(alpha = 0.85f), RoundedCornerShape(size * 0.06f)),
        contentAlignment = Alignment.Center,
    ) {
        Text(
            char,
            fontFamily = Kai,
            color = OnSeal,
            fontWeight = FontWeight.Bold,
            fontSize = with(density) { (size * 0.62f).toSp() },
        )
    }
}

/** Pastille de série : un petit sceau rouge avec le nombre de jours. */
@Composable
fun StreakBadge(days: Int, modifier: Modifier = Modifier) {
    Row(modifier, verticalAlignment = Alignment.CenterVertically) {
        Box(
            Modifier.size(30.dp).graphicsLayer { rotationZ = -6f }
                .clip(RoundedCornerShape(3.dp)).background(Seal),
            contentAlignment = Alignment.Center,
        ) { Text("$days", color = OnSeal, fontWeight = FontWeight.Black, fontSize = 14.sp) }
        Text("  " + tr(if (days > 1) "jours d'affilée" else "jour", if (days == 1) "day streak" else "day streak",
            ruPlural(days, "день подряд", "дня подряд", "дней подряд")), color = Ink,
            fontWeight = FontWeight.Bold, fontSize = 14.sp)
    }
}

/** Bouton clair, posé sur la carte encre profonde. */
@Composable
fun LightButton(text: String, onClick: () -> Unit, modifier: Modifier = Modifier) {
    val interaction = remember { MutableInteractionSource() }
    Button(
        onClick = onClick,
        shape = PillShape,
        interactionSource = interaction,
        colors = ButtonDefaults.buttonColors(
            containerColor = com.zhuzi.app.ui.theme.OnDeep,
            contentColor = com.zhuzi.app.ui.theme.DeepInk,
        ),
        modifier = modifier.height(56.dp).pressScale(interaction),
    ) { Text(text, style = androidx.compose.material3.MaterialTheme.typography.labelLarge) }
}

/**
 * Tiges de bambou dessinées au trait, très discrètes : le décor de la carte « Aujourd'hui ».
 * Trois tiges à nœuds, quelques feuilles effilées.
 */
@Composable
fun BambooArt(modifier: Modifier = Modifier, color: Color = Color.White) {
    Canvas(modifier) {
        val w = size.width
        val h = size.height
        val stalks = listOf(
            Triple(0.30f, 0.050f, 0.10f), // position x, épaisseur, opacité
            Triple(0.58f, 0.065f, 0.14f),
            Triple(0.84f, 0.045f, 0.08f),
        )
        stalks.forEachIndexed { k, (fx, fw, a) ->
            val x = w * fx
            val sw = w * fw
            val seg = h / (4.2f + k * 0.4f)
            var y = h
            while (y > -seg) {
                // Un segment, un peu plus étroit au nœud
                drawRoundRect(
                    color.copy(alpha = a), topLeft = Offset(x - sw / 2, y - seg + sw * 0.35f),
                    size = Size(sw, seg - sw * 0.7f), cornerRadius = CornerRadius(sw / 2, sw / 2),
                )
                y -= seg
            }
        }
        // Feuilles : fines amandes inclinées
        fun leaf(cx: Float, cy: Float, len: Float, angle: Float, a: Float) {
            val path = androidx.compose.ui.graphics.Path().apply {
                moveTo(0f, 0f)
                quadraticTo(len * 0.5f, -len * 0.16f, len, 0f)
                quadraticTo(len * 0.5f, len * 0.16f, 0f, 0f)
                close()
            }
            withTransform({
                translate(cx, cy)
                rotate(angle, pivot = Offset.Zero)
            }) { drawPath(path, color.copy(alpha = a)) }
        }
        leaf(w * 0.58f, h * 0.22f, w * 0.42f, -28f, 0.14f)
        leaf(w * 0.58f, h * 0.24f, w * 0.34f, -150f, 0.10f)
        leaf(w * 0.30f, h * 0.48f, w * 0.30f, -160f, 0.09f)
        leaf(w * 0.84f, h * 0.40f, w * 0.30f, -40f, 0.08f)
    }
}

/** Phrase d'exemple : le mot étudié ressort en couleurs de tons dans la phrase. */
@Composable
fun ExampleBlock(word: com.zhuzi.app.data.Word, speak: (String) -> Unit, modifier: Modifier = Modifier) {
    val ex = com.zhuzi.app.data.examples[word.hanzi] ?: return
    val tones = Tones.forWord(word.hanzi, word.pinyin).map { toneColor(it) }
    val ink = Ink
    val start = ex.hanzi.indexOf(word.hanzi)
    val sentence = buildAnnotatedString {
        ex.hanzi.forEachIndexed { i, c ->
            val inWord = start >= 0 && i >= start && i < start + word.hanzi.length
            withStyle(SpanStyle(color = if (inWord) tones[i - start] else ink)) { append(c) }
        }
    }
    androidx.compose.foundation.layout.Column(
        modifier.fillMaxWidth().clip(com.zhuzi.app.ui.theme.TileShape)
            .background(com.zhuzi.app.ui.theme.Mist).padding(start = 16.dp, end = 16.dp, top = 14.dp, bottom = 4.dp),
    ) {
        Text(tr("EXEMPLE", "EXAMPLE", "ПРИМЕР"), style = com.zhuzi.app.ui.theme.Kicker)
        Text(sentence, fontFamily = Kai, fontSize = 26.sp, modifier = Modifier.padding(top = 8.dp))
        Text(ex.pinyin, fontFamily = com.zhuzi.app.ui.theme.PinyinFont, fontSize = 14.sp,
            color = InkSoft, modifier = Modifier.padding(top = 2.dp))
        Text(ex.meaning(currentLang()), fontSize = 15.sp, color = Ink, fontWeight = FontWeight.Medium,
            modifier = Modifier.padding(top = 6.dp))
        ActionLink(tr("Écouter la phrase", "Listen to the sentence", "Слушать фразу"), Glyph.SPEAKER,
            { speak(ex.hanzi) }, Modifier.padding(top = 4.dp).offset(x = (-12).dp))
    }
}

/** Un élément de liste qui entre en cascade (voir `staggerIn`). */
fun androidx.compose.foundation.lazy.LazyListScope.staggerItem(
    index: Int,
    seen: MutableSet<Any>,
    content: @Composable () -> Unit,
) {
    item { Box(Modifier.staggerIn(index, index, seen)) { content() } }
}
