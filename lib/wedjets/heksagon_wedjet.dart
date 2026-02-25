import 'package:flutter/material.dart';
import 'dart:math' as math;
import 'dart:ui' as ui;
import 'package:flutter/foundation.dart'
    show
        defaultTargetPlatform,
        TargetPlatform,
        kIsWeb; // Import for platform check
import '../modalz/kepad_konfeg.dart';
import 'heksagon_tutcboks.dart'; // Import the new touchbox

class HeksagonWedjet extends StatefulWidget {
  final String label;
  final String? secondaryLabel;
  final Color backgroundColor;
  final Color textColor;
  final double size;
  final bool? isPressed;
  final bool isHovering;
  final VoidCallback? onTap;
  final VoidCallback? onLongPress;
  final Function(bool)? onHover;
  final Widget? child;
  final double rotationAngle;
  final double? fontSize;
  final ValueChanged<bool>? onPressedChanged;

  const HeksagonWedjet({
    super.key,
    required this.label,
    this.secondaryLabel,
    required this.backgroundColor,
    required this.textColor,
    required this.size,
    this.isPressed = false,
    this.isHovering = false,
    this.onTap,
    this.onLongPress,
    this.onHover,
    this.child,
    this.rotationAngle = 0.0,
    this.fontSize,
    this.onPressedChanged,
  });

  @override
  State<HeksagonWedjet> createState() => _HeksagonWedjetSteyt();
}

class _HeksagonWedjetSteyt extends State<HeksagonWedjet> {
  bool _isPressed = false;
  bool _isHovering = false;

  void _handleHover(bool isHovering) {
    if (isHovering != _isHovering) {
      setState(() {
        _isHovering = isHovering;
      });
      widget.onHover?.call(isHovering);
    }
  }

  Color _getDisplayTextContrastColor() {
    if (_isPressed) {
      // Background is inverted, so text should be the original background color.
      return widget.backgroundColor;
    } else {
      // Background is normal, so text should be the complementary color.
      return KepadKonfeg.getComplementaryColor(widget.backgroundColor);
    }
  }

  @override
  Widget build(BuildContext context) {
    // The HeksagonHitbox provides the accurate hit-testing shape.
    // The MouseRegion and GestureDetector handle the user interaction.

    Widget gestureDetectorChild = GestureDetector(
      behavior: HitTestBehavior.translucent,
      onTapDown: (_) {
        setState(() => _isPressed = true);
        widget.onPressedChanged?.call(true);
        widget.onTap?.call(); // Fire action immediately on press down
      },
      onTapUp: (_) {
        // Delay the visual "un-press" to ensure it's visible on quick taps.
        Future.delayed(const Duration(milliseconds: 1000 ~/ 12), () {
          if (mounted) {
            setState(() => _isPressed = false);
            widget.onPressedChanged?.call(false);
          }
        });
      },
      onTapCancel: () {
        // If cancelled, revert immediately.
        setState(() => _isPressed = false);
        widget.onPressedChanged?.call(false);
      },
      onLongPress: widget.onLongPress,
      onLongPressStart: (_) {
        setState(() => _isPressed = true);
        widget.onPressedChanged?.call(true);
      },
      onLongPressEnd: (_) {
        // On long press end, revert immediately.
        setState(() => _isPressed = false);
        widget.onPressedChanged?.call(false);
      },
      child: SizedBox(
        width: widget.size,
        height: widget.size * (2 / math.sqrt(3)),
        child: CustomPaint(
          painter: HexagonPainter(
            color: widget.backgroundColor,
            textColor: widget.textColor,
            isPressed: widget.isPressed,
            isMomentarilyPressed: _isPressed,
            isHovering: _isHovering || widget.isHovering,
            rotationAngle: widget.rotationAngle,
          ),
          child: Transform.rotate(
            angle: -widget.rotationAngle,
            child: Center(
              child: widget.child ??
                  (widget.secondaryLabel != null
                      ? _buildDualLabel()
                      : _buildSingleLabel()),
            ),
          ),
        ),
      ),
    );

    // Conditionally wrap with MouseRegion for non-mobile platforms
    if (defaultTargetPlatform == TargetPlatform.macOS ||
        defaultTargetPlatform == TargetPlatform.windows ||
        defaultTargetPlatform == TargetPlatform.linux ||
        defaultTargetPlatform == TargetPlatform.fuchsia ||
        kIsWeb) {
      gestureDetectorChild = MouseRegion(
        onEnter: (_) => _handleHover(true),
        onExit: (_) => _handleHover(false),
        cursor: SystemMouseCursors.click,
        child: gestureDetectorChild,
      );
    }

    return HeksagonTutcboks(
      rotationAngle: widget.rotationAngle,
      child: gestureDetectorChild,
    );
  }

