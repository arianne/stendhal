create table if not exists character_stats
  (
  name varchar(32) not null,
  online integer,

  admin int default 0,
  sentence varchar(256),
  age integer,
  level integer,
  outfit varchar(32),
  outfit_colors varchar(100),
  outfit_layers varchar(255),
  xp integer,
  money integer,

  married varchar(32),

  /* Attributes */
  atk integer,
  def integer,
  ratk integer,
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
  finger varchar(32),

  zone varchar(50),

  timedate timestamp default CURRENT_TIMESTAMP,
  lastseen timestamp null,
  primary key(name)
  );

CREATE INDEX IF NOT EXISTS i_character_stats_name ON character_stats(name);

create table if not exists halloffame
  (
  id integer auto_increment not null,
  charname varchar(32) not null,
  fametype char(10) not null,
  points integer not null,

  primary key(id)
  );

CREATE INDEX IF NOT EXISTS i_halloffame_charname ON halloffame(charname);

create table if not exists halloffame_archive_recent
  (
  id integer auto_increment not null,
  charname varchar(32) not null,
  fametype char(10) not null,
  `rank` integer not null,
  points integer not null,
  day date not null,
  primary key(id)
  );

CREATE INDEX IF NOT EXISTS i_halloffame_day_charname ON halloffame_archive_recent(day, charname);


create table if not exists halloffame_archive_alltimes
  (
  id integer auto_increment not null,
  charname varchar(32) not null,
  fametype char(10) not null,
  `rank` integer not null,
  points integer not null,
  day date not null,
  primary key(id)
  );

CREATE INDEX IF NOT EXISTS i_halloffame_archive_alltimes_day_charname ON halloffame_archive_alltimes(day, charname);


CREATE TABLE IF NOT EXISTS item (
  id INTEGER AUTO_INCREMENT NOT NULL,
  name VARCHAR(64),
  timedate TIMESTAMP DEFAULT CURRENT_TIMESTAMP,
  PRIMARY KEY(id)
);

CREATE INDEX IF NOT EXISTS i_item_timedate ON item(timedate);

CREATE TABLE IF NOT EXISTS itemlog (
  id         INTEGER AUTO_INCREMENT NOT NULL,
  timedate   TIMESTAMP default CURRENT_TIMESTAMP,
  itemid     INTEGER,
  source     VARCHAR(64),
  event      VARCHAR(64),
  param1     VARCHAR(64),
  param2     VARCHAR(64),
  param3     VARCHAR(64),
  param4     VARCHAR(64),
  PRIMARY KEY (id)
);

CREATE INDEX IF NOT EXISTS i_itemlog_itemid ON itemlog(itemid);
CREATE INDEX IF NOT EXISTS i_itemlog_source_timedate ON itemlog(source, timedate);
CREATE INDEX IF NOT EXISTS i_itemlog_event_timedate ON itemlog(event, timedate);
CREATE INDEX IF NOT EXISTS i_itemlog_param1 ON itemlog(param1);
CREATE INDEX IF NOT EXISTS i_itemlog_param2 ON itemlog(param2);
CREATE INDEX IF NOT EXISTS i_itemlog_param3 ON itemlog(param3);
CREATE INDEX IF NOT EXISTS i_itemlog_param4 ON itemlog(param4);
CREATE INDEX IF NOT EXISTS i_itemlog_source_itemid ON itemlog(source, itemid);


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

CREATE INDEX IF NOT EXISTS i_kills_day_killed ON kills (day, killed);
CREATE INDEX IF NOT EXISTS i_kills_killer_day ON kills (killer, day);


CREATE TABLE IF NOT EXISTS npcs (
  id            INTEGER AUTO_INCREMENT NOT NULL,
  name          VARCHAR(64),
  title         VARCHAR(64),
  class         VARCHAR(64),
  outfit        VARCHAR(32),
  outfit_layers VARCHAR(255),
  level         INTEGER,
  hp            INTEGER,
  base_hp       INTEGER,
  image         VARCHAR(255),
  zone          VARCHAR(64),
  x             INTEGER,
  y             INTEGER,
  description   VARCHAR(1000),
  job           VARCHAR(1000),
  cloned        VARCHAR(64),
  PRIMARY KEY (id)
);

CREATE INDEX IF NOT EXISTS i_npcs_name ON npcs (name);

CREATE TABLE IF NOT EXISTS zoneinfo (
  id            INTEGER AUTO_INCREMENT NOT NULL,
  name          VARCHAR(64),
  level         INTEGER,
  iterior       INTEGER,
  x             INTEGER,
  y             INTEGER,
  height        INTEGER,
  width         INTEGER,
  accessable    INTEGER,
  readableName  VARCHAR(64),
  description   VARCHAR(64),
  colorMethod   VARCHAR(64),
  color         VARCHAR(64),
  blendMethod   VARCHAR(64),
  dangerLevel   FLOAT,
  weather       VARCHAR(64),
  PRIMARY KEY (id)
);

CREATE INDEX IF NOT EXISTS i_zoneinfo_name ON zoneinfo (name);


