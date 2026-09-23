import 'package:flutter/material.dart';

import 'offline_assistant_service.dart';

class AssistantPanel extends StatefulWidget {
  const AssistantPanel({super.key, required this.service});

  final OfflineAssistantService service;

  @override
  State<AssistantPanel> createState() => _AssistantPanelState();
}

class _AssistantPanelState extends State<AssistantPanel> {
  final _questionController = TextEditingController();
  String _answer = 'Pregunta algo sobre crear, listar, editar o eliminar.';

  @override
  void dispose() {
    _questionController.dispose();
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
            Text(
              'Asistente offline',
              style: Theme.of(context).textTheme.titleMedium,
            ),
            const SizedBox(height: 8),
            TextField(
              controller: _questionController,
              decoration: const InputDecoration(
                labelText: 'Pregunta',
                hintText: 'Ejemplo: Como crear un cliente?',
                border: OutlineInputBorder(),
              ),
              onSubmitted: (_) => _answerQuestion(),
            ),
            const SizedBox(height: 8),
            FilledButton.icon(
              onPressed: _answerQuestion,
              icon: const Icon(Icons.smart_toy_outlined),
              label: const Text('Responder sin internet'),
            ),
            const SizedBox(height: 8),
            Text(_answer),
          ],
        ),
      ),
    );
  }

  void _answerQuestion() {
    setState(() {
      _answer = widget.service.answer(_questionController.text);
    });
  }
}
