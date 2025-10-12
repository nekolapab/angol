
import 'package:flutter/material.dart';

class HexagonPosition {
  final double x;
  final double y;
  const HexagonPosition({required this.x, required this.y});
}

class AxialCoordinate {
  final int q;
  final int r;
  const AxialCoordinate({required this.q, required this.r});
}

class ModuleData {
  final String id;
  final String name;
  final Color color;
  final int position;
  final bool isActive;

  const ModuleData({
    required this.id,
    required this.name,
    required this.color,
    required this.position,
    this.isActive = false,
  });

  ModuleData copyWith({
    String? id,
    String? name,
    Color? color,
    int? position,
    bool? isActive,
  }) {
    return ModuleData(
      id: id ?? this.id,
      name: name ?? this.name,
      color: color ?? this.color,
      position: position ?? this.position,
      isActive: isActive ?? this.isActive,
    );
  }

  Map<String, dynamic> toJson() => {
    'id': id,
    'name': name,
    'color': color.value,
    'position': position,
    'isActive': isActive,
  };

  factory ModuleData.fromJson(Map<String, dynamic> json) => ModuleData(
    id: json['id'],
    name: json['name'],
    color: Color(json['color']),
    position: json['position'],
    isActive: json['isActive'] ?? false,
  );
}
