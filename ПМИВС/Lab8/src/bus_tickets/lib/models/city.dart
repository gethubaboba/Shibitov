/// Модель города Гродненской области.
class City {

  const City({
    this.id,
    required this.name,
    required this.nameRu,
    required this.latitude,
    required this.longitude,
  });

  factory City.fromMap(Map<String, dynamic> map) => City(
        id: map['id'] as int?,
        name: map['name'] as String,
        nameRu: map['name_ru'] as String,
        latitude: (map['latitude'] as num).toDouble(),
        longitude: (map['longitude'] as num).toDouble(),
      );
  final int? id;
  final String name;       // ключ локализации, например 'grodno'
  final String nameRu;     // русское название (для отображения в БД)
  final double latitude;
  final double longitude;

  Map<String, dynamic> toMap() => {
        if (id != null) 'id': id,
        'name': name,
        'name_ru': nameRu,
        'latitude': latitude,
        'longitude': longitude,
      };

  City copyWith({int? id}) => City(
        id: id ?? this.id,
        name: name,
        nameRu: nameRu,
        latitude: latitude,
        longitude: longitude,
      );
}
