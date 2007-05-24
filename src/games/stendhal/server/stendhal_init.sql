create table if not exists characters_stats
  (
  id integer auto_increment not null,
  name varchar(32) not null,
  outfit varchar(32),
  level integer,
  xp integer,
  
  primary key(id)
  )
  TYPE=INNODB;
  
  
create table if not exists halloffame
  (
  id integer auto_increment not null,
  charname varchar(32) not null,
  fametype char(1) not null,
  points integer not null,

  primary key(id)
  ) TYPE=INNODB;