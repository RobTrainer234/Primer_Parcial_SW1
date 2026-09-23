class EntityDefinition {
  const EntityDefinition({
    required this.title,
    required this.resourcePath,
    required this.fields,
    this.idField = 'id',
  });

  final String title;
  final String resourcePath;
  final List<EntityField> fields;
  final String idField;
}

class EntityField {
  const EntityField({
    required this.name,
    required this.label,
    this.required = false,
  });

  final String name;
  final String label;
  final bool required;
}

const defaultClientEntity = EntityDefinition(
  title: 'Clientes',
  resourcePath: 'clientes',
  fields: [
    EntityField(name: 'nombre', label: 'Nombre', required: true),
    EntityField(name: 'email', label: 'Email'),
    EntityField(name: 'telefono', label: 'Telefono'),
  ],
);
