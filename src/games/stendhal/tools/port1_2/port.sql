-- DELETE FROM marauroa.rpobject WHERE object_id IN (SELECT object_id FROM characters WHERE charname='Snaketails>oB<');
DELETE FROM marauroa.characters WHERE charname='Snaketails>oB<';
DELETE FROM marauroa.player WHERE username='Snaketails>oB<';

DELETE FROM rpattribute WHERE name in ('fullghostmode', 'features');

insert into account(id, username, password, email, timedate, status)
select id, username, password, email, timedate, status from marauroa.player;

insert into characters(player_id, charname, object_id)
select player_id, charname, object_id from marauroa.characters;

insert into rpobject(object_id)
select object_id from marauroa.characters;

insert into banlist(id, address, mask)
select id, address, mask from marauroa.banlist;