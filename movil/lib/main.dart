import 'package:flutter/material.dart';

import 'funcionalidades/asistente/assistant_panel.dart';
import 'funcionalidades/asistente/offline_assistant_service.dart';
import 'funcionalidades/crud/entity_definition.dart';
import 'funcionalidades/crud/generic_entity_page.dart';
import 'nucleo/backend_config.dart';
import 'nucleo/generic_rest_client.dart';
import 'nucleo/mobile_descriptor_service.dart';
import 'nucleo/offline_queue_service.dart';

Future<void> main() async {
  WidgetsFlutterBinding.ensureInitialized();
  final assistant = OfflineAssistantService();
  await assistant.load();
  runApp(ConsumerDemoApp(assistant: assistant));
}

class ConsumerDemoApp extends StatefulWidget {
  const ConsumerDemoApp({super.key, required this.assistant});

  final OfflineAssistantService assistant;

  @override
  State<ConsumerDemoApp> createState() => _ConsumerDemoAppState();
}

class _ConsumerDemoAppState extends State<ConsumerDemoApp> {
  final _config = BackendConfig();
  late final _client = GenericRestClient(config: _config);
  late final _descriptorService = MobileDescriptorService(config: _config);
  late final _offlineQueue = OfflineQueueService(config: _config);

  @override
  Widget build(BuildContext context) {
    return MaterialApp(
      title: 'Generated Backend Consumer',
      debugShowCheckedModeBanner: false,
      theme: ThemeData(
        colorScheme: ColorScheme.fromSeed(seedColor: Colors.indigo),
        useMaterial3: true,
      ),
      home: HomePage(
        config: _config,
        client: _client,
        descriptorService: _descriptorService,
        offlineQueue: _offlineQueue,
        assistant: widget.assistant,
      ),
    );
  }
}

class HomePage extends StatefulWidget {
  const HomePage({
    super.key,
    required this.config,
    required this.client,
    required this.descriptorService,
    required this.offlineQueue,
    required this.assistant,
  });

  final BackendConfig config;
  final GenericRestClient client;
  final MobileDescriptorService descriptorService;
  final OfflineQueueService offlineQueue;
  final OfflineAssistantService assistant;

  @override
  State<HomePage> createState() => _HomePageState();
}

class _HomePageState extends State<HomePage> {
  late final TextEditingController _baseUrlController;

  @override
  void initState() {
    super.initState();
    _baseUrlController = TextEditingController(text: widget.config.baseUrl);
    widget.config.addListener(_syncBaseUrl);
  }

  @override
  void dispose() {
    widget.config.removeListener(_syncBaseUrl);
    _baseUrlController.dispose();
    super.dispose();
  }

  @override
  Widget build(BuildContext context) {
    return Scaffold(
      appBar: AppBar(title: const Text('Flutter consumer app')),
      body: ListenableBuilder(
        listenable: widget.config,
        builder: (context, _) {
          return SingleChildScrollView(
            padding: const EdgeInsets.all(16),
            child: Center(
              child: ConstrainedBox(
                constraints: const BoxConstraints(maxWidth: 900),
                child: Column(
                  crossAxisAlignment: CrossAxisAlignment.stretch,
                  children: [
                    _BackendConfigCard(
                      controller: _baseUrlController,
                      currentBaseUrl: widget.config.baseUrl,
                      onSave: widget.config.updateBaseUrl,
                    ),
                    const SizedBox(height: 16),
                    GenericEntityPage(
                      key: ValueKey(widget.config.baseUrl),
                      definition: defaultClientEntity,
                      client: widget.client,
                    ),
                    const SizedBox(height: 16),
                    _MobileDescriptorCard(
                      descriptorService: widget.descriptorService,
                      offlineQueue: widget.offlineQueue,
                    ),
                    const SizedBox(height: 16),
                    AssistantPanel(service: widget.assistant),
                  ],
                ),
              ),
            ),
          );
        },
      ),
    );
  }

  void _syncBaseUrl() {
    if (_baseUrlController.text != widget.config.baseUrl) {
      _baseUrlController.text = widget.config.baseUrl;
    }
  }
}

class _MobileDescriptorCard extends StatefulWidget {
  const _MobileDescriptorCard({
    required this.descriptorService,
    required this.offlineQueue,
  });

  final MobileDescriptorService descriptorService;
  final OfflineQueueService offlineQueue;

  @override
  State<_MobileDescriptorCard> createState() => _MobileDescriptorCardState();
}

class _MobileDescriptorCardState extends State<_MobileDescriptorCard> {
  final _projectIdController = TextEditingController(text: '1');
  MobileDescriptor? _descriptor;
  String _status = 'Descriptor movil sin cargar.';
  bool _loading = false;

  @override
  void dispose() {
    _projectIdController.dispose();
    super.dispose();
  }

