/// Модель рейса маршрутного автобуса.
class BusRoute { // количество свободных мест

  const BusRoute({
    this.id,
    required this.cityId,
    required this.number,
    required this.departure,
    required this.arrival,
    required this.price,
    required this.seatsAvailable,
  });

  factory BusRoute.fromMap(Map<String, dynamic> map) => BusRoute(
        id: map['id'] as int?,
        cityId: map['city_id'] as int,
        number: map['number'] as String,
        departure: map['departure'] as String,
        arrival: map['arrival'] as String,
        price: (map['price'] as num).toDouble(),
        seatsAvailable: map['seats_available'] as int,
      );
  final int? id;
  final int cityId;        // FK на City
  final String number;     // номер маршрутного автобуса, например '101'
  final String departure;  // время отправления, например '08:30'
  final String arrival;    // время прибытия, например '10:00'
  final double price;      // стоимость проезда в BYN
  final int seatsAvailable;

  /// true — есть хотя бы одно свободное место.
  bool get hasSeats => seatsAvailable > 0;

  Map<String, dynamic> toMap() => {
        if (id != null) 'id': id,
        'city_id': cityId,
        'number': number,
        'departure': departure,
        'arrival': arrival,
        'price': price,
        'seats_available': seatsAvailable,
      };

  BusRoute copyWith({int? id, int? seatsAvailable}) => BusRoute(
        id: id ?? this.id,
        cityId: cityId,
        number: number,
        departure: departure,
        arrival: arrival,
        price: price,
        seatsAvailable: seatsAvailable ?? this.seatsAvailable,
      );
}
