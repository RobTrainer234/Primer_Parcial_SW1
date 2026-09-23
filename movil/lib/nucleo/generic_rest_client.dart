import 'dart:convert';

import 'package:http/http.dart' as http;

import 'backend_config.dart';

class GenericRestClient {
  GenericRestClient({required BackendConfig config, http.Client? httpClient})
      : _config = config,
        _httpClient = httpClient ?? http.Client();

  final BackendConfig _config;
  final http.Client _httpClient;

  Future<List<Map<String, dynamic>>> list(String resourcePath) async {
    final response = await _httpClient.get(_config.endpoint(resourcePath));
    final decoded = _decodeResponse(response);

    if (decoded is List) {
      return decoded
          .whereType<Map>()
          .map((item) => Map<String, dynamic>.from(item))
          .toList();
    }

    if (decoded is Map) {
      final embedded = decoded['_embedded'];
      if (embedded is Map && embedded.isNotEmpty) {
        final firstCollection = embedded.values.first;
        if (firstCollection is List) {
          return firstCollection
              .whereType<Map>()
              .map((item) => Map<String, dynamic>.from(item))
              .toList();
        }
      }

      final content = decoded['content'];
      if (content is List) {
        return content
            .whereType<Map>()
            .map((item) => Map<String, dynamic>.from(item))
            .toList();
      }
    }

    return const [];
  }

  Future<Map<String, dynamic>> create(
    String resourcePath,
    Map<String, dynamic> body,
  ) async {
    final response = await _httpClient.post(
      _config.endpoint(resourcePath),
      headers: _jsonHeaders,
      body: jsonEncode(body),
    );
    final decoded = _decodeResponse(response);
    return decoded is Map ? Map<String, dynamic>.from(decoded) : body;
  }

  Future<Map<String, dynamic>> update(
    String resourcePath,
    Object id,
    Map<String, dynamic> body,
  ) async {
    final response = await _httpClient.put(
      _config.endpoint(resourcePath, id),
      headers: _jsonHeaders,
      body: jsonEncode(body),
    );
    final decoded = _decodeResponse(response);
    return decoded is Map ? Map<String, dynamic>.from(decoded) : body;
  }

  Future<void> delete(String resourcePath, Object id) async {
    final response = await _httpClient.delete(_config.endpoint(resourcePath, id));
    _decodeResponse(response, allowEmpty: true);
  }

  static const _jsonHeaders = <String, String>{
    'Content-Type': 'application/json',
    'Accept': 'application/json',
  };

  dynamic _decodeResponse(http.Response response, {bool allowEmpty = false}) {
    if (response.statusCode < 200 || response.statusCode >= 300) {
      throw RestClientException(
        'HTTP ${response.statusCode}: ${response.reasonPhrase ?? response.body}',
      );
    }

    if (response.body.trim().isEmpty) {
      if (allowEmpty) {
        return null;
      }
      return const <String, dynamic>{};
    }

    return jsonDecode(response.body);
  }
}

class RestClientException implements Exception {
  RestClientException(this.message);

  final String message;

  @override
  String toString() => message;
}
