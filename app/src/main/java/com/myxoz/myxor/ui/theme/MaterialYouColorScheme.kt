import android.R.color
import android.content.Context
import android.os.Build
import androidx.annotation.RequiresApi
import androidx.compose.foundation.isSystemInDarkTheme
import androidx.compose.material3.ColorScheme
import androidx.compose.runtime.Composable
import androidx.compose.runtime.remember
import androidx.compose.ui.graphics.Color
import androidx.core.content.ContextCompat

@RequiresApi(Build.VERSION_CODES.UPSIDE_DOWN_CAKE)
fun getColorScheme(context: Context, isDark: Boolean): ColorScheme {
    return if (isDark) {
        // Dark color scheme
        ColorScheme(
            // Primary (accent1)
            primary = Color(ContextCompat.getColor(context, color.system_accent1_300)),
            onPrimary = Color(ContextCompat.getColor(context, color.system_accent1_900)),
            primaryContainer = Color(ContextCompat.getColor(context, color.system_accent1_700)),
            onPrimaryContainer = Color(ContextCompat.getColor(context, color.system_accent1_100)),
            inversePrimary = Color(ContextCompat.getColor(context, color.system_accent1_400)),

            // Secondary (accent2)
            secondary = Color(ContextCompat.getColor(context, color.system_accent2_300)),
            onSecondary = Color(ContextCompat.getColor(context, color.system_accent2_900)),
            secondaryContainer = Color(ContextCompat.getColor(context, color.system_accent2_700)),
            onSecondaryContainer = Color(ContextCompat.getColor(context, color.system_accent2_100)),

            // Tertiary (accent3)
            tertiary = Color(ContextCompat.getColor(context, color.system_accent3_300)),
            onTertiary = Color(ContextCompat.getColor(context, color.system_accent3_900)),
            tertiaryContainer = Color(ContextCompat.getColor(context, color.system_accent3_700)),
            onTertiaryContainer = Color(ContextCompat.getColor(context, color.system_accent3_100)),

            // Neutral/Surface (neutral1)
            background = Color(ContextCompat.getColor(context, color.system_neutral1_900)),
            onBackground = Color(ContextCompat.getColor(context, color.system_neutral1_100)),
            surface = Color(ContextCompat.getColor(context, color.system_neutral1_900)),
            onSurface = Color(ContextCompat.getColor(context, color.system_neutral1_100)),
            surfaceVariant = Color(ContextCompat.getColor(context, color.system_neutral1_800)),
            onSurfaceVariant = Color(ContextCompat.getColor(context, color.system_neutral1_200)),
            surfaceTint = Color(ContextCompat.getColor(context, color.system_accent1_700)),
            inverseSurface = Color(ContextCompat.getColor(context, color.system_neutral1_100)),
            inverseOnSurface = Color(ContextCompat.getColor(context, color.system_neutral1_800)),

            // Error
            error = Color(ContextCompat.getColor(context, color.system_error_light)),
            onError = Color(ContextCompat.getColor(context, color.system_error_dark)),
            errorContainer = Color(ContextCompat.getColor(context, color.system_error_container_light)),
            onErrorContainer = Color(ContextCompat.getColor(context, color.system_on_error_container_light)),

            // Outlines
            outline = Color(ContextCompat.getColor(context, color.system_neutral1_600)),
            outlineVariant = Color(ContextCompat.getColor(context, color.system_neutral1_700)),

            // Scrim (neutral1_900 at 50%)
            scrim = Color(ContextCompat.getColor(context, color.system_neutral1_900)).copy(alpha = 0.5f),

            // Extended surfaces
            surfaceBright = Color(ContextCompat.getColor(context, color.system_neutral1_700)),
            surfaceDim = Color(ContextCompat.getColor(context, color.system_neutral1_900)),
            surfaceContainer = Color(ContextCompat.getColor(context, color.system_neutral1_800)),
            surfaceContainerHigh = Color(ContextCompat.getColor(context, color.system_neutral1_700)),
            surfaceContainerHighest = Color(ContextCompat.getColor(context, color.system_neutral1_600)),
            surfaceContainerLow = Color(ContextCompat.getColor(context, color.system_neutral1_900)),
            surfaceContainerLowest = Color(ContextCompat.getColor(context, color.system_neutral1_900))
        )
    } else {
        // Light color scheme (original)
        ColorScheme(
            // Primary (accent1)
            primary = Color(ContextCompat.getColor(context, color.system_accent1_500)),
            onPrimary = Color(ContextCompat.getColor(context, color.system_accent1_100)),
            primaryContainer = Color(ContextCompat.getColor(context, color.system_accent1_200)),
            onPrimaryContainer = Color(ContextCompat.getColor(context, color.system_accent1_800)),
            inversePrimary = Color(ContextCompat.getColor(context, color.system_accent1_300)),

            // Secondary (accent2)
            secondary = Color(ContextCompat.getColor(context, color.system_accent2_500)),
            onSecondary = Color(ContextCompat.getColor(context, color.system_accent2_100)),
            secondaryContainer = Color(ContextCompat.getColor(context, color.system_accent2_200)),
            onSecondaryContainer = Color(ContextCompat.getColor(context, color.system_accent2_800)),

            // Tertiary (accent3)
            tertiary = Color(ContextCompat.getColor(context, color.system_accent3_500)),
            onTertiary = Color(ContextCompat.getColor(context, color.system_accent3_100)),
            tertiaryContainer = Color(ContextCompat.getColor(context, color.system_accent3_200)),
            onTertiaryContainer = Color(ContextCompat.getColor(context, color.system_accent3_800)),

            // Neutral/Surface (neutral1)
            background = Color(ContextCompat.getColor(context, color.system_neutral1_50)),
            onBackground = Color(ContextCompat.getColor(context, color.system_neutral1_900)),
            surface = Color(ContextCompat.getColor(context, color.system_neutral1_100)),
            onSurface = Color(ContextCompat.getColor(context, color.system_neutral1_900)),
            surfaceVariant = Color(ContextCompat.getColor(context, color.system_neutral1_200)),
            onSurfaceVariant = Color(ContextCompat.getColor(context, color.system_neutral1_800)),
            surfaceTint = Color(ContextCompat.getColor(context, color.system_accent1_200)),
            inverseSurface = Color(ContextCompat.getColor(context, color.system_neutral1_800)),
            inverseOnSurface = Color(ContextCompat.getColor(context, color.system_neutral1_100)),

            // Error
            error = Color(ContextCompat.getColor(context, color.system_error_dark)),
            onError = Color(ContextCompat.getColor(context, color.system_error_light)),
            errorContainer = Color(ContextCompat.getColor(context, color.system_error_container_dark)),
            onErrorContainer = Color(ContextCompat.getColor(context, color.system_on_error_container_dark)),

            // Outlines
            outline = Color(ContextCompat.getColor(context, color.system_neutral1_400)),
            outlineVariant = Color(ContextCompat.getColor(context, color.system_neutral1_300)),

            // Scrim (neutral1_900 at 50%)
            scrim = Color(ContextCompat.getColor(context, color.system_neutral1_900)).copy(alpha = 0.5f),

            // Extended surfaces
            surfaceBright = Color(ContextCompat.getColor(context, color.system_neutral1_50)),
            surfaceDim = Color(ContextCompat.getColor(context, color.system_neutral1_100)),
            surfaceContainer = Color(ContextCompat.getColor(context, color.system_neutral1_200)),
            surfaceContainerHigh = Color(ContextCompat.getColor(context, color.system_neutral1_200)),
            surfaceContainerHighest = Color(ContextCompat.getColor(context, color.system_neutral1_300)),
            surfaceContainerLow = Color(ContextCompat.getColor(context, color.system_neutral1_100)),
            surfaceContainerLowest = Color(ContextCompat.getColor(context, color.system_neutral1_100))
        )
    }
}
@Composable
fun rememberColorScheme(context: Context, dark: Boolean = isSystemInDarkTheme()) = remember {
    if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.UPSIDE_DOWN_CAKE) getColorScheme(context, dark) else null
}
