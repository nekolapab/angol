import 'dart:math';
import '../models/angol_modalz.dart';

class HeksagonDjeyometre {
  final double hexSize;
  final HexagonPosition center;
  final bool isLetterMode;

  HeksagonDjeyometre({
    required this.hexSize,
    required this.center,
    this.isLetterMode = true,
  });

  double get hexWidth => sqrt(3) * hexSize;
  double get hexHeight => 2 * hexSize;
  double get rotationAngle => 0.0;

  HexagonPosition axialToPixel(int q, int r) {
    double x, y;
    x = hexSize * (sqrt(3) * q + sqrt(3) / 2 * r);
    y = hexSize * (3 / 2) * r;
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
