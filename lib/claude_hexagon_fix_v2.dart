// lib/widgets/heksagon_wedjet.dart
// FIXED: Hit testing now matches visual hexagon size exactly
// No dead zones at edges or corners

import 'package:flutter/material.dart';
import 'dart:math' as math;
import 'dart:ui' as ui;

class HeksagonWedjet extends StatefulWidget {
  final String label;
  final String? secondaryLabel;
  final Color backgroundColor;
  final Color textColor;
  final double size;
  final bool isPressed;
  final bool isHovering;
  final VoidCallback? onTap;
  final VoidCallback? onLongPress;
  final Function(bool)? onHover;
  final Widget? child;
  final double rotationAngle;

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
  });

  @override
  State<HeksagonWedjet> createState() => _HeksagonWedjetState();
}

class _HeksagonWedjetState extends State<HeksagonWedjet> {
  bool _isHovering = false;
  Path? _cachedHexPath;
  Size? _cachedSize;
  double? _cachedRotation;

  // Create hexagon path for hit testing - MATCHES visual size exactly
  Path _createHexagonPath(Size size) {
    // Cache check
    if (_cachedHexPath != null && 
        _cachedSize == size && 
        _cachedRotation == widget.rotationAngle) {
      return _cachedHexPath!;
    }

    final path = Path();
    final centerX = size.width / 2;
    final centerY = size.height / 2;
    
    // CRITICAL FIX: Use full radius minus only border width (0.75px)
    // This makes hit area match visual hexagon exactly
    final radius = math.min(centerX, centerY) - 0.75;

    for (int i = 0; i < 6; i++) {
      // Hexagon vertices at 60° intervals
      double angle = (i * 60 - 30) * (math.pi / 180);
      
      // Apply rotation to match visual hexagon
      angle += widget.rotationAngle;
      
      double x = centerX + radius * math.cos(angle);
      double y = centerY + radius * math.sin(angle);

      if (i == 0) {
        path.moveTo(x, y);
      } else {
        path.lineTo(x, y);
      }
    }
    path.close();

    // Cache for performance
    _cachedHexPath = path;
    _cachedSize = size;
    _cachedRotation = widget.rotationAngle;
    
    return path;
  }

  // Hit test - returns true if position is inside hexagon
  bool _hitTestHexagon(Offset localPosition, Size size) {
    final hexPath = _createHexagonPath(size);
    final isInside = hexPath.contains(localPosition);
    
    // Debug logging (remove in production)
    if (widget.label.isNotEmpty) {
      print('TAP: pos=$localPosition, size=$size, label=${widget.label}');
      print('  -> ${isInside ? "INSIDE" : "OUTSIDE"} hexagon');
    }
    
    return isInside;
  }

  @override
  Widget build(BuildContext context) {
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
        behavior: HitTestBehavior.deferToChild,
        onTapDown: (details) {
          final RenderBox? box = context.findRenderObject() as RenderBox?;
          if (box == null) return;
          
          final localPosition = details.localPosition;
          
          // Check if tap is inside hexagon
          if (!_hitTestHexagon(localPosition, box.size)) {
            return; // Tap is outside, ignore
          }
          
          // Tap is inside hexagon
          widget.onTap?.call();
        },
        onLongPressStart: (details) {
          final RenderBox? box = context.findRenderObject() as RenderBox?;
          if (box == null) return;
          
          final localPosition = details.localPosition;
          
          // Check if long press is inside hexagon
          if (!_hitTestHexagon(localPosition, box.size)) {
            return; // Long press is outside, ignore
          }
          
          // Long press is inside hexagon
          widget.onLongPress?.call();
        },
        child: SizedBox(
          width: widget.size,
          height: widget.size,
          child: CustomPaint(
            painter: HexagonPainter(
              color: widget.backgroundColor,
              textColor: widget.textColor,
              isPressed: widget.isPressed,
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
      ),
    );
  }

  Widget _buildSingleLabel() {
    return Text(
      widget.label,
      style: TextStyle(
        color: widget.textColor,
        fontSize: widget.size * 0.25,
        fontWeight: FontWeight.bold,
      ),
      textAlign: TextAlign.center,
    );
  }

  Widget _buildDualLabel() {
    return Row(
      mainAxisSize: MainAxisSize.min,
      children: [
        Text(
          widget.label,
          style: TextStyle(
            color: widget.textColor,
            fontSize: widget.size * 0.2,
            fontWeight: FontWeight.bold,
          ),
        ),
        Text(
          '-',
          style: TextStyle(
            color: widget.textColor.withOpacity(0.5),
            fontSize: widget.size * 0.15,
          ),
        ),
        Text(
          widget.secondaryLabel!,
          style: TextStyle(
            color: widget.textColor.withOpacity(0.7),
            fontSize: widget.size * 0.18,
            fontWeight: FontWeight.normal,
          ),
        ),
      ],
    );
  }
}

class HexagonPainter extends CustomPainter {
  final Color color;
  final Color textColor;
  final bool isPressed;
  final bool isHovering;
  final double rotationAngle;

