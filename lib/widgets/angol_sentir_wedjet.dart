import 'package:flutter/material.dart';
import '../utils/heksagon_djeyometre.dart';

class AngolSentirWedjet extends StatefulWidget {
  final HeksagonDjeyometre geometry;
  final bool isKeypadVisible;

  const AngolSentirWedjet({
    super.key,
    required this.geometry,
    required this.isKeypadVisible,
  });

  @override
  State<AngolSentirWedjet> createState() => _AngolSentirWedjetState();
}

class _AngolSentirWedjetState extends State<AngolSentirWedjet> {
  @override
  Widget build(BuildContext context) {
    return Container();
  }
}