CREATE TABLE IF NOT EXISTS postman (
  id         INTEGER AUTO_INCREMENT NOT NULL,
  source     VARCHAR(64),
  target     VARCHAR(64),
  message    TEXT,
  delivered  INTEGER DEFAULT 0,
  deleted    CHAR (1) DEFAULT 'N',
  timedate TIMESTAMP default CURRENT_TIMESTAMP,
  PRIMARY KEY (id)
);

CREATE INDEX IF NOT EXISTS i_postman_source ON postman(source);
CREATE INDEX IF NOT EXISTS i_postman_target ON postman(target);

CREATE TABLE IF NOT EXISTS buddy (
  id           INTEGER AUTO_INCREMENT NOT NULL,
  charname     VARCHAR(64),
  relationtype VARCHAR(6),
  buddy        VARCHAR(64),
  PRIMARY KEY (id)
);

CREATE INDEX IF NOT EXISTS i_buddy_charname_relationtype ON buddy (charname, relationtype);
CREATE INDEX IF NOT EXISTS i_buddy_buddy_relationtype ON buddy (buddy, relationtype);


/*
CREATE TABLE IF NOT EXISTS openid_allowedsites (
  id         INTEGER AUTO_INCREMENT NOT NULL,
  player_id  INT NOT NULL,
  realm      TEXT NOT NULL,
  attribute  TEXT NOT NULL,
  PRIMARY KEY (id)
);*/

/* CREATE INDEX i_openid_allowedsites ON openid_allowedsites (player_id, realm); */


CREATE TABLE IF NOT EXISTS openid_associations (
  id         INTEGER AUTO_INCREMENT NOT NULL,
  handle     VARCHAR(255),
  data TEXT NOT NULL,
  timedate   TIMESTAMP DEFAULT CURRENT_TIMESTAMP,
  PRIMARY KEY (id)
 );

CREATE INDEX IF NOT EXISTS i_openid_associations_handle ON openid_associations (handle);

CREATE TABLE IF NOT EXISTS achievement (
    id INTEGER AUTO_INCREMENT NOT NULL,
    identifier VARCHAR(64),
    title VARCHAR(64),
    category VARCHAR(64),
    description VARCHAR(254),
    base_score INTEGER,
    active INTEGER,
    reached INTEGER,
    PRIMARY KEY(id)
);
CREATE UNIQUE INDEX IF NOT EXISTS i_achievement_identifier ON achievement(identifier);

CREATE TABLE IF NOT EXISTS reached_achievement (
    id INTEGER AUTO_INCREMENT NOT NULL,
    charname VARCHAR(32),
    timedate TIMESTAMP default CURRENT_TIMESTAMP,
    achievement_id INTEGER,
    PRIMARY KEY(id)
);


CREATE INDEX IF NOT EXISTS i_reached_achievement_charname ON reached_achievement(charname);
CREATE INDEX IF NOT EXISTS i_reached_achievement_achievement_id ON reached_achievement(achievement_id);

CREATE TABLE IF NOT EXISTS pending_achievement (
    id INTEGER AUTO_INCREMENT NOT NULL,
    charname VARCHAR(32),
    achievement_id INTEGER,
    param VARCHAR(64),
    cnt INTEGER,
    PRIMARY KEY(id)
);

CREATE INDEX IF NOT EXISTS i_pending_achievement_charname ON pending_achievement(charname);

create table if not exists statistics_archive
  (
  id integer auto_increment not null,
  name varchar(32) not null,
  val integer not null,
  day date not null,
  primary key(id)
  );

CREATE INDEX IF NOT EXISTS i_statistics_archive_day ON statistics_archive(day);

create table if not exists trade
  (
  id        integer auto_increment not null,
  charname  varchar(32),
  itemname  varchar(32),
  itemid    integer,
  quantity  integer,
  price     integer,
  stats     varchar(255),
  timedate  timestamp default CURRENT_TIMESTAMP,
  primary key(id)
  );
CREATE INDEX IF NOT EXISTS i_trade_timedate ON trade(timedate);


CREATE TABLE IF NOT EXISTS searchindex
  (
  id          INTEGER auto_increment NOT NULL,
  searchterm  VARCHAR(64),
  entitytype  CHAR(1),
  entityname  VARCHAR(64),
  searchscore INTEGER,
  PRIMARY KEY(id)
  );

CREATE INDEX IF NOT EXISTS i_searchindex_searchterm ON searchindex(searchterm);
CREATE INDEX IF NOT EXISTS i_searchindex_entitytype_entityname ON searchindex(entitytype, entityname);


CREATE TABLE IF NOT EXISTS group_quest
  (
  id          INTEGER auto_increment NOT NULL,
  questname   VARCHAR(64),
  charname    VARCHAR(32),
  itemname    VARCHAR(32),
  quantity    INTEGER,
  day         DATE NOT NULL,
  PRIMARY KEY(id)
  );

CREATE INDEX IF NOT EXISTS i_group_quest_questname ON group_quest(questname);
