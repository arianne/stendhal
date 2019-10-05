alter table account character set utf8mb4 collate utf8mb4_unicode_ci,
change username username varchar(255) character set utf8mb4 collate utf8mb4_unicode_ci not null,
change password password  varchar(255) character set utf8mb4 collate utf8mb4_unicode_ci,
change status status char(8) character set utf8mb4 collate utf8mb4_unicode_ci not null default 'active';


DROP INDEX i_characters_charname ON characters;
alter table characters character set utf8mb4 collate utf8mb4_unicode_ci,
change charname charname varchar(50) character set utf8mb4 collate utf8mb4_unicode_ci  not null,
change status status char(8) character set utf8mb4 collate utf8mb4_unicode_ci not null default 'active';

alter table rpobject character set utf8mb4 collate utf8mb4_unicode_ci;

alter table rpzone character set utf8mb4 collate utf8mb4_unicode_ci,
change zone_id zone_id varchar(32) character set utf8mb4 collate utf8mb4_unicode_ci not null;


alter table loginEvent character set utf8mb4 collate utf8mb4_unicode_ci,
change address address varchar(64) character set utf8mb4 collate utf8mb4_unicode_ci,
change service service char(20) character set utf8mb4 collate utf8mb4_unicode_ci,
change seed seed varchar(120) character set utf8mb4 collate utf8mb4_unicode_ci;
-- too slow


alter table passwordChange character set utf8mb4 collate utf8mb4_unicode_ci,
change address address varchar(64) character set utf8mb4 collate utf8mb4_unicode_ci,
change service service char(20) character set utf8mb4 collate utf8mb4_unicode_ci,
change oldpassword oldpassword varchar(255) character set utf8mb4 collate utf8mb4_unicode_ci;

alter table statistics character set utf8mb4 collate utf8mb4_unicode_ci;
-- maybe too slow

alter table gameEvents character set utf8mb4 collate utf8mb4_unicode_ci,
change source source varchar(64) character set utf8mb4 collate utf8mb4_unicode_ci,
change event event  varchar(64) character set utf8mb4 collate utf8mb4_unicode_ci,
change param1 param1 varchar(128) character set utf8mb4 collate utf8mb4_unicode_ci,
change param2 param2 varchar(255) character set utf8mb4 collate utf8mb4_unicode_ci;
-- too slow

alter table loginseed character set utf8mb4 collate utf8mb4_unicode_ci,
change seed seed varchar(120) character set utf8mb4 collate utf8mb4_unicode_ci,
change address address varchar(64) character set utf8mb4 collate utf8mb4_unicode_ci;


alter table banlist character set utf8mb4 collate utf8mb4_unicode_ci,
change address address varchar(64) character set utf8mb4 collate utf8mb4_unicode_ci,
change mask mask    varchar(64) character set utf8mb4 collate utf8mb4_unicode_ci,
change reason reason  varchar(255) character set utf8mb4 collate utf8mb4_unicode_ci;

alter table accountban character set utf8mb4 collate utf8mb4_unicode_ci,
change reason reason varchar(255) character set utf8mb4 collate utf8mb4_unicode_ci;

alter table accountLink character set utf8mb4 collate utf8mb4_unicode_ci,
change type type char(10) character set utf8mb4 collate utf8mb4_unicode_ci,
change username username varchar(255) character set utf8mb4 collate utf8mb4_unicode_ci,
change nickname nickname varchar(255) character set utf8mb4 collate utf8mb4_unicode_ci,
change email email varchar(255) character set utf8mb4 collate utf8mb4_unicode_ci,
change secret secret varchar(255) character set utf8mb4 collate utf8mb4_unicode_ci;

UPDATE email SET confirmed='1999-01-01' WHERE confirmed<'1999-01-01';
alter table email character set utf8mb4 collate utf8mb4_unicode_ci,
change email email varchar(64) character set utf8mb4 collate utf8mb4_unicode_ci,
change token token varchar(64) character set utf8mb4 collate utf8mb4_unicode_ci,
change address address  varchar(64) character set utf8mb4 collate utf8mb4_unicode_ci,
change confirmed confirmed timestamp NULL;
UPDATE email SET confirmed=null WHERE confirmed='1999-01-01';

