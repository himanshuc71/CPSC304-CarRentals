--
-- 	Part 3: Partial Implementation
--
--	This file is for creating the tables of Part 3 of the CPSC 304 Project
--
--	CPSC 304 2019W1
--
--

drop table Return;
drop table Rental;
drop table Vehicle;
drop table Reservation;
drop table Branch;
drop table VehicleType;
drop table Customer;

create table Customer(
	dlicense number(7,0) primary key,
	cellphone number(10,0),
	cName varchar2(30),
	address varchar2(30));

create table VehicleType(
	vtname varchar2(30) primary key,
	features varchar2(30),
	wRate number(10,0),
	dRate number(10,0),
	hRate number(10,0),
	wiRate number(10,0),
	diRate number(10,0),
	hiRate number(10,0),
	kRate number(10,0));

create table Branch(
	location varchar2(30),
	city varchar2(30),
	primary key(location,city));

create table Reservation(
	confNo number(9,0) primary key,
	vtname varchar2(30),
	dlicense number(7,0),
	fromDateTime timestamp(6) NOT NULL,
	toDateTime timestamp(6) NOT NULL,
	foreign key (vtname) references VehicleType(vtname),
	foreign key (dlicense) references Customer(dlicense),
  UNIQUE(fromDateTime, toDateTime));

create table Vehicle(
	vlicense varchar2(10) primary key,
	make varchar2(30),
	model varchar(30),
	year varchar2(10),
	colour varchar2(10), --color instead?
	odometer number(10,0),
	status varchar2(30) DEFAULT 'available',  -- available, maintenance, or rented
	vtname varchar2(30),
	location varchar2(30),
	city varchar2(30),
	foreign key(vtname) references VehicleType(vtname),
	foreign key(location, city) references Branch(location,city),
	CONSTRAINT check_status_in_Rent
	CHECK (status in ('available','maintenance','rented'))
);

create table Rental(
	rid number(32,0) primary key, --unique 32but number
	vlicense varchar2(10),
	dlicense number(7,0),
	fromDateTime timestamp(6),
	toDateTime timestamp(6),
	odometer number(10,0),
	cardName varchar(30),
	cardNo number(16,0),
	expDate varchar2(5),
	confNo number(9,0),
	foreign key(vlicense) references Vehicle(vlicense),
	foreign key(dlicense) references Customer(dlicense),
	foreign key(confNo) references Reservation(confNo),
  foreign key(fromDateTime, toDateTime) references Reservation(fromDateTime, toDateTime));

create table Return(
	rid number(32,0) primary key,
	rtnDateTime timestamp(6),
	odometer number(10,0),
	fullTank number(1,0), -- full tank? yes or no...
	value number (6,2),   -- total dollar value
	foreign key(rid) references Rental(rid),
	CONSTRAINT check_fullTank
	CHECK (fullTank in (0,1)));

--INCLUDE ADDITIONAL TABLES IF NEEDED

--
-- 	Part 3: Partial Implementation
--
--	This file is for inserting tuples into the tables of Part 3 of the CPSC 304 Project
--
--	CPSC 304 2019W1
--

--Customer(dlicense, cellphone, cName, address)
insert into Customer values (2837224, 6043489400, 'John Abraham','1234 Main St. Vancouver, BC');
insert into Customer values (2837214, 6043489405, 'Sheila Wang','2389 Lark St. Vancouver, BC');
insert into Customer values (2837204, 6043489410, 'Frank Smith','1234 Williams St. Richmond, BC');
insert into Customer values (2837193, 6043489415, 'Tommy Timmons','49 105th St. Surrey, BC');
insert into Customer values (2837183, 6043489420, 'John Howes','9887 Main St. Vancouver, BC');
insert into Customer values (2837173, 6043489425, 'Bob Paul','5657 Trent St. Burnaby, BC');
insert into Customer values (2837163, 6043489430, 'John Doe','1234 Main St. Vancouver, BC');
insert into Customer values (2837153, 6043489435, 'Ryan Doe','2244 Nanaimo St. Vancouver, BC');
insert into Customer values (2837143, 6043489440, 'Sally Brown','4321 Ash St. Vancouver, BC');
insert into Customer values (2837133, 6043489445, 'Raj Malhotra','7856 Totem St. Vancouver, BC');
insert into Customer values (2837123, 6043489450, 'Judy Jones','4445 Main St. Vancouver, BC');
insert into Customer values (2837113, 6043489455, 'Tanya Smith','72 Northwood Dr. Richmond, BC');
insert into Customer values (2837103, 6043489460, 'Jenny Sharma','1234 Main St. Vancouver, BC');
insert into Customer values (2837092, 6043489465, 'Terry Wu','98 Downing St. Burnaby, BC');
insert into Customer values (2837082, 6043489470, 'Lea Gonzalves','467 Peach St. Victoria, BC');
insert into Customer values (2837072, 6043489475, 'Paul Parker','1234 Tree Dr. Coquitlam, BC');
insert into Customer values (2837062, 6043489480, 'Tina Vega','10 Fir St. Vancouver, BC');

