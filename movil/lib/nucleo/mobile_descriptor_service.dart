import 'dart:convert';

import 'package:http/http.dart' as http;

import 'backend_config.dart';

class MobileDescriptorService {
  MobileDescriptorService({required BackendConfig config, http.Client? httpClient})
      : _config = config,
        _httpClient = httpClient ?? http.Client();

  final BackendConfig _config;
  final http.Client _httpClient;

  Future<MobileDescriptor> loadForProject(int projectId) async {
    final response = await _httpClient.get(
      _config.endpoint('/projects/$projectId/mobile/descriptor'),
      headers: const {'Accept': 'application/json'},
    );
    if (response.statusCode < 200 || response.statusCode >= 300) {
      throw MobileDescriptorException(
        'HTTP ${response.statusCode}: ${response.reasonPhrase ?? response.body}',
      );
    }
    return MobileDescriptor.fromJson(
      Map<String, dynamic>.from(jsonDecode(response.body) as Map),
    );
  }
}

class MobileDescriptor {
  MobileDescriptor({
    required this.projectId,
    required this.modelId,
    required this.revision,
    required this.entities,
    required this.syncCommandsPath,
    required this.conflictHandoffNote,
    required this.limitation,
  });

  final int projectId;
  final int modelId;
  final int revision;
  final List<DescriptorEntity> entities;
  final String syncCommandsPath;
  final String conflictHandoffNote;
  final String limitation;

  factory MobileDescriptor.fromJson(Map<String, dynamic> json) {
    final entities = (json['entities'] as List? ?? const [])
        .whereType<Map>()
        .map((item) => DescriptorEntity.fromJson(Map<String, dynamic>.from(item)))
        .toList();
    return MobileDescriptor(
      projectId: (json['projectId'] as num).toInt(),
      modelId: (json['modelId'] as num).toInt(),
      revision: (json['revision'] as num).toInt(),
      entities: entities,
      syncCommandsPath: json['syncCommandsPath']?.toString() ?? '',
      conflictHandoffNote: json['conflictHandoffNote']?.toString() ?? '',
      limitation: json['limitation']?.toString() ?? '',
    );
  }
}

class DescriptorEntity {
  DescriptorEntity({
    required this.id,
    required this.name,
    required this.resourcePath,
    required this.fields,
  });

  final int id;
  final String name;
  final String resourcePath;
  final List<DescriptorField> fields;

  factory DescriptorEntity.fromJson(Map<String, dynamic> json) {
    return DescriptorEntity(
      id: (json['id'] as num).toInt(),
      name: json['name']?.toString() ?? 'Entidad',
      resourcePath: json['resourcePath']?.toString() ?? 'items',
      fields: (json['fields'] as List? ?? const [])
          .whereType<Map>()
          .map((item) => DescriptorField.fromJson(Map<String, dynamic>.from(item)))
          .toList(),
    );
  }
}

class DescriptorField {
  DescriptorField({
    required this.name,
    required this.label,
    required this.type,
    required this.required,
  });

  final String name;
  final String label;
  final String type;
  final bool required;

  factory DescriptorField.fromJson(Map<String, dynamic> json) {
    return DescriptorField(
      name: json['name']?.toString() ?? 'campo',
      label: json['label']?.toString() ?? json['name']?.toString() ?? 'Campo',
      type: json['type']?.toString() ?? 'TEXTO',
      required: json['required'] == true,
    );
  }
}

class MobileDescriptorException implements Exception {
  MobileDescriptorException(this.message);

  final String message;

  @override
  String toString() => message;
}
