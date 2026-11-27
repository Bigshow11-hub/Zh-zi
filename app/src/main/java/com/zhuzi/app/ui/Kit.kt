package com.zhuzi.app.ui

import androidx.compose.animation.core.Spring
import androidx.compose.animation.core.animateDpAsState
import androidx.compose.animation.core.animateFloatAsState
import androidx.compose.animation.core.spring
import androidx.compose.animation.core.tween
import androidx.compose.foundation.Canvas
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.interaction.MutableInteractionSource
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.BoxScope
import androidx.compose.foundation.layout.BoxWithConstraints
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.ColumnScope
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxHeight
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.heightIn
import androidx.compose.foundation.layout.offset
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.selection.selectable
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.ModalBottomSheet
import androidx.compose.material3.SheetState
import androidx.compose.material3.Text
import androidx.compose.material3.rememberModalBottomSheetState
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.draw.drawBehind
import androidx.compose.ui.draw.shadow
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.geometry.Rect
import androidx.compose.ui.geometry.Size
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.Path
import androidx.compose.ui.graphics.PathEffect
import androidx.compose.ui.graphics.StrokeCap
import androidx.compose.ui.graphics.StrokeJoin
import androidx.compose.ui.graphics.drawscope.Fill
import androidx.compose.ui.graphics.drawscope.Stroke
import androidx.compose.ui.graphics.graphicsLayer
import androidx.compose.ui.semantics.Role
import androidx.compose.ui.semantics.contentDescription
import androidx.compose.ui.semantics.heading
import androidx.compose.ui.semantics.semantics
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.Dp
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.zhuzi.app.ui.theme.Accent
import com.zhuzi.app.ui.theme.AccentFill
import com.zhuzi.app.ui.theme.BgTop
import com.zhuzi.app.ui.theme.GroupShape
import com.zhuzi.app.ui.theme.Ink
import com.zhuzi.app.ui.theme.InkSoft
import com.zhuzi.app.ui.theme.Kai
import com.zhuzi.app.ui.theme.Kicker
import com.zhuzi.app.ui.theme.Mist
import com.zhuzi.app.ui.theme.Paper
import com.zhuzi.app.ui.theme.PillShape
import com.zhuzi.app.ui.theme.Rule
import com.zhuzi.app.ui.theme.SmoothShape
import com.zhuzi.app.ui.theme.Thumb

/*
 * Le « kit » de l'interface, inspiré d'iOS : pictogrammes au trait fin, listes groupées,
 * grands titres qui laissent place à une barre compacte, sélecteur segmenté, panneaux à poignée.
 * Les couleurs du thème sont lues AVANT les blocs Canvas (jamais dedans).
 */

// ---------------------------------------------------------------------------------------------
// Pictogrammes : un seul trait (1,6 sur une grille de 24), bouts ronds, comme les SF Symbols.
// ---------------------------------------------------------------------------------------------

/** Les pictogrammes dessinés de l'appli. */
enum class Glyph { SEARCH, BELL, CHEVRON_RIGHT, CHEVRON_LEFT, CHECK, SPEAKER, HOME, BOOK, GRID, CHART, CLOCK, GLOBE, CONTRAST, INFO, PENCIL, CLOSE }

/**
 * Un pictogramme au trait. `filled` remplit légèrement les formes fermées (onglet sélectionné),
 * `weight` épaissit le trait (chevrons, coches).
 */
