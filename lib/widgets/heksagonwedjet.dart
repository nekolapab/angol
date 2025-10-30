import 'package:flutter/material.dart';
import 'dart:math' as math;
import '../models/kepadkonfeg.dart';

class Heksagonwedjet extends StatefulWidget {
  final String label;
  final String? secondaryLabel;
  final String? tertiaryLabel;
  final String? quaternaryLabel;
  final Color backgroundColor;
  final Color textColor;
  final double size;
  final bool isPressed;
  final GestureTapDownCallback? onTapDown;
  final VoidCallback? onLongPress;
  final Function(DragUpdateDetails)? onVerticalDragUpdate;
  final Function(bool)? onHover;
  final Function(bool isPressed)? onPressedStateChanged; // New callback
  final Widget? child;
  final double rotationAngle;

  final double? fontSize;

  const Heksagonwedjet({
    super.key,
    required this.label,
    this.secondaryLabel,
    this.tertiaryLabel,
    this.quaternaryLabel,
    required this.backgroundColor,
    required this.textColor,
    required this.size,
    this.isPressed = false,
    this.onTapDown,
    this.onLongPress,
    this.onVerticalDragUpdate,
    this.onHover,
    this.onPressedStateChanged, // New parameter
    this.child,
    this.rotationAngle = 0.0,
    this.fontSize,
  });

  @override
  State<Heksagonwedjet> createState() => _HeksagonwedjetState();
}

class _HeksagonwedjetState extends State<Heksagonwedjet> {
  bool _isPressed = false;

  @override
  Widget build(BuildContext context) {
    final displayBgColor = _isPressed
        ? Kepadkonfeg.getComplementaryColor(widget.backgroundColor)
        : widget.backgroundColor;

    final finalTextColor = _isPressed
        ? Kepadkonfeg.getComplementaryColor(widget.textColor)
        : widget.textColor;

    return MouseRegion(
      onEnter: (_) {
        widget.onHover?.call(true);
      },
      onExit: (_) {
        widget.onHover?.call(false);
      },
      child: GestureDetector(
        onTapDown: (details) {
          setState(() => _isPressed = true);
          widget.onPressedStateChanged?.call(true);
          widget.onTapDown?.call(details);
        },
        onTapUp: (_) {
          setState(() => _isPressed = false);
          widget.onPressedStateChanged?.call(false);
        },
        onTapCancel: () {
          setState(() => _isPressed = false);
          widget.onPressedStateChanged?.call(false);
        },
        onLongPress: widget.onLongPress,
        onVerticalDragUpdate: widget.onVerticalDragUpdate,
        child: Transform.rotate(
          angle: widget.rotationAngle,
          child: SizedBox(
            width: widget.size,
            height: widget.size * 2 / math.sqrt(3),
            child: CustomPaint(
              painter: HexagonPainter(
                color: displayBgColor,
                glowIntensity: _isPressed ? 0.8 : 0.0,
                size: Size(widget.size, widget.size * 2 / math.sqrt(3)),
              ),
              child: Transform.rotate(
                angle: -widget.rotationAngle,
                child: Stack(
                  alignment: Alignment.center,
                  children: [
                    // Main label (horizontal)
                    if (widget.child != null) widget.child! else
                    if (widget.secondaryLabel != null)
                      Row(
                        mainAxisSize: MainAxisSize.min,
                        children: [
                          Text(
                            widget.label,
                            style: TextStyle(
                              color: finalTextColor,
                              fontSize: widget.fontSize ?? widget.size * 0.35,
                              fontWeight: FontWeight.bold,
                            ),
                          ),
                          const SizedBox(width: 8), // Add space between labels
                          Text(
                            widget.secondaryLabel!,
                            style: TextStyle(
                              color: finalTextColor,
                              fontSize: widget.fontSize ?? widget.size * 0.35,
                              fontWeight: FontWeight.bold,
                            ),
                          ),
                        ],
                      )
                    else
                      Text(
                        widget.label,
                        style: TextStyle(
                          color: finalTextColor,
                          fontSize: widget.fontSize ?? widget.size * 0.35,
                          fontWeight: FontWeight.bold,
                        ),
                        textAlign: TextAlign.center,
                      ),

                    // Tertiary label (rotated 60 degrees)
                    if (widget.tertiaryLabel != null)
                      Transform.rotate(
                        angle: 60 * math.pi / 180, // 60 degrees
                        child: Text(
                          widget.tertiaryLabel!,
                          style: TextStyle(
                            color: finalTextColor,
                            fontSize: widget.fontSize ?? widget.size * 0.35,
                            fontWeight: FontWeight.bold,
                          ),
                          textAlign: TextAlign.center,
                        ),
                      ),

                    // Quaternary label (rotated -60 degrees)
                    if (widget.quaternaryLabel != null)
                      Transform.rotate(
                        angle: -60 * math.pi / 180, // -60 degrees
                        child: Text(
                          widget.quaternaryLabel!,
                          style: TextStyle(
                            color: finalTextColor,
                            fontSize: widget.fontSize ?? widget.size * 0.35,
                            fontWeight: FontWeight.bold,
                          ),
                          textAlign: TextAlign.center,
                        ),
                      ),
                  ],
                ),
              ),
            ),
          ),
        ),
      ),
    );
  }
}

class HexagonPainter extends CustomPainter {
  final Color color;
  final double glowIntensity;
  final Size size;

  HexagonPainter({
    required this.color,
    this.glowIntensity = 0.0,
    required this.size,
  });

  @override
  void paint(Canvas canvas, Size size) {
    final paint = Paint()
      ..color = color
      ..style = PaintingStyle.fill;

    final path = _createHexagonPath(size);

    if (glowIntensity > 0) {
      final glowPaint = Paint()
        ..color = color.withAlpha((255 * glowIntensity * 0.6).round())
        ..maskFilter = const MaskFilter.blur(BlurStyle.normal, 12);
      canvas.drawPath(path, glowPaint);
    }

    canvas.drawPath(path, paint);

    final borderPaint = Paint()
      ..color = Colors.white.withAlpha(51)
      ..style = PaintingStyle.stroke
      ..strokeWidth = 1;
    canvas.drawPath(path, borderPaint);

    if (glowIntensity > 0) {
      final complementaryPaint = Paint()
        ..color = Kepadkonfeg.getComplementaryColor(color)
        ..style = PaintingStyle.stroke
        ..strokeWidth = 3;
      canvas.drawPath(path, complementaryPaint);
    }
  }

  Path _createHexagonPath(Size size) {
    final path = Path()
      ..moveTo(size.width * 0.5, 0)
      ..lineTo(size.width, size.height * 0.25)
      ..lineTo(size.width, size.height * 0.75)
      ..lineTo(size.width * 0.5, size.height)
      ..lineTo(0, size.height * 0.75)
      ..lineTo(0, size.height * 0.25)
      ..close();
    return path;
  }

  @override
  bool shouldRepaint(covariant CustomPainter oldDelegate) => true;
}
