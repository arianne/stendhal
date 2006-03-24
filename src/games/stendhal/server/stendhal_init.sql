create table if not exists avatars
  (
  object_id integer auto_increment not null,
  name varchar(32) not null,
  outfit varchar(32),
  level integer,
  xp integer,
  data blob,  
  
  primary key(object_id)
  );