@Composable
fun ZIcon(
    glyph: Glyph,
    tint: Color,
    modifier: Modifier = Modifier,
    size: Dp = 22.dp,
    weight: Float = 1f,
    filled: Boolean = false,
) {
    Canvas(modifier.size(size)) {
        val u = this.size.width / 24f
        val stroke = Stroke(1.6f * weight * u, cap = StrokeCap.Round, join = StrokeJoin.Round)
        val soft = tint.copy(alpha = tint.alpha * 0.18f)
        fun p(x: Float, y: Float) = Offset(x * u, y * u)
        fun line(x1: Float, y1: Float, x2: Float, y2: Float, w: Stroke = stroke) =
            drawLine(tint, p(x1, y1), p(x2, y2), w.width, cap = StrokeCap.Round)
        fun poly(vararg xy: Float, close: Boolean = false): Path = Path().apply {
            moveTo(xy[0] * u, xy[1] * u)
            var i = 2
            while (i < xy.size) { lineTo(xy[i] * u, xy[i + 1] * u); i += 2 }
            if (close) close()
        }
        fun shape(path: Path) {
            if (filled) drawPath(path, soft, style = Fill)
            drawPath(path, tint, style = stroke)
        }
        when (glyph) {
            Glyph.SEARCH -> {
                drawCircle(tint, 6.6f * u, p(10.4f, 10.4f), style = stroke)
                line(15.3f, 15.3f, 20f, 20f, Stroke(stroke.width * 1.15f))
            }
            Glyph.BELL -> {
                val body = Path().apply {
                    moveTo(5f * u, 17.2f * u)
                    lineTo(6.6f * u, 15f * u)
                    lineTo(6.6f * u, 10.8f * u)
                    arcTo(Rect(6.6f * u, 5.2f * u, 17.4f * u, 16f * u), 180f, 180f, false)
                    lineTo(17.4f * u, 15f * u)
                    lineTo(19f * u, 17.2f * u)
                    close()
                }
                shape(body)
                line(12f, 3.4f, 12f, 5.2f)
                drawArc(tint, 20f, 140f, false, p(9.6f, 17.4f), Size(4.8f * u, 3.6f * u), style = stroke)
            }
            Glyph.CHEVRON_RIGHT -> drawPath(poly(9f, 4.8f, 16.2f, 12f, 9f, 19.2f), tint, style = stroke)
            Glyph.CHEVRON_LEFT -> drawPath(poly(15f, 4.8f, 7.8f, 12f, 15f, 19.2f), tint, style = stroke)
            Glyph.CHECK -> drawPath(poly(4.8f, 12.6f, 9.8f, 17.4f, 19.4f, 7f), tint, style = stroke)
            Glyph.SPEAKER -> {
                shape(poly(3.6f, 9.4f, 7.2f, 9.4f, 11.8f, 5.4f, 11.8f, 18.6f, 7.2f, 14.6f, 3.6f, 14.6f, close = true))
                drawArc(tint, -42f, 84f, false, p(10.6f, 8.6f), Size(6.8f * u, 6.8f * u), style = stroke)
                drawArc(tint, -46f, 92f, false, p(9.4f, 5.2f), Size(13.6f * u, 13.6f * u), style = stroke)
            }
            Glyph.HOME -> {
                shape(poly(5.2f, 10.2f, 12f, 4.4f, 18.8f, 10.2f, 18.8f, 19.6f, 5.2f, 19.6f, close = true))
                drawPath(poly(10f, 19.6f, 10f, 14.6f, 14f, 14.6f, 14f, 19.6f), tint, style = stroke)
            }
            Glyph.BOOK -> {
                val book = Path().apply {
                    moveTo(12f * u, 6.6f * u)
                    cubicTo(10f * u, 5f * u, 7f * u, 4.6f * u, 3.6f * u, 5.1f * u)
                    lineTo(3.6f * u, 18.4f * u)
                    cubicTo(7f * u, 17.9f * u, 10f * u, 18.4f * u, 12f * u, 19.8f * u)
                    cubicTo(14f * u, 18.4f * u, 17f * u, 17.9f * u, 20.4f * u, 18.4f * u)
                    lineTo(20.4f * u, 5.1f * u)
                    cubicTo(17f * u, 4.6f * u, 14f * u, 5f * u, 12f * u, 6.6f * u)
                    close()
                }
                shape(book)
                line(12f, 6.6f, 12f, 19.6f)
            }
            Glyph.GRID -> {
                // La case d'écriture 田字格 : cadre doux, croix en tirets
                val r = androidx.compose.ui.geometry.CornerRadius(3.4f * u, 3.4f * u)
                if (filled) drawRoundRect(soft, p(4f, 4f), Size(16f * u, 16f * u), r)
                drawRoundRect(tint, p(4f, 4f), Size(16f * u, 16f * u), r, style = stroke)
                val dash = PathEffect.dashPathEffect(floatArrayOf(1.9f * u, 1.9f * u))
                drawLine(tint, p(12f, 6.6f), p(12f, 17.4f), stroke.width * 0.75f, pathEffect = dash)
                drawLine(tint, p(6.6f, 12f), p(17.4f, 12f), stroke.width * 0.75f, pathEffect = dash)
            }
            Glyph.CHART -> {
                val r = androidx.compose.ui.geometry.CornerRadius(1.2f * u, 1.2f * u)
                listOf(Triple(4.4f, 13.2f, 3.6f), Triple(10.2f, 8.4f, 3.6f), Triple(16f, 4.4f, 3.6f)).forEach { (x, y, w) ->
                    if (filled) drawRoundRect(soft, p(x, y), Size(w * u, (19.6f - y) * u), r)
                    drawRoundRect(tint, p(x, y), Size(w * u, (19.6f - y) * u), r, style = stroke)
                }
            }
            Glyph.CLOCK -> {
                drawCircle(tint, 8.4f * u, p(12f, 12f), style = stroke)
                drawPath(poly(12f, 7.4f, 12f, 12f, 15.2f, 14f), tint, style = stroke)
            }
            Glyph.GLOBE -> {
                drawCircle(tint, 8.4f * u, p(12f, 12f), style = stroke)
                drawOval(tint, p(8.4f, 3.6f), Size(7.2f * u, 16.8f * u), style = stroke)
                line(3.8f, 12f, 20.2f, 12f)
                line(5.4f, 7.8f, 18.6f, 7.8f, Stroke(stroke.width * 0.8f))
                line(5.4f, 16.2f, 18.6f, 16.2f, Stroke(stroke.width * 0.8f))
            }
            Glyph.CONTRAST -> {
                drawCircle(tint, 8.4f * u, p(12f, 12f), style = stroke)
                drawArc(tint, -90f, 180f, true, p(3.6f, 3.6f), Size(16.8f * u, 16.8f * u), style = Fill)
            }
            Glyph.INFO -> {
                drawCircle(tint, 8.4f * u, p(12f, 12f), style = stroke)
                line(12f, 11f, 12f, 16.4f)
                drawCircle(tint, 1.1f * u, p(12f, 7.8f))
            }
            Glyph.PENCIL -> {
                shape(poly(5f, 19f, 5.8f, 15.4f, 15.6f, 5.6f, 18.4f, 8.4f, 8.6f, 18.2f, close = true))
                line(13.6f, 7.6f, 16.4f, 10.4f)
            }
            Glyph.CLOSE -> {
                line(7f, 7f, 17f, 17f)
                line(17f, 7f, 7f, 17f)
            }
        }
    }
}

