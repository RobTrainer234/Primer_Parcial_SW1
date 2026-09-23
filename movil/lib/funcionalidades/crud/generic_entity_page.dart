import 'package:flutter/material.dart';

import '../../nucleo/generic_rest_client.dart';
import 'entity_definition.dart';

class GenericEntityPage extends StatefulWidget {
  const GenericEntityPage({
    super.key,
    required this.definition,
    required this.client,
  });

  final EntityDefinition definition;
  final GenericRestClient client;

  @override
  State<GenericEntityPage> createState() => _GenericEntityPageState();
}

class _GenericEntityPageState extends State<GenericEntityPage> {
  late Future<List<Map<String, dynamic>>> _items;

  @override
  void initState() {
    super.initState();
    _items = _loadItems();
  }

  @override
  Widget build(BuildContext context) {
    return Card(
      child: Padding(
        padding: const EdgeInsets.all(16),
        child: Column(
          crossAxisAlignment: CrossAxisAlignment.stretch,
          children: [
            Row(
              children: [
                Expanded(
                  child: Text(
                    widget.definition.title,
                    style: Theme.of(context).textTheme.titleLarge,
                  ),
                ),
                IconButton(
                  tooltip: 'Recargar',
                  onPressed: _reload,
                  icon: const Icon(Icons.refresh),
                ),
                FilledButton.icon(
                  onPressed: () => _openForm(),
                  icon: const Icon(Icons.add),
                  label: const Text('Nuevo'),
                ),
              ],
            ),
            const SizedBox(height: 12),
            FutureBuilder<List<Map<String, dynamic>>>(
              future: _items,
              builder: (context, snapshot) {
                if (snapshot.connectionState == ConnectionState.waiting) {
                  return const Center(child: CircularProgressIndicator());
                }
                if (snapshot.hasError) {
                  return _ErrorView(
                    message: snapshot.error.toString(),
                    onRetry: _reload,
                  );
                }

                final items = snapshot.data ?? const [];
                if (items.isEmpty) {
                  return const Text('No hay registros para mostrar.');
                }

                return ListView.separated(
                  shrinkWrap: true,
                  physics: const NeverScrollableScrollPhysics(),
                  itemCount: items.length,
                  separatorBuilder: (_, __) => const Divider(height: 1),
                  itemBuilder: (context, index) {
                    final item = items[index];
                    final title = _displayTitle(item);
                    final id = _recordId(item);
                    return ListTile(
                      title: Text(title),
                      subtitle: Text(item.toString()),
                      trailing: Wrap(
                        spacing: 4,
                        children: [
                          IconButton(
                            tooltip: 'Editar',
                            onPressed: id == null ? null : () => _openForm(item),
                            icon: const Icon(Icons.edit_outlined),
                          ),
                          IconButton(
                            tooltip: 'Eliminar',
                            onPressed: id == null ? null : () => _confirmDelete(id),
                            icon: const Icon(Icons.delete_outline),
                          ),
                        ],
                      ),
                    );
                  },
                );
              },
            ),
          ],
        ),
      ),
    );
  }

  Future<List<Map<String, dynamic>>> _loadItems() {
    return widget.client.list(widget.definition.resourcePath);
  }

  void _reload() {
    setState(() {
      _items = _loadItems();
    });
  }

  Object? _recordId(Map<String, dynamic> item) {
    if (item.containsKey(widget.definition.idField)) {
      return item[widget.definition.idField];
    }

    final links = item['_links'];
    if (links is Map) {
      final self = links['self'];
      if (self is Map && self['href'] is String) {
        final href = self['href'] as String;
        return Uri.tryParse(href)?.pathSegments.lastOrNull;
      }
    }

    return null;
  }

  String _displayTitle(Map<String, dynamic> item) {
    for (final field in widget.definition.fields) {
      final value = item[field.name];
      if (value != null && value.toString().trim().isNotEmpty) {
        return value.toString();
      }
    }
    return 'Registro sin nombre';
  }

  Future<void> _openForm([Map<String, dynamic>? existing]) async {
    final saved = await showDialog<bool>(
      context: context,
      builder: (context) {
        return _EntityFormDialog(
          definition: widget.definition,
          client: widget.client,
          existing: existing,
        );
      },
    );

    if (saved == true) {
      _reload();
    }
  }

