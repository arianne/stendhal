create table if not exists avatars
  (
  object_id integer auto_increment not null,
  name varchar(32) not null,
  outfit varchar(32),
  level integer,
  xp integer,
  data blob,  
  
  primary key(object_id)
  )
  TYPE=INNODB;
  
  
create table if not exists halloffame
  (
  id integer auto_increment not null,
  charname varchar(32) not null,
  fametype CHAR(1) not null,
  points integer not null,

  primary key(id)
  ) TYPE=INNODB;