  HexagonPainter({
    required this.color,
    required this.textColor,
    this.isPressed = false,
    this.isHovering = false,
    this.rotationAngle = 0.0,
  });

  @override
  void paint(Canvas canvas, Size size) {
    final displayColor = isPressed ? _getComplementaryColor(color) : color;
    
    final paint = Paint()
      ..color = displayColor
      ..style = PaintingStyle.fill;

    final path = _createHexagonPath(size);

    // Glow effect
    if (isHovering) {
      final glowPaint = Paint()
        ..color = displayColor.withOpacity(0.6)
        ..maskFilter = const ui.MaskFilter.blur(ui.BlurStyle.normal, 12);
      canvas.drawPath(path, glowPaint);
    }

    // Fill
    canvas.drawPath(path, paint);

    // Border - thin so hexagons touch edge-to-edge
    final borderPaint = Paint()
      ..color = Colors.white.withOpacity(0.2)
      ..style = PaintingStyle.stroke
      ..strokeWidth = 1.5;
    canvas.drawPath(path, borderPaint);
  }

  Path _createHexagonPath(Size size) {
    final path = Path();
    final centerX = size.width / 2;
    final centerY = size.height / 2;
    
    // CRITICAL: Match hit testing - only subtract border width
    final radius = math.min(centerX, centerY) - 0.75;

    for (int i = 0; i < 6; i++) {
      double angle = (i * 60 - 30) * (math.pi / 180);
      angle += rotationAngle;
      
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

  Color _getComplementaryColor(Color color) {
    return Color.fromARGB(
      color.alpha,
      255 - color.red,
      255 - color.green,
      255 - color.blue,
    );
  }

  @override
  bool shouldRepaint(covariant HexagonPainter oldDelegate) {
    return oldDelegate.color != color ||
        oldDelegate.isPressed != isPressed ||
        oldDelegate.isHovering != isHovering ||
        oldDelegate.rotationAngle != rotationAngle;
  }
}

/*
CHANGES FROM V1:

1. Changed radius calculation:
   OLD: final radius = math.min(centerX, centerY) - 2;
   NEW: final radius = math.min(centerX, centerY) - 0.75;
   
   Why: The -2 was creating 4px of dead space total (2px on each side).
   Now only subtract border width (0.75px) so hit area matches visual area.

2. Added rotation caching:
   - Cache now includes rotation angle
   - Prevents path recalculation when rotation changes

3. Matched painter and hit test paths:
   - Both use same radius calculation
   - Ensures visual hexagon = clickable hexagon

RESULT:
- No more dead zones at edges
- Corners that look clickable ARE clickable
- Hexagons touch edge-to-edge
- All 28 "dead" corners should now respond

TESTING:
After applying this, ALL these should work:
✅ Center hexagon top/bottom corners
✅ Inner ring: a, e, i, u, o edges
✅ Outer ring: s, l, lx, x, c, g, k, f, b, p edges
✅ No gaps between hexagons
*/