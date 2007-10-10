-- deleted banned user with invalid characters
DELETE FROM marauroa.rpobject USING marauroa.rpobject, marauroa.rpattribute
WHERE rpobject.object_id = rpattribute.object_id AND rpattribute.name LIKE 'name%' AND rpattribute.value='Snaketails>oB<';
DELETE FROM marauroa.characters WHERE charname='Snaketails>oB<';
DELETE FROM marauroa.player WHERE username='Snaketails>oB<';

-- delete deprecated attributes
DELETE FROM marauroa.rpattribute WHERE name in ('fullghostmode', 'features');


INSERT INTO account(id, username, password, email, timedate, status)
SELECT id, username, password, email, timedate, status FROM marauroa.player;

alter table marauroa.rpattribute CHANGE value value BINARY(255);
alter table marauroa.player CHANGE username username BINARY;

INSERT INTO characters(player_id, charname, object_id)
SELECT player.id, rpattribute.object_id, value 
FROM marauroa.rpattribute, marauroa.player, marauroa.rpobject 
WHERE player.username = rpattribute.value AND rpattribute.name LIKE 'name%' 
AND rpobject.object_id=rpattribute.object_id AND rpobject.slot_id=0;

insert into rpobject(object_id)
select object_id from marauroa.characters;

insert into banlist(id, address, mask)
select id, address, mask from marauroa.banlist;
