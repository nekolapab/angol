import 'dart:math';
import '../models/hexagon_models.dart';

class HexGeometry {
  final double hexSize;
  final HexagonPosition center;
  final bool isLetterMode;

  const HexGeometry({
    this.hexSize = 72,
    required this.center,
    this.isLetterMode = true,
  });

  double get hexWidth => sqrt(3) * hexSize;
  double get hexHeight => 2 * hexSize;
  double get rotationAngle => isLetterMode ? 0.0 : pi / 6;

  HexagonPosition axialToPixel(int q, int r) {
<<<<<<< Updated upstream
    final x = hexSize * (sqrt(3) * q + sqrt(3) / 2 * r);
    final y = hexSize * (3 / 2) * r;
=======
    double x, y;
    if (isLetterMode) {
      // Pointed-top
      x = hexSize * (sqrt(3) * q + sqrt(3) / 2 * r);
      y = hexSize * (3 / 2) * r;
    } else {
      // Flat-top - adjusted for no spacing between hexagons
      x = hexSize * 2 * q;
      y = hexSize * sqrt(3) * r;
    }
>>>>>>> Stashed changes
    return HexagonPosition(
      x: center.x + x,
      y: center.y + y,
    );
  }
  
  List<AxialCoordinate> getInnerRingCoordinates() {
    return const [
      AxialCoordinate(q: 1, r: -1),
      AxialCoordinate(q: 1, r: 0),
      AxialCoordinate(q: 0, r: 1),
      AxialCoordinate(q: -1, r: 1),
      AxialCoordinate(q: -1, r: 0),
      AxialCoordinate(q: 0, r: -1),
    ];
  }
  
  List<AxialCoordinate> getOuterRingCoordinates() {
    return const [
      AxialCoordinate(q: 2, r: -2),
      AxialCoordinate(q: 2, r: -1),
      AxialCoordinate(q: 2, r: 0),
      AxialCoordinate(q: 1, r: 1),
      AxialCoordinate(q: 0, r: 2),
      AxialCoordinate(q: -1, r: 2),
      AxialCoordinate(q: -2, r: 2),
      AxialCoordinate(q: -2, r: 1),
      AxialCoordinate(q: -2, r: 0),
      AxialCoordinate(q: -1, r: -1),
      AxialCoordinate(q: 0, r: -2),
      AxialCoordinate(q: 1, r: -2),
    ];
  }
}