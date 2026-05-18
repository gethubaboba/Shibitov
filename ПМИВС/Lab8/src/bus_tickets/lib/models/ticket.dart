/// Модель забронированного билета.
class Ticket {    // дата бронирования ISO-8601

  const Ticket({
    this.id,
    required this.routeId,
    required this.cityName,
    required this.routeNumber,
    required this.departure,
    required this.price,
    required this.bookedAt,
  });

  factory Ticket.fromMap(Map<String, dynamic> map) => Ticket(
        id: map['id'] as int?,
        routeId: map['route_id'] as int,
        cityName: map['city_name'] as String,
        routeNumber: map['route_number'] as String,
        departure: map['departure'] as String,
        price: (map['price'] as num).toDouble(),
        bookedAt: map['booked_at'] as String,
      );
  final int? id;
  final int routeId;        // FK на BusRoute
  final String cityName;    // название города (денормализовано для удобства)
  final String routeNumber; // номер маршрута
  final String departure;
  final double price;
  final String bookedAt;

  Map<String, dynamic> toMap() => {
        if (id != null) 'id': id,
        'route_id': routeId,
        'city_name': cityName,
        'route_number': routeNumber,
        'departure': departure,
        'price': price,
        'booked_at': bookedAt,
      };

  Ticket copyWith({int? id}) => Ticket(
        id: id ?? this.id,
        routeId: routeId,
        cityName: cityName,
        routeNumber: routeNumber,
        departure: departure,
        price: price,
        bookedAt: bookedAt,
      );
}
