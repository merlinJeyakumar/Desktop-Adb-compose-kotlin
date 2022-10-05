#Show a ToolTip

```kotlin
tooltipPlacement = TooltipPlacement.CursorPoint(
alignment = Alignment.BottomEnd,
offset = if (index % 2 == 0) DpOffset(-16.dp, 0.dp) else DpOffset.Zero // tooltip offset
)
```

#Gravity = center
```kotlin
modifier = Modifier.align(alignment = Alignment.CenterHorizontally),
```

#Window wrap_content
```kotlin
state = rememberWindowState(width = Dp.Unspecified, height = Dp.Unspecified),
```