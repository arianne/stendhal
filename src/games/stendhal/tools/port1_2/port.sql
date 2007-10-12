-- deleted banned user with invalid characters
DELETE FROM marauroa.rpobject USING marauroa.rpobject, marauroa.rpattribute
WHERE rpobject.object_id = rpattribute.object_id AND rpattribute.name LIKE 'name%' AND rpattribute.value LIKE 'Snaketails>oB<%' AND slot_id = 0;
DELETE FROM marauroa.characters WHERE charname LIKE 'Snaketails>oB<%';
DELETE FROM marauroa.player WHERE username LIKE 'Snaketails>oB<%';


-- delete deprecated attributes
DELETE FROM marauroa.rpattribute WHERE name LIKE 'fullghostmode%';
DELETE FROM marauroa.rpattribute WHERE name LIKE 'features%';

INSERT INTO account(id, username, password, email, timedate, status)
SELECT id, username, password, email, timedate, status FROM marauroa.player;

--ALTER TABLE marauroa.rpattribute CHANGE value value BINARY(255);
--ALTER TABLE marauroa.player CHANGE username username BINARY(255);

-- INSERT INTO characters(player_id, charname, object_id)
-- SELECT player.id, rpattribute.value , rpattribute.object_id
-- FROM marauroa.rpattribute, marauroa.player, marauroa.rpobject 
-- WHERE player.username = rpattribute.value AND rpattribute.name LIKE 'name%' 
-- AND rpobject.object_id=rpattribute.object_id AND rpobject.slot_id=0;

INSERT INTO characters(player_id, charname, object_id)
SELECT player.id, player.username, rpobject.object_id
FROM marauroa.player, marauroa.rpobject, marauroa.characters
WHERE player.username = characters.charname AND rpobject.old_object_id=characters.object_id AND rpobject.slot_id=0;


insert into rpobject(object_id)
select object_id from characters;

insert into banlist(id, address, mask)
select id, address, mask from marauroa.banlist;
