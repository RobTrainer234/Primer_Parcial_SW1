import 'dart:convert';

import 'package:http/http.dart' as http;

import 'backend_config.dart';

class OfflineQueueService {
  OfflineQueueService({required BackendConfig config, http.Client? httpClient})
      : _config = config,
        _httpClient = httpClient ?? http.Client();

  final BackendConfig _config;
  final http.Client _httpClient;
  final List<QueuedSyncCommand> _commands = [];

  List<QueuedSyncCommand> get commands => List.unmodifiable(_commands);

  int enqueue({
    required int modelId,
    required String type,
    required Map<String, dynamic> payload,
  }) {
    final command = QueuedSyncCommand(
      localId: DateTime.now().microsecondsSinceEpoch.toString(),
      modelId: modelId,
      type: type,
      payload: payload,
    );
    _commands.add(command);
    return _commands.length;
  }

  Future<FlushResult> flush() async {
    final sent = <QueuedSyncCommand>[];
    final conflicts = <String>[];
    for (final command in List<QueuedSyncCommand>.from(_commands)) {
      final response = await _httpClient.post(
        _config.endpoint('/modelos/${command.modelId}/sync/commands'),
        headers: const {'Content-Type': 'application/json', 'Accept': 'application/json'},
        body: jsonEncode({
          'tipo': command.type,
          'payload': {
            ...command.payload,
            'operationId': command.localId,
          },
        }),
      );
      if (response.statusCode < 200 || response.statusCode >= 300) {
        throw OfflineQueueException('HTTP ${response.statusCode}: ${response.reasonPhrase ?? response.body}');
      }
      final decoded = response.body.trim().isEmpty ? const <String, dynamic>{} : jsonDecode(response.body) as Map;
      if (decoded['estado'] == 'CONFLICTO') {
        conflicts.add(command.localId);
      } else {
        sent.add(command);
      }
    }
    _commands.removeWhere(sent.contains);
    return FlushResult(sent: sent.length, conflicts: conflicts);
  }
}

class QueuedSyncCommand {
  QueuedSyncCommand({
    required this.localId,
    required this.modelId,
    required this.type,
    required this.payload,
  });

  final String localId;
  final int modelId;
  final String type;
  final Map<String, dynamic> payload;
}

class FlushResult {
  FlushResult({required this.sent, required this.conflicts});

  final int sent;
  final List<String> conflicts;
}

class OfflineQueueException implements Exception {
  OfflineQueueException(this.message);

  final String message;

  @override
  String toString() => message;
}