UPDATE character_stats SET lastseen='1999-01-01' WHERE lastseen<'1999-01-01';
alter table character_stats character set utf8mb4 collate utf8mb4_unicode_ci,
change name name varchar(50) character set utf8mb4 collate utf8mb4_unicode_ci  not null,
change married married varchar(50) character set utf8mb4 collate utf8mb4_unicode_ci,
change sentence sentence varchar(256) character set utf8mb4 collate utf8mb4_unicode_ci,
change outfit outfit varchar(32) character set utf8mb4 collate utf8mb4_unicode_ci,
change head head varchar(32) character set utf8mb4 collate utf8mb4_unicode_ci,
change armor armor varchar(32) character set utf8mb4 collate utf8mb4_unicode_ci,
change lhand lhand varchar(32) character set utf8mb4 collate utf8mb4_unicode_ci,
change rhand rhand varchar(32) character set utf8mb4 collate utf8mb4_unicode_ci,
change legs legs varchar(32) character set utf8mb4 collate utf8mb4_unicode_ci,
change feet feet varchar(32) character set utf8mb4 collate utf8mb4_unicode_ci,
change cloak cloak varchar(32) character set utf8mb4 collate utf8mb4_unicode_ci,
change finger finger varchar(32) character set utf8mb4 collate utf8mb4_unicode_ci,
change outfit_colors outfit_colors varchar(100) character set utf8mb4 collate utf8mb4_unicode_ci,
change zone zone varchar(50) character set utf8mb4 collate utf8mb4_unicode_ci
change lastseen lasstseen timestamp null;
UPDATE character_stats SET lastseen=null WHERE lastseen='1999-01-01';


alter table halloffame character set utf8mb4 collate utf8mb4_unicode_ci,
change charname charname varchar(50) character set utf8mb4 collate utf8mb4_unicode_ci not null,
change fametype fametype char(1) character set utf8mb4 collate utf8mb4_unicode_ci not null;

alter table halloffame_archive_recent character set utf8mb4 collate utf8mb4_unicode_ci,
change charname charname varchar(50) character set utf8mb4 collate utf8mb4_unicode_ci not null,
change fametype fametype char(1) character set utf8mb4 collate utf8mb4_unicode_ci not null;

alter table halloffame_archive_alltimes character set utf8mb4 collate utf8mb4_unicode_ci,
change charname charname varchar(50) character set utf8mb4 collate utf8mb4_unicode_ci not null,
change fametype fametype char(1) character set utf8mb4 collate utf8mb4_unicode_ci not null;


alter table item character set utf8mb4 collate utf8mb4_unicode_ci,
change name name VARCHAR(64) character set utf8mb4 collate utf8mb4_unicode_ci;

alter table itemlog character set utf8mb4 collate utf8mb4_unicode_ci,
change source source VARCHAR(64) character set utf8mb4 collate utf8mb4_unicode_ci,
change event event VARCHAR(64) character set utf8mb4 collate utf8mb4_unicode_ci,
change param1 param1 VARCHAR(64) character set utf8mb4 collate utf8mb4_unicode_ci,
change param2 param2 VARCHAR(64) character set utf8mb4 collate utf8mb4_unicode_ci,
change param3 param3 VARCHAR(64) character set utf8mb4 collate utf8mb4_unicode_ci,
change param4 param4 VARCHAR(64) character set utf8mb4 collate utf8mb4_unicode_ci;
-- too slow

-- -----------------------------------------

alter table kills character set utf8mb4 collate utf8mb4_unicode_ci,
change killed killed VARCHAR(64) character set utf8mb4 collate utf8mb4_unicode_ci,
change killer killer VARCHAR(64) character set utf8mb4 collate utf8mb4_unicode_ci,
change killed_type killed_type CHAR(1) character set utf8mb4 collate utf8mb4_unicode_ci,
change killer_type killer_type CHAR(1) character set utf8mb4 collate utf8mb4_unicode_ci;
-- too slow

alter table npcs character set utf8mb4 collate utf8mb4_unicode_ci,
change name name VARCHAR(64) character set utf8mb4 collate utf8mb4_unicode_ci,
change title title VARCHAR(64) character set utf8mb4 collate utf8mb4_unicode_ci,
change class class VARCHAR(64) character set utf8mb4 collate utf8mb4_unicode_ci,
change outfit outfit VARCHAR(32) character set utf8mb4 collate utf8mb4_unicode_ci,
change image image VARCHAR(255) character set utf8mb4 collate utf8mb4_unicode_ci,
change zone zone VARCHAR(64) character set utf8mb4 collate utf8mb4_unicode_ci,
change description description VARCHAR(1000) character set utf8mb4 collate utf8mb4_unicode_ci,
change job job VARCHAR(1000) character set utf8mb4 collate utf8mb4_unicode_ci;

alter table zoneinfo character set utf8mb4 collate utf8mb4_unicode_ci,
change name name VARCHAR(64) character set utf8mb4 collate utf8mb4_unicode_ci,
change readableName readableName VARCHAR(64) character set utf8mb4 collate utf8mb4_unicode_ci,
change description description VARCHAR(64) character set utf8mb4 collate utf8mb4_unicode_ci,
change colorMethod colorMethod VARCHAR(64) character set utf8mb4 collate utf8mb4_unicode_ci,
change color color VARCHAR(64) character set utf8mb4 collate utf8mb4_unicode_ci,
change blendMethod blendMethod VARCHAR(64) character set utf8mb4 collate utf8mb4_unicode_ci,
change weather weather VARCHAR(64) character set utf8mb4 collate utf8mb4_unicode_ci;

