import 'package:flutter/material.dart';
import '../modalz/angol_modalz.dart';

class AngolSteyt extends ChangeNotifier {
  List<ModyilDeyda> modyilz = [
    const ModyilDeyda(
        id: 'dayl',
        neym: 'dayl',
        kulor: Color(0xFF000000),
        pozecon: 0,
        ezAktiv: true),
    const ModyilDeyda(
        id: 'keypad',
        neym: 'kepad',
        kulor: Color(0xFFFF0000),
        pozecon: 1,
        ezAktiv: false),
    const ModyilDeyda(
        id: 'module3',
        neym: '',
        kulor: Color(0xFFFFFF00),
        pozecon: 2,
        ezAktiv: false),
    const ModyilDeyda(
        id: 'module4',
        neym: '',
        kulor: Color(0xFF00FF00),
        pozecon: 3,
        ezAktiv: false),
    const ModyilDeyda(
        id: 'module5',
        neym: '',
        kulor: Color(0xFF00FFFF),
        pozecon: 4,
        ezAktiv: false),
    const ModyilDeyda(
        id: 'module6',
        neym: '',
        kulor: Color(0xFF0000FF),
        pozecon: 5,
        ezAktiv: false),
    const ModyilDeyda(
        id: 'module7',
        neym: '',
        kulor: Color(0xFFFF00FF),
        pozecon: 6,
        ezAktiv: false),
  ];

  bool get ezKepadVezebil {
    final keypadModule = modyilz.firstWhere((m) => m.id == 'keypad');
    return keypadModule.ezAktiv;
  }

  void togilModyil(int index) {
    final tappedModule = modyilz.firstWhere((m) => m.pozecon == index);
    final bool wasActive = tappedModule.ezAktiv;

    modyilz = modyilz.map((m) {
      if (m.pozecon == index) {
        return m.copyWith(ezAktiv: !wasActive);
      } else {
        return m.copyWith(ezAktiv: false);
      }
    }).toList();

    notifyListeners();
  }

  void reset({bool notify = true}) {
    modyilz = [
      const ModyilDeyda(
          id: 'dayl',
          neym: 'dayl',
          kulor: Color(0xFF000000),
          pozecon: 0,
          ezAktiv: true),
      const ModyilDeyda(
          id: 'keypad',
          neym: 'kepad',
          kulor: Color(0xFFFF0000),
          pozecon: 1,
          ezAktiv: false),
      const ModyilDeyda(
          id: 'module3',
          neym: '',
          kulor: Color(0xFFFFFF00),
          pozecon: 2,
          ezAktiv: false),
      const ModyilDeyda(
          id: 'module4',
          neym: '',
          kulor: Color(0xFF00FF00),
          pozecon: 3,
          ezAktiv: false),
      const ModyilDeyda(
          id: 'module5',
          neym: '',
          kulor: Color(0xFF00FFFF),
          pozecon: 4,
          ezAktiv: false),
      const ModyilDeyda(
          id: 'module6',
          neym: '',
          kulor: Color(0xFF0000FF),
          pozecon: 5,
          ezAktiv: false),
      const ModyilDeyda(
          id: 'module7',
          neym: '',
          kulor: Color(0xFFFF00FF),
          pozecon: 6,
          ezAktiv: false),
    ];
    if (notify) {
      notifyListeners();
    }
  }
}
