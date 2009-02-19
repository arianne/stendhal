rename table gameEvents to gameEvents_2009_02_19;
create table if not exists gameEvents
  (
  id integer auto_increment not null,
  timedate timestamp,
  source varchar(64),
  event  varchar(64),
  param1 varchar(128),
  param2 varchar(255),
  PRIMARY KEY(id)
  );
create index i_gameEvents_timedate ON gameEvents(timedate);
create index i_gameEvents_source ON gameEvents(source);
create index i_gameEvents_event  ON gameEvents(event);
create index i_gameEvents_param1 ON gameEvents(param1);
create index i_gameEvents_param2 ON gameEvents(param2);
