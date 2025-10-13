import 'dart:math';
import '../models/hexagon_models.dart';

class HexGeometry {
  final double hexSize;
  final HexagonPosition center;
  final bool isLetterMode;
  
  const HexGeometry({
    required this.hexSize,
    required this.center,
    this.isLetterMode = true,
  });
  
  double get hexWidth => sqrt(3) * hexSize;
  double get hexHeight => 2 * hexSize;
  
  // Rotation angle: 0° for letter mode (pointed top), 30° for number mode (flat top)
  double get rotationAngle => isLetterMode ? 0.0 : pi / 6;
  
  HexagonPosition axialToPixel(int q, int r) {
    final x = hexSize * (sqrt(3) * q + sqrt(3) / 2 * r);
    final y = hexSize * (3 / 2) * r;
    return HexagonPosition(
      x: center.x + x,
      y: center.y + y,
    );
  }
  
  // Inner ring - clockwise from 1 o'clock (30° offset)
  List<AxialCoordinate> getInnerRingCoordinates() {
    return const [
      AxialCoordinate(q: 1, r: -1),  // 1 o'clock
      AxialCoordinate(q: 1, r: 0),   // 3 o'clock
      AxialCoordinate(q: 0, r: 1),   // 5 o'clock
      AxialCoordinate(q: -1, r: 1),  // 7 o'clock
      AxialCoordinate(q: -1, r: 0),  // 9 o'clock
      AxialCoordinate(q: 0, r: -1),  // 11 o'clock
    ];
  }
  
  // Outer ring - clockwise from 1 o'clock
  List<AxialCoordinate> getOuterRingCoordinates() {
    return const [
      AxialCoordinate(q: 2, r: -2),  // 1 o'clock
      AxialCoordinate(q: 2, r: -1),  // 2 o'clock
      AxialCoordinate(q: 2, r: 0),   // 3 o'clock
      AxialCoordinate(q: 1, r: 1),   // 4 o'clock
      AxialCoordinate(q: 0, r: 2),   // 5 o'clock
      AxialCoordinate(q: -1, r: 2),  // 6 o'clock
      AxialCoordinate(q: -2, r: 2),  // 7 o'clock
      AxialCoordinate(q: -2, r: 1),  // 8 o'clock
      AxialCoordinate(q: -2, r: 0),  // 9 o'clock
      AxialCoordinate(q: -1, r: -1), // 10 o'clock
      AxialCoordinate(q: 0, r: -2),  // 11 o'clock
      AxialCoordinate(q: 1, r: -2),  // 12 o'clock
    ];
  }
}