--VehicleType(vtname, features, wrate, drate, hrate, wirate, dirate, hirate, krate)
insert into VehicleType values ('Compact', 'Sun roof', 150, 45, 5, 40, 8, 1, 0.5);
insert into VehicleType values ('Economy', 'USB charging', 200, 55, 8, 50, 12, 3, 1);
insert into VehicleType values ('Mid-size', 'Rear wiper', 250, 65, 12, 60, 16, 5, 2);
insert into VehicleType values ('Standard', 'GPS', 300, 75, 15, 70, 20, 7, 4);
insert into VehicleType values ('Full-size', 'Bluetooth', 350, 85, 18, 80, 24, 9, 8);
insert into VehicleType values ('SUV', 'Rear view camera', 400, 95, 21, 90, 28, 11, 10);
insert into VehicleType values ('Truck', 'Heated steering wheel', 450, 105, 24, 100, 32, 13, 12);

--Branch(location,city)
insert into Branch values ('East', 'Vancouver');
insert into Branch values ('Downtown', 'Vancouver');
insert into Branch values ('Minoru', 'Richmond');
insert into Branch values ('YVR', 'Richmond');
insert into Branch values ('Strawberry Hill', 'Surrey');
insert into Branch values ('King George', 'Surrey');
insert into Branch values ('Metrotown', 'Burnaby');
insert into Branch values ('Lougheed', 'Burnaby');
--insert into Branch values ('Pinetree', 'Coquitlam');

--Reservation(confNo, vtname, dlicense, fromDateTime, toDateTime)
insert into Reservation values (1, 'Economy', 2837224, timestamp '2019-05-01 12:00:00', timestamp '2019-05-03 12:00:00');
insert into Reservation values (2, 'Compact', 2837214, timestamp '2019-05-01 13:00:00', timestamp '2019-05-04 13:00:00');
insert into Reservation values (3, 'Mid-size', 2837204, timestamp '2019-09-01 15:00:00', timestamp '2019-09-03 17:00:00');
insert into Reservation values (4, 'Standard', 2837193, timestamp '2019-10-11 9:00:00', timestamp '2019-10-13 9:00:00');
insert into Reservation values (5, 'Full-size', 2837183, timestamp '2019-05-01 9:00:00', timestamp '2019-05-03 9:00:00');
insert into Reservation values (6, 'SUV', 2837173, timestamp '2019-08-22 11:00:00', timestamp '2019-08-25 11:00:00');
insert into Reservation values (7, 'Truck', 2837163, timestamp '2019-01-01 12:00:00', timestamp '2019-01-03 12:00:00');
insert into Reservation values (8, 'Economy', 2837153, timestamp '2019-04-05 13:00:00', timestamp '2019-04-10 15:00:00');
insert into Reservation values (9, 'Compact', 2837143, timestamp '2019-06-25 10:00:00', timestamp '2019-06-28 12:00:00');
insert into Reservation values (10, 'Mid-size', 2837133, timestamp '2019-05-30 9:00:00', timestamp '2019-06-02 10:00:00');
insert into Reservation values (11, 'Standard', 2837123, timestamp '2019-08-22 16:00:00', timestamp '2019-08-25 16:00:00');
insert into Reservation values (12, 'Full-size', 2837113, timestamp '2019-04-07 11:00:00', timestamp '2019-04-10 12:00:00');
insert into Reservation values (13, 'SUV', 2837103, timestamp '2019-10-10 12:00:00', timestamp '2019-10-13 12:00:00');
insert into Reservation values (14, 'Truck', 2837092, timestamp '2019-09-01 9:00:00', timestamp '2019-09-03 12:00:00');
insert into Reservation values (15, 'Economy', 2837082, timestamp '2019-11-20 10:00:00', timestamp '2019-11-24 10:00:00');
insert into Reservation values (16, 'Compact', 2837072, timestamp '2019-08-25 13:00:00', timestamp '2019-06-28 14:00:00');
insert into Reservation values (17, 'Mid-size', 2837062, timestamp '2019-04-07 12:00:00', timestamp '2019-04-10 12:00:00');

