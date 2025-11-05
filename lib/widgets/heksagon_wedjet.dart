import 'package:flutter/material.dart';
import 'dart:math' as math;
import '../models/kepad_konfeg.dart';
import './awtpit_heksagon_wedjet.dart';

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
  Path? _path;
  OverlayEntry? _overlayEntry;

  @override
  void initState() {
    super.initState();
    _path = _createHexagonPath(Size(widget.size, widget.size * 2 / math.sqrt(3)));
  }

  void _showOverlay(BuildContext context) {
    final overlay = Overlay.of(context, rootOverlay: true);

    _overlayEntry = OverlayEntry(
      builder: (context) => Center(
        child: AwtpitHeksagonWedjet(
          text: widget.label ?? '',
          style: TextStyle(
            color: widget.textColor,
            fontSize: widget.fontSize ?? widget.size * 0.35,
            fontWeight: FontWeight.bold,
          ),
        ),
      ),
    );

    overlay.insert(_overlayEntry!); 
  }

  void _removeOverlay() {
    _overlayEntry?.remove();
    _overlayEntry = null;
  }

  @override
  void dispose() {
    _removeOverlay();
    super.dispose();
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

  bool _hitTestHexagon(Offset position) {
    if (_path == null) return false;

    final Matrix4 transform = Matrix4.identity()
      ..translate(widget.size / 2, (widget.size * 2 / math.sqrt(3)) / 2)
      ..rotateZ(widget.rotationAngle)
      ..translate(-widget.size / 2, -(widget.size * 2 / math.sqrt(3)) / 2);

    final Offset transformedPosition = MatrixUtils.transformPoint(transform, position);

    return _path!.contains(transformedPosition);
  }

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
        behavior: HitTestBehavior.translucent,
        onTapDown: (details) {
          if (_hitTestHexagon(details.localPosition)) {
            setState(() => _isPressed = true);
            widget.onPressedStateChanged?.call(true);
            widget.onTapDown?.call(details);
            _showOverlay(context);
          }
        },
        onTapUp: (_) {
          setState(() => _isPressed = false);
          widget.onPressedStateChanged?.call(false);
          _removeOverlay();
        },
        onTapCancel: () {
          setState(() => _isPressed = false);
          widget.onPressedStateChanged?.call(false);
          _removeOverlay();
        },
        onLongPressStart: (details) {
          if (_hitTestHexagon(details.localPosition)) {
            widget.onLongPress?.call();
          }
        },
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

  const HexagonPainter({
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
        ..color = KepadKonfeg.getComplementaryColor(color).withValues(alpha: glowIntensity * 0.6)
        ..maskFilter = const MaskFilter.blur(BlurStyle.normal, 12);
      canvas.drawPath(path, glowPaint);
    }

    canvas.drawPath(path, paint);

    final borderPaint = Paint()
      ..color = Colors.white.withValues(alpha: 0.2)
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
  bool shouldRepaint(covariant HexagonPainter oldDelegate) {
    return color != oldDelegate.color || glowIntensity != oldDelegate.glowIntensity;
  }
}

