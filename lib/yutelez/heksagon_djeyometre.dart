import 'dart:math';
import '../modalz/angol_modalz.dart';

class HeksagonDjeyometre {
  final double heksSayz;
  final HeksagonPozecon sentir;
  final bool ezLeterMod;

  HeksagonDjeyometre({
    required this.heksSayz,
    required this.sentir,
    this.ezLeterMod = true,
  });

  double get heksWidlx => sqrt(3) * heksSayz;
  double get heksHayt => 2 * heksSayz;
  double get roteyconAngol => 0.0;

  HeksagonPozecon aksyalTuPeksel(int q, int r) {
    double x, y;
    x = heksSayz * (sqrt(3) * q + sqrt(3) / 2 * r);
    y = heksSayz * (3 / 2) * r;
    return HeksagonPozecon(
      x: sentir.x + x,
      y: sentir.y + y,
    );
  }

  List<AksyalKowordenat> getEnirRenqKowordenats() {
    return const [
      AksyalKowordenat(q: 1, r: -1),
      AksyalKowordenat(q: 1, r: 0),
      AksyalKowordenat(q: 0, r: 1),
      AksyalKowordenat(q: -1, r: 1),
      AksyalKowordenat(q: -1, r: 0),
      AksyalKowordenat(q: 0, r: -1),
    ];
  }

  List<AksyalKowordenat> getAwdirRenqKowordenats() {
    return const [
      AksyalKowordenat(q: 2, r: -2),
      AksyalKowordenat(q: 2, r: -1),
      AksyalKowordenat(q: 2, r: 0),
      AksyalKowordenat(q: 1, r: 1),
      AksyalKowordenat(q: 0, r: 2),
      AksyalKowordenat(q: -1, r: 2),
      AksyalKowordenat(q: -2, r: 2),
      AksyalKowordenat(q: -2, r: 1),
      AksyalKowordenat(q: -2, r: 0),
      AksyalKowordenat(q: -1, r: -1),
      AksyalKowordenat(q: 0, r: -2),
      AksyalKowordenat(q: 1, r: -2),
    ];
  }
}
