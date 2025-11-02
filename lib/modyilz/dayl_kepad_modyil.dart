import 'package:flutter/material.dart';
import '../utils/heksagon_djeyometre.dart';
import '../widgets/sentir_heksagon_wedjet.dart';
import '../widgets/enir_renq_wedjet.dart';
import '../widgets/awdir_renq_wedjet.dart';

class DaylKepadModyil extends StatefulWidget {
  final HeksagonDjeyometre geometry;
  final void Function(String, {bool isLongPress, String? primaryChar})
      onHexKeyPress;
  final bool isKeypadVisible;

  const DaylKepadModyil({
    super.key,
    required this.geometry,
    required this.onHexKeyPress,
    required this.isKeypadVisible,
  });

  @override
  State<DaylKepadModyil> createState() => _DaylKepadModyilState();
}

class _DaylKepadModyilState extends State<DaylKepadModyil> {

  @override
  Widget build(BuildContext context) {

    return Stack(
      children: [
        SentirHeksagonWedjet(geometry: widget.geometry),
        EnirRenqWedjet(
          geometry: widget.geometry,
          onHexKeyPress: widget.onHexKeyPress,
        ),
        AwdirRenqWedjet(
          geometry: widget.geometry,
          onHexKeyPress: widget.onHexKeyPress,
        ),
      ],
    );
  }
}

