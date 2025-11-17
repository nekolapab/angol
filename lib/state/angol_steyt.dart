import 'package:flutter/material.dart';
import '../models/angol_modalz.dart';

class AngolSteyt extends ChangeNotifier {
  List<ModuleData> modules = [
    const ModuleData(
        id: 'dayl', name: 'dayl', color: Color(0xFF000000), position: 0, isActive: true),
    const ModuleData(
        id: 'keypad', name: 'kepad', color: Color(0xFFFF0000), position: 1, isActive: false),
    const ModuleData(
        id: 'module3', name: '', color: Color(0xFFFFFF00), position: 2, isActive: false),
    const ModuleData(
        id: 'module4', name: '', color: Color(0xFF00FF00), position: 3, isActive: false),
    const ModuleData(
        id: 'module5', name: '', color: Color(0xFF00FFFF), position: 4, isActive: false),
    const ModuleData(
        id: 'module6', name: '', color: Color(0xFF0000FF), position: 5, isActive: false),
    const ModuleData(
        id: 'module7', name: '', color: Color(0xFFFF00FF), position: 6, isActive: false),
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

  void reset({bool notify = true}) {
    modules = [
      const ModuleData(
          id: 'dayl', name: 'dayl', color: Color(0xFF000000), position: 0, isActive: true),
      const ModuleData(
          id: 'keypad', name: 'kepad', color: Color(0xFFFF0000), position: 1, isActive: false),
      const ModuleData(
          id: 'module3', name: '', color: Color(0xFFFFFF00), position: 2, isActive: false),
      const ModuleData(
          id: 'module4', name: '', color: Color(0xFF00FF00), position: 3, isActive: false),
      const ModuleData(
          id: 'module5', name: '', color: Color(0xFF00FFFF), position: 4, isActive: false),
      const ModuleData(
          id: 'module6', name: '', color: Color(0xFF0000FF), position: 5, isActive: false),
      const ModuleData(
          id: 'module7', name: '', color: Color(0xFFFF00FF), position: 6, isActive: false),
    ];
    if (notify) {
      notifyListeners();
    }
  }
}

