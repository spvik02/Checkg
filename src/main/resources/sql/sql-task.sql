--Вывести к каждому самолету класс обслуживания и количество мест этого класса
select 
seats.aircraft_code, model -> 'ru' as model,  fare_conditions, count(*) 
from seats 
join aircrafts_data
on seats.aircraft_code = aircrafts_data.aircraft_code
group by seats.aircraft_code, fare_conditions, model
order by seats.aircraft_code, fare_conditions;

--Найти 3 самых вместительных самолета (модель + кол-во мест)
select model -> 'ru' as model, count(*) as seat_count
from seats 
join aircrafts_data
on seats.aircraft_code = aircrafts_data.aircraft_code
group by model
order by seat_count desc
limit 3;

-- Вывести код,модель самолета и места не эконом класса для самолета 'Аэробус A321-200' с сортировкой по местам
select seats.aircraft_code, model -> 'ru' as model, seat_no
from seats
join aircrafts_data
on seats.aircraft_code = aircrafts_data.aircraft_code
where  model -> 'ru' @> '"Аэробус A321-200"' ::jsonb	
	and  fare_conditions != 'Economy'
order by seat_no;

--Вывести города в которых больше 1 аэропорта ( код аэропорта, аэропорт, город)
--только города
select 
city ->> 'ru', count(airport_code) as airports_count
from airports_data
group by city
having count(airport_code) > 1;

-- города и их аэропорты, разделенные ;
select airports_data.city ->> 'ru' as city, 
	string_agg(airports_data.airport_code || ' ' 
			   || (airports_data.airport_name ->> 'ru')|| ' ' 
			   || (airports_data.city ->> 'ru'), '; ') as airports
from (
	select city
	from airports_data
	group by city
	having count(airport_code) > 1
) as airportMore1
join airports_data on airportMore1.city = airports_data.city
group by airports_data.city;

--таблицей ( код аэропорта, аэропорт, город)
select airports_data.airport_code, airports_data.airport_name ->> 'ru' as airport_name, airports_data.city ->> 'ru' as city
from (
	select city
	from airports_data
	group by city
	having count(airport_code) > 1
) as airportMore1
join airports_data on airportMore1.city = airports_data.city
order by airports_data.city, airports_data.airport_name;

-- Найти ближайший вылетающий рейс из Екатеринбурга в Москву, на который еще не завершилась регистрация
select * 
from flights_v 
where departure_city like 'Екатеринбург' and arrival_city like 'Москва' 
and status in ('Scheduled', 'On Time', 'Delayed')
order by scheduled_departure 
limit 1;

-- Вывести самый дешевый и дорогой билет и стоимость ( в одном результирующем ответе)
select ticket_no, amount
from 
	((select * from ticket_flights where amount = (select max(amount) from ticket_flights) limit 1)
	union
	(select * from ticket_flights where amount = (select min(amount) from ticket_flights) limit 1)) as max_min

-- Написать DDL таблицы Customers , должны быть поля id , firstName, LastName, email , phone. 
-- Добавить ограничения на поля ( constraints) .
create table if not exists bookings.Customers (
	customerId varchar(16),
	firstName text not null,
	lastName text not null,
	email text not null,
	phone varchar(15) unique not null,
	constraint customer_pkey primary key (customerId),
	constraint phone_min_lenght check (char_length(phone) > 11)
)
	
-- Написать DDL таблицы Orders , должен быть id, customerId,	quantity. 
-- Должен быть внешний ключ на таблицу customers + ограничения
create table if not exists bookings.Orders(
	orderId bigint, 
	customerId varchar(16),	
	quantity int not null,
	constraint order_pk primary key (orderId),
	constraint order_customer_ref_key foreign key (customerId)
		references bookings.Customers (customerId),
	constraint quantity_more_zero check (quantity > 0)
)
-- Написать 5 insert в эти таблицы
insert into bookings.Customers (customerId, firstName, lastName, email, phone)
values 
	('5010100A032PB7', 'fn1', 'ln1', 'fn1ln1@gmail.com', '702345216213'),
	('5010100A033PB7', 'fn2', 'ln2', 'fn2ln2@gmail.com', '702345216230'),
	('5010100A034PB7', 'fn3', 'ln3', 'fn3ln3@gmail.com', '7023452162230'),
	('5010100A035PB7', 'fn4', 'ln4', 'fn4ln4@gmail.com', '702345256213'),
	('5010100A036PB7', 'fn5', 'ln5', 'fn5ln5@gmail.com', '7023452166213');
insert into bookings.Orders(orderId, customerId, quantity)
values 
	(1, '5010100A033PB7', 4),
	(2, '5010100A033PB7', 1),
	(3, '5010100A033PB7', 4),
	(4, '5010100A033PB7', 4),
	(5, '5010100A033PB7', 4);

-- удалить таблицы
drop table if exists bookings.Orders;
drop table if exists bookings.Customers;

-- Написать свой кастомный запрос ( rus + sql) 
--Найти все самолеты в воздухе 
select flight_id, flight_no, departure_airport_name, 
	departure_city, arrival_airport_name, arrival_city, aircraft_code 
	from flights_v
	where status like 'Departed'
	order by flight_id
	--58
--Вывести количество проданных мест и количество всех мест для самолетов в воздухе
select departed_flights.flight_id, flight_no, departure_airport_name, 
	departure_city, arrival_airport_name, arrival_city, departed_flights.aircraft_code,
	count(ticket_flights) as sold_seats, all_seats
from (select * from flights_v
	where status like 'Departed') as departed_flights
left join ticket_flights
on departed_flights.flight_id = ticket_flights.flight_id
join (select aircrafts_data.aircraft_code, count(*) as all_seats
	from seats join aircrafts_data
	on seats.aircraft_code = aircrafts_data.aircraft_code
	group by aircrafts_data.aircraft_code) as all_seats_in_aircraft
on all_seats_in_aircraft.aircraft_code = departed_flights.aircraft_code
group by departed_flights.flight_id, flight_no, departure_airport_name, 
	departure_city, arrival_airport_name, arrival_city, departed_flights.aircraft_code, all_seats
order by departed_flights.flight_id;
