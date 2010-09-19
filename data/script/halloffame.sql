-- D  deathmatch
-- M  maze
-- P  paper chase (mine town revival weeks)

INSERT INTO halloffame_archive (charname, fametype, rank, points, day, recent) 
SELECT charname, fametype, @rownum:=@rownum+1 as rank, points, CURRENT_DATE(), 0 
FROM halloffame, character_stats, (SELECT @rownum:=0) r 
WHERE fametype='D' AND halloffame.charname = character_stats.name AND admin<=600  
ORDER BY points DESC;

INSERT INTO halloffame_archive (charname, fametype, rank, points, day, recent) 
SELECT charname, fametype, @rownum:=@rownum+1 as rank, points, CURRENT_DATE(), 1 
FROM halloffame, character_stats, (SELECT @rownum:=0) r 
WHERE fametype='D' AND halloffame.charname = character_stats.name AND admin<=600 AND character_stats.lastseen>date_sub(CURRENT_TIMESTAMP, interval 1 month)
ORDER BY points DESC;


INSERT INTO halloffame_archive (charname, fametype, rank, points, day, recent) 
SELECT charname, fametype, @rownum:=@rownum+1 as rank, points, CURRENT_DATE(), 0 
FROM halloffame, character_stats, (SELECT @rownum:=0) r 
WHERE fametype='M' AND halloffame.charname = character_stats.name AND admin<=600 
ORDER BY points DESC;

INSERT INTO halloffame_archive (charname, fametype, rank, points, day, recent) 
SELECT charname, fametype, @rownum:=@rownum+1 as rank, points, CURRENT_DATE(), 1 
FROM halloffame, character_stats, (SELECT @rownum:=0) r 
WHERE fametype='M' AND halloffame.charname = character_stats.name AND admin<=600 AND character_stats.lastseen>date_sub(CURRENT_TIMESTAMP, interval 1 month)
ORDER BY points DESC;


INSERT INTO halloffame_archive (charname, fametype, rank, points, day, recent) 
SELECT charname, fametype, @rownum:=@rownum+1 as rank, points, CURRENT_DATE(), 0 
FROM halloffame, character_stats, (SELECT @rownum:=0) r 
WHERE fametype='P' AND halloffame.charname = character_stats.name AND admin<=600  
ORDER BY points;

INSERT INTO halloffame_archive (charname, fametype, rank, points, day, recent) 
SELECT charname, fametype, @rownum:=@rownum+1 as rank, points, CURRENT_DATE(), 1 
FROM halloffame, character_stats, (SELECT @rownum:=0) r 
WHERE fametype='P' AND halloffame.charname = character_stats.name AND admin<=600 AND character_stats.lastseen>date_sub(CURRENT_TIMESTAMP, interval 1 month)
ORDER BY points;

-- A  online age
-- T  ATK
-- F  DEF
-- W  wealth (money)
-- X  XP
INSERT INTO halloffame_archive (charname, fametype, rank, points, day, recent) 
SELECT name, 'A', @rownum:=@rownum+1 as rank, age/60, CURRENT_DATE(), 0 
FROM character_stats, (SELECT @rownum:=0) r 
WHERE admin<=600 
ORDER BY age DESC;

INSERT INTO halloffame_archive (charname, fametype, rank, points, day, recent) 
SELECT name, 'A', @rownum:=@rownum+1 as rank, age/60, CURRENT_DATE(), 1 
FROM character_stats, (SELECT @rownum:=0) r 
WHERE admin<=600 AND character_stats.lastseen>date_sub(CURRENT_TIMESTAMP, interval 1 month)
ORDER BY age DESC;


INSERT INTO halloffame_archive (charname, fametype, rank, points, day, recent) 
SELECT name, 'T', @rownum:=@rownum+1 as rank, atk*(1+0.03*level) As points, CURRENT_DATE(), 0 
FROM character_stats, (SELECT @rownum:=0) r
WHERE admin<=600 
ORDER BY points DESC;

INSERT INTO halloffame_archive (charname, fametype, rank, points, day, recent) 
SELECT name, 'T', @rownum:=@rownum+1 as rank, atk*(1+0.03*level) As points, CURRENT_DATE(), 1 
FROM character_stats, (SELECT @rownum:=0) r 
WHERE admin<=600 AND character_stats.lastseen>date_sub(CURRENT_TIMESTAMP, interval 1 month)
ORDER BY points DESC;


INSERT INTO halloffame_archive (charname, fametype, rank, points, day, recent) 
SELECT name, 'F', @rownum:=@rownum+1 as rank, def*(1+0.03*level) As points, CURRENT_DATE(), 0 
FROM character_stats, (SELECT @rownum:=0) r 
WHERE admin<=600 
ORDER BY points DESC;

INSERT INTO halloffame_archive (charname, fametype, rank, points, day, recent) 
SELECT name, 'F', @rownum:=@rownum+1 as rank, def*(1+0.03*level) As points, CURRENT_DATE(), 1 
FROM character_stats, (SELECT @rownum:=0) r 
WHERE admin<=600 AND character_stats.lastseen>date_sub(CURRENT_TIMESTAMP, interval 1 month)
ORDER BY points DESC;


INSERT INTO halloffame_archive (charname, fametype, rank, points, day, recent) 
SELECT name, 'W', @rownum:=@rownum+1 as rank, money, CURRENT_DATE(), 0 
FROM character_stats, (SELECT @rownum:=0) r 
WHERE admin<=600 
ORDER BY money DESC;

INSERT INTO halloffame_archive (charname, fametype, rank, points, day, recent) 
SELECT name, 'W', @rownum:=@rownum+1 as rank, money, CURRENT_DATE(), 1 
FROM character_stats, (SELECT @rownum:=0) r 
WHERE admin<=600 AND character_stats.lastseen>date_sub(CURRENT_TIMESTAMP, interval 1 month)
ORDER BY money DESC;


INSERT INTO halloffame_archive (charname, fametype, rank, points, day, recent) 
SELECT name, 'X', @rownum:=@rownum+1 as rank, xp, CURRENT_DATE(), 0 
FROM character_stats, (SELECT @rownum:=0) r 
WHERE admin<=600 
ORDER BY xp DESC;

INSERT INTO halloffame_archive (charname, fametype, rank, points, day, recent) 
SELECT name, 'X', @rownum:=@rownum+1 as rank, xp, CURRENT_DATE(), 1 
FROM character_stats, (SELECT @rownum:=0) r 
WHERE admin<=600 AND character_stats.lastseen>date_sub(CURRENT_TIMESTAMP, interval 1 month)
ORDER BY xp DESC;


INSERT INTO halloffame_archive (charname, fametype, rank, points, day, recent) 
SELECT name, 'B', @rownum:=@rownum+1 as rank, xp/(age+1) As points, CURRENT_DATE(), 0 
FROM character_stats, (SELECT @rownum:=0) r 
WHERE admin<=600 
ORDER BY points DESC;

INSERT INTO halloffame_archive (charname, fametype, rank, points, day, recent) 
SELECT name, 'B', @rownum:=@rownum+1 as rank, xp/(age+1) As points, CURRENT_DATE(), 1 
FROM character_stats, (SELECT @rownum:=0) r 
WHERE admin<=600 AND character_stats.lastseen>date_sub(CURRENT_TIMESTAMP, interval 1 month)
ORDER BY points DESC;

