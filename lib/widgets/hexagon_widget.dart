import 'package:flutter/material.dart';
import 'dart:math' as math;
import '../models/keypad_config.dart';

class HexagonWidget extends StatefulWidget {
  final String label;
  final String? secondaryLabel;
  final Color backgroundColor;
  final Color textColor;
  final double size;
  final bool isPressed;
  final VoidCallback? onTap;
  final VoidCallback? onLongPress;
  final Function(bool)? onHover;
  final Widget? child;
  final double rotationAngle;

  const HexagonWidget({
    super.key,
    required this.label,
    this.secondaryLabel,
    required this.backgroundColor,
    required this.textColor,
    required this.size,
    this.isPressed = false,
    this.onTap,
    this.onLongPress,
    this.onHover,
    this.child,
    this.rotationAngle = 0.0,
  });

  @override
  State<HexagonWidget> createState() => _HexagonWidgetState();
}

class _HexagonWidgetState extends State<HexagonWidget> {
  bool _isHovering = false;
  bool _isPressed = false;

  @override
  Widget build(BuildContext context) {
    final displayBgColor = (widget.isPressed || _isPressed)
        ? KeypadConfig.getComplementaryColor(widget.backgroundColor)
        : widget.backgroundColor;

    final finalTextColor = KeypadConfig.getComplementaryColor(displayBgColor);

    return MouseRegion(
      onEnter: (_) {
        setState(() => _isHovering = true);
        widget.onHover?.call(true);
      },
      onExit: (_) {
        setState(() => _isHovering = false);
        widget.onHover?.call(false);
      },
      child: GestureDetector(
        onTapDown: (_) => setState(() => _isPressed = true),
        onTapUp: (_) => setState(() => _isPressed = false),
        onTapCancel: () => setState(() => _isPressed = false),
        onTap: widget.onTap,
        onLongPress: widget.onLongPress,
        child: Transform.rotate(
          angle: widget.rotationAngle,
          child: SizedBox(
            width: widget.size,
            height: widget.size * 2 / math.sqrt(3),
            child: CustomPaint(
              painter: HexagonPainter(
                color: displayBgColor,
                glowIntensity: _isHovering ? 0.8 : 0.0,
              ),
              child: Transform.rotate(
                angle: -widget.rotationAngle,
                child: Center(
                  child: widget.child ??
                      (widget.secondaryLabel != null
                          ? Row(
                              mainAxisSize: MainAxisSize.min,
                              children: [
                                Text(
                                  widget.label,
                                  style: TextStyle(
                                    color: finalTextColor,
                                    fontSize: widget.size * 0.35,
                                    fontWeight: FontWeight.bold,
                                  ),
                                ),
                                Text(
                                  ' ',
                                  style: TextStyle(
                                    color: finalTextColor,
                                    fontSize: widget.size * 0.35,
                                    fontWeight: FontWeight.bold,
                                  ),
                                ),
                                Text(
                                  widget.secondaryLabel!,
                                  style: TextStyle(
                                    color: finalTextColor,
                                    fontSize: widget.size * 0.35,
                                    fontWeight: FontWeight.bold,
                                  ),
                                ),
                              ],
                            )
                          : Text(
                              widget.label,
                              style: TextStyle(
                                color: finalTextColor,
                                fontSize: widget.size * 0.35,
                                fontWeight: FontWeight.bold,
                              ),
                              textAlign: TextAlign.center,
                            )),
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

  HexagonPainter({
    required this.color,
    this.glowIntensity = 0.0,
  });

  @override
  void paint(Canvas canvas, Size size) {
    final paint = Paint()
      ..color = color
      ..style = PaintingStyle.fill;

    final path = _createHexagonPath(size);

    if (glowIntensity > 0) {
      final glowPaint = Paint()
        ..color = color.withOpacity(glowIntensity * 0.6)
        ..maskFilter = const MaskFilter.blur(BlurStyle.normal, 12);
      canvas.drawPath(path, glowPaint);
    }

    canvas.drawPath(path, paint);

    final borderPaint = Paint()
      ..color = Colors.white.withOpacity(0.2)
      ..style = PaintingStyle.stroke
      ..strokeWidth = 1;
    canvas.drawPath(path, borderPaint);

    if (glowIntensity > 0) {
      final complementaryPaint = Paint()
        ..color = KeypadConfig.getComplementaryColor(color)
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