/** Coche ronde : pleine (accent + coche blanche) quand c'est fait, sinon un simple anneau. */
@Composable
fun CheckCircle(done: Boolean, modifier: Modifier = Modifier, size: Dp = 24.dp) {
    val fill = AccentFill
    val ring = InkSoft.copy(alpha = 0.45f)
    Box(modifier.size(size), contentAlignment = Alignment.Center) {
        Canvas(Modifier.fillMaxSize()) {
            if (done) drawCircle(fill)
            else drawCircle(ring, radius = this.size.minDimension / 2 - 0.75.dp.toPx(), style = Stroke(1.5.dp.toPx()))
        }
        if (done) ZIcon(Glyph.CHECK, Color.White, size = size * 0.7f, weight = 1.5f)
    }
}

/** Chevron « › » discret des lignes qu'on peut ouvrir. */
@Composable
fun Chevron(modifier: Modifier = Modifier) {
    ZIcon(Glyph.CHEVRON_RIGHT, InkSoft.copy(alpha = 0.55f), modifier, size = 16.dp, weight = 1.4f)
}

// ---------------------------------------------------------------------------------------------
// Listes groupées (« inset grouped ») : section blanche aux coins continus sur fond gris,
// filets fins décalés après l'icône, chevrons.
// ---------------------------------------------------------------------------------------------

/** Une section groupée : en-tête discret, la carte, une note de bas facultative. */
@Composable
fun GroupedSection(
    modifier: Modifier = Modifier,
    header: String? = null,
    footer: String? = null,
    content: @Composable ColumnScope.() -> Unit,
) {
    Column(modifier.fillMaxWidth()) {
        header?.let {
            Text(it, style = Kicker, modifier = Modifier.padding(start = 16.dp, end = 16.dp, bottom = 8.dp))
        }
        Column(Modifier.fillMaxWidth().clip(GroupShape).background(Paper), content = content)
        footer?.let {
            Text(it, style = MaterialTheme.typography.bodySmall, color = InkSoft,
                modifier = Modifier.padding(start = 16.dp, end = 16.dp, top = 8.dp))
        }
    }
}