  Future<void> _confirmDelete(Object id) async {
    final confirmed = await showDialog<bool>(
      context: context,
      builder: (context) => AlertDialog(
        title: const Text('Eliminar registro'),
        content: const Text('Esta accion no se puede deshacer.'),
        actions: [
          TextButton(
            onPressed: () => Navigator.pop(context, false),
            child: const Text('Cancelar'),
          ),
          FilledButton(
            onPressed: () => Navigator.pop(context, true),
            child: const Text('Eliminar'),
          ),
        ],
      ),
    );

    if (confirmed != true) {
      return;
    }

    try {
      await widget.client.delete(widget.definition.resourcePath, id);
      if (mounted) {
        ScaffoldMessenger.of(context).showSnackBar(
          const SnackBar(content: Text('Registro eliminado.')),
        );
      }
      _reload();
    } catch (error) {
      if (mounted) {
        ScaffoldMessenger.of(context).showSnackBar(
          SnackBar(content: Text('No se pudo eliminar: $error')),
        );
      }
    }
  }
}

class _EntityFormDialog extends StatefulWidget {
  const _EntityFormDialog({
    required this.definition,
    required this.client,
    this.existing,
  });

  final EntityDefinition definition;
  final GenericRestClient client;
  final Map<String, dynamic>? existing;

  @override
  State<_EntityFormDialog> createState() => _EntityFormDialogState();
}

class _EntityFormDialogState extends State<_EntityFormDialog> {
  final _formKey = GlobalKey<FormState>();
  late final Map<String, TextEditingController> _controllers;
  bool _saving = false;

  bool get _isEditing => widget.existing != null;

  @override
  void initState() {
    super.initState();
    _controllers = {
      for (final field in widget.definition.fields)
        field.name: TextEditingController(
          text: widget.existing?[field.name]?.toString() ?? '',
        ),
    };
  }

  @override
  void dispose() {
    for (final controller in _controllers.values) {
      controller.dispose();
    }
    super.dispose();
  }

  @override
  Widget build(BuildContext context) {
    return AlertDialog(
      title: Text(_isEditing ? 'Editar registro' : 'Nuevo registro'),
      content: Form(
        key: _formKey,
        child: SingleChildScrollView(
          child: Column(
            mainAxisSize: MainAxisSize.min,
            children: [
              for (final field in widget.definition.fields)
                Padding(
                  padding: const EdgeInsets.only(bottom: 12),
                  child: TextFormField(
                    controller: _controllers[field.name],
                    decoration: InputDecoration(
                      labelText: field.label,
                      border: const OutlineInputBorder(),
                    ),
                    validator: (value) {
                      if (field.required && (value == null || value.trim().isEmpty)) {
                        return 'Campo obligatorio';
                      }
                      return null;
                    },
                  ),
                ),
            ],
          ),
        ),
      ),
      actions: [
        TextButton(
          onPressed: _saving ? null : () => Navigator.pop(context, false),
          child: const Text('Cancelar'),
        ),
        FilledButton(
          onPressed: _saving ? null : _save,
          child: _saving
              ? const SizedBox.square(
                  dimension: 18,
                  child: CircularProgressIndicator(strokeWidth: 2),
                )
              : const Text('Guardar'),
        ),
      ],
    );
  }

  Object? _recordId(Map<String, dynamic> item) {
    if (item.containsKey(widget.definition.idField)) {
      return item[widget.definition.idField];
    }

    final links = item['_links'];
    if (links is Map) {
      final self = links['self'];
      if (self is Map && self['href'] is String) {
        final href = self['href'] as String;
        return Uri.tryParse(href)?.pathSegments.lastOrNull;
      }
    }

    return null;
  }

  Future<void> _save() async {
    if (!_formKey.currentState!.validate()) {
      return;
    }

    setState(() => _saving = true);
    final body = {
      for (final field in widget.definition.fields)
        field.name: _controllers[field.name]!.text.trim(),
    };

    try {
      if (_isEditing) {
        final id = _recordId(widget.existing!);
        if (id == null) {
          throw RestClientException('El registro no tiene id editable.');
        }
        await widget.client.update(widget.definition.resourcePath, id, body);
      } else {
        await widget.client.create(widget.definition.resourcePath, body);
      }
      if (mounted) {
        Navigator.pop(context, true);
      }
    } catch (error) {
      if (mounted) {
        ScaffoldMessenger.of(context).showSnackBar(
          SnackBar(content: Text('No se pudo guardar: $error')),
        );
        setState(() => _saving = false);
      }
    }
  }
}

class _ErrorView extends StatelessWidget {
  const _ErrorView({required this.message, required this.onRetry});

  final String message;
  final VoidCallback onRetry;

  @override
  Widget build(BuildContext context) {
    return Column(
      crossAxisAlignment: CrossAxisAlignment.stretch,
      children: [
        Text('No se pudo cargar la entidad: $message'),
        const SizedBox(height: 8),
        OutlinedButton.icon(
          onPressed: onRetry,
          icon: const Icon(Icons.refresh),
          label: const Text('Reintentar'),
        ),
      ],
    );
  }
}
