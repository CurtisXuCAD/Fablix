/*select top 20 movies include:
title;
year;
director;
first three genres (order does not matter) ;
first three stars (order does not matter);
rating.
*/

SELECT m.id, m.title, m.year, m.director, 
        substring_index(group_concat(DISTINCT g.name separator ', '), ', ' , 3) as gnames, 
        substring_index(group_concat(DISTINCT CONCAT_WS('-', s.id, s.name) separator ', '), ', ' , 3) as snames,
        r.rating
from movies as m,
    genres as g, 
    genres_in_movies as gim, 
    ratings as r, 
    stars as s, 
    stars_in_movies as sim
where m.id = gim.movieId and m.id = sim.movieId and gim.genreId = g.id and sim.starId = s.id and m.id = r.movieId
group by m.id
order by r.rating DESC
limit 20;