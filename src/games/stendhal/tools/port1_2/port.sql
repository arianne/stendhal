insert into account(id, username, password, email, timedate, status)
select id, username, password, email, timedate, status from olddatabase.player;

insert into characters(player_id, charname, object_id)
select player_id, charname, object_id from olddatabase.characters;

insert into rpobject(object_id)
select object_id from olddatabase.characters;

insert into banlist(id, address, mask)
select id, address, mask from olddatabase.banlist;