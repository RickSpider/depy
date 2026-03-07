Select  u.usuarioid as id, u.account as usuario, u.fullname as Nombre
from sucursalesusuarios su
join usuarios u on u.usuarioid = su.usuarioid
where su.empresaid = ?1
and su.sucursalid = ?2
order by su.sucursalid asc;