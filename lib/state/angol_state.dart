import 'package:flutter/material.dart';
import '../models/hexagon_models.dart';

class AngolState extends ChangeNotifier {
  List<ModuleData> modules = [
    const ModuleData(
        id: 'dayl', name: 'dayl', color: Color(0xFFFF0000), position: 0),
    const ModuleData(
        id: 'keypad', name: 'kepad', color: Color(0xFFFFFF00), position: 1),
    const ModuleData(
        id: 'module3', name: '', color: Color(0xFF00FF00), position: 2),
    const ModuleData(
        id: 'module4', name: '', color: Color(0xFF00FFFF), position: 3),
    const ModuleData(
        id: 'module5', name: '', color: Color(0xFF0000FF), position: 4),
    const ModuleData(
        id: 'module6', name: '', color: Color(0xFFFF00FF), position: 5),
  ];

  bool get isKeypadVisible {
    final keypadModule = modules.firstWhere((m) => m.id == 'keypad');
    return keypadModule.isActive;
  }

  void toggleModule(int index) {
    final tappedModule = modules.firstWhere((m) => m.position == index);
    final bool wasActive = tappedModule.isActive;

    modules = modules.map((m) {
      if (m.position == index) {
        return m.copyWith(isActive: !wasActive);
      } else {
        return m.copyWith(isActive: false);
      }
    }).toList();

    notifyListeners();
  }
}