--Vehicle(vlicense, make, model, year, colour, odometer, status, vtname, location, city)
insert into Vehicle values ('ABC123', 'Honda', 'Civic Hatchback', '2019', 'White', 200, 'available', 'Economy', 'East', 'Vancouver');
insert into Vehicle values ('ABC456', 'Toyota', 'Camry', '2020', 'Black', 0, 'available', 'Full-size', 'East', 'Vancouver');
insert into Vehicle values ('ABC789', 'Toyota', 'Camry', '2015', 'Beige', 500, 'available', 'Mid-size', 'Downtown', 'Vancouver');
insert into Vehicle values ('123ABC', 'Subaru', 'Forester', '2013', 'Black', 1800, 'available', 'SUV', 'Downtown', 'Vancouver');
insert into Vehicle values ('456ABC', 'Mazda', 'Mazda3', '2016', 'Red', 700, 'available', 'Compact', 'Minoru', 'Richmond');
insert into Vehicle values ('789ABC', 'Mazda', 'CX5', '2020', 'Blue', 0, 'available', 'SUV', 'Minoru', 'Richmond');
insert into Vehicle values ('1A2B3C', 'Hyundai', 'Elantra', '2016', 'Black', 300, 'available', 'Economy', 'YVR', 'Richmond');
insert into Vehicle values ('4A5B6C', 'Kia', 'K900', '2017', 'Silver', 2200, 'available', 'Full-size', 'YVR', 'Richmond');
insert into Vehicle values ('7A8B9C', 'Toyota', 'RAV4', '2016', 'Black', 700, 'available', 'Standard', 'Metrotown', 'Burnaby');
insert into Vehicle values ('Z9Y8X7', 'VolksWagen', 'Tiguan', '2016', 'Black', 1400, 'available', 'SUV', 'Lougheed', 'Burnaby');
insert into Vehicle values ('Z6Y5X4', 'Subaru', 'Outback', '2016', 'White', 2400, 'available', 'SUV', 'Metrotown', 'Burnaby');
insert into Vehicle values ('Z3Y2X1', 'Honda', 'Accord', '2016', 'Black', 1200, 'available', 'Economy', 'Lougheed', 'Burnaby');
insert into Vehicle values ('12AZ89', 'RAM', '1500', '2016', 'Dark Grey', 2800, 'available', 'Truck', 'King George', 'Surrey');
insert into Vehicle values ('A12Z89', 'Ford', 'Fusion', '2017', 'Blue', 1400, 'available', 'Mid-size', 'Strawberry Hill', 'Surrey');
insert into Vehicle values ('19GH28', 'Ford', 'F-150', '2016', 'Black', 1700, 'available', 'Truck', 'King George', 'Surrey');
insert into Vehicle values ('56MN78', 'Toyota','Echo', '2017', 'White', 1900, 'available', 'Compact', 'YVR', 'Richmond');
insert into Vehicle values ('55KL22', 'Nissan', 'Versa', '2019', 'Black', 500, 'available', 'Compact', 'Downtown', 'Vancouver');
insert into Vehicle values ('33LP12', 'Nissan', 'Ultima', '2018', 'Dark Blue', 700, 'available', 'Standard', 'Metrotown', 'Burnaby');
insert into Vehicle values ('FL78P0', 'Ford', 'Focus', '2018', 'Black', 1000, 'available', 'Standard', 'Lougheed', 'Burnaby');
insert into Vehicle values ('T7H3D2', 'Ford', 'Flex', '2019', 'Silver', 400, 'available', 'Full-size', 'King George', 'Surrey');
insert into Vehicle values ('VTH783', 'Subaru', 'Legacy', '2020', 'White', 0, 'available', 'Mid-size', 'Strawberry Hill', 'Surrey');
insert into Vehicle values ('DFGK98', 'Toyota', 'Tundra', '2018', 'Silver', 1200, 'available', 'Truck', 'YVR', 'Richmond');

