CREATE TABLE __temp_known (charname VARCHAR(32));
CREATE TABLE __temp_chars (charname VARCHAR(32), itemlog CHAR(1), gameEvents CHAR(1), loginEvent CHAR(1), cid CHAR(1), buddy CHAR(1), halloffame CHAR(1), character_stats CHAR(1), kills CHAR(1));

CREATE INDEX i__temp_chars ON __temp_chars(charname);
CREATE INDEX i__temp_known ON __temp_known(charname);

INSERT INTO __temp_chars (charname) SELECT DISTINCT charname FROM characters WHERE timedate<'2008-03-24';
INSERT INTO __temp_known (charname) SELECT DISTINCT source FROM itemlog;

UPDATE __temp_chars, __temp_known SET itemlog='y' WHERE __temp_known.charname=__temp_chars.charname;

UPDATE __temp_chars, cid SET __temp_chars.cid='y' WHERE cid.charname=__temp_chars.charname;

truncate __temp_known;
INSERT INTO __temp_known (charname) SELECT DISTINCT charname FROM characters, loginEvent WHERE loginEvent.player_id=characters.player_id;
UPDATE __temp_chars, __temp_known SET loginEvent='y' WHERE __temp_known.charname=__temp_chars.charname;

truncate __temp_known;
INSERT INTO __temp_known (charname) SELECT DISTINCT source FROM gameEvents;
INSERT INTO __temp_known (charname) SELECT DISTINCT source FROM gameEvents_2008_08_21;
INSERT INTO __temp_known (charname) SELECT DISTINCT source FROM gameEvents_2009_02_19;
INSERT INTO __temp_known (charname) SELECT DISTINCT source FROM gameEvents_2009_08_17;
UPDATE __temp_chars, __temp_known SET gameEvents='y' WHERE __temp_known.charname=__temp_chars.charname;

UPDATE __temp_chars, buddy SET __temp_chars.buddy='y' WHERE buddy.buddy=__temp_chars.charname;
UPDATE __temp_chars, character_stats SET __temp_chars.character_stats'y' WHERE character_stats.name=__temp_chars.charname;
UPDATE __temp_chars, character_stats SET __temp_chars.character_stats='y' WHERE character_stats.name=__temp_chars.charname;

UPDATE __temp_chars, kills SET __temp_chars.kills='y' WHERE kills.killed=__temp_chars.charname AND kills.killed_type='P';
UPDATE __temp_chars, kills SET __temp_chars.kills='y' WHERE kills.killer=__temp_chars.charname AND kills.killer='P';


SELECT count(*) FROM __temp_chars WHERE itemlog IS NULL AND gameEvents IS NULL AND loginEvent IS NULL AND cid  IS NULL AND buddy IS NULL AND halloffame IS NULL AND character_stats  IS NULL AND kills IS NULL;