  Widget _buildSingleLabel() {
    return Text(
      widget.label,
      style: TextStyle(
        color: _getDisplayTextContrastColor(),
        fontSize: widget.fontSize ?? widget.size * 1 / 4,
        fontWeight: FontWeight.bold,
      ),
      textAlign: TextAlign.center,
    );
  }

  Widget _buildDualLabel() {
    return FittedBox(
      fit: BoxFit.contain,
      child: Row(
        mainAxisSize: MainAxisSize.min,
        children: [
          Text(
            widget.label,
            style: TextStyle(
              color: _getDisplayTextContrastColor(),
              fontSize: widget.fontSize ?? 12.0,
              fontWeight: FontWeight.bold,
            ),
          ),
          const SizedBox(width: 12.0),
          Text(
            widget.secondaryLabel!,
            style: TextStyle(
              color: _getDisplayTextContrastColor(),
              fontSize: widget.fontSize ?? 12.0,
              fontWeight: FontWeight.bold,
            ),
          ),
        ],
      ),
    );
  }
}

class HexagonPainter extends CustomPainter {
  final Color color;
  final Color textColor;
  final bool? isPressed; // This now represents isActive
  final bool isMomentarilyPressed; // New parameter for momentary press
  final bool isHovering;
  final double rotationAngle;

  HexagonPainter({
    required this.color,
    required this.textColor,
    this.isPressed = false,
    this.isMomentarilyPressed = false, // Initialize new parameter
    this.isHovering = false,
    this.rotationAngle = 0.0,
  });

  @override
  void paint(Canvas canvas, Size size) {
    final displayColor =
        isMomentarilyPressed ? KepadKonfeg.getComplementaryColor(color) : color;

    final paint = Paint()
      ..color = displayColor
      ..style = PaintingStyle.fill;

    // VISUAL hexagon - ORIGINAL SIZE with gap
    final path = _createVisualPath(size);

    // Glow
    if (isHovering) {
      final glowPaint = Paint()
        ..color = displayColor.withAlpha(255) // Using withAlpha for clarity
        ..maskFilter = const ui.MaskFilter.blur(ui.BlurStyle.normal, 24);
      canvas.drawPath(path, glowPaint);
    }

    // Fill
    canvas.drawPath(path, paint);
  }

  // Visual path - fills the SizedBox
  Path _createVisualPath(Size size) {
    final path = Path();
    final centerX = size.width / 2;
    final centerY = size.height / 2;

    final side = size.height / 2;

    // Vertices for a pointy-top hexagon
    path.moveTo(centerX, centerY - side);
    path.lineTo(centerX + side * math.sqrt(3) / 2, centerY - side / 2);
    path.lineTo(centerX + side * math.sqrt(3) / 2, centerY + side / 2);
    path.lineTo(centerX, centerY + side);
    path.lineTo(centerX - side * math.sqrt(3) / 2, centerY + side / 2);
    path.lineTo(centerX - side * math.sqrt(3) / 2, centerY - side / 2);
    path.close();
    return path;
  }

  @override
  bool shouldRepaint(covariant HexagonPainter oldDelegate) {
    return oldDelegate.color != color ||
        oldDelegate.isPressed != isPressed ||
        oldDelegate.isMomentarilyPressed != isMomentarilyPressed ||
        oldDelegate.isHovering != isHovering ||
        oldDelegate.rotationAngle != rotationAngle;
  }
}
