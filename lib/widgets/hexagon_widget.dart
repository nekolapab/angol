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

  @override
  Widget build(BuildContext context) {
    final displayBgColor = widget.isPressed
        ? KeypadConfig.getComplementaryColor(widget.backgroundColor)
        : widget.backgroundColor;
    final displayTextColor = widget.isPressed
        ? KeypadConfig.getComplementaryColor(widget.textColor)
        : widget.textColor;

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
        onTap: widget.onTap,
        onLongPress: widget.onLongPress,
        child: Transform.rotate(
          angle: widget.rotationAngle,
          child: SizedBox(
            width: widget.size,
            height: widget.size,
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
                                    color: displayTextColor,
                                    fontSize: widget.size * 0.35,
                                    fontWeight: FontWeight.bold,
                                  ),
                                ),
                                Text(
                                  '-',
                                  style: TextStyle(
                                    color: displayTextColor.withOpacity(0.5),
                                    fontSize: widget.size * 0.25,
                                  ),
                                ),
                                Text(
                                  widget.secondaryLabel!,
                                  style: TextStyle(
                                    color: displayTextColor.withOpacity(0.7),
                                    fontSize: widget.size * 0.3,
                                    fontWeight: FontWeight.normal,
                                  ),
                                ),
                              ],
                            )
                          : Text(
                              widget.label,
                              style: TextStyle(
                                color: displayTextColor,
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
  }

  Path _createHexagonPath(Size size) {
    final path = Path();
    final centerX = size.width / 2;
    final centerY = size.height / 2;
    final radius = math.min(centerX, centerY);

    for (int i = 0; i < 6; i++) {
      double angle = (i * 60 - 30) * (math.pi / 180);
      double x = centerX + radius * math.cos(angle);
      double y = centerY + radius * math.sin(angle);

      if (i == 0) {
        path.moveTo(x, y);
      } else {
        path.lineTo(x, y);
      }
    }
    path.close();
    return path;
  }

  @override
  bool shouldRepaint(covariant CustomPainter oldDelegate) => true;
}
