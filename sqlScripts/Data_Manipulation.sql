--
-- 	Part 3: Partial Implementation
--
--	This file is for data manipulation of the tables of Part 3 of the CPSC 304 Project with corresponding examples
--
--	CPSC 304 2019W1

-- view all tables in database:
select table_name from user_tables;

-- add data to a particular table in the database
insert into R(A,B...) values ('abc', 'def'...);
-- ie. insert into Branch(location, city) values ('UVic', 'Victoria');

-- update data to a particular table in the database
update R set A = 'abc' where B = 'def';
--ie. update Branch set location = 'Downtown' where city = 'Victoria';

-- delete data from a particular table in the database
delete from R where B = 'def';
-- delete from Branch where city='Victoria';

-- view the data in a particular table
select * from R;
-- select * from Branch;