/** Filet fin entre deux lignes, décalé à gauche (il commence après l'icône, comme sur iOS). */
@Composable
fun RowDivider(inset: Dp = 16.dp) {
    val color = Rule
    Box(
        Modifier.fillMaxWidth().height(1.dp).padding(start = inset)
            .drawBehind { drawLine(color, Offset(0f, size.height / 2), Offset(size.width, size.height / 2), 1f) },
    )
}

/** Largeur occupée à gauche par une icône de ligne (pour décaler le filet suivant). */
val RowIconInset = 60.dp

/**
 * Une ligne de liste groupée : icône (facultative), titre et sous-titre, valeur grise,
 * élément à droite (interrupteur, coche) ou chevron. Hauteur ≥ 52 dp (cible tactile confortable).
 */
@Composable
fun GroupedRow(
    title: String,
    modifier: Modifier = Modifier,
    subtitle: String? = null,
    icon: (@Composable () -> Unit)? = null,
    value: String? = null,
    chevron: Boolean = false,
    titleColor: Color = Color.Unspecified,
    onClick: (() -> Unit)? = null,
    role: Role? = Role.Button,
    trailing: (@Composable () -> Unit)? = null,
) {
    Row(
        modifier.fillMaxWidth().heightIn(min = 52.dp)
            .then(if (onClick != null) Modifier.clickable(role = role, onClick = onClick) else Modifier)
            .padding(start = 16.dp, end = 14.dp, top = 10.dp, bottom = 10.dp),
        verticalAlignment = Alignment.CenterVertically,
    ) {
        if (icon != null) {
            icon()
            Spacer(Modifier.width(14.dp))
        }
        Column(Modifier.weight(1f)) {
            Text(title, style = MaterialTheme.typography.bodyLarge, color = if (titleColor == Color.Unspecified) Ink else titleColor,
                maxLines = 2, overflow = TextOverflow.Ellipsis)
            subtitle?.let {
                Text(it, style = MaterialTheme.typography.bodySmall, color = InkSoft, maxLines = 3,
                    overflow = TextOverflow.Ellipsis, modifier = Modifier.padding(top = 1.dp))
            }
        }
        value?.let {
            Text(it, style = MaterialTheme.typography.bodyLarge, color = InkSoft, maxLines = 1,
                modifier = Modifier.padding(start = 12.dp))
        }
        trailing?.let {
            Spacer(Modifier.width(10.dp))
            it()
        }
        if (chevron) {
            Spacer(Modifier.width(8.dp))
            Chevron()
        }
    }
}

/** Icône de ligne : petit carré aux coins continus, en accent plein, pictogramme blanc. */
@Composable
fun IconTile(glyph: Glyph, size: Dp = 30.dp) {
    Box(Modifier.size(size).clip(SmoothShape(7.dp)).background(AccentFill), contentAlignment = Alignment.Center) {
        ZIcon(glyph, Color.White, size = size * 0.64f, weight = 1.15f)
    }
}

/** Icône de ligne avec un caractère au pinceau (même gabarit que IconTile). */
@Composable
fun KaiTile(char: String, size: Dp = 30.dp, filled: Boolean = true) {
    Box(
        Modifier.size(size).clip(SmoothShape(size * 0.24f)).background(if (filled) AccentFill else Mist),
        contentAlignment = Alignment.Center,
    ) {
        Text(char, fontFamily = Kai, fontSize = (size.value * 0.6f).sp, color = if (filled) Color.White else Ink)
    }
}

// ---------------------------------------------------------------------------------------------
// Titres : grand titre en tête d'onglet, barre compacte qui apparaît quand on défile.
// ---------------------------------------------------------------------------------------------

