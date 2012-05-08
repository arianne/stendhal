-- D  deathmatch
-- M  maze
-- P  paper chase (mine town revival weeks)
-- A  online age
-- T  ATK
-- F  DEF
-- W  wealth (money)
-- X  XP
-- B  Best (xp and online age)
-- @  Achievement score
-- R  Role play score (xp, online age and achievement)

INSERT INTO halloffame_archive_recent (charname, fametype, rank, points, day) 
SELECT charname, fametype, @rownum:=@rownum+1 as rank, points, CURRENT_DATE() 
FROM halloffame, character_stats, (SELECT @rownum:=0) r 
WHERE fametype='D' AND halloffame.charname = character_stats.name AND admin<=600 AND points > 0 AND character_stats.lastseen>date_sub(CURRENT_TIMESTAMP, interval 1 month)
ORDER BY points DESC;


INSERT INTO halloffame_archive_recent (charname, fametype, rank, points, day) 
SELECT charname, fametype, @rownum:=@rownum+1 as rank, points, CURRENT_DATE() 
FROM halloffame, character_stats, (SELECT @rownum:=0) r 
WHERE fametype='M' AND halloffame.charname = character_stats.name AND admin<=600 AND points > 0 AND character_stats.lastseen>date_sub(CURRENT_TIMESTAMP, interval 1 month)
ORDER BY points DESC;


INSERT INTO halloffame_archive_recent (charname, fametype, rank, points, day) 
SELECT charname, fametype, @rownum:=@rownum+1 as rank, points, CURRENT_DATE() 
FROM halloffame, character_stats, (SELECT @rownum:=0) r 
WHERE fametype='P' AND halloffame.charname = character_stats.name AND admin<=600 AND points > 0 AND character_stats.lastseen>date_sub(CURRENT_TIMESTAMP, interval 1 month)
ORDER BY points;


INSERT INTO halloffame_archive_recent (charname, fametype, rank, points, day) 
SELECT name, 'A', @rownum:=@rownum+1 as rank, age/60, CURRENT_DATE() 
FROM character_stats, (SELECT @rownum:=0) r 
WHERE admin<=600 AND age >= 5 AND character_stats.lastseen>date_sub(CURRENT_TIMESTAMP, interval 1 month)
ORDER BY age DESC;


INSERT INTO halloffame_archive_recent (charname, fametype, rank, points, day) 
SELECT name, 'T', @rownum:=@rownum+1 as rank, atk*(1+0.03*level) As points, CURRENT_DATE() 
FROM character_stats, (SELECT @rownum:=0) r 
WHERE admin<=600 AND level >= 2 AND character_stats.lastseen>date_sub(CURRENT_TIMESTAMP, interval 1 month)
ORDER BY points DESC;


INSERT INTO halloffame_archive_recent (charname, fametype, rank, points, day) 
SELECT name, 'F', @rownum:=@rownum+1 as rank, def*(1+0.03*level) As points, CURRENT_DATE() 
FROM character_stats, (SELECT @rownum:=0) r 
WHERE admin<=600 AND level >= 2 AND character_stats.lastseen>date_sub(CURRENT_TIMESTAMP, interval 1 month)
ORDER BY points DESC;


INSERT INTO halloffame_archive_recent (charname, fametype, rank, points, day) 
SELECT name, 'W', @rownum:=@rownum+1 as rank, money, CURRENT_DATE() 
FROM character_stats, (SELECT @rownum:=0) r 
WHERE admin<=600 AND money >= 100 AND character_stats.lastseen>date_sub(CURRENT_TIMESTAMP, interval 1 month)
ORDER BY money DESC;


INSERT INTO halloffame_archive_recent (charname, fametype, rank, points, day) 
SELECT name, 'X', @rownum:=@rownum+1 as rank, xp, CURRENT_DATE() 
FROM character_stats, (SELECT @rownum:=0) r 
WHERE admin<=600 AND xp >= 100 AND character_stats.lastseen>date_sub(CURRENT_TIMESTAMP, interval 1 month)
ORDER BY xp DESC, karma DESC;


INSERT INTO halloffame_archive_recent (charname, fametype, rank, points, day) 
SELECT name, 'B', @rownum:=@rownum+1 as rank, xp/(age+1) As points, CURRENT_DATE() 
FROM character_stats, (SELECT @rownum:=0) r 
WHERE admin<=600 AND xp >= 100 AND character_stats.lastseen>date_sub(CURRENT_TIMESTAMP, interval 1 month)
ORDER BY points DESC;