--Rental(rid, vLicence, dLicense, fromDateTime, toDateTime, odometer, cardName, cardNo, expDate, confNo)
insert into Rental values (101, 'ABC123', 2837224, timestamp '2019-05-01 12:00:00', timestamp '2019-05-03 12:00:00', 200, 'John Abraham', 8793428791234567, '02/21', 1);
insert into Rental values (102, '456ABC', 2837214, timestamp '2019-05-01 13:00:00', timestamp '2019-05-04 13:00:00', 700, 'Sheila Wang', 1234428791234567, '04/20', 2);
insert into Rental values (103, 'ABC789', 2837204, timestamp '2019-09-01 15:00:00', timestamp '2019-09-03 17:00:00', 500, 'Frank Smith', 8793428791239080, '10/22', 3);
insert into Rental values (104, '7A8B9C', 2837193, timestamp '2019-10-11 9:00:00', timestamp '2019-10-13 9:00:00', 700, 'Tommy Timmons', 8793357457348567, '04/21', 4);
insert into Rental values (105, '4A5B6C', 2837183, timestamp '2019-05-01 9:00:00', timestamp '2019-05-03 9:00:00', 2200, 'John Howes', 5843928791234567, '02/20', 5);
insert into Rental values (106, 'Z6Y5X4', 2837173, timestamp '2019-08-22 11:00:00', timestamp '2019-08-25 11:00:00', 2400, 'Bob Paul', 9043428791234567, '08/21', 6);
insert into Rental values (107, '19GH28', 2837163, timestamp '2019-01-01 12:00:00', timestamp '2019-01-03 12:00:00', 1700, 'John Doe', 8793428747374567, '02/20', 7);
insert into Rental values (108, 'Z3Y2X1', 2837153, timestamp '2019-04-05 13:00:00', timestamp '2019-04-10 15:00:00', 1900, 'Ryan Doe', 8793428791234567, '05/21', 8);
insert into Rental values (109, '56MN78', 2837143, timestamp '2019-06-25 10:00:00', timestamp '2019-06-28 12:00:00', 1000, 'Sally Brown', 5616428791234567, '10/21', 9);
insert into Rental values (110, 'A12Z89', 2837133, timestamp '2019-05-30 9:00:00', timestamp '2019-06-02 10:00:00', 1400, 'Raj Malhotra', 7843428791239847, '05/20', 10);
insert into Rental values (111, '33LP12', 2837123, timestamp '2019-08-22 16:00:00', timestamp '2019-08-25 16:00:00', 700, 'Judy Jones', 6734228791239847, '05/22', 11);
insert into Rental values (112, 'ABC456', 2837113, timestamp '2019-04-07 11:00:00', timestamp '2019-04-10 12:00:00', 2200, 'Tanya Smith', 7123287912392847, '07/20', 12);
insert into Rental values (113, '123ABC', 2837103, timestamp '2019-10-10 12:00:00', timestamp '2019-10-13 12:00:00', 1800, 'Jenny Sharma', 7843448294139847, '09/21', 13);
insert into Rental values (114, 'DFGK98', 2837092, timestamp '2019-09-01 9:00:00', timestamp '2019-09-03 12:00:00', 1200, 'Terry Wu', 9012448294139847, '11/21', 14);
insert into Rental values (115, '1A2B3C', 2837082, timestamp '2019-11-20 10:00:00', timestamp '2019-11-24 10:00:00', 300, 'Lea Gonzalves', 5643428791238097, '07/21', 15);
insert into Rental values (116, '55KL22', 2837072, timestamp '2019-08-25 13:00:00', timestamp '2019-06-28 14:00:00', 500, 'Paul Parker', 3239428791239847, '02/20', 16);
insert into Rental values (117, 'VTH783', 2837062, timestamp '2019-04-07 12:00:00', timestamp '2019-04-10 12:00:00', 0, 'Tina Vega', 5467428791239847, '12/20', 17);

--Return(rid, rtnDateTime, odometer, fulltank, value) --w/d/h rate + wi/di/hi rate
insert into Return values (101, timestamp '2019-05-03 12:00:00', 400, 1, 134.00);
insert into Return values (102, timestamp '2019-05-04 13:00:00', 1200, 1, 159.00);
insert into Return values (103, timestamp '2019-09-03 17:00:00', 800, 0, 192.00);
insert into Return values (104, timestamp '2019-10-13 9:00:00', 1000, 1, 190.00);
insert into Return values (105, timestamp '2019-05-03 9:00:00', 2600, 1, 218.00);
insert into Return values (106, timestamp '2019-05-03 12:00:00', 2500, 1, 369.00);
insert into Return values (107, timestamp '2019-01-03 12:00:00', 2000, 0, 274.00);
insert into Return values (108, timestamp '2019-04-10 15:00:00', 1400, 1, 357.00);
insert into Return values (109, timestamp '2019-06-28 12:00:00', 1500, 1, 171.00);
insert into Return values (110, timestamp '2019-06-02 9:00:00', 1600, 1, 243.00);
insert into Return values (111, timestamp '2019-08-25 16:00:00', 1600, 1, 285.00);
insert into Return values (112, timestamp '2019-04-10 12:00:00', 2700, 1, 354.00);
insert into Return values (113, timestamp '2019-10-13 12:00:00', 2400, 0, 369.00);
insert into Return values (114, timestamp '2019-09-03 12:00:00', 1700, 1, 385.00);
insert into Return values (115, timestamp '2019-11-24 10:00:00', 900, 1, 268.00);
insert into Return values (116, timestamp '2019-06-28 14:00:00', 900, 0, 165.00);
insert into Return values (117, timestamp '2019-04-10 12:00:00', 400, 1, 214.00);
