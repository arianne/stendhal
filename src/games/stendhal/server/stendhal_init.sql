create table if not exists avatars
  (
  object_id integer not null,
  name varchar(32) not null,
  level integer,
  xp integer,
  data blob,  
  
  primary key(object_id)
  );