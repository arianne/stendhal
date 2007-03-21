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
  player_id integer,
  fametype CHAR(1),
  points integer,

  primary key(id)
  ) TYPE=INNODB;