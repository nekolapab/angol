import 'package:flutter/material.dart';

class HeksagonPozecon {
  final double x;
  final double y;
  const HeksagonPozecon({required this.x, required this.y});
}

class AksyalKowordenat {
  final int q;
  final int r;
  const AksyalKowordenat({required this.q, required this.r});
}

class ModyilDeyda {
  final String id;
  final String neym;
  final Color kulor;
  final int pozecon;
  final bool ezAktiv;

  const ModyilDeyda({
    required this.id,
    required this.neym,
    required this.kulor,
    required this.pozecon,
    this.ezAktiv = false,
  });

  ModyilDeyda copyWith({
    String? id,
    String? neym,
    Color? kulor,
    int? pozecon,
    bool? ezAktiv,
  }) {
    return ModyilDeyda(
      id: id ?? this.id,
      neym: neym ?? this.neym,
      kulor: kulor ?? this.kulor,
      pozecon: pozecon ?? this.pozecon,
      ezAktiv: ezAktiv ?? this.ezAktiv,
    );
  }

  Map<String, dynamic> toJson() => {
        'id': id,
        'neym': neym,
        // ignore: deprecated_member_use
        'kulor': kulor.value,
        'pozecon': pozecon,
        'ezAktiv': ezAktiv,
      };

  factory ModyilDeyda.fromJson(Map<String, dynamic> json) => ModyilDeyda(
        id: json['id'],
        neym: json['neym'],
        kulor: Color(json['kulor']),
        pozecon: json['pozecon'],
        ezAktiv: json['ezAktiv'] ?? false,
      );
}
