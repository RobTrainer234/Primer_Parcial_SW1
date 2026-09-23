import 'package:flutter/foundation.dart';

class BackendConfig extends ChangeNotifier {
  BackendConfig({String initialBaseUrl = 'http://localhost:8080'})
      : _baseUrl = initialBaseUrl;

  String _baseUrl;

  String get baseUrl => _baseUrl;

  Uri endpoint(String resourcePath, [Object? id]) {
    final normalizedBase = _baseUrl.endsWith('/')
        ? _baseUrl.substring(0, _baseUrl.length - 1)
        : _baseUrl;
    final normalizedPath = resourcePath.startsWith('/')
        ? resourcePath.substring(1)
        : resourcePath;
    final suffix = id == null ? '' : '/$id';
    return Uri.parse('$normalizedBase/$normalizedPath$suffix');
  }

  void updateBaseUrl(String value) {
    final trimmed = value.trim();
    if (trimmed.isEmpty || trimmed == _baseUrl) {
      return;
    }
    _baseUrl = trimmed;
    notifyListeners();
  }
}
