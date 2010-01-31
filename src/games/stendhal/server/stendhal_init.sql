create table if not exists character_stats
  (
  name varchar(32) not null,
  online boolean,

  admin int default 0,
  sentence varchar(256),
  age integer,
  level integer,
  outfit varchar(32),
  xp integer,
  money integer,

  married varchar(32),

  /* Attributes */
  atk integer,
  def integer,
  hp integer,
  karma integer,

  /* Equipment */
  head varchar(32),
  armor varchar(32),
  lhand varchar(32),
  rhand varchar(32),
  legs varchar(32),
  feet varchar(32),
  cloak varchar(32),

  timedate timestamp,
  primary key(name)
  )
 ;
/*CREATE INDEX i_character_stats_name ON character_stats(name);*/

create table if not exists halloffame
  (
  id integer auto_increment not null,
  charname varchar(32) not null,
  fametype char(1) not null,
  points integer not null,

  primary key(id)
  ) 
 ;

/*CREATE INDEX i_halloffame_charname ON halloffame(charname);*/

  
CREATE TABLE IF NOT EXISTS itemid (
  last_id INTEGER
);

CREATE TABLE IF NOT EXISTS itemlog (
  id         INTEGER AUTO_INCREMENT NOT NULL,
  timedate   TIMESTAMP,
  itemid     INTEGER,
  source     VARCHAR(64),
  event      VARCHAR(64),
  param1     VARCHAR(64),
  param2     VARCHAR(64),
  param3     VARCHAR(64),
  param4     VARCHAR(64),
  PRIMARY KEY (id)
);

/*CREATE INDEX i_itemlog_itemid ON itemlog(itemid);*/
/*CREATE INDEX i_itemlog_source ON itemlog(source);*/
/*CREATE INDEX i_itemlog_event ON itemlog(event);*/
/*CREATE INDEX i_itemlog_param1 ON itemlog(param1);*/
/*CREATE INDEX i_itemlog_param2 ON itemlog(param2);*/
/*CREATE INDEX i_itemlog_param3 ON itemlog(param3);*/
/*CREATE INDEX i_itemlog_param4 ON itemlog(param4);*/
/*CREATE INDEX i_itemlog_source_event ON itemlog(source, event);*/
/*CREATE INDEX i_itemlog_itemid_event ON itemlog(itemid, event);*/
/*CREATE INDEX i_itemlog_event_param12 ON itemlog(event, param1, param2);*/
/*CREATE INDEX i_itemlog_event_param34 ON itemlog(event, param3, param4);*/


CREATE TABLE IF NOT EXISTS kills (
  id          INTEGER AUTO_INCREMENT NOT NULL,
  killed      VARCHAR(64),
  killer      VARCHAR(64),
  killed_type CHAR(1),
  killer_type CHAR(1),
  day         DATE,
  cnt         INTEGER,
  PRIMARY KEY (id)
);

/*CREATE INDEX i_kills_killed ON kills (killed_type, killed);*/
/*CREATE INDEX i_kills_killer ON kills (killer_type, killer);*/


CREATE TABLE IF NOT EXISTS cid
  (
  id INTEGER auto_increment NOT NULL,
  charname VARCHAR(64) NOT NULL,
  address VARCHAR(64) NOT NULL,
  cid VARCHAR(64) NOT NULL,
  timedate TIMESTAMP,
  PRIMARY KEY(id)
  ) 
 ;

/*CREATE INDEX i_cid_charname ON cid(charname);*/


create table if not exists words (
  id         INTEGER AUTO_INCREMENT NOT NULL,
  normalized VARCHAR(64) NOT NULL,
  type		VARCHAR(64),
  plural	VARCHAR(64),
  value		INTEGER NULL,
  alias_id	INTEGER NULL,
  PRIMARY KEY (id)
);

/*CREATE INDEX i_word_normalized ON words(normalized);*/


CREATE TABLE IF NOT EXISTS npcs (
  id         INTEGER AUTO_INCREMENT NOT NULL,
  name       VARCHAR(64),
  title      VARCHAR(64),
  class      VARCHAR(64),
  outfit     VARCHAR(32),
  level      INTEGER,
  hp         INTEGER,
  base_hp    INTEGER,
  zone       VARCHAR(64),
  x          INTEGER,
  y          INTEGER,
  description      VARCHAR(1000),
  job       VARCHAR(1000),
  PRIMARY KEY (id)
);

/*CREATE INDEX i_npcs_id ON npcs (id);*/
/*CREATE INDEX i_npcs_name ON npcs (name);*/