INSERT INTO halloffame_archive_recent (charname, fametype, rank, points, day) 
SELECT scoretable.charname, '@', @rownum:=@rownum+1 as rank, scoretable.points, CURRENT_DATE() 
FROM (
SELECT charname, round(1000000*sum(1/cnt)) As points, count(*) As nmb, xp
FROM reached_achievement ra
JOIN 
    (SELECT achievement_id, count(*) as cnt FROM reached_achievement 
     JOIN achievement on achievement.id = achievement_id 
     JOIN character_stats on name = charname 
     WHERE admin<=600 and category != 'SPECIAL' AND active=1
     GROUP BY achievement_id) t ON ra.achievement_id = t.achievement_id 
JOIN character_stats ON name = charname
WHERE admin<=600 AND character_stats.lastseen>date_sub(CURRENT_TIMESTAMP, interval 1 month)
GROUP BY charname 
ORDER BY points DESC, nmb DESC, xp DESC) As scoretable,
(SELECT @rownum:=0) r WHERE points > 500;



INSERT INTO halloffame_archive_recent (charname, fametype, rank, points, day) 
SELECT scoretable.name, 'R', @rownum:=@rownum+1 as rank, scoretable.points, CURRENT_DATE()
FROM (
SELECT c.name, round(ln(xp*(ifnull(score*1000,0)+0.0001)/(age+1))*1000) As points, score FROM character_stats c
JOIN (
SELECT name, sum(1/cnt) As score 
FROM reached_achievement ra
JOIN 
    (SELECT achievement_id, count(*) as cnt FROM reached_achievement 
     JOIN achievement on achievement.id = achievement_id 
     JOIN character_stats on name = charname 
     WHERE admin<=600 and category != 'SPECIAL' AND active=1
     GROUP BY achievement_id) t ON ra.achievement_id = t.achievement_id 
RIGHT JOIN character_stats ON name = charname
WHERE admin<=600 AND character_stats.lastseen>date_sub(CURRENT_TIMESTAMP, interval 1 month)
GROUP BY name 
) temp on temp.name = c.name
order by ln(xp*(ifnull(score*1000,0)+0.0001)/(age+1))*1000 desc) As scoretable,
(SELECT @rownum:=0) r WHERE points > 0;





TRUNCATE TABLE halloffame_archive_alltimes;


INSERT INTO halloffame_archive_alltimes (charname, fametype, rank, points, day) 
SELECT charname, fametype, @rownum:=@rownum+1 as rank, points, CURRENT_DATE() 
FROM halloffame, character_stats, (SELECT @rownum:=0) r 
WHERE fametype='D' AND halloffame.charname = character_stats.name AND admin<=600 AND points > 0
ORDER BY points DESC;


INSERT INTO halloffame_archive_alltimes (charname, fametype, rank, points, day) 
SELECT charname, fametype, @rownum:=@rownum+1 as rank, points, CURRENT_DATE() 
FROM halloffame, character_stats, (SELECT @rownum:=0) r 
WHERE fametype='M' AND halloffame.charname = character_stats.name AND admin<=600 AND points > 0 
ORDER BY points DESC;


INSERT INTO halloffame_archive_alltimes (charname, fametype, rank, points, day) 
SELECT charname, fametype, @rownum:=@rownum+1 as rank, points, CURRENT_DATE() 
FROM halloffame, character_stats, (SELECT @rownum:=0) r 
WHERE fametype='P' AND halloffame.charname = character_stats.name AND admin<=600 AND points > 0 
ORDER BY points;


INSERT INTO halloffame_archive_alltimes (charname, fametype, rank, points, day) 
SELECT name, 'A', @rownum:=@rownum+1 as rank, age/60, CURRENT_DATE()
FROM character_stats, (SELECT @rownum:=0) r 
WHERE admin<=600 AND age >= 5
ORDER BY age DESC;


INSERT INTO halloffame_archive_alltimes (charname, fametype, rank, points, day) 
SELECT name, 'T', @rownum:=@rownum+1 as rank, atk*(1+0.03*level) As points, CURRENT_DATE() 
FROM character_stats, (SELECT @rownum:=0) r
WHERE admin<=600 AND level >= 2
ORDER BY points DESC;


INSERT INTO halloffame_archive_alltimes (charname, fametype, rank, points, day) 
SELECT name, 'F', @rownum:=@rownum+1 as rank, def*(1+0.03*level) As points, CURRENT_DATE() 
FROM character_stats, (SELECT @rownum:=0) r 
WHERE admin<=600 AND level >= 2 
ORDER BY points DESC;


