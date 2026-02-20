import 'package:flutter/material.dart';
import 'package:flutter/rendering.dart';
import 'dart:math' as math;

class HeksagonTutcboks extends SingleChildRenderObjectWidget {
  final double rotationAngle;

  const HeksagonTutcboks({
    super.key,
    this.rotationAngle = 0.0,
    required super.child,
  });

  @override
  RenderObject createRenderObject(BuildContext context) {
    return RenderHeksagonTutcboks(rotationAngle: rotationAngle);
  }

  @override
  void updateRenderObject(
      BuildContext context, RenderHeksagonTutcboks renderObject) {
    renderObject.rotationAngle = rotationAngle;
  }
}

class RenderHeksagonTutcboks extends RenderProxyBox {
  double rotationAngle;

  RenderHeksagonTutcboks({
    this.rotationAngle = 0.0,
  });

  // Cache path for performance
  Path? _cachedHexPath;
  Size? _cachedSize;
  double? _cachedRotation;

  Path _createHexagonPath(Size size) {
    if (_cachedHexPath != null &&
        _cachedSize == size &&
        _cachedRotation == rotationAngle) {
      return _cachedHexPath!;
    }

    final path = Path();
    final centerX = size.width / 2;
    final centerY = size.height / 2;

    // The painter uses size.height / 2 for the radius. We subtract a small
    // amount to create a "safe zone" and prevent the hit area from
    // bleeding into neighboring hexagons.
    final radius = (size.height / 2) - 2.0;

    for (int i = 0; i < 6; i++) {
      double angle = (i * 60 - 30) * (math.pi / 180); // Pointy-top
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

    _cachedHexPath = path;
    _cachedSize = size;
    _cachedRotation = rotationAngle;
    return path;
  }

  @override
  bool hitTest(BoxHitTestResult result, {required Offset position}) {
    // Custom check: ensure the point is inside the hexagon path. If not,
    // we return false immediately, and neither this widget nor its children
    // (including the GestureDetector) will receive the event.
    if (!_createHexagonPath(size).contains(position)) {
      return false;
    }
    // If the point is inside our shape, proceed with the default hit-testing
    // logic for our children.
    return super.hitTest(result, position: position);
  }
}