alter table postman character set utf8mb4 collate utf8mb4_unicode_ci,
change source source VARCHAR(64) character set utf8mb4 collate utf8mb4_unicode_ci,
change target target VARCHAR(64) character set utf8mb4 collate utf8mb4_unicode_ci,
change message message TEXT character set utf8mb4 collate utf8mb4_unicode_ci,
change deleted deleted CHAR (1) character set utf8mb4 collate utf8mb4_unicode_ci DEFAULT 'N';

alter table buddy character set utf8mb4,
change buddy buddy varchar(64) character set utf8mb4 collate utf8mb4_unicode_ci,
change charname charname varchar(64) character set utf8mb4 collate utf8mb4_unicode_ci,
change relationtype relationtype varchar(64) character set utf8mb4 default 'buddy';

alter table openid_associations character set utf8mb4 collate utf8mb4_unicode_ci,
change handle handle VARCHAR(255) character set utf8mb4 collate utf8mb4_unicode_ci,
change data data TEXT character set utf8mb4 collate utf8mb4_unicode_ci NOT NULL;

alter table achievement character set utf8mb4 collate utf8mb4_unicode_ci,
change identifier identifier VARCHAR(64) character set utf8mb4 collate utf8mb4_unicode_ci,
change title title VARCHAR(64) character set utf8mb4 collate utf8mb4_unicode_ci,
change category category VARCHAR(64) character set utf8mb4 collate utf8mb4_unicode_ci,
change description description VARCHAR(254) character set utf8mb4 collate utf8mb4_unicode_ci;

alter table reached_achievement character set utf8mb4 collate utf8mb4_unicode_ci,
change charname charname VARCHAR(32) character set utf8mb4 collate utf8mb4_unicode_ci;

alter table pending_achievement character set utf8mb4 collate utf8mb4_unicode_ci,
change charname charname VARCHAR(32) character set utf8mb4 collate utf8mb4_unicode_ci,
change param param VARCHAR(64) character set utf8mb4 collate utf8mb4_unicode_ci;

alter table statistics_archive character set utf8mb4 collate utf8mb4_unicode_ci,
change name name varchar(32) character set utf8mb4 collate utf8mb4_unicode_ci not null;

alter table trade character set utf8mb4 collate utf8mb4_unicode_ci,
change charname charname  varchar(32) character set utf8mb4 collate utf8mb4_unicode_ci,
change itemname itemname  varchar(32) character set utf8mb4 collate utf8mb4_unicode_ci,
change stats stats varchar(255) character set utf8mb4 collate utf8mb4_unicode_ci;

alter table searchindex character set utf8mb4 collate utf8mb4_unicode_ci,
change searchterm searchterm VARCHAR(64) character set utf8mb4 collate utf8mb4_unicode_ci,
change entitytype entitytype CHAR(1) character set utf8mb4 collate utf8mb4_unicode_ci,
change entityname entityname VARCHAR(64) character set utf8mb4 collate utf8mb4_unicode_ci;



alter table cid character set utf8mb4 collate utf8mb4_unicode_ci,
change charname charname varchar(64) character set utf8mb4 collate utf8mb4_unicode_ci NOT NULL,
change address address varchar(64) character set utf8mb4 collate utf8mb4_unicode_ci NOT NULL,
change cid cid varchar(64) character set utf8mb4 collate utf8mb4_unicode_ci NOT NULL,
change version version varchar(50) character set utf8mb4 collate utf8mb4_unicode_ci DEFAULT NULL,
change dist dist varchar(100) character set utf8mb4 collate utf8mb4_unicode_ci DEFAULT NULL,
change build build varchar(50) character set utf8mb4 collate utf8mb4_unicode_ci DEFAULT NULL;
-- too slow

alter table actions character set utf8mb4 collate utf8mb4_unicode_ci,
change charname charname varchar(64) character set utf8mb4 collate utf8mb4_unicode_ci NOT NULL,
change zone zone varchar(64) character set utf8mb4 collate utf8mb4_unicode_ci,
change action action varchar(64) character set utf8mb4 collate utf8mb4_unicode_ci,
change details details varchar(255) character set utf8mb4 collate utf8mb4_unicode_ci;
-- too slow

alter table webactions character set utf8mb4 collate utf8mb4_unicode_ci,
change charname charname varchar(64) character set utf8mb4 collate utf8mb4_unicode_ci NOT NULL,
change zone zone varchar(64) character set utf8mb4 collate utf8mb4_unicode_ci,
change action action varchar(64) character set utf8mb4 collate utf8mb4_unicode_ci,
change useragent useragent varchar(255) character set utf8mb4 collate utf8mb4_unicode_ci;
-- too slow