  @override
  Widget build(BuildContext context) {
    return Card(
      child: Padding(
        padding: const EdgeInsets.all(16),
        child: Column(
          crossAxisAlignment: CrossAxisAlignment.stretch,
          children: [
            Text('CU13 mobile descriptor + offline queue', style: Theme.of(context).textTheme.titleLarge),
            const SizedBox(height: 8),
            TextField(
              controller: _projectIdController,
              decoration: const InputDecoration(
                labelText: 'Project id',
                border: OutlineInputBorder(),
              ),
              keyboardType: TextInputType.number,
            ),
            const SizedBox(height: 8),
            Wrap(
              spacing: 8,
              runSpacing: 8,
              children: [
                FilledButton.icon(
                  onPressed: _loading ? null : _loadDescriptor,
                  icon: const Icon(Icons.download_outlined),
                  label: const Text('Load descriptor'),
                ),
                OutlinedButton.icon(
                  onPressed: _descriptor == null ? null : _enqueueDemo,
                  icon: const Icon(Icons.offline_bolt_outlined),
                  label: const Text('Queue demo command'),
                ),
                OutlinedButton.icon(
                  onPressed: widget.offlineQueue.commands.isEmpty ? null : _flushQueue,
                  icon: const Icon(Icons.sync_outlined),
                  label: const Text('Flush queue'),
                ),
              ],
            ),
            const SizedBox(height: 8),
            Text(_status),
            if (_descriptor != null) ...[
              const SizedBox(height: 8),
              Text('Model #${_descriptor!.modelId} v${_descriptor!.revision} · queued ${widget.offlineQueue.commands.length}'),
              Text('Limitacion: ${_descriptor!.limitation}'),
              Text('Conflictos: ${_descriptor!.conflictHandoffNote}'),
              const SizedBox(height: 8),
              for (final entity in _descriptor!.entities)
                ExpansionTile(
                  title: Text(entity.name),
                  subtitle: Text(entity.resourcePath),
                  children: [
                    for (final field in entity.fields)
                      ListTile(
                        dense: true,
                        title: Text(field.label),
                        subtitle: Text('${field.name} · ${field.type} · requerido: ${field.required ? 'si' : 'no'}'),
                      ),
                  ],
                ),
            ],
          ],
        ),
      ),
    );
  }

  Future<void> _loadDescriptor() async {
    final projectId = int.tryParse(_projectIdController.text.trim());
    if (projectId == null) {
      setState(() => _status = 'Project id invalido.');
      return;
    }
    setState(() {
      _loading = true;
      _status = 'Cargando descriptor...';
    });
    try {
      final descriptor = await widget.descriptorService.loadForProject(projectId);
      setState(() {
        _descriptor = descriptor;
        _status = 'Descriptor cargado con ${descriptor.entities.length} entidades.';
      });
    } catch (error) {
      setState(() => _status = 'No se pudo cargar descriptor: $error');
    } finally {
      setState(() => _loading = false);
    }
  }

  void _enqueueDemo() {
    final descriptor = _descriptor;
    if (descriptor == null) {
      return;
    }
    widget.offlineQueue.enqueue(
      modelId: descriptor.modelId,
      type: 'mobile-demo-command',
      payload: {
        'baseRevision': descriptor.revision,
        'source': 'flutter-mobile-demo',
      },
    );
    setState(() => _status = 'Comando encolado en memoria. Persistencia local no agregada para evitar nuevas dependencias.');
  }

  Future<void> _flushQueue() async {
    setState(() => _status = 'Enviando cola offline...');
    try {
      final result = await widget.offlineQueue.flush();
      setState(() => _status = 'Flush completo: ${result.sent} enviados, conflictos ${result.conflicts.length}.');
    } catch (error) {
      setState(() => _status = 'Flush pendiente: $error');
    }
  }
}

class _BackendConfigCard extends StatelessWidget {
  const _BackendConfigCard({
    required this.controller,
    required this.currentBaseUrl,
    required this.onSave,
  });

  final TextEditingController controller;
  final String currentBaseUrl;
  final ValueChanged<String> onSave;

  @override
  Widget build(BuildContext context) {
    return Card(
      child: Padding(
        padding: const EdgeInsets.all(16),
        child: Column(
          crossAxisAlignment: CrossAxisAlignment.stretch,
          children: [
            Text(
              'Backend configuration',
              style: Theme.of(context).textTheme.titleLarge,
            ),
            const SizedBox(height: 8),
            Text('Current base URL: $currentBaseUrl'),
            const SizedBox(height: 8),
            TextField(
              controller: controller,
              decoration: const InputDecoration(
                labelText: 'Backend base URL',
                hintText: 'http://localhost:8080',
                border: OutlineInputBorder(),
              ),
              keyboardType: TextInputType.url,
              onSubmitted: onSave,
            ),
            const SizedBox(height: 8),
            Align(
              alignment: Alignment.centerRight,
              child: FilledButton.icon(
                onPressed: () => onSave(controller.text),
                icon: const Icon(Icons.save_outlined),
                label: const Text('Save URL'),
              ),
            ),
          ],
        ),
      ),
    );
  }
}
