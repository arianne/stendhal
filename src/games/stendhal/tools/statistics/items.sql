create table if not exists items
  (
  id integer auto_increment not null,
  datewhen date,
  charname varchar(32),
  slotname varchar(32),
  itemid integer,
  itemname varchar(32),
  amount integer,
  primary key(id)
) TYPE=INNODB;

CREATE INDEX i_items_charname ON items(charname);
CREATE INDEX i_items_slotname ON items(slotname);
CREATE INDEX i_items_itemid ON items(itemid);
CREATE INDEX i_items_itemname ON items(itemname);
CREATE INDEX i_items_amount ON items(amount);
