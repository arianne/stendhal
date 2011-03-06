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
-- B  Best (xp and online age)
-- @  Achievement score
-- R  Role play score (xp, online age and achievement)

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
ORDER BY xp DESC, karma DESC;

INSERT INTO halloffame_archive (charname, fametype, rank, points, day, recent) 
SELECT name, 'X', @rownum:=@rownum+1 as rank, xp, CURRENT_DATE(), 1 
FROM character_stats, (SELECT @rownum:=0) r 
WHERE admin<=600 AND character_stats.lastseen>date_sub(CURRENT_TIMESTAMP, interval 1 month)
ORDER BY xp DESC, karma DESC;


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


INSERT INTO halloffame_archive (charname, fametype, rank, points, day, recent) 
SELECT scoretable.charname, '@', @rownum:=@rownum+1 as rank, scoretable.points, CURRENT_DATE(), 0 
FROM (
SELECT charname, sum(1/cnt) As points 
FROM reached_achievement ra
JOIN 
    (SELECT achievement_id, count(*) as cnt FROM reached_achievement 
     JOIN achievement on achievement.id = achievement_id 
     JOIN character_stats on name = charname 
     WHERE admin<=600 
     GROUP BY achievement_id) t ON ra.achievement_id = t.achievement_id 
JOIN character_stats ON name = charname
WHERE admin<=600
GROUP BY charname 
ORDER BY points DESC) As scoretable,
(SELECT @rownum:=0) r;

INSERT INTO halloffame_archive (charname, fametype, rank, points, day, recent) 
SELECT scoretable.charname, '@', @rownum:=@rownum+1 as rank, scoretable.points, CURRENT_DATE(), 1 
FROM (
SELECT charname, sum(1/cnt) As points 
FROM reached_achievement ra
JOIN 
    (SELECT achievement_id, count(*) as cnt FROM reached_achievement 
     JOIN achievement on achievement.id = achievement_id 
     JOIN character_stats on name = charname 
     WHERE admin<=600 
     GROUP BY achievement_id) t ON ra.achievement_id = t.achievement_id 
JOIN character_stats ON name = charname
WHERE admin<=600 AND character_stats.lastseen>date_sub(CURRENT_TIMESTAMP, interval 1 month)
GROUP BY charname 
ORDER BY points DESC) As scoretable,
(SELECT @rownum:=0) r;


INSERT INTO halloffame_archive (charname, fametype, rank, points, day, recent) 
SELECT scoretable.name, 'R', @rownum:=@rownum+1 as rank, scoretable.points, CURRENT_DATE(), 0
FROM (
SELECT c.name, xp*(ifnull(score,0)+0.0001)/(age+1) As points, score FROM character_stats c
JOIN (
SELECT name, sum(1/cnt) As score 
FROM reached_achievement ra
JOIN 
    (SELECT achievement_id, count(*) as cnt FROM reached_achievement 
     JOIN achievement on achievement.id = achievement_id 
     JOIN character_stats on name = charname 
     WHERE admin<=600 
     GROUP BY achievement_id) t ON ra.achievement_id = t.achievement_id 
RIGHT JOIN character_stats ON name = charname
WHERE admin<=600 
GROUP BY name 
) temp on temp.name = c.name
order by xp*(ifnull(score,0)+0.0001)/(age+1) desc) As scoretable,
(SELECT @rownum:=0) r;

INSERT INTO halloffame_archive (charname, fametype, rank, points, day, recent) 
SELECT scoretable.name, 'R', @rownum:=@rownum+1 as rank, scoretable.points, CURRENT_DATE(), 1
FROM (
SELECT c.name, xp*(ifnull(score,0)+0.0001)/(age+1) As points, score FROM character_stats c
JOIN (
SELECT name, sum(1/cnt) As score 
FROM reached_achievement ra
JOIN 
    (SELECT achievement_id, count(*) as cnt FROM reached_achievement 
     JOIN achievement on achievement.id = achievement_id 
     JOIN character_stats on name = charname 
     WHERE admin<=600 
     GROUP BY achievement_id) t ON ra.achievement_id = t.achievement_id 
RIGHT JOIN character_stats ON name = charname
WHERE admin<=600 AND character_stats.lastseen>date_sub(CURRENT_TIMESTAMP, interval 1 month)
GROUP BY name 
) temp on temp.name = c.name
order by xp*(ifnull(score,0)+0.0001)/(age+1) desc) As scoretable,
(SELECT @rownum:=0) r;

