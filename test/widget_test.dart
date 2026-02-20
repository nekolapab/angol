// This is a basic Flutter widget test.
//
// To perform an interaction with a widget in your test, use the WidgetTester
// utility in the flutter_test package. For example, you can send tap and scroll
// gestures. You can also use WidgetTester to find child widgets in the widget
// tree, read text, and verify that the values of widget properties are correct.

import 'package:flutter/material.dart';
import 'package:flutter_test/flutter_test.dart';
import 'package:provider/provider.dart'; // Import provider

import 'package:angol/main.dart';
import 'package:angol/services/enpit_sirves.dart'; // Import EnpitSirves
import 'package:angol/state/angol_steyt.dart'; // Import AngolSteyt

void main() {
  testWidgets('AngolApp renders DaylSkren', (WidgetTester tester) async {
    // Build our app and trigger a frame.
    await tester.pumpWidget(
      MultiProvider(
        providers: [
          ChangeNotifierProvider(create: (_) => EnpitSirves()),
          ChangeNotifierProvider(create: (context) => AngolSteyt()),
        ],
        child: const AngolApp(),
      ),
    );

    // Verify that DaylSkren is rendered.
    // We can look for the MaterialApp's title as a proxy, or a Scaffold.
    expect(find.byType(MaterialApp), findsOneWidget);
    expect(find.byType(Scaffold), findsOneWidget);
    // You might also check for specific text or widgets on DaylSkren if you know them.
    // For example, if DaylSkren has an AppBar with "Angol" title:
    // expect(find.text('Angol'), findsOneWidget);
  });
}
