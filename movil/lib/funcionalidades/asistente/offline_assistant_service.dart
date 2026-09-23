import 'dart:convert';

import 'package:flutter/services.dart';

class OfflineAssistantService {
  OfflineAssistantService({
    this.assetPath = 'assets/conocimiento_asistente.json',
  });

  final String assetPath;
  List<AssistantKnowledge> _knowledge = const [];

  Future<void> load() async {
    final raw = await rootBundle.loadString(assetPath);
    final decoded = jsonDecode(raw) as Map<String, dynamic>;
    final responses = decoded['respuestas'];
    if (responses is List) {
      _knowledge = responses
          .whereType<Map>()
          .map((item) => AssistantKnowledge.fromJson(item))
          .toList();
    }
  }

  String answer(String question) {
    final normalized = _normalize(question);
    if (normalized.isEmpty) {
      return 'Escribi una pregunta sobre crear, listar, editar o eliminar registros.';
    }

    for (final item in _knowledge) {
      if (normalized.contains(_normalize(item.intent))) {
        return item.answer;
      }
    }

    return 'No encontre una respuesta local para esa consulta. Puedo ayudar con crear, listar, editar o eliminar registros.';
  }

  String _normalize(String value) {
    return value.toLowerCase().trim();
  }
}

class AssistantKnowledge {
  const AssistantKnowledge({required this.intent, required this.answer});

  factory AssistantKnowledge.fromJson(Map<dynamic, dynamic> json) {
    return AssistantKnowledge(
      intent: json['intencion']?.toString() ?? '',
      answer: json['respuesta']?.toString() ?? '',
    );
  }

  final String intent;
  final String answer;
}