/** Grand titre d'onglet (34 sp), sous-titre facultatif, élément à droite facultatif. */
@Composable
fun LargeTitle(
    title: String,
    modifier: Modifier = Modifier,
    subtitle: String? = null,
    trailing: (@Composable () -> Unit)? = null,
) {
    Row(modifier.fillMaxWidth().padding(bottom = 12.dp), verticalAlignment = Alignment.Bottom) {
        Column(Modifier.weight(1f)) {
            Text(title, style = MaterialTheme.typography.displaySmall, color = Ink,
                modifier = Modifier.semantics { heading() })
            subtitle?.let {
                Text(it, style = MaterialTheme.typography.bodyMedium, color = InkSoft, modifier = Modifier.padding(top = 2.dp))
            }
        }
        trailing?.invoke()
    }
}

/** Titre d'onglet (ancien nom) : le grand titre et son sous-titre. */
@Composable
fun ScreenTitle(title: String, subtitle: String) = LargeTitle(title, subtitle = subtitle)

/**
 * Barre compacte du haut : invisible tant que le grand titre est à l'écran, elle apparaît en fondu
 * (matière translucide + filet) quand on défile, avec le titre centré. À placer dans un Box.
 */
@Composable
fun BoxScope.CompactTitleBar(title: String, visible: Boolean, topInset: Dp) {
    val alpha by animateFloatAsState(if (visible) 1f else 0f, tween(180), label = "compactBar")
    val bg = BgTop
    val line = Rule
    Box(
        Modifier.align(Alignment.TopCenter).fillMaxWidth().height(topInset + 44.dp)
            .graphicsLayer { this.alpha = alpha }
            .drawBehind {
                drawRect(bg.copy(alpha = 0.94f))
                drawLine(line, Offset(0f, size.height - 0.5f), Offset(size.width, size.height - 0.5f), 1f)
            },
        contentAlignment = Alignment.BottomCenter,
    ) {
        if (alpha > 0.01f) {
            Text(title, style = MaterialTheme.typography.titleMedium, color = Ink, maxLines = 1,
                overflow = TextOverflow.Ellipsis,
                modifier = Modifier.height(44.dp).padding(horizontal = 56.dp).padding(top = 11.dp))
        }
    }
}

/**
 * Barre de navigation d'un écran plein : chevron + libellé de retour en accent (à gauche),
 * repère discret à droite. Le bouton de retour fait 48 dp de haut.
 */
@Composable
fun TopBar(back: String, right: String, onBack: () -> Unit) {
    Row(
        Modifier.fillMaxWidth().height(52.dp).padding(start = 4.dp, end = 16.dp),
        verticalAlignment = Alignment.CenterVertically,
    ) {
        val accent = Accent
        Row(
            Modifier.heightIn(min = 48.dp).clip(PillShape)
                .clickable(role = Role.Button, onClick = onBack)
                .padding(start = 6.dp, end = 12.dp),
            verticalAlignment = Alignment.CenterVertically,
        ) {
            ZIcon(Glyph.CHEVRON_LEFT, accent, size = 24.dp, weight = 1.45f)
            Text(back, color = accent, style = MaterialTheme.typography.bodyLarge, maxLines = 1)
        }
        Spacer(Modifier.weight(1f))
        Text(right, color = InkSoft, style = MaterialTheme.typography.bodyMedium, maxLines = 1,
            overflow = TextOverflow.Ellipsis, modifier = Modifier.padding(start = 12.dp))
    }
}

// ---------------------------------------------------------------------------------------------
// Sélecteur segmenté façon iOS : rail gris, pastille claire qui glisse (ressort sans rebond).
// Visuel de 36 dp, mais 48 dp de zone tactile.
// ---------------------------------------------------------------------------------------------

