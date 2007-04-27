create table if not exists age
  (
  id integer auto_increment not null,
  datewhen date,
  charname varchar(32),
  age int,
  version varchar(32),
  primary key(id)
) TYPE=INNODB;

CREATE INDEX i_age_charname ON age(charname);
CREATE INDEX i_age_age ON age(age);
CREATE INDEX i_age_version ON age(version);

