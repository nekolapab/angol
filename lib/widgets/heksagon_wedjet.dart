import 'package:flutter/material.dart';
import 'dart:math' as math;
import '../models/kepad_konfeg.dart';

class HeksagonWedjet extends StatefulWidget {
  final String? label;
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
  final Function(bool isPressed)? onPressedStateChanged;
  final Widget? child;
  final double rotationAngle;

  final double? fontSize;

  const HeksagonWedjet({
    super.key,
    this.label,
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
    this.onPressedStateChanged,
    this.child,
    this.rotationAngle = 0.0,
    this.fontSize,
  }) : assert(label != null || child != null, 'Either label or child must be provided');

  @override
  State<HeksagonWedjet> createState() => _HeksagonWedjetState();
}

class _HeksagonWedjetState extends State<HeksagonWedjet> {
  bool _isPressed = false;

  @override
  Widget build(BuildContext context) {
    final displayBgColor = _isPressed
        ? KepadKonfeg.getComplementaryColor(widget.backgroundColor)
        : widget.backgroundColor;

    final finalTextColor = _isPressed
        ? KepadKonfeg.getComplementaryColor(widget.textColor)
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
                    if (widget.child != null)
                      widget.child!
                    else
                      FittedBox(
                        fit: BoxFit.scaleDown,
                        child: Row(
                          mainAxisAlignment: MainAxisAlignment.center,
                          children: [
                            if (widget.label != null)
                              Text(
                                widget.label!,
                                style: TextStyle(
                                  color: finalTextColor,
                                  fontSize: widget.fontSize ?? widget.size * 0.35,
                                  fontWeight: FontWeight.bold,
                                ),
                              ),
                            if (widget.secondaryLabel != null)
                              const SizedBox(width: 8),
                            if (widget.secondaryLabel != null)
                              Text(
                                widget.secondaryLabel!,
                                style: TextStyle(
                                  color: finalTextColor,
                                  fontSize: widget.fontSize ?? widget.size * 0.25,
                                ),
                              ),
                          ],
                        ),
                      ),
                    if (widget.tertiaryLabel != null)
                      Positioned(
                        top: 10,
                        right: 10,
                        child: Transform.rotate(
                          angle: 45 * math.pi / 180,
                          child: Text(
                            widget.tertiaryLabel!,
                            style: TextStyle(
                              color: finalTextColor,
                              fontSize: widget.fontSize ?? widget.size * 0.2,
                            ),
                          ),
                        ),
                      ),
                    if (widget.quaternaryLabel != null)
                      Positioned(
                        bottom: 10,
                        left: 10,
                        child: Transform.rotate(
                          angle: -45 * math.pi / 180,
                          child: Text(
                            widget.quaternaryLabel!,
                            style: TextStyle(
                              color: finalTextColor,
                              fontSize: widget.fontSize ?? widget.size * 0.2,
                            ),
                          ),
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

  const HexagonPainter({
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
        ..color = KepadKonfeg.getComplementaryColor(color).withAlpha((255 * glowIntensity * 0.6).round())
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
        ..color = KepadKonfeg.getComplementaryColor(color)
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