INSERT INTO halloffame_archive_alltimes (charname, fametype, rank, points, day) 
SELECT name, 'W', @rownum:=@rownum+1 as rank, money, CURRENT_DATE() 
FROM character_stats, (SELECT @rownum:=0) r 
WHERE admin<=600 AND money >= 100
ORDER BY money DESC;


INSERT INTO halloffame_archive_alltimes (charname, fametype, rank, points, day) 
SELECT name, 'X', @rownum:=@rownum+1 as rank, xp, CURRENT_DATE() 
FROM character_stats, (SELECT @rownum:=0) r 
WHERE admin<=600 AND xp >= 100 
ORDER BY xp DESC, karma DESC;


INSERT INTO halloffame_archive_alltimes (charname, fametype, rank, points, day) 
SELECT name, 'B', @rownum:=@rownum+1 as rank, xp/(age+1) As points, CURRENT_DATE() 
FROM character_stats, (SELECT @rownum:=0) r 
WHERE admin<=600 AND xp >= 100 
ORDER BY points DESC;


INSERT INTO halloffame_archive_alltimes (charname, fametype, rank, points, day) 
SELECT scoretable.charname, '@', @rownum:=@rownum+1 as rank, scoretable.points, CURRENT_DATE() 
FROM (
SELECT charname, round(1000000*sum(1/cnt)) As points, count(*) As nmb
FROM reached_achievement ra
JOIN 
    (SELECT achievement_id, count(*) as cnt FROM reached_achievement 
     JOIN achievement on achievement.id = achievement_id 
     JOIN character_stats on name = charname 
     WHERE admin<=600 and category != 'SPECIAL' AND active=1
     GROUP BY achievement_id) t ON ra.achievement_id = t.achievement_id 
JOIN character_stats ON name = charname
WHERE admin<=600
GROUP BY charname 
ORDER BY points DESC, nmb DESC, xp DESC) As scoretable,
(SELECT @rownum:=0) r WHERE points > 500;


INSERT INTO halloffame_archive_alltimes (charname, fametype, rank, points, day) 
SELECT scoretable.name, 'R', @rownum:=@rownum+1 as rank, scoretable.points, CURRENT_DATE()
FROM (
SELECT c.name, round(ln(xp*(ifnull(score*1000,0)+0.0001)/(age+1))*1000) As points, score FROM character_stats c
JOIN (
SELECT name, sum(1/cnt) As score 
FROM reached_achievement ra
JOIN 
    (SELECT achievement_id, count(*) as cnt FROM reached_achievement 
     JOIN achievement on achievement.id = achievement_id 
     JOIN character_stats on name = charname 
     WHERE admin<=600 and category != 'SPECIAL' AND active=1
     GROUP BY achievement_id) t ON ra.achievement_id = t.achievement_id 
RIGHT JOIN character_stats ON name = charname
WHERE admin<=600 
GROUP BY name 
) temp on temp.name = c.name
order by ln(xp*(ifnull(score*1000,0)+0.0001)/(age+1))*1000 desc) As scoretable,
(SELECT @rownum:=0) r WHERE points > 0;


-- statistics

INSERT INTO statistics_archive (name, val, day)
SELECT 'accounts_30', count(DISTINCT player_id), CURRENT_DATE() 
FROM loginEvent 
WHERE timedate>date_sub(CURRENT_TIMESTAMP, interval 30 day) AND result=1;

INSERT INTO statistics_archive (name, val, day)
SELECT 'accounts_7', count(DISTINCT player_id), CURRENT_DATE() 
FROM loginEvent 
WHERE timedate>date_sub(CURRENT_TIMESTAMP, interval 7 day) AND result=1;

INSERT INTO statistics_archive (name, val, day)
SELECT 'accounts_1', count(DISTINCT player_id), CURRENT_DATE() 
FROM loginEvent 
WHERE timedate>date_sub(CURRENT_TIMESTAMP, interval 1 day) AND result=1;


INSERT INTO statistics_archive (name, val, day)
SELECT 'characters_30', count(DISTINCT charname), CURRENT_DATE() 
FROM cid 
WHERE timedate>date_sub(CURRENT_TIMESTAMP, interval 30 day);

INSERT INTO statistics_archive (name, val, day)
SELECT 'characters_7', count(DISTINCT charname), CURRENT_DATE() 
FROM cid 
WHERE timedate>date_sub(CURRENT_TIMESTAMP, interval 7 day);

INSERT INTO statistics_archive (name, val, day)
SELECT 'characters_1', count(DISTINCT charname), CURRENT_DATE() 
FROM cid 
WHERE timedate>date_sub(CURRENT_TIMESTAMP, interval 1 day);