@Composable
fun Segmented(labels: List<String>, selected: Int, onSelect: (Int) -> Unit, modifier: Modifier = Modifier) {
    val track = Mist
    val thumb = Thumb
    val sep = InkSoft.copy(alpha = 0.25f)
    val n = labels.size
    BoxWithConstraints(modifier.fillMaxWidth().height(48.dp)) {
        val inner = maxWidth - 6.dp
        val segW = inner / n
        val x by animateDpAsState(segW * selected, spring(dampingRatio = 1f, stiffness = 700f), label = "segment")
        // Rail
        Box(Modifier.align(Alignment.Center).fillMaxWidth().height(36.dp).clip(RoundedCornerShape(10.dp)).background(track))
        // Pastille
        Box(
            Modifier.align(Alignment.CenterStart).offset(x = 3.dp + x).width(segW).height(30.dp)
                .shadow(3.dp, RoundedCornerShape(8.dp), ambientColor = Color.Black.copy(alpha = 0.06f),
                    spotColor = Color.Black.copy(alpha = 0.14f))
                .clip(RoundedCornerShape(8.dp)).background(thumb),
        )
        Row(Modifier.fillMaxSize().padding(horizontal = 3.dp)) {
            labels.forEachIndexed { i, label ->
                val on = i == selected
                // Filet entre deux choix, caché à côté de la pastille
                val showSep = i > 0 && i != selected && i != selected + 1
                Box(
                    Modifier.weight(1f).fillMaxHeight()
                        .drawBehind {
                            if (showSep) drawLine(sep, Offset(0f, size.height / 2 - 7.dp.toPx()),
                                Offset(0f, size.height / 2 + 7.dp.toPx()), 1.dp.toPx())
                        }
                        .selectable(
                            selected = on, role = Role.Tab,
                            interactionSource = remember { MutableInteractionSource() }, indication = null,
                        ) { onSelect(i) },
                    contentAlignment = Alignment.Center,
                ) {
                    Text(label, color = Ink, fontSize = if (n >= 4) 13.sp else 14.sp,
                        fontWeight = if (on) FontWeight.Bold else FontWeight.Medium,
                        maxLines = 1, softWrap = false, overflow = TextOverflow.Ellipsis,
                        modifier = Modifier.padding(horizontal = 4.dp))
                }
            }
        }
    }
}

// ---------------------------------------------------------------------------------------------
// Panneaux du bas, façon « sheet » d'iOS : poignée, coins doux.
// ---------------------------------------------------------------------------------------------

/** La poignée d'un panneau : petite pilule grise. */
@Composable
fun Grabber() {
    val c = InkSoft.copy(alpha = 0.35f)
    Box(Modifier.padding(top = 8.dp, bottom = 12.dp).size(36.dp, 5.dp).clip(PillShape).background(c))
}

/**
 * Panneau du bas commun. `grouped` : fond gris « groupé » (pour y poser des cartes blanches),
 * sinon fond de carte.
 */
@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun ZhuziSheet(
    onDismiss: () -> Unit,
    grouped: Boolean = false,
    sheetState: SheetState = rememberModalBottomSheetState(),
    content: @Composable ColumnScope.() -> Unit,
) {
    ModalBottomSheet(
        onDismissRequest = onDismiss,
        sheetState = sheetState,
        containerColor = if (grouped) com.zhuzi.app.ui.theme.SheetBg else Paper,
        contentColor = Ink,
        shape = RoundedCornerShape(topStart = 20.dp, topEnd = 20.dp),
        dragHandle = { Grabber() },
        content = content,
    )
}

// ---------------------------------------------------------------------------------------------
// Liens d'action : pictogramme + libellé en accent, sans soulignement (cible de 48 dp).
// ---------------------------------------------------------------------------------------------

@Composable
fun ActionLink(text: String, glyph: Glyph, onClick: () -> Unit, modifier: Modifier = Modifier) {
    val accent = Accent
    Row(
        modifier.heightIn(min = 48.dp).clip(PillShape).clickable(role = Role.Button, onClick = onClick)
            .padding(horizontal = 12.dp),
        verticalAlignment = Alignment.CenterVertically,
    ) {
        ZIcon(glyph, accent, size = 20.dp, weight = 1.1f)
        Spacer(Modifier.width(6.dp))
        Text(text, color = accent, style = MaterialTheme.typography.titleSmall)
    }
}

/** Bouton rond de barre d'outils (recherche, cloche) : pastille claire de 40 dp, zone de 48 dp. */
@Composable
fun ToolbarButton(label: String, onClick: () -> Unit, content: @Composable () -> Unit) {
    Box(
        Modifier.size(48.dp).clip(androidx.compose.foundation.shape.CircleShape)
            .clickable(role = Role.Button, onClickLabel = null, onClick = onClick)
            .semantics { contentDescription = label },
        contentAlignment = Alignment.Center,
    ) {
        Box(Modifier.size(40.dp).clip(androidx.compose.foundation.shape.CircleShape).background(Paper),
            contentAlignment = Alignment.Center) { content() }
    }